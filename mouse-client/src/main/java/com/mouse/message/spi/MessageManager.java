/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi;

import com.mouse.message.Message;
import com.mouse.message.Transaction;

/**
 * ���ڹ���CAT��Ϣ����Ϣ������
 * <p>
 * ע�⣺�˷��������ڲ�ʹ�á� Ӧ�ó��򿪷���Ա��Ӧֱ�ӵ��ô˷�����
 * @author kris
 * @version $Id: MessageManager.java, v 0.1 2018��5��23�� ����10:29:28 kris Exp $
 */
public interface MessageManager {

    /**
     * ������Ϣ
     * @param message
     */
    public void add(Message message);

    /**
     * ���������ʱ�������������Ǹ�������Ƕ������ ���ǣ�������Ǹ������������첽ˢ�µ����CAT��������
     * <p>
     * @param transaction
     */
    public void end(Transaction transaction);

    /**
     * ��ȡ��ǰ�̵߳�peek����
     * @return ��ǰ�̵߳�peek�������û�������򷵻�null��
     */
    public Transaction getPeekTransaction();

    /**
     * ��ȡ�̱߳�����Ϣ��Ϣ
     * @return ��Ϣ����null��ʾ��ǰ�߳�δ��ȷ���á�
     */
    public MessageTree getThreadLocalMessageTree();

    /** 
     * ����߳��������Ƿ����á�
     * @return ����߳������������ã��򷵻�true�����򷵻�false
     */
    public boolean hasContext();

    /**
     * ��鵱ǰ�����ļ�¼�����û��ǽ��á�
     * @return ������õ�ǰ�����ģ��򷵻�true
     */
    public boolean isMessageEnabled();

    /**
     * ���CAT��¼�����û��ǽ��á�
     * @return ���CAT�����ã��򷵻�true
     */
    public boolean isCatEnabled();

    /**
     * ���CAT����ģʽ�����û��ǽ��á�
     * @return ���CAT��������ģʽ���򷵻�true
     */
    public boolean isTraceMode();

    /**
     * Ϊ����ǰ�̻߳������ͷ��̱߳��ض����е���Դ��
     */
    public void reset();

    /**
     * ����CAT����ģʽ��
     * @param traceMode
     */
    public void setTradeMode(boolean traceMode);

    /**
     * Ϊ��ǰ�̻߳��������̱߳��ض���
     */
    public void setup();

    /**
     * ����������ʱ�������������Ǹ�������Ƕ������
     * @param transaction
     * @param forked
     */
    public void start(Transaction transaction, boolean forked);

    /**
     * ����ǰ��Ϣ���󶨵�ʹ��tag��ǵ������ϡ�
     * @param tag    �������ı������
     * @param title  ��־��ͼ����ʾ�ı���
     */
    public void bind(String tag, String title);

    /**
     * ��ȡdomain
     * @return
     */
    public String getDomain();

}
