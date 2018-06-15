/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.io;

import java.net.InetSocketAddress;
import java.util.List;

import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

import com.mouse.configuration.ClientConfigManager;
import com.mouse.message.internal.MessageIdFactory;
import com.mouse.message.spi.MessageCodec;
import com.mouse.message.spi.MessageQueue;
import com.mouse.message.spi.MessageStatistics;
import com.mouse.message.spi.MessageTree;

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

    private List<InetSocketAddress> serverAddress;

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    /** 
     * @see org.unidal.lookup.logging.LogEnabled#enableLogging(org.unidal.lookup.logging.Logger)
     */
    @Override
    public void enableLogging(Logger logger) {
        // TODO Auto-generated method stub

    }

    /** 
     * @see com.mouse.message.io.MessageSender#initialize()
     */
    @Override
    public void initialize() {
        // TODO Auto-generated method stub

    }

    /** 
     * @see com.mouse.message.io.MessageSender#send(com.mouse.message.spi.MessageTree)
     */
    @Override
    public void send(MessageTree tree) {
        // TODO Auto-generated method stub

    }

    /** 
     * @see org.unidal.helper.Threads.Task#getName()
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * @see org.unidal.helper.Threads.Task#shutdown()
     */
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

}
