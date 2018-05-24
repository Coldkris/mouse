/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

import com.mouse.message.Message;

/**
 * 消息树
 * @author kris
 * @version $Id: MessageTree.java, v 0.1 2018年5月24日 上午9:08:42 kris Exp $
 */
public interface MessageTree extends Cloneable {

    /**
     * 复制消息树
     * @return
     */
    public MessageTree copy();

    /**
     * 获取Domain
     * @return
     */
    public String getDomain();

    /**
     * 获取主机名
     * @return
     */
    public String getHostName();

    /**
     * 获取IP地址
     * @return
     */
    public String getIpAddress();

    /**
     * 获取消息
     * @return
     */
    public Message getMessage();

    /**
     * 获取消息id
     * @return
     */
    public String getMessageId();

    /**
     * 获取父消息id
     * @return
     */
    public String getParentMessageId();

    /**
     * 获取根消息id
     * @return
     */
    public String getRootMessageId();

    /**
     * 获取会话令牌
     * @return
     */
    public String getSessionToken();

    /**
     * 获取线程组名
     * @return
     */
    public String getThreadGroupName();

    /**
     * 获取线程id
     * @return
     */
    public String getThreadId();

    /**
     * 获取线程名
     * @return
     */
    public String getThreadName();

    /**
     * TODO
     * @return
     */
    public boolean isSample();

    /**
     * 设置domain
     * @param domain
     */
    public void setDomain(String domain);

    /**
     * 设置主机名
     * @param hostName
     */
    public void setHostName(String hostName);

    /**
     * 设置IP地址
     * @param ipAddress
     */
    public void setIpAddress(String ipAddress);

    /**
     * 设置消息
     * @param message
     */
    public void setMessage(Message message);

    /**
     * 设置消息id
     * @param messageId
     */
    public void setMessageId(String messageId);

    /**
     * 设置父消息id
     * @param parentMessageId
     */
    public void setParentMessageId(String parentMessageId);

    /**
     * 设置根消息id
     * @param rootMessageId
     */
    public void setRootMessageId(String rootMessageId);

    /**
     * 设置会话令牌
     * @param sessionToken
     */
    public void setSessionToken(String sessionToken);

    /**
     * 设置线程组名
     * @param name
     */
    public void setThreadGroupName(String name);

    /**
     * 设置线程id
     * @param threadId
     */
    public void setThreadId(String threadId);

    /**
     * 设置线程名
     * @param id
     */
    public void setThreadName(String id);

    /**
     * TODO
     * @param sample
     */
    public void setSample(boolean sample);

}
