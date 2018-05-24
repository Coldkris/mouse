/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

import com.mouse.message.Message;

/**
 * ��Ϣ��
 * @author kris
 * @version $Id: MessageTree.java, v 0.1 2018��5��24�� ����9:08:42 kris Exp $
 */
public interface MessageTree extends Cloneable {

    /**
     * ������Ϣ��
     * @return
     */
    public MessageTree copy();

    /**
     * ��ȡDomain
     * @return
     */
    public String getDomain();

    /**
     * ��ȡ������
     * @return
     */
    public String getHostName();

    /**
     * ��ȡIP��ַ
     * @return
     */
    public String getIpAddress();

    /**
     * ��ȡ��Ϣ
     * @return
     */
    public Message getMessage();

    /**
     * ��ȡ��Ϣid
     * @return
     */
    public String getMessageId();

    /**
     * ��ȡ����Ϣid
     * @return
     */
    public String getParentMessageId();

    /**
     * ��ȡ����Ϣid
     * @return
     */
    public String getRootMessageId();

    /**
     * ��ȡ�Ự����
     * @return
     */
    public String getSessionToken();

    /**
     * ��ȡ�߳�����
     * @return
     */
    public String getThreadGroupName();

    /**
     * ��ȡ�߳�id
     * @return
     */
    public String getThreadId();

    /**
     * ��ȡ�߳���
     * @return
     */
    public String getThreadName();

    /**
     * TODO
     * @return
     */
    public boolean isSample();

    /**
     * ����domain
     * @param domain
     */
    public void setDomain(String domain);

    /**
     * ����������
     * @param hostName
     */
    public void setHostName(String hostName);

    /**
     * ����IP��ַ
     * @param ipAddress
     */
    public void setIpAddress(String ipAddress);

    /**
     * ������Ϣ
     * @param message
     */
    public void setMessage(Message message);

    /**
     * ������Ϣid
     * @param messageId
     */
    public void setMessageId(String messageId);

    /**
     * ���ø���Ϣid
     * @param parentMessageId
     */
    public void setParentMessageId(String parentMessageId);

    /**
     * ���ø���Ϣid
     * @param rootMessageId
     */
    public void setRootMessageId(String rootMessageId);

    /**
     * ���ûỰ����
     * @param sessionToken
     */
    public void setSessionToken(String sessionToken);

    /**
     * �����߳�����
     * @param name
     */
    public void setThreadGroupName(String name);

    /**
     * �����߳�id
     * @param threadId
     */
    public void setThreadId(String threadId);

    /**
     * �����߳���
     * @param id
     */
    public void setThreadName(String id);

    /**
     * TODO
     * @param sample
     */
    public void setSample(boolean sample);

}
