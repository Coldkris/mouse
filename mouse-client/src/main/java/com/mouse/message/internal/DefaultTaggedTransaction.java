/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import com.mouse.message.Message;
import com.mouse.message.TaggedTransaction;
import com.mouse.message.spi.MessageManager;
import com.mouse.message.spi.MessageTree;

/**
 * 默认标记事务实现
 * @author kris
 * @version $Id: DefaultTaggedTransaction.java, v 0.1 2018年6月14日 上午11:34:11 kris Exp $
 */
public class DefaultTaggedTransaction extends DefaultTransaction implements TaggedTransaction {

    private String rootMessageId;

    private String parentMessageId;

    private String tag;

    public DefaultTaggedTransaction(String type, String name, String tag, MessageManager manager) {
        super(type, name, manager);

        this.tag = tag;

        setStandalone(false);

        MessageTree tree = manager.getThreadLocalMessageTree();

        if (tree != null) {
            rootMessageId = tree.getRootMessageId();
            parentMessageId = tree.getMessageId();
        }
    }

    @Override
    public void bind(String tag, String childMessageId, String title) {
        DefaultEvent event = new DefaultEvent("RemoteCall", "Tagged");

        if (title == null) {
            title = getType() + ":" + getName();
        }

        event.addData(childMessageId, title);
        event.setTimestamp(getTimestamp());
        event.setStatus(Message.SUCCESS);
        event.setCompleted(true);

        addChild(event);
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
    public String getTag() {
        return tag;
    }

    @Override
    public void start() {
        MessageTree tree = getManager().getThreadLocalMessageTree();

        if (tree != null && tree.getRootMessageId() == null) {
            tree.setParentMessageId(parentMessageId);
            tree.setRootMessageId(rootMessageId);
        }
    }

}
