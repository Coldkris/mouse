/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

import java.util.List;

/**
 * <p>
 * <code>Transaction</code>是需要时间完成并可能失败的任何有趣的工作单元。
 * </p>
 * <p>
 * 基本上，跨越边界的所有数据访问都需要记录为<code>Transaction</code>，因为它可能会失败并且很耗时。
 * 例如，URL请求，磁盘IO，JDBC查询，搜索查询，HTTP请求，第三方API调用等。
 * </p>
 * <p>
 * 有时A去调用由另一个团队开发的B时，尽管A和B部署在一起没有任何无力边界，为了清除所有权，当A调用B时可能会使用<code>Transaction</code>
 * </p>
 * <p>
 * 大部分<code>Transaction</code>都应记录在对应用程序透明的基础结构或框架中。
 * </p>
 * <p>
 * 所有的CAT消息都会被构建为消息树的形式并被发送到后端以供进一步分析和监控。
 * 只有<code>Transaction</code>可以作为树节点，其他消息作为叶子节点。
 * 没有嵌套其他消息的Transaction是原子Transaction。
 * </p>
 * @author kris
 * @version $Id: Transaction.java, v 0.1 2018年5月23日 下午9:26:11 kris Exp $
 */
public interface Transaction extends Message {

    /**
     * 向当前transaction添加一个嵌套的子消息
     * @param message
     * @return
     */
    public Transaction addChild(Message message);

    /**
     * 获取当前transaction的所有子消息
     * <p>
     * 通常，<code>Transaction</code>可嵌套其他<code>Transaction</code>，<code>Event</code>和<code>Heartbeat</code>，
     * 而<code>Event</code>或<code>Heartbeat</code>不能嵌套其他消息。
     * </p>
     * @return 所有的子消息，如果没有嵌套的子消息，则为空。
     */
    public List<Message> getChildren();

    /**
     * 获取transaction从建立到完成耗费的时间，单位是微秒。
     * @return 以微秒为单位的持续时间
     */
    public long getDurationInMicros();

    /**
     * 获取transaction从建立到完成耗费的时间，单位是毫秒。
     * @return 以毫秒为单位的持续时间
     */
    public long getDurationInMillis();

    /**
     * 判断是否有子消息。原子transaction没有任何子消息
     * @return 有子消息返回true，否则返回false
     */
    public boolean hasChildren();

    /**
     * 判断transaction是独立的还是附属另一个transaction
     * @return 如果是根transaction返回true
     */
    public boolean isStandalone();

}
