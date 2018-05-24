/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * <p>��Ϣ��ʾӦ�ó���������ʱ�ռ������ݣ����ǽ��ᱻ�첽�ط��͵����ϵͳ����һ������</p>
 * <p>Message��<code>Event</code>, <code>Heartbeat</code>��Transaction�ĸ��ӿ�
 * @see Event, Heartbeat, Transaction
 * @author kris
 * @version $Id: Message.java, v 0.1 2018��5��23�� ����4:01:56 kris Exp $
 */
public interface Message {

    public static final String SUCCESS = "0";

    /**
     * ����Ϣ�����һ������key-value��
     * @param keyValuePairs ����'a=1&b=2&...'
     */
    public void addData(String keyValuePairs);

    /**
     * ����Ϣ�����һ��key-value��
     * @param key
     * @param value
     */
    public void addData(String key, Object value);

    /**
     * �����Ϣ����
     */
    public void complete();

    /**
     * ��ȡkey-value������
     * @return
     */
    public Object getData();

    /**
     * ��ȡ��Ϣ��
     * @return
     */
    public String getName();

    /**
     * ��ȡ��Ϣ״̬
     * @return  ��Ϣ״̬��"0"��ʾ�ɹ���������ʾ�������
     */
    public String getStatus();

    /**
     * ��Ϣ������ʱ���
     * @return
     */
    public long getTimestamp();

    /**
     * ��Ϣ����
     * <p>
     * ���͵���Ϣ�����У�
     * <ul>
     * <li>URL:ӳ�䵽һ��action����</li>
     * <li>Service:ӳ�䵽һ��������÷���</li>
     * <li>Search:ӳ�䵽һ���������÷���</li>
     * <li>SQL:ӳ�䵽һ��SQL���</li>
     * <li>Cache:ӳ�䵽һ���������</li>
     * <li>Error:ӳ�䵽java.lang.Throwable (java.lang.Exception and java.lang.Error)</li>
     * </ul>
     * </p>
     * @return  ��Ϣ����
     */
    public String getType();

    /**
     * �ж�complete()�����Ƿ����
     * @return  true��ʾcomplete()�����ã�false��ʾδ����
     */
    public boolean isCompleted();

    /**
     * �ж���Ϣ״̬�Ƿ�ɹ�
     * @return
     */
    public boolean isSuccess();

    /**
     * ������Ϣ״̬
     * @param status ��Ϣ״̬��"0"��ʾ�ɹ���������ʾ�������
     */
    public void setStatus(String status);

    /**
     * ���쳣����������Ϣ״̬
     * @param e
     */
    public void setStatuc(Throwable e);

}
