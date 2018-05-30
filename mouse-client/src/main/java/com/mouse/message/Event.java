/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * <p>
 * <code>Event</code>用于记录在特定时间发生的有趣事件。 如抛出的异常，用户添加的评论，注册的新用户，登录系统的用户等。
 * </p>
 * <p>
 * 但是，如果它可能会失败或者会持续很长事件，比如远程API调用，数据库调用或搜索引擎调用，则应该使用<code>Transaction</code>
 * </p>
 * <p>
 * 所有的Mouse消息都会被构建为消息树的形式并被发送到后端以供进一步分析和监控。
 * 只有<code>Transaction</code>可以作为树节点，其他消息作为叶子节点。
 * 没有嵌套其他消息的Transaction是原子Transaction。
 * @author kris
 * @version $Id: Event.java, v 0.1 2018年5月23日 下午3:59:02 kris Exp $
 */
public interface Event extends Message {

}
