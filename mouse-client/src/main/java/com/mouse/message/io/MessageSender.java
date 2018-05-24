/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.io;

import com.mouse.message.spi.MessageTree;

/**
 * 消息发送者
 * @author kris
 * @version $Id: MessageSender.java, v 0.1 2018年5月24日 上午11:46:04 kris Exp $
 */
public interface MessageSender {

    /**
     * 初始化
     */
    public void initialize();

    /**
     * 发送
     * @param tree
     */
    public void send(MessageTree tree);

    /**
     * 停止
     */
    public void shutdown();

}
