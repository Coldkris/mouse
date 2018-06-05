/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

import io.netty.buffer.ByteBuf;

/**
 * 消息编解码器
 * @author kris
 * @version $Id: MessageCodec.java, v 0.1 2018年6月4日 上午9:01:40 kris Exp $
 */
public interface MessageCodec {

    /**
     * 解码
     * @param buf
     * @return
     */
    public MessageTree decode(ByteBuf buf);

    /**
     * 解码
     * @param buf
     * @param tree
     */
    public void decode(ByteBuf buf, MessageTree tree);

    /**
     * 编码
     * @param tree
     * @param buf
     */
    public void encode(MessageTree tree, ByteBuf buf);

}
