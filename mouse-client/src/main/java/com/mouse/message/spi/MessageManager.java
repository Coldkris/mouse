/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

import com.mouse.message.Message;
import com.mouse.message.Transaction;

/**
 * 用于构建CAT消息的消息管理器
 * <p>
 * 注意：此方法仅供内部使用。 应用程序开发人员不应直接调用此方法。
 * @author kris
 * @version $Id: MessageManager.java, v 0.1 2018年5月23日 下午10:29:28 kris Exp $
 */
public interface MessageManager {

    /**
     * 新增消息
     * @param message
     */
    public void add(Message message);

    /**
     * 在事务结束时触发，无论它是根事务还是嵌套事务。 但是，如果它是根事务，则它将异步刷新到后端CAT服务器。
     * <p>
     * @param transaction
     */
    public void end(Transaction transaction);

    /**
     * 获取当前线程的peek事务。
     * @return 当前线程的peek事务，如果没有事务，则返回null。
     */
    public Transaction getPeekTransaction();

    /**
     * 获取线程本地消息信息
     * @return 消息树，null表示当前线程未正确设置。
     */
    public MessageTree getThreadLocalMessageTree();

    /** 
     * 检查线程上下文是否设置。
     * @return 如果线程上下文已设置，则返回true，否则返回false
     */
    public boolean hasContext();

    /**
     * 检查当前上下文记录是启用还是禁用。
     * @return 如果启用当前上下文，则返回true
     */
    public boolean isMessageEnabled();

    /**
     * 检查CAT记录是启用还是禁用。
     * @return 如果CAT已启用，则返回true
     */
    public boolean isCatEnabled();

    /**
     * 检查CAT跟踪模式是启用还是禁用。
     * @return 如果CAT启动跟踪模式，则返回true
     */
    public boolean isTraceMode();

    /**
     * 为清理当前线程环境以释放线程本地对象中的资源。
     */
    public void reset();

    /**
     * 设置CAT跟踪模式。
     * @param traceMode
     */
    public void setTradeMode(boolean traceMode);

    /**
     * 为当前线程环境设置线程本地对象。
     */
    public void setup();

    /**
     * 新事务启动时触发，无论它是根事务还是嵌套事务。
     * @param transaction
     * @param forked
     */
    public void start(Transaction transaction, boolean forked);

    /**
     * 将当前消息树绑定到使用tag标记的事务上。
     * @param tag    标记事务的标记名称
     * @param title  日志视图中显示的标题
     */
    public void bind(String tag, String title);

    /**
     * 获取domain
     * @return
     */
    public String getDomain();

}
