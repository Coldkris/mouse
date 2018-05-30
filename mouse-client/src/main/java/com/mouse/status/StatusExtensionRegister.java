/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.status;

import java.util.ArrayList;
import java.util.List;

/**
 * 状态扩展注册器
 * @author kris
 * @version $Id: StatusExtensionRegister.java, v 0.1 2018年5月30日 下午3:05:38 kris Exp $
 */
public class StatusExtensionRegister {

    private static StatusExtensionRegister register   = new StatusExtensionRegister();

    private List<StatusExtension>          extensions = new ArrayList<>();

    public static StatusExtensionRegister getInstance() {
        return register;
    }

    private StatusExtensionRegister() {
    }

    public List<StatusExtension> getStatusExtension() {
        synchronized (this) {
            return extensions;
        }
    }

    /**
     * 注册
     * @param monitor
     */
    public void register(StatusExtension monitor) {
        synchronized (this) {
            extensions.add(monitor);
        }
    }

    /**
     * 注销
     * @param monitor
     */
    public void unregister(StatusExtension monitor) {
        synchronized (this) {
            extensions.remove(monitor);
        }
    }
}
