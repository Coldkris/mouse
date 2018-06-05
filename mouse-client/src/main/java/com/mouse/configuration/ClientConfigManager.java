/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.configuration;

import java.io.File;
import java.util.List;

import com.mouse.configuration.client.entity.Domain;
import com.mouse.configuration.client.entity.Server;

/**
 * Client配置管理器
 * @author kris
 * @version $Id: ClientConfigManager.java, v 0.1 2018年5月24日 下午2:39:07 kris Exp $
 */
public interface ClientConfigManager {

    /**
     * 获取domain
     * @return
     */
    public Domain getDomain();

    /**
     * 获取最大消息长度
     * @return
     */
    public int getMaxMessageLength();

    /**
     * 获取服务器url配置
     * @return
     */
    public String getServerConfigUrl();

    /**
     * 获取服务器列表
     * @return
     */
    public List<Server> getServers();

    /**
     * 获取标记事务缓存大小
     * @return
     */
    public int getTaggedTransactionCacheSize();

    /**
     * 初始化
     * @param configFile
     * @throws Exception
     */
    public void initialize(File configFile) throws Exception;

    /**
     * Mouse是否启动
     * @return
     */
    public boolean isMouseEnabled();

    /**
     * TODO
     * @return
     */
    public boolean isDumpLocked();

}
