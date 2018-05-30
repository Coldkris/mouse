/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.status;

import java.util.ArrayList;
import java.util.List;

/**
 * ״̬��չע����
 * @author kris
 * @version $Id: StatusExtensionRegister.java, v 0.1 2018��5��30�� ����3:05:38 kris Exp $
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
     * ע��
     * @param monitor
     */
    public void register(StatusExtension monitor) {
        synchronized (this) {
            extensions.add(monitor);
        }
    }

    /**
     * ע��
     * @param monitor
     */
    public void unregister(StatusExtension monitor) {
        synchronized (this) {
            extensions.remove(monitor);
        }
    }
}
