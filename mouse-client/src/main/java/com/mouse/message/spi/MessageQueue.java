/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

/**
 * ��Ϣ����
 * @author kris
 * @version $Id: MessageQueue.java, v 0.1 2018��6��14�� ����5:40:31 kris Exp $
 */
public interface MessageQueue {

    public boolean offer(MessageTree tree);

    public boolean offer(MessageTree tree, double sampleRatio);

    public MessageTree peek();

    public MessageTree poll();

    public int size();
}
