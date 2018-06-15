/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.util.Collections;
import java.util.List;

import com.mouse.message.Event;
import com.mouse.message.ForkedTransaction;
import com.mouse.message.Heartbeat;
import com.mouse.message.Message;
import com.mouse.message.Metric;
import com.mouse.message.TaggedTransaction;
import com.mouse.message.Trace;
import com.mouse.message.Transaction;

/**
 * 
 * @author kris
 * @version $Id: NullMessage.java, v 0.1 2018年6月14日 下午4:59:23 kris Exp $
 */
public enum NullMessage implements Transaction, Event, Metric, Trace, Heartbeat, ForkedTransaction, TaggedTransaction {
                                                                                                                       TRANSACTION,

                                                                                                                       EVENT,

                                                                                                                       METRIC,

                                                                                                                       TRACE,

                                                                                                                       HEARTBEAT;

    @Override
    public void addData(String keyValuePairs) {

    }

    @Override
    public void addData(String key, Object value) {

    }

    @Override
    public void complete() {

    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTimestamp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public void setStatus(String status) {

    }

    @Override
    public void setStatus(Throwable e) {

    }

    @Override
    public void bind(String tag, String childMessageId, String title) {

    }

    @Override
    public String getParentMessageId() {
        return null;
    }

    @Override
    public String getRootMessageId() {
        return null;
    }

    @Override
    public String getTag() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() {

    }

    @Override
    public void fork() {

    }

    @Override
    public String getForkedMessageId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Transaction addChild(Message message) {
        return this;
    }

    @Override
    public List<Message> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public long getDurationInMicros() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getDurationInMillis() {
        return 0;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean isStandalone() {
        return true;
    }

}
