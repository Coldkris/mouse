/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mouse.Mouse;
import com.mouse.message.Message;
import com.mouse.message.Transaction;
import com.mouse.message.spi.MessageManager;

/**
 * 默认Transaction实现
 * @author kris
 * @version $Id: DefaultTransaction.java, v 0.1 2018年6月5日 上午9:52:24 kris Exp $
 */
public class DefaultTransaction extends AbstractMessage implements Transaction {

    private long           durationInMicro = -1; // 必须小于0

    private List<Message>  children;

    private MessageManager manager;

    private boolean        standalone;

    private long           durationStart;

    public DefaultTransaction(String type, String name, MessageManager manager) {
        super(type, name);

        this.manager = manager;
        standalone = true;
        durationStart = System.nanoTime();
    }

    @Override
    public void complete() {
        try {
            if (isCompleted()) {
                // complete()不止一次调用
                DefaultEvent event = new DefaultEvent("mouse", "BadInstrument");

                event.setStatus("Transation已完成");
                event.complete();
                addChild(event);
            } else {
                durationInMicro = (System.nanoTime() - durationStart) / 1000L;

                setCompleted(true);

                if (manager != null) {
                    manager.end(this);
                }
            }
        } catch (Exception e) {
            // 忽略
        }
    }

    @Override
    public DefaultTransaction addChild(Message message) {
        if (children == null) {
            children = new ArrayList<>();
        }

        if (message != null) {
            children.add(message);
        } else {
            Mouse.logError(new Exception("空子消息"));
        }
        return this;
    }

    @Override
    public List<Message> getChildren() {
        if (children == null) {
            return Collections.emptyList();
        }
        return children;
    }

    @Override
    public long getDurationInMicros() {
        if (durationInMicro >= 0) {
            return durationInMicro;
        } else { // 如果没有明确完成
            long duration = 0;
            int len = children == null ? 0 : children.size();

            if (len > 0) {
                Message lastChild = children.get(len - 1);

                if (lastChild instanceof Transaction) {
                    DefaultTransaction trx = (DefaultTransaction) lastChild;

                    duration = (trx.getTimestamp() - getTimestamp()) * 1000L;
                } else {
                    duration = (lastChild.getTimestamp() - getTimestamp()) * 1000L;
                }
            }
            return duration;
        }
    }

    @Override
    public long getDurationInMillis() {
        return getDurationInMicros() / 1000L;
    }

    @Override
    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }

    @Override
    public boolean isStandalone() {
        return standalone;
    }

    protected MessageManager getManager() {
        return manager;
    }

    public void setDurationInMicros(long duration) {
        this.durationInMicro = duration;
    }

    public void setDurationInMillis(long duration) {
        this.durationInMicro = duration * 1000L;
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    public void setDurationStart(long durationStart) {
        this.durationStart = durationStart;
    }

}
