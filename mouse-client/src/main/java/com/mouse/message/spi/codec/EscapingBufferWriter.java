/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi.codec;

import io.netty.buffer.ByteBuf;

/**
 * 转义字符缓冲输出流
 * @author kris
 * @version $Id: EscapingBufferWriter.java, v 0.1 2018年6月4日 上午9:19:27 kris Exp $
 */
public class EscapingBufferWriter implements BufferWriter {

    public static final String ID = "escape";

    @Override
    public int writeTo(ByteBuf buf, byte[] data) {
        int len = data.length;
        int count = len;
        int offset = 0;

        for (int i = 0; i < len; i++) {
            byte b = data[i];

            if (b == '\t' || b == '\r' || b == '\n' || b == '\\') {
                buf.writeBytes(data, offset, i - offset);
                buf.writeByte('\\');

                if (b == '\t') {
                    buf.writeByte('t');
                } else if (b == '\r') {
                    buf.writeByte('r');
                } else if (b == '\n') {
                    buf.writeByte('n');
                } else {
                    buf.writeByte(b);
                }

                count++;
                offset = i + 1;
            }
        }

        if (len > offset) {
            buf.writeBytes(data, offset, len - offset);
        }

        return count;
    }
}
