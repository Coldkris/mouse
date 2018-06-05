/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import com.mouse.message.Event;
import com.mouse.message.spi.MessageManager;

/**
 * Ĭ��Eventʵ����
 * @author kris
 * @version $Id: DefaultEvent.java, v 0.1 2018��6��5�� ����10:07:27 kris Exp $
 */
public class DefaultEvent extends AbstractMessage implements Event {

    private MessageManager manager;

    public DefaultEvent(String type, String name) {
        super(type, name);
    }

    public DefaultEvent(String type, String name, MessageManager manager) {
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
