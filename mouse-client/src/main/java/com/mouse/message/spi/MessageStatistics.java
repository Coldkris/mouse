/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

/**
 * 消息统计
 * @author kris
 * @version $Id: MessageStatistics.java, v 0.1 2018年5月25日 下午5:00:16 kris Exp $
 */
public interface MessageStatistics {

    /**
     * 获取字节数
     * @return
     */
    public long getBytes();

    /**
     * 获取溢出
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
     * 获取溢出
     * @param tree
     */
    public void onOverflowed(MessageTree tree);

}
