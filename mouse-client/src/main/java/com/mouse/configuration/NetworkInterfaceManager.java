/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.configuration;

import org.unidal.helper.Inets;

/**
 * 网络接口管理器
 * @author kris
 * @version $Id: NetworkInterfaceManager.java, v 0.1 2018年5月25日 下午5:18:23 kris Exp $
 */
public enum NetworkInterfaceManager {
    
    INSTANCE;

    private NetworkInterfaceManager() {
    }

    /**
     * 获取本地主机地址
     * @return
     */
    public String getLocalHostAddress() {
        return Inets.IP4.getLocalHostAddress();
    }

    /**
     * 获取本地主机名
     * @return
     */
    public String getLocalHostName() {
        return Inets.IP4.getLocalHostName();
    }

}
