/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.configuration;

import java.util.Stack;

import com.mouse.configuration.client.entity.ClientConfig;
import com.mouse.configuration.client.entity.Domain;
import com.mouse.configuration.client.entity.Property;
import com.mouse.configuration.client.transform.DefaultMerger;

/**
 * 客户端配置合并
 * @author kris
 * @version $Id: ClientConfigMerger.java, v 0.1 2018年5月31日 下午7:33:17 kris Exp $
 */
public class ClientConfigMerger extends DefaultMerger {

    public ClientConfigMerger(ClientConfig config) {
        super(config);
    }

    @Override
    protected void mergeDomain(Domain old, Domain domain) {
        if (domain.getIp() != null) {
            old.setIp(domain.getIp());
        }

        if (domain.getEnabled()) {
            old.setEnabled(domain.getEnabled());
        }

        if (domain.getMaxMessageSize() > 0) {
            old.setMaxMessageSize(domain.getMaxMessageSize());
        }
    }

    @Override
    protected void visitConfigChildren(ClientConfig to, ClientConfig from) {
        if (to != null) {
            Stack<Object> objects = getObjects();

            // 覆盖
            if (!from.getServers().isEmpty()) {
                to.getServers().clear();
                to.getServers().addAll(from.getServers());
            }

            // 只有客户端配置才会合并
            for (Domain source : from.getDomains().values()) {
                Domain target = to.findDomain(source.getId());

                if (target == null) {
                    target = new Domain(source.getId());
                    to.addDomain(target);
                }

                if (to.getDomains().containsKey(source.getId())) {
                    objects.push(target);
                    source.accept(this);
                    objects.pop();
                }

            }

            for (Property source : from.getProperties().values()) {
                Property target = to.findProperty(source.getName());

                if (target == null) {
                    target = new Property(source.getName());
                    to.addProperty(target);
                }

                objects.push(target);
                source.accept(this);
                objects.pop();
            }
        }
    }

}
