/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * 标记事务
 * <p>
 * 标记事务是当前事务的子事务而不是另一个平行事务。
 * 其他线程可以通过tag（标记）找到标记事务并进行绑定，标记事务创建新event记录绑定信息，
 * 每一次绑定对应一个event，event里记录绑定线程的事务信息；同时绑定线程会也通过parentMessageId维护与主事务消息之间的关系。 
 * </p>
 * @author kris
 * @version $Id: TaggedTransaction.java, v 0.1 2018年5月23日 下午10:17:34 kris Exp $
 */
public interface TaggedTransaction extends Transaction {

    /**
     * 绑定事务
     * @param tag
     * @param childMessageId
     * @param title
     */
    public void bind(String tag, String childMessageId, String title);

    /**
     * 获取父消息id
     * @return
     */
    public String getParentMessageId();

    /**
     * 获取根消息id
     * @return
     */
    public String getRootMessageId();

    /**
     * 获取标记
     * @return
     */
    public String getTag();

    /**
     * 启动
     */
    public void start();

}
