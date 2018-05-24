/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message;

/**
 * <p>
 * <code>Trace</code>用于记录发生在特定时间的跟踪消息信息的任何内容。 如调试或信息消息。
 * </p>
 * <p>
 * 所有的CAT消息都会被构建为消息树的形式并被发送到后端以供进一步分析和监控。
 * 只有<code>Transaction</code>可以作为树节点，其他消息作为叶子节点。
 * 没有嵌套其他消息的Transaction是原子Transaction。
 * </p>
 * @author kris
 * @version $Id: Trace.java, v 0.1 2018年5月23日 下午9:00:53 kris Exp $
 */
public interface Trace extends Message {

}
