/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.configuration;

import org.unidal.helper.Inets;

/**
 * ����ӿڹ�����
 * @author kris
 * @version $Id: NetworkInterfaceManager.java, v 0.1 2018��5��25�� ����5:18:23 kris Exp $
 */
public enum NetworkInterfaceManager {
    
    INSTANCE;

    private NetworkInterfaceManager() {
    }

    /**
     * ��ȡ����������ַ
     * @return
     */
    public String getLocalHostAddress() {
        return Inets.IP4.getLocalHostAddress();
    }

    /**
     * ��ȡ����������
     * @return
     */
    public String getLocalHostName() {
        return Inets.IP4.getLocalHostName();
    }

}
