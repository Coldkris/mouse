/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * ��֧����
 * <p>
 * ��֧�����Ǵӵ�ǰ���������������첽���񣬵�ǰ������Ҫ�ȴ�����ɡ�
 * ��֧���񴴽��µ���Ϣ������������֮����ƽ�еĹ�ϵ��
 * ������ͨ���½�event��¼���֧����֮��Ĺ�ϵ��һ�������ӹ�ϵ������֧����ͨ��������Ϣ����parentMessageIdά������������Ϣ֮��Ĺ�ϵ��
 * </p>
 * @author kris
 * @version $Id: ForkedTransaction.java, v 0.1 2018��5��23�� ����10:11:42 kris Exp $
 */
public interface ForkedTransaction extends Transaction {

    /**
     * ������֧
     */
    public void fork();

    /**
     * ��ȡ��֧��Ϣid
     * @return
     */
    public String getForkedMessageId();

}
