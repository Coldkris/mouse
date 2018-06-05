/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import com.mouse.message.Metric;
import com.mouse.message.spi.MessageManager;

/**
 * 默认Metric实现类
 * @author kris
 * @version $Id: DefaultMetric.java, v 0.1 2018年6月5日 上午11:27:15 kris Exp $
 */
public class DefaultMetric extends AbstractMessage implements Metric {

    private MessageManager manager;

    public DefaultMetric(String type, String name) {
        super(type, name);
    }

    public DefaultMetric(String type, String name, MessageManager manager) {
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
