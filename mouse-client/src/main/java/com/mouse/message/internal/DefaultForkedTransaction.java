/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import com.mouse.Mouse;
import com.mouse.message.ForkedTransaction;
import com.mouse.message.spi.MessageManager;
import com.mouse.message.spi.MessageTree;

/**
 * Ĭ�Ϸ�֧����ʵ��
 * @author kris
 * @version $Id: DefaultForkedTransaction.java, v 0.1 2018��6��14�� ����11:14:24 kris Exp $
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

            // �ʼ����linkAsRunAway()�����븸����͵�ǰ���������Ա㽫������Ҫ�����߼����߳�ͬ��
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
            // �ø��̵߳�tree.messageId���ǵ�ǰ��֧�����forkedMessageId��
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
