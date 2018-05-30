/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.status;

import java.util.Map;

/**
 * 状态扩展
 * @author kris
 * @version $Id: StatusExtension.java, v 0.1 2018年5月30日 下午3:00:48 kris Exp $
 */
public interface StatusExtension {

    /**
     * 获取id
     * @return
     */
    public String getId();

    /**
     * 获取描述
     * @return
     */
    public String getDescription();

    /**
     * 获取属性
     * @return
     */
    public Map<String, String> getProperties();

}
