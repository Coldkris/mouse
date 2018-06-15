/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

/**
 * 消息队列
 * @author kris
 * @version $Id: MessageQueue.java, v 0.1 2018年6月14日 下午5:40:31 kris Exp $
 */
public interface MessageQueue {

    public boolean offer(MessageTree tree);

    public boolean offer(MessageTree tree, double sampleRatio);

    public MessageTree peek();

    public MessageTree poll();

    public int size();
}
