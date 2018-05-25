/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

/**
 * ��Ϣͳ��
 * @author kris
 * @version $Id: MessageStatistics.java, v 0.1 2018��5��25�� ����5:00:16 kris Exp $
 */
public interface MessageStatistics {

    /**
     * ��ȡ�ֽ���
     * @return
     */
    public long getBytes();

    /**
     * ��ȡ���
     * @return
     */
    public long getOverflowed();

    /**
     * TODO
     * @return
     */
    public long getProdeced();

    /**
     * TODO
     */
    public void onBytes();

    /**
     * ��ȡ���
     * @param tree
     */
    public void onOverflowed(MessageTree tree);

}
