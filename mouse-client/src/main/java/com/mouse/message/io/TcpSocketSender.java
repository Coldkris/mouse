/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.io;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

import com.mouse.configuration.ClientConfigManager;
import com.mouse.message.Message;
import com.mouse.message.Transaction;
import com.mouse.message.internal.DefaultMessageTree;
import com.mouse.message.internal.DefaultTransaction;
import com.mouse.message.internal.MessageIdFactory;
import com.mouse.message.spi.MessageCodec;
import com.mouse.message.spi.MessageQueue;
import com.mouse.message.spi.MessageStatistics;
import com.mouse.message.spi.MessageTree;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * TCP Socket发送器
 * @author kris
 * @version $Id: TcpSocketSender.java, v 0.1 2018年6月14日 下午5:34:24 kris Exp $
 */
public class TcpSocketSender implements Task, MessageSender, LogEnabled {

    public static final String      ID               = "tcp-socket-sender";

    private static final int        MAX_CHILD_NUMBER = 200;

    private static final long       HOUR             = 1000 * 60 * 60L;

    @Inject
    private MessageCodec            codec;

    @Inject
    private MessageStatistics       statistics;

    @Inject
    private ClientConfigManager     configManager;

    @Inject
    private MessageIdFactory        factory;

    private MessageQueue            queue;

    private MessageQueue            atomicTrees;

    private List<InetSocketAddress> serverAddresses;

    private ChannelManager          manager;

    private Logger                  logger;

    private transient boolean       active;

    private AtomicInteger           errors           = new AtomicInteger();

    private AtomicInteger           attempts         = new AtomicInteger();

    public static int getQueueSize() {
        String size = System.getProperty("queue.size", "1000");

        return Integer.parseInt(size);
    }

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        active = true;

        try {
            while (active) {
                ChannelFuture channel = manager.channel();

                if (channel != null && checkWritable(channel)) {
                    try {
                        MessageTree tree = queue.poll();

                        if (tree != null) {
                            sendInternal(tree);
                            tree.setMessage(null);
                        }
                    } catch (Throwable t) {
                        logger.error("Error when sending message over TCP socket!", t);
                    }
                } else {
                    long current = System.currentTimeMillis();
                    long oldTimestamp = current - HOUR;

                    while (true) {
                        try {
                            MessageTree tree = queue.peek();

                            if (tree != null && tree.getMessage().getTimestamp() < oldTimestamp) {
                                MessageTree discradTree = queue.poll();

                                if (discradTree != null) {
                                    statistics.onOverflowed(discradTree);
                                }
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            break;
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(5);
                }
            }
        } catch (Exception e) {
            active = false;
        }

    }

    private void sendInternal(MessageTree tree) {
        ChannelFuture future = manager.channel();
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(10 * 1024); // 10K

        codec.encode(tree, buf);

        int size = buf.readableBytes();
        Channel channel = future.channel();

        channel.writeAndFlush(buf);
        if (statistics != null) {
            statistics.onBytes(size);
        }

    }

    private boolean checkWritable(ChannelFuture future) throws InterruptedException {
        boolean isWriteable = false;
        Channel channel = future.channel();

        if (channel.isOpen()) {
            if (channel.isActive() && channel.isWritable()) {
                isWriteable = true;
            } else {
                int count = attempts.incrementAndGet();

                if (count % 1000 == 0 || count == 1) {
                    logger.warn("Netty write buffer is full! Attempts: " + count);
                }

                TimeUnit.MILLISECONDS.sleep(5);
            }
        }

        return isWriteable;
    }

    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void initialize() {
        int len = getQueueSize();

        queue = new DefaultMessageQueue(len);
        atomicTrees = new DefaultMessageQueue(len);

        manager = new ChannelManager(logger, serverAddresses, queue, configManager, factory);

        Threads.forGroup("mouse").start(this);
        Threads.forGroup("mouse").start(manager);
        Threads.forGroup("mouse").start(new MergeAtomicTask());
    }

    @Override
    public void send(MessageTree tree) {
        if (isAtomicMessage(tree)) {
            boolean result = atomicTrees.offer(tree, manager.getSample());

            if (!result) {
                logQueueFullInfo(tree);
            }
        } else {
            boolean result = queue.offer(tree, manager.getSample());

            if (!result) {
                logQueueFullInfo(tree);
            }
        }

    }

    private boolean isAtomicMessage(MessageTree tree) {
        Message message = tree.getMessage();

        if (message instanceof Transaction) {
            String type = message.getType();

            if (type.startsWith("Cache.") || "SQL".equals(type)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public String getName() {
        return "TcpSocketSender";
    }

    @Override
    public void shutdown() {
        active = false;
        manager.shutdown();

    }

    public class MergeAtomicTask implements Task {

        @Override
        public void run() {
            while (true) {
                if (shouldMerge(atomicTrees)) {
                    MessageTree tree = mergeTree(atomicTrees);
                    boolean result = queue.offer(tree);

                    if (!result) {
                        logQueueFullInfo(tree);
                    }
                } else {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }

        @Override
        public String getName() {
            return "merge-atomic-task";
        }

        @Override
        public void shutdown() {

        }

    }

    // 每30秒或每200条合并一次原子消息
    private boolean shouldMerge(MessageQueue trees) {
        MessageTree tree = trees.peek();

        if (tree != null) {
            long firstTime = tree.getMessage().getTimestamp();
            int maxDuration = 1000 * 30;

            if (System.currentTimeMillis() - firstTime > maxDuration || trees.size() >= MAX_CHILD_NUMBER) {
                return true;
            }
        }
        return false;
    }

    private void logQueueFullInfo(MessageTree tree) {
        if (statistics != null) {
            statistics.onOverflowed(tree);
        }

        int count = errors.incrementAndGet();

        if (count % 1000 == 0 || count == 1) {
            logger.error("Message queue is full in tcp socket sender! Count: " + count);
        }

        tree = null;
    }

    private MessageTree mergeTree(MessageQueue trees) {
        int max = MAX_CHILD_NUMBER;
        DefaultTransaction t = new DefaultTransaction("System", "MergeTree", null);
        MessageTree first = trees.poll();

        t.setStatus(Transaction.SUCCESS);
        t.setCompleted(true);
        t.addChild(first.getMessage());
        t.setTimestamp(first.getMessage().getTimestamp());
        long lastTimestamp = 0;
        long lastDuration = 0;

        while (max >= 0) {
            MessageTree tree = trees.poll();

            if (tree == null) {
                t.setDurationInMillis(lastTimestamp - t.getTimestamp() + lastDuration);
                break;
            }
            lastTimestamp = tree.getMessage().getTimestamp();
            if (tree.getMessage() instanceof DefaultTransaction) {
                lastDuration = ((DefaultTransaction) tree.getMessage()).getDurationInMillis();
            } else {
                lastDuration = 0;
            }
            t.addChild(tree.getMessage());
            factory.reuse(tree.getMessageId());
            max--;
        }

        ((DefaultMessageTree) first).setMessage(t);
        return first;
    }

}
