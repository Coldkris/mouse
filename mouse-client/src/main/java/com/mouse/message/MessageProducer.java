/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * <p>���ڴ���transaction, event��heartbeat����Ϣ����</p>
 * <p>ͨ��Ӧ�ó��������Բ������·�ʽ��¼��Ϣ:
 * <ul>
 * <li>Event
 * <pre>
 * public class MyClass { 
 *    public static MessageFactory MOUSE = Mouse.getFactory();
 * 
 *    public void bizMethod() { 
 *       Event event = MOUSE.newEvent("Review", "New");
 * 
 *       event.addData("id", 12345); 
 *       event.addData("user", "john");
 *       ...
 *       event.setStatus("0"); 
 *       event.complete(); 
 *    }
 *    ...
 * }
 * </pre>
 * </li>
 * 
 * <li>Heartbeat
 * <pre>
 * public class MyClass { 
 *    public static MessageFactory MOUSE = Mouse.getFactory();
 * 
 *    public void bizMethod() { 
 *       Heartbeat event = MOUSE.newHeartbeat("System", "Status");
 * 
 *       event.addData("ip", "192.168.10.111");
 *       event.addData("host", "host-1");
 *       event.addData("load", "2.1");
 *       event.addData("cpu", "0.12,0.10");
 *       event.addData("memory.total", "2G");
 *       event.addData("memory.free", "456M");
 *       event.setStatus("0");
 *       event.complete();
 *    }
 *    ...
 * }
 * </pre>
 * </li>
 * 
 * <li>Transaction
 * <pre>
 * public class MyClass { 
 *    public static MessageFactory MOUSE = Mouse.getFactory();
 * 
 *    public void bizMethod() { 
 *       Transaction t = MOUSE.newTransaction("URL", "MyPage");
 * 
 *       try {
 *          // do your business here
 *          t.addData("k1", "v1");
 *          t.addData("k2", "v2");
 *          t.addData("k3", "v3");
 *          Thread.sleep(30);
 * 
 *          t.setStatus("0");
 *       } catch (Exception e) {
 *          t.setStatus(e);
 *       } finally {
 *          t.complete();
 *       }
 *    }
 *    ...
 * }
 * </pre>
 * </li>
 * </ul>
 * 
 * ���߼�¼һ��Event��Heartbeat:
 * <ul>
 * <li>Event
 * <pre>
 * public class MyClass { 
 *    public static MessageFactory MOUSE = Mouse.getFactory();
 * 
 *    public void bizMethod() { 
 *       MOUSE.logEvent("Review", "New", "0", "id=12345&user=john");
 *    }
 *    ...
 * }
 * </pre>
 * </li>
 * 
 * <li>Heartbeat
 * <pre>
 * public class MyClass { 
 *    public static MessageFactory MOUSE = Mouse.getFactory();
 * 
 *    public void bizMethod() { 
 *       MOUSE.logHeartbeat("System", "Status", "0", "ip=192.168.10.111&host=host-1&load=2.1&cpu=0.12,0.10&memory.total=2G&memory.free=456M");
 *    }
 *    ...
 * }
 * </pre>
 * </li>
 * </ul>
 * </p>
 * @author kris
 * @version $Id: MessageProducer.java, v 0.1 2018��5��23�� ����11:47:43 kris Exp $
 */
public interface MessageProducer {

    /**
     * ����һ����ϢId
     * @return
     */
    public String createMessageId();

    /**
     * ���mouse client��domian�Ƿ�����
     * @return ��������true��δ��������false
     */
    public boolean isEnabled();

    /**
     * ��¼����
     * @param cause
     */
    public void logError(Throwable cause);

    /**
     * ��¼����
     * @param message
     * @param cause
     */
    public void logError(String message, Throwable cause);

    /**
     * ��¼һ�γɹ�״̬��Event
     * @param type          ����
     * @param name          ����
     */
    public void logEvent(String type, String name);

    /**
     * ��¼һ�γɹ�״̬��Trace
     * @param type          ����
     * @param name          ����
     */
    public void logTrace(String type, String name);

    /**
     * ��¼һ��Event
     * @param type           ����
     * @param name           ����
     * @param status         ״̬��"0"��ʾ�ɹ���������ʾʧ��
     * @param nameValuePairs ��ʽ��"a=1&b=2&..."������ֵ��
     */
    public void logEvent(String type, String name, String status, String nameValuePairs);

    /**
     * ��¼һ��Trace
     * @param type           ����
     * @param name           ����
     * @param status         ״̬��"0"��ʾ�ɹ���������ʾʧ��
     * @param nameValuePairs ��ʽ��"a=1&b=2&..."������ֵ��
     */
    public void logTrace(String type, String name, String status, String nameValuePairs);

    /**
     * ��¼һ��Heartbeat
     * @param type           ����
     * @param name           ����
     * @param status         ״̬��"0"��ʾ�ɹ���������ʾʧ��
     * @param nameValuePairs ��ʽ��"a=1&b=2&..."������ֵ��
     */
    public void logHeartbeat(String type, String name, String status, String nameValuePairs);

    /**
     * ��¼һ��Metric
     * @param name           ����
     * @param status         ״̬��"0"��ʾ�ɹ���������ʾʧ��
     * @param nameValuePairs ��ʽ��"a=1&b=2&..."������ֵ��
     */
    public void logMetric(String name, String status, String nameValuePairs);

    /**
     * �ø��������ͺ����ƴ���һ���µ�Event
     * @param type           ����
     * @param name           ����
     * @return
     */
    public Event newEvent(String type, String name);

    /**
     * �ø��������ͺ����ƴ���һ���µ�Trace
     * @param type           ����
     * @param name           ����
     * @return
     */
    public Trace newTrace(String type, String name);

    /**
     * �ø��������ͺ����ƴ���һ���µ�Heartbeat
     * @param type           ����
     * @param name           ����
     * @return
     */
    public Heartbeat newHeartbeat(String type, String name);

    /**
     * �ø��������ͺ����ƴ���һ���µ�Metric
     * @param type           ����
     * @param name           ����
     * @return
     */
    public Metric newMetric(String type, String name);

    /**
     * �ø��������ͺ����ƴ���һ���µ�Transation
     * @param type           ����
     * @param name           ����
     * @return
     */
    public Transaction newTransaction(String type, String name);

    /**
     * Ϊ���̴߳�����֧����
     * @param type           ����
     * @param name           ����
     * @return
     */
    public ForkedTransaction newForkedTransaction(String type, String name);

    /**
     * Ϊ��һ�����̻��̴߳����������
     * @param type           ��������
     * @param name           ��������
     * @param tag            �����ǩ
     * @return
     */
    public TaggedTransaction newTaggedTransaction(String type, String name, String tag);

}
