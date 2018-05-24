/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * <p>消息表示应用程序在运行时收集的数据，它们将会被异步地发送到后端系统做进一步处理</p>
 * <p>Message是<code>Event</code>, <code>Heartbeat</code>和Transaction的父接口
 * @see Event, Heartbeat, Transaction
 * @author kris
 * @version $Id: Message.java, v 0.1 2018年5月23日 下午4:01:56 kris Exp $
 */
public interface Message {

    public static final String SUCCESS = "0";

    /**
     * 向消息中添加一个或多个key-value对
     * @param keyValuePairs 类似'a=1&b=2&...'
     */
    public void addData(String keyValuePairs);

    /**
     * 向消息中添加一个key-value对
     * @param key
     * @param value
     */
    public void addData(String key, Object value);

    /**
     * 完成消息构建
     */
    public void complete();

    /**
     * 获取key-value对数据
     * @return
     */
    public Object getData();

    /**
     * 获取消息名
     * @return
     */
    public String getName();

    /**
     * 获取消息状态
     * @return  消息状态，"0"表示成功，其他表示错误代码
     */
    public String getStatus();

    /**
     * 消息创建的时间戳
     * @return
     */
    public long getTimestamp();

    /**
     * 消息类型
     * <p>
     * 典型的消息类型有：
     * <ul>
     * <li>URL:映射到一个action方法</li>
     * <li>Service:映射到一个服务调用方法</li>
     * <li>Search:映射到一个搜索调用方法</li>
     * <li>SQL:映射到一个SQL语句</li>
     * <li>Cache:映射到一个缓存访问</li>
     * <li>Error:映射到java.lang.Throwable (java.lang.Exception and java.lang.Error)</li>
     * </ul>
     * </p>
     * @return  消息类型
     */
    public String getType();

    /**
     * 判断complete()方法是否调用
     * @return  true表示complete()被调用，false表示未调用
     */
    public boolean isCompleted();

    /**
     * 判断消息状态是否成功
     * @return
     */
    public boolean isSuccess();

    /**
     * 设置消息状态
     * @param status 消息状态，"0"表示成功，其他表示错误代码
     */
    public void setStatus(String status);

    /**
     * 用异常类名设置消息状态
     * @param e
     */
    public void setStatuc(Throwable e);

}
