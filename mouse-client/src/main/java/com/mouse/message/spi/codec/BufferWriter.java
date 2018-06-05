/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi.codec;

import io.netty.buffer.ByteBuf;

/**
 * 字符缓冲输出流
 * @author kris
 * @version $Id: BufferWriter.java, v 0.1 2018年6月4日 上午9:15:43 kris Exp $
 */
public interface BufferWriter {

    /**
     * 写入
     * @param buf
     * @param data
     * @return
     */
    public int writeTo(ByteBuf buf, byte[] data);

}
