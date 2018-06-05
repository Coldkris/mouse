/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

import com.mouse.configuration.ClientConfigManager;
import com.mouse.configuration.client.entity.Domain;
import com.mouse.message.Message;
import com.mouse.message.TaggedTransaction;
import com.mouse.message.Transaction;
import com.mouse.message.io.TransportManager;
import com.mouse.message.spi.MessageManager;
import com.mouse.message.spi.MessageTree;

/**
 * 默认消息管理器
 * @author kris
 * @version $Id: DefaultMessageManager.java, v 0.1 2018年6月1日 下午3:59:23 kris Exp $
 */
public class DefaultMessageManager extends ContainerHolder implements MessageManager, Initializable, LogEnabled {

    @Inject
    private ClientConfigManager            configManager;

    @Inject
    private TransportManager               transportManager;

    @Inject
    private MessageIdFactory               factory;

    // 不使用静态修饰符，因为MessageManager被配置为单例
    private ThreadLocal<Context>           context      = new ThreadLocal<>();

    private long                           throttleTimes;

    private Domain                         domain;

    private String                         hostName;

    private boolean                        firstMessage = true;

    private TransactionHelper              validator    = new TransactionHelper();

    private Map<String, TaggedTransaction> taggerdTransactions;

    private Logger                         logger;

    @Override
    public void enableLogging(Logger logger) {
        // TODO Auto-generated method stub

    }

    /** 
     * @see org.unidal.lookup.extension.Initializable#initialize()
     */
    @Override
    public void initialize() throws InitializationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void add(Message message) {

    }

    /** 
     * @see com.mouse.message.spi.MessageManager#end(com.mouse.message.Transaction)
     */
    @Override
    public void end(Transaction transaction) {
        // TODO Auto-generated method stub

    }

    /** 
     * @see com.mouse.message.spi.MessageManager#getPeekTransaction()
     */
    @Override
    public Transaction getPeekTransaction() {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * @see com.mouse.message.spi.MessageManager#getThreadLocalMessageTree()
     */
    @Override
    public MessageTree getThreadLocalMessageTree() {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * @see com.mouse.message.spi.MessageManager#hasContext()
     */
    @Override
    public boolean hasContext() {
        // TODO Auto-generated method stub
        return false;
    }

    /** 
     * @see com.mouse.message.spi.MessageManager#isMessageEnabled()
     */
    @Override
    public boolean isMessageEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    /** 
     * @see com.mouse.message.spi.MessageManager#isMouseEnabled()
     */
    @Override
    public boolean isMouseEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    /** 
     * @see com.mouse.message.spi.MessageManager#isTraceMode()
     */
    @Override
    public boolean isTraceMode() {
        // TODO Auto-generated method stub
        return false;
    }

    /** 
     * @see com.mouse.message.spi.MessageManager#reset()
     */
    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    /** 
     * @see com.mouse.message.spi.MessageManager#setTradeMode(boolean)
     */
    @Override
    public void setTradeMode(boolean traceMode) {
        // TODO Auto-generated method stub

    }

    /** 
     * @see com.mouse.message.spi.MessageManager#setup()
     */
    @Override
    public void setup() {
        // TODO Auto-generated method stub

    }

    /** 
     * @see com.mouse.message.spi.MessageManager#start(com.mouse.message.Transaction, boolean)
     */
    @Override
    public void start(Transaction transaction, boolean forked) {
        // TODO Auto-generated method stub

    }

    /** 
     * @see com.mouse.message.spi.MessageManager#bind(java.lang.String, java.lang.String)
     */
    @Override
    public void bind(String tag, String title) {
        // TODO Auto-generated method stub

    }

    /** 
     * @see com.mouse.message.spi.MessageManager#getDomain()
     */
    @Override
    public String getDomain() {
        // TODO Auto-generated method stub
        return null;
    }

    class Context {

        private MessageTree        tree;

        private Stack<Transaction> stack;

        private int                length;

        private boolean            traceMode;

        private long               totalDurationInMicros; // 截断消息

        private Set<Throwable>     knownExceptions;

        public Context(String domain, String hostName, String ipAddress) {

        }
    }

    class TransactionHelper {

    }

}
