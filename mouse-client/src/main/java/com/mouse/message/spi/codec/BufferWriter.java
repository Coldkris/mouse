/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi.codec;

import io.netty.buffer.ByteBuf;

/**
 * �ַ����������
 * @author kris
 * @version $Id: BufferWriter.java, v 0.1 2018��6��4�� ����9:15:43 kris Exp $
 */
public interface BufferWriter {

    /**
     * д��
     * @param buf
     * @param data
     * @return
     */
    public int writeTo(ByteBuf buf, byte[] data);

}
