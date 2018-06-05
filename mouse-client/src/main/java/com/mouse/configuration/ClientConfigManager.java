/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.configuration;

import java.io.File;
import java.util.List;

import com.mouse.configuration.client.entity.Domain;
import com.mouse.configuration.client.entity.Server;

/**
 * Client���ù�����
 * @author kris
 * @version $Id: ClientConfigManager.java, v 0.1 2018��5��24�� ����2:39:07 kris Exp $
 */
public interface ClientConfigManager {

    /**
     * ��ȡdomain
     * @return
     */
    public Domain getDomain();

    /**
     * ��ȡ�����Ϣ����
     * @return
     */
    public int getMaxMessageLength();

    /**
     * ��ȡ������url����
     * @return
     */
    public String getServerConfigUrl();

    /**
     * ��ȡ�������б�
     * @return
     */
    public List<Server> getServers();

    /**
     * ��ȡ������񻺴��С
     * @return
     */
    public int getTaggedTransactionCacheSize();

    /**
     * ��ʼ��
     * @param configFile
     * @throws Exception
     */
    public void initialize(File configFile) throws Exception;

    /**
     * Mouse�Ƿ�����
     * @return
     */
    public boolean isMouseEnabled();

    /**
     * TODO
     * @return
     */
    public boolean isDumpLocked();

}
