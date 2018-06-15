/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import com.mouse.Mouse;
import com.mouse.message.ForkedTransaction;
import com.mouse.message.spi.MessageManager;
import com.mouse.message.spi.MessageTree;

/**
 * 默认分支事务实现
 * @author kris
 * @version $Id: DefaultForkedTransaction.java, v 0.1 2018年6月14日 上午11:14:24 kris Exp $
 */
public class DefaultForkedTransaction extends DefaultTransaction implements ForkedTransaction {

    private String rootMessageId;

    private String parentMessageId;

    private String forkedMessageId;

    public DefaultForkedTransaction(String type, String name, MessageManager manager) {
        super(type, name, manager);

        setStandalone(false);

        MessageTree tree = manager.getThreadLocalMessageTree();

        if (tree != null) {
            rootMessageId = tree.getRootMessageId();
            parentMessageId = tree.getMessageId();

            // 最开始调用linkAsRunAway()来分离父事务和当前派生事务，以便将来不需要在两者间做线程同步
            forkedMessageId = Mouse.createMessageId();
        }
    }

    @Override
    public void fork() {
        MessageManager manager = getManager();

        manager.setup();
        manager.start(this, false);

        MessageTree tree = manager.getThreadLocalMessageTree();

        if (tree != null) {
            // 用父线程的tree.messageId覆盖当前分支事务的forkedMessageId。
            tree.setMessageId(forkedMessageId);
            tree.setRootMessageId(rootMessageId == null ? parentMessageId : rootMessageId);
            tree.setParentMessageId(parentMessageId);
        }

    }

    @Override
    public String getForkedMessageId() {
        return forkedMessageId;
    }

}
