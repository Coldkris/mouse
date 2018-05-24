/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * 分支事务
 * <p>
 * 分支事务是从当前事务里派生出的异步事务，当前事务不需要等待其完成。
 * 分支事务创建新的消息树，和主事务之间是平行的关系。
 * 主事务通过新建event记录与分支事务之间的关系（一种软连接关系），分支事务通过设置消息树的parentMessageId维护与主事务消息之间的关系。
 * </p>
 * @author kris
 * @version $Id: ForkedTransaction.java, v 0.1 2018年5月23日 下午10:11:42 kris Exp $
 */
public interface ForkedTransaction extends Transaction {

    /**
     * 创建分支
     */
    public void fork();

    /**
     * 获取分支消息id
     * @return
     */
    public String getForkedMessageId();

}
