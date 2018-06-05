/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.configuration.client.entity;

import java.util.List;
import java.util.Map;

import com.mouse.configuration.ClientConfigMerger;
import com.mouse.configuration.ClientConfigValidator;

/**
 * 客户端配置
 * @author kris
 * @version $Id: ClientConfig.java, v 0.1 2018年5月31日 下午3:41:20 kris Exp $
 */
public class ClientConfig {

    public Map<String, Domain> getDomains() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Server> getServers() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isDumpLocked() {
        // TODO Auto-generated method stub
        return false;
    }

    public Domain findDomain(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addDomain(Domain target) {
        // TODO Auto-generated method stub

    }

    public Map<String, Property> getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    public Property findProperty(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addProperty(Property target) {
        // TODO Auto-generated method stub

    }

    public void accept(ClientConfigMerger clientConfigMerger) {
        // TODO Auto-generated method stub

    }

    public void setMode(String string) {
        // TODO Auto-generated method stub

    }

    public void setEnabled(boolean b) {
        // TODO Auto-generated method stub

    }

    public void accept(ClientConfigValidator clientConfigValidator) {
        // TODO Auto-generated method stub

    }

}
