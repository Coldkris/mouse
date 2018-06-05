/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import com.mouse.message.Trace;
import com.mouse.message.spi.MessageManager;

/**
 * 默认Trace实现类
 * @author kris
 * @version $Id: DefaultTrace.java, v 0.1 2018年6月5日 下午2:14:17 kris Exp $
 */
public class DefaultTrace extends AbstractMessage implements Trace {

    private MessageManager manager;

    public DefaultTrace(String type, String name) {
        super(type, name);
    }

    public DefaultTrace(String type, String name, MessageManager manager) {
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
