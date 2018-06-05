/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.configuration;

import java.text.MessageFormat;
import java.util.Date;

import com.mouse.configuration.client.entity.ClientConfig;
import com.mouse.configuration.client.entity.Domain;
import com.mouse.configuration.client.entity.Server;
import com.mouse.configuration.client.transform.DefaultValidator;

/**
 * �ͻ���������֤��
 * @author kris
 * @version $Id: ClientConfigValidator.java, v 0.1 2018��6��1�� ����9:58:53 kris Exp $
 */
public class ClientConfigValidator extends DefaultValidator {

    private ClientConfig clientConfig;

    private String getLocalAddress() {
        return NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
    }

    private void Log(String severity, String message) {
        MessageFormat format = new MessageFormat("[{0,date,MM-dd HH:mm:ss.sss}] [{1}] [{2}] {3}");

        System.out.println(format.format(new Object[] { new Date(), severity, "mouse", message }));
    }

    @Override
    public void visitConfig(ClientConfig config) {
        config.setMode("client");

        if (config.getServers().size() == 0) {
            config.setEnabled(false);
            Log("WARN", "δ����Mouse��������Mouse�ͻ��˱����ã�");
        } else if (!config.isEnabled()) {
            Log("WARN", "Mouse�ͻ��˱�ȫ�ֽ��ã�");
        }

        clientConfig = config;
        super.visitConfig(config);

        if (clientConfig.isEnabled()) {
            for (Domain domain : clientConfig.getDomains().values()) {
                if (!domain.isEnabled()) {
                    clientConfig.setEnabled(false);
                    Log("WARN", "Mouse�ͻ�������(" + domain.getId() + ")" + "�б���ȷ���ã�");
                }

                break;// ���޵�һ�������������
            }
        }
    }

    @Override
    public void visitDomain(Domain domain) {
        super.visitDomain(domain);

        if (domain.getEnabled()) {
            domain.setEnabled(true);
        }

        if (domain.getIp() == null) {
            domain.setIp(getLocalAddress());
        }
    }

    @Override
    public void visitServer(Server server) {
        super.visitServer(server);

        if (server.getPort() == 0) {
            server.setPort(2280);
        }

        if (server.getEnabled()) {
            server.setEnabled(true);
        }
    }

}
