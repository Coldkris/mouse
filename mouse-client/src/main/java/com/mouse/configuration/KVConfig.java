/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author kris
 * @version $Id: KVConfig.java, v 0.1 2018��6��15�� ����4:37:47 kris Exp $
 */
public class KVConfig {

    private Map<String, String> kvs = new HashMap<>();

    public Set<String> getKeys() {
        return kvs.keySet();
    }

    public Map<String, String> getKvs() {
        return kvs;
    }

    public String getValue(String key) {
        return kvs.get(key);
    }

    public void setKvs(Map<String, String> kvs) {
        this.kvs = kvs;
    }

}
