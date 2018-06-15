/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.unidal.lookup.annotation.Inject;

import com.mouse.Mouse;
import com.mouse.message.Event;
import com.mouse.message.ForkedTransaction;
import com.mouse.message.Heartbeat;
import com.mouse.message.Message;
import com.mouse.message.MessageProducer;
import com.mouse.message.Metric;
import com.mouse.message.TaggedTransaction;
import com.mouse.message.Trace;
import com.mouse.message.Transaction;
import com.mouse.message.spi.MessageManager;
import com.mouse.message.spi.MessageTree;

/**
 * 默认消息生产者实现
 * @author kris
 * @version $Id: DefaultMessageProducer.java, v 0.1 2018年6月14日 下午4:02:45 kris Exp $
 */
public class DefaultMessageProducer implements MessageProducer {

    @Inject
    private MessageManager   manager;

    @Inject
    private MessageIdFactory factory;

    @Override
    public String createMessageId() {
        return factory.getNextId();
    }

    @Override
    public boolean isEnabled() {
        return manager.isMessageEnabled();
    }

    @Override
    public void logError(Throwable cause) {
        logError(null, cause);
    }

    @Override
    public void logError(String message, Throwable cause) {
        if (Mouse.getManager().isMouseEnabled()) {
            if (shouldLog(cause)) {
                manager.getThreadLocalMessageTree().setSample(false);

                StringWriter writer = new StringWriter(2048);

                if (message != null) {
                    writer.write(message);
                    writer.write(' ');
                }

                cause.printStackTrace(new PrintWriter(writer));

                String detailMessage = writer.toString();

                if (cause instanceof Error) {
                    logEvent("Error", cause.getClass().getName(), "ERROR", detailMessage);
                } else if (cause instanceof RuntimeException) {
                    logEvent("RuntimeException", cause.getClass().getName(), "ERROR", detailMessage);
                } else {
                    logEvent("Exception", cause.getClass().getName(), "ERROR", detailMessage);
                }
            }
        } else {
            cause.printStackTrace();
        }
    }

    @Override
    public void logEvent(String type, String name) {
        logEvent(type, name, Message.SUCCESS, null);
    }

    @Override
    public void logEvent(String type, String name, String status, String nameValuePairs) {
        Event event = newEvent(type, name);

        if (nameValuePairs != null && nameValuePairs.length() > 0) {
            event.addData(nameValuePairs);
        }

        event.setStatus(status);
        event.complete();
    }

    @Override
    public void logTrace(String type, String name) {
        logTrace(type, name, Message.SUCCESS, null);
    }

    @Override
    public void logTrace(String type, String name, String status, String nameValuePairs) {
        if (manager.isTraceMode()) {
            Trace trace = newTrace(type, name);

            if (nameValuePairs != null && nameValuePairs.length() > 0) {
                trace.addData(nameValuePairs);
            }

            trace.setStatus(status);
            trace.complete();
        }
    }

    @Override
    public void logHeartbeat(String type, String name, String status, String nameValuePairs) {
        Heartbeat heartbeat = newHeartbeat(type, name);

        heartbeat.addData(nameValuePairs);
        heartbeat.setStatus(status);
        heartbeat.complete();
    }

    @Override
    public void logMetric(String name, String status, String nameValuePairs) {
        String type = "";
        Metric metric = newMetric(type, name);

        if (nameValuePairs != null && nameValuePairs.length() > 0) {
            metric.addData(nameValuePairs);
        }

        metric.setStatus(status);
        metric.complete();
    }

    @Override
    public Event newEvent(String type, String name) {
        if (!manager.hasContext()) {
            manager.setup();
        }

        if (manager.isMessageEnabled()) {
            DefaultEvent event = new DefaultEvent(type, name, manager);

            return event;
        } else {
            return NullMessage.EVENT;
        }

    }

    @Override
    public Trace newTrace(String type, String name) {
        if (!manager.hasContext()) {
            manager.setup();
        }

        if (manager.isMessageEnabled()) {
            DefaultTrace trace = new DefaultTrace(type, name, manager);

            return trace;
        } else {
            return NullMessage.TRACE;
        }
    }

    @Override
    public Heartbeat newHeartbeat(String type, String name) {
        if (!manager.hasContext()) {
            manager.setup();
        }

        if (manager.isMessageEnabled()) {
            DefaultHeartbeat heartbeat = new DefaultHeartbeat(type, name, manager);

            manager.getThreadLocalMessageTree().setSample(false);
            return heartbeat;
        } else {
            return NullMessage.HEARTBEAT;
        }
    }

    @Override
    public Metric newMetric(String type, String name) {
        if (!manager.hasContext()) {
            manager.setup();
        }

        if (manager.isMessageEnabled()) {
            DefaultMetric metric = new DefaultMetric(type == null ? "" : type, name, manager);

            manager.getThreadLocalMessageTree().setSample(false);
            return metric;
        } else {
            return NullMessage.METRIC;
        }
    }

    @Override
    public Transaction newTransaction(String type, String name) {
        // 客户端无需显式设置即可记录猫消息
        if (!manager.hasContext()) {
            manager.setup();
        }

        if (manager.isMessageEnabled()) {
            DefaultTransaction transaction = new DefaultTransaction(type, name, manager);

            manager.start(transaction, false);
            return transaction;
        } else {
            return NullMessage.TRANSACTION;
        }
    }

    public Transaction newTransaction(Transaction parent, String type, String name) {
        // 客户端无需显式设置即可记录猫消息
        if (!manager.hasContext()) {
            manager.setup();
        }

        if (manager.isMessageEnabled() && parent != null) {
            DefaultTransaction transaction = new DefaultTransaction(type, name, manager);

            parent.addChild(transaction);
            transaction.setStandalone(false);
            return transaction;
        } else {
            return NullMessage.TRANSACTION;
        }
    }

    @Override
    public ForkedTransaction newForkedTransaction(String type, String name) {
        // 客户端无需显式设置即可记录猫消息
        if (!manager.hasContext()) {
            manager.setup();
        }

        if (manager.isMessageEnabled()) {
            MessageTree tree = manager.getThreadLocalMessageTree();

            if (tree.getMessageId() == null) {
                tree.setMessageId(createMessageId());
            }

            DefaultForkedTransaction transaction = new DefaultForkedTransaction(type, name, manager);

            if (manager instanceof DefaultMessageManager) {
                ((DefaultMessageManager) manager).linkAsRunAway(transaction);
            }
            manager.start(transaction, true);
            return transaction;
        } else {
            return NullMessage.TRANSACTION;
        }
    }

    @Override
    public TaggedTransaction newTaggedTransaction(String type, String name, String tag) {
        // 客户端无需显式设置即可记录猫消息
        if (!manager.hasContext()) {
            manager.setup();
        }

        if (manager.isMessageEnabled()) {
            MessageTree tree = manager.getThreadLocalMessageTree();

            if (tree.getMessageId() == null) {
                tree.setMessageId(createMessageId());
            }

            DefaultTaggedTransaction transaction = new DefaultTaggedTransaction(type, name, tag, manager);

            manager.start(transaction, true);
            return transaction;
        } else {
            return NullMessage.TRANSACTION;
        }
    }

    private boolean shouldLog(Throwable e) {
        if (manager instanceof DefaultMessageManager) {
            return ((DefaultMessageManager) manager).shouldLog(e);
        } else {
            return true;
        }
    }

}
