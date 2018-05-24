/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.io;

/**
 * 消息传输管理器
 * @author kris
 * @version $Id: TransportManager.java, v 0.1 2018年5月24日 上午11:43:42 kris Exp $
 */
public interface TransportManager {

    /**
     * 获取消息发送者
     * @return
     */
    public MessageSender getSender();

}
