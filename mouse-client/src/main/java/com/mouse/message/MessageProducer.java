/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * <p>用于创建transaction, event或heartbeat的消息工厂</p>
 * <p>通常应用程序代码可以采用如下方式记录消息:
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
 * 或者记录一次Event或Heartbeat:
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
 * @version $Id: MessageProducer.java, v 0.1 2018年5月23日 上午11:47:43 kris Exp $
 */
public interface MessageProducer {

    /**
     * 创建一个消息Id
     * @return
     */
    public String createMessageId();

    /**
     * 检测mouse client在domian是否启动
     * @return 启动返回true，未启动返回false
     */
    public boolean isEnabled();

    /**
     * 记录错误
     * @param cause
     */
    public void logError(Throwable cause);

    /**
     * 记录错误
     * @param message
     * @param cause
     */
    public void logError(String message, Throwable cause);

    /**
     * 记录一次成功状态的Event
     * @param type          类型
     * @param name          名字
     */
    public void logEvent(String type, String name);

    /**
     * 记录一次成功状态的Trace
     * @param type          类型
     * @param name          名字
     */
    public void logTrace(String type, String name);

    /**
     * 记录一次Event
     * @param type           类型
     * @param name           名字
     * @param status         状态，"0"表示成功，其他表示失败
     * @param nameValuePairs 格式如"a=1&b=2&..."的名称值对
     */
    public void logEvent(String type, String name, String status, String nameValuePairs);

    /**
     * 记录一次Trace
     * @param type           类型
     * @param name           名字
     * @param status         状态，"0"表示成功，其他表示失败
     * @param nameValuePairs 格式如"a=1&b=2&..."的名称值对
     */
    public void logTrace(String type, String name, String status, String nameValuePairs);

    /**
     * 记录一次Heartbeat
     * @param type           类型
     * @param name           名字
     * @param status         状态，"0"表示成功，其他表示失败
     * @param nameValuePairs 格式如"a=1&b=2&..."的名称值对
     */
    public void logHeartbeat(String type, String name, String status, String nameValuePairs);

    /**
     * 记录一次Metric
     * @param name           名字
     * @param status         状态，"0"表示成功，其他表示失败
     * @param nameValuePairs 格式如"a=1&b=2&..."的名称值对
     */
    public void logMetric(String name, String status, String nameValuePairs);

    /**
     * 用给定的类型和名称创建一个新的Event
     * @param type           类型
     * @param name           名字
     * @return
     */
    public Event newEvent(String type, String name);

    /**
     * 用给定的类型和名称创建一个新的Trace
     * @param type           类型
     * @param name           名字
     * @return
     */
    public Trace newTrace(String type, String name);

    /**
     * 用给定的类型和名称创建一个新的Heartbeat
     * @param type           类型
     * @param name           名字
     * @return
     */
    public Heartbeat newHeartbeat(String type, String name);

    /**
     * 用给定的类型和名称创建一个新的Metric
     * @param type           类型
     * @param name           名字
     * @return
     */
    public Metric newMetric(String type, String name);

    /**
     * 用给定的类型和名称创建一个新的Transation
     * @param type           类型
     * @param name           名字
     * @return
     */
    public Transaction newTransaction(String type, String name);

    /**
     * 为子线程创建分支事务
     * @param type           类型
     * @param name           名字
     * @return
     */
    public ForkedTransaction newForkedTransaction(String type, String name);

    /**
     * 为另一个进程或线程创建标记事务。
     * @param type           事务类型
     * @param name           事务名称
     * @param tag            事务标签
     * @return
     */
    public TaggedTransaction newTaggedTransaction(String type, String name, String tag);

}
