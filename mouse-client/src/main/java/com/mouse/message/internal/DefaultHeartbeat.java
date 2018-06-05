/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import com.mouse.message.Heartbeat;
import com.mouse.message.spi.MessageManager;

/**
 * 默认心跳实现类
 * @author kris
 * @version $Id: DefaultHeartbeat.java, v 0.1 2018年6月5日 下午3:15:44 kris Exp $
 */
public class DefaultHeartbeat extends AbstractMessage implements Heartbeat {

    private MessageManager manager;

    public DefaultHeartbeat(String type, String name) {
        super(type, name);
    }

    public DefaultHeartbeat(String type, String name, MessageManager manager) {
        super(type, name);

        this.manager = manager;
    }

    @Override
    public void complete() {
        setCompleted(true);

        if (manager != null) {
            manager.add(this);
        }
    }

}
