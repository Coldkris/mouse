/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.io;

import com.mouse.message.spi.MessageTree;

/**
 * ��Ϣ������
 * @author kris
 * @version $Id: MessageSender.java, v 0.1 2018��5��24�� ����11:46:04 kris Exp $
 */
public interface MessageSender {

    /**
     * ��ʼ��
     */
    public void initialize();

    /**
     * ����
     * @param tree
     */
    public void send(MessageTree tree);

    /**
     * ֹͣ
     */
    public void shutdown();

}
