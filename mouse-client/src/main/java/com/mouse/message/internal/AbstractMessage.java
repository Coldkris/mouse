/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.nio.charset.Charset;

import com.mouse.message.Message;
import com.mouse.message.spi.codec.PlainTextMessageCodec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * 抽象消息类
 * @author kris
 * @version $Id: AbstractMessage.java, v 0.1 2018年6月5日 上午9:03:53 kris Exp $
 */
public abstract class AbstractMessage implements Message {

    private String       type;

    private String       name;

    private String       status = "unset";

    private long         timestampInMillis;

    private CharSequence data;

    private boolean      completed;

    public AbstractMessage(String type, String name) {
        this.type = String.valueOf(type);
        this.name = String.valueOf(name);
        timestampInMillis = MilliSecondTimer.currentTimeMillis();
    }

    @Override
    public void addData(String keyValuePairs) {
        if (data == null) {
            data = keyValuePairs;
        } else if (data instanceof StringBuilder) {
            ((StringBuilder) data).append('&').append(keyValuePairs);
        } else {
            StringBuilder sb = new StringBuilder(data.length() + keyValuePairs.length() + 16);

            sb.append(data).append('&');
            sb.append(keyValuePairs);
            data = sb;
        }

    }

    @Override
    public void addData(String key, Object value) {
        if (data instanceof StringBuilder) {
            ((StringBuilder) data).append('&').append(key).append('=').append(value);
        } else {
            String str = String.valueOf(value);
            int old = data == null ? 0 : data.length();
            StringBuilder sb = new StringBuilder(old + key.length() + str.length() + 16);

            if (data != null) {
                sb.append(data).append('&');
            }

            sb.append(key).append('=').append(str);
            data = sb;
        }
    }

    @Override
    public CharSequence getData() {
        if (data == null) {
            return "";
        } else {
            return data;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public long getTimestamp() {
        return timestampInMillis;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean isSuccess() {
        return Message.SUCCESS.equals(status);
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void setStatus(Throwable e) {
        this.status = e.getClass().getName();
    }

    public void setTimestamp(long timestamp) {
        this.timestampInMillis = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        PlainTextMessageCodec codec = new PlainTextMessageCodec();
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

        codec.encodeMessage(this, buf);
        codec.reset();
        return buf.toString(Charset.forName("utf-8"));
    }
}
