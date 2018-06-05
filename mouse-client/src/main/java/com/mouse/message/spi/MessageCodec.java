/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

import io.netty.buffer.ByteBuf;

/**
 * ��Ϣ�������
 * @author kris
 * @version $Id: MessageCodec.java, v 0.1 2018��6��4�� ����9:01:40 kris Exp $
 */
public interface MessageCodec {

    /**
     * ����
     * @param buf
     * @return
     */
    public MessageTree decode(ByteBuf buf);

    /**
     * ����
     * @param buf
     * @param tree
     */
    public void decode(ByteBuf buf, MessageTree tree);

    /**
     * ����
     * @param tree
     * @param buf
     */
    public void encode(MessageTree tree, ByteBuf buf);

}
