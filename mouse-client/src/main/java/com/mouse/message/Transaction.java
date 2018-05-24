/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

import java.util.List;

/**
 * <p>
 * <code>Transaction</code>����Ҫʱ����ɲ�����ʧ�ܵ��κ���Ȥ�Ĺ�����Ԫ��
 * </p>
 * <p>
 * �����ϣ���Խ�߽���������ݷ��ʶ���Ҫ��¼Ϊ<code>Transaction</code>����Ϊ�����ܻ�ʧ�ܲ��Һܺ�ʱ��
 * ���磬URL���󣬴���IO��JDBC��ѯ��������ѯ��HTTP���󣬵�����API���õȡ�
 * </p>
 * <p>
 * ��ʱAȥ��������һ���Ŷӿ�����Bʱ������A��B������һ��û���κ������߽磬Ϊ���������Ȩ����A����Bʱ���ܻ�ʹ��<code>Transaction</code>
 * </p>
 * <p>
 * �󲿷�<code>Transaction</code>��Ӧ��¼�ڶ�Ӧ�ó���͸���Ļ����ṹ�����С�
 * </p>
 * <p>
 * ���е�CAT��Ϣ���ᱻ����Ϊ��Ϣ������ʽ�������͵�����Թ���һ�������ͼ�ء�
 * ֻ��<code>Transaction</code>������Ϊ���ڵ㣬������Ϣ��ΪҶ�ӽڵ㡣
 * û��Ƕ��������Ϣ��Transaction��ԭ��Transaction��
 * </p>
 * @author kris
 * @version $Id: Transaction.java, v 0.1 2018��5��23�� ����9:26:11 kris Exp $
 */
public interface Transaction extends Message {

    /**
     * ��ǰtransaction���һ��Ƕ�׵�����Ϣ
     * @param message
     * @return
     */
    public Transaction addChild(Message message);

    /**
     * ��ȡ��ǰtransaction����������Ϣ
     * <p>
     * ͨ����<code>Transaction</code>��Ƕ������<code>Transaction</code>��<code>Event</code>��<code>Heartbeat</code>��
     * ��<code>Event</code>��<code>Heartbeat</code>����Ƕ��������Ϣ��
     * </p>
     * @return ���е�����Ϣ�����û��Ƕ�׵�����Ϣ����Ϊ�ա�
     */
    public List<Message> getChildren();

    /**
     * ��ȡtransaction�ӽ�������ɺķѵ�ʱ�䣬��λ��΢�롣
     * @return ��΢��Ϊ��λ�ĳ���ʱ��
     */
    public long getDurationInMicros();

    /**
     * ��ȡtransaction�ӽ�������ɺķѵ�ʱ�䣬��λ�Ǻ��롣
     * @return �Ժ���Ϊ��λ�ĳ���ʱ��
     */
    public long getDurationInMillis();

    /**
     * �ж��Ƿ�������Ϣ��ԭ��transactionû���κ�����Ϣ
     * @return ������Ϣ����true�����򷵻�false
     */
    public boolean hasChildren();

    /**
     * �ж�transaction�Ƕ����Ļ��Ǹ�����һ��transaction
     * @return ����Ǹ�transaction����true
     */
    public boolean isStandalone();

}
