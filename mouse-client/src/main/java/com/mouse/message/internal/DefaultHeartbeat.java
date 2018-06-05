/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import com.mouse.message.Heartbeat;
import com.mouse.message.spi.MessageManager;

/**
 * Ĭ������ʵ����
 * @author kris
 * @version $Id: DefaultHeartbeat.java, v 0.1 2018��6��5�� ����3:15:44 kris Exp $
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
