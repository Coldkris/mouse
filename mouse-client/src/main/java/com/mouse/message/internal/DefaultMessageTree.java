/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.nio.charset.Charset;

import com.mouse.message.Message;
import com.mouse.message.spi.MessageTree;
import com.mouse.message.spi.codec.PlainTextMessageCodec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * 消息树默认实现
 * @author kris
 * @version $Id: DefaultMessageTree.java, v 0.1 2018年6月1日 下午4:57:04 kris Exp $
 */
public class DefaultMessageTree implements MessageTree {

    private ByteBuf buf;

    private String  domain;

    private String  hostName;

    private String  ipAddress;

    private Message message;

    private String  messageId;

    private String  parentMessageId;

    private String  rootMessageId;

    private String  sessionToken;

    private String  threadGroupName;

    private String  threadId;

    private String  threadName;

    private boolean sample = true;

    @Override
    public MessageTree copy() {

        MessageTree tree = new DefaultMessageTree();

        tree.setDomain(domain);
        tree.setHostName(hostName);
        tree.setIpAddress(ipAddress);
        tree.setMessageId(messageId);
        tree.setParentMessageId(parentMessageId);
        tree.setRootMessageId(rootMessageId);
        tree.setSessionToken(sessionToken);
        tree.setThreadGroupName(threadGroupName);
        tree.setThreadId(threadId);
        tree.setThreadName(threadName);
        tree.setMessage(message);
        tree.setSample(sample);

        return tree;
    }

    public ByteBuf getBuffer() {
        return buf;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getParentMessageId() {
        return parentMessageId;
    }

    @Override
    public String getRootMessageId() {
        return rootMessageId;
    }

    @Override
    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public String getThreadGroupName() {
        return threadGroupName;
    }

    @Override
    public String getThreadId() {
        return threadId;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    @Override
    public boolean isSample() {
        return sample;
    }

    public void setBuffer(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public void setMessageId(String messageId) {
        if (messageId != null && messageId.length() > 0) {
            this.messageId = messageId;
        }

    }

    @Override
    public void setParentMessageId(String parentMessageId) {
        if (parentMessageId != null && parentMessageId.length() > 0) {
            this.parentMessageId = parentMessageId;
        }

    }

    @Override
    public void setRootMessageId(String rootMessageId) {
        if (rootMessageId != null && rootMessageId.length() > 0) {
            this.rootMessageId = rootMessageId;
        }
    }

    @Override
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public void setThreadGroupName(String threadGroupName) {
        this.threadGroupName = threadGroupName;
    }

    @Override
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    @Override
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void setSample(boolean sample) {
        this.sample = sample;
    }

    @Override
    public String toString() {
        PlainTextMessageCodec codec = new PlainTextMessageCodec();
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

        codec.encode(this, buf);
        buf.readInt(); // 去除长度
        codec.reset();
        return buf.toString(Charset.forName("utf-8"));
    }
}
