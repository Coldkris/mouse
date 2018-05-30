/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.status;

import java.util.Map;

/**
 * ״̬��չ
 * @author kris
 * @version $Id: StatusExtension.java, v 0.1 2018��5��30�� ����3:00:48 kris Exp $
 */
public interface StatusExtension {

    /**
     * ��ȡid
     * @return
     */
    public String getId();

    /**
     * ��ȡ����
     * @return
     */
    public String getDescription();

    /**
     * ��ȡ����
     * @return
     */
    public Map<String, String> getProperties();

}
