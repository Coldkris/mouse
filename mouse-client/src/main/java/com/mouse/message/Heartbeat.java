/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * <p>
 * <code>Heartbeat</p>不应该在每个请求中使用，因为请求不是常规可预测的，而是可以记录在守护程序后台线程中，或者类似Timer一样。
 * </p>
 * <p>
 * 所有的Mouse消息都会被构建为消息树的形式并被发送到后端以供进一步分析和监控。
 * 只有<code>Transaction</code>可以作为树节点，其他消息作为叶子节点。
 * 没有嵌套其他消息的Transaction是原子Transaction。
 * </p>
 * @author kris
 * @version $Id: Heartbeat.java, v 0.1 2018年5月23日 下午9:05:38 kris Exp $
 */
public interface Heartbeat extends Message {

}
