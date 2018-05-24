/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * �������
 * <p>
 * ��������ǵ�ǰ������������������һ��ƽ������
 * �����߳̿���ͨ��tag����ǣ��ҵ�������񲢽��а󶨣�������񴴽���event��¼����Ϣ��
 * ÿһ�ΰ󶨶�Ӧһ��event��event���¼���̵߳�������Ϣ��ͬʱ���̻߳�Ҳͨ��parentMessageIdά������������Ϣ֮��Ĺ�ϵ�� 
 * </p>
 * @author kris
 * @version $Id: TaggedTransaction.java, v 0.1 2018��5��23�� ����10:17:34 kris Exp $
 */
public interface TaggedTransaction extends Transaction {

    /**
     * ������
     * @param tag
     * @param childMessageId
     * @param title
     */
    public void bind(String tag, String childMessageId, String title);

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
     * ��ȡ���
     * @return
     */
    public String getTag();

    /**
     * ����
     */
    public void start();

}
