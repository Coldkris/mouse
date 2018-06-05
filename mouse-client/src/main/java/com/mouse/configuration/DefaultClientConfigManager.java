/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.unidal.helper.Files;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

import com.mouse.Mouse;
import com.mouse.configuration.client.entity.ClientConfig;
import com.mouse.configuration.client.entity.Domain;
import com.mouse.configuration.client.entity.Server;
import com.mouse.configuration.client.transform.DefaultSaxParser;

/**
 * 默认Client配置管理器
 * @author kris
 * @version $Id: DefaultClientConfigManager.java, v 0.1 2018年5月31日 下午3:34:59 kris Exp $
 */
public class DefaultClientConfigManager implements LogEnabled, ClientConfigManager, Initializable {

    private static final String MOUSE_CLIENT_XML      = "/META-INF/mouse/client.xml";

    private static final String PROPERTIES_CLIENT_XML = "/META-INF/app.properties";

    private static final String XML                   = "/data/appdatas/mouse/client.xml";

    private Logger              logger;

    private ClientConfig        config;

    @Override
    public Domain getDomain() {
        Domain domain = null;

        if (config != null) {
            Map<String, Domain> domains = config.getDomains();

            domain = domains.isEmpty() ? null : domains.values().iterator().next();
        }

        if (domain != null) {
            return domain;
        } else {
            return new Domain("UNKNOWN").setEnabled(false);
        }
    }

    @Override
    public int getMaxMessageLength() {
        if (config == null) {
            return 5000;
        } else {
            return getDomain().getMaxMessageSize();
        }
    }

    @Override
    public String getServerConfigUrl() {
        if (config == null) {
            return null;
        } else {
            List<Server> servers = config.getServers();

            for (Server server : servers) {
                Integer httpPort = server.getHttpPort();

                if (httpPort == null || httpPort == 0) {
                    httpPort = 8080;
                }
                return String.format("http://%s:%d/mouse/s/router?domain=%s&ip=%s&op=json", server.getIp().trim(), httpPort, getDomain().getId(),
                    NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
            }
        }
        return null;
    }

    @Override
    public List<Server> getServers() {
        if (config == null) {
            return Collections.emptyList();
        } else {
            return config.getServers();
        }
    }

    @Override
    public int getTaggedTransactionCacheSize() {
        return 1024;
    }

    @Override
    public boolean isMouseEnabled() {
        if (config == null) {
            return false;
        } else {
            return config.isEnabled();
        }
    }

    @Override
    public boolean isDumpLocked() {
        if (config == null) {
            return false;
        } else {
            return config.isDumpLocked();
        }
    }

    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void initialize() throws InitializationException {
        File configFile = new File(XML);

        initialize(configFile);
    }

    @Override
    public void initialize(File configFile) throws InitializationException {

        try {
            ClientConfig globalConfig = null;
            ClientConfig clientConfig = null;

            if (configFile != null) {
                if (configFile.exists()) {
                    String xml = Files.forIO().readFrom(configFile.getCanonicalFile(), "utf-8");

                    globalConfig = DefaultSaxParser.parse(xml);
                    logger.info(String.format("发现全局配置文件(%s)", configFile));
                } else {
                    logger.warn(String.format("未发现全局配置文件(%s)，忽略", configFile));
                }
            }

            // 从Java类路径加载客户端配置
            clientConfig = loadConfigFromEnviroment();

            if (clientConfig == null) {
                clientConfig = loadConfigFromXml();
            }

            // 将两个配置文件合并使其生效
            if (globalConfig != null && clientConfig != null) {
                globalConfig.accept(new ClientConfigMerger(clientConfig));
            }

            if (clientConfig != null) {
                clientConfig.accept(new ClientConfigValidator());
            }

            config = clientConfig;
        } catch (Exception e) {
            throw new InitializationException(e.getMessage(), e);
        }

    }

    private ClientConfig loadConfigFromEnviroment() {
        String appName = loadProjectName();

        if (appName != null) {
            ClientConfig config = new ClientConfig();

            config.addDomain(new Domain(appName));
            return config;
        }
        return null;
    }

    private ClientConfig loadConfigFromXml() {
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(MOUSE_CLIENT_XML);

            if (in == null) {
                in = Mouse.class.getResourceAsStream(MOUSE_CLIENT_XML);
            }
            if (in != null) {
                String xml = Files.forIO().readFrom(in, "utf-8");

                logger.info(String.format("发现资源文件(%s)", Mouse.class.getResource(MOUSE_CLIENT_XML)));
                return DefaultSaxParser.parse(xml);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private String loadProjectName() {
        String appName = null;
        InputStream in = null;

        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_CLIENT_XML);

            if (in == null) {
                in = Mouse.class.getResourceAsStream(PROPERTIES_CLIENT_XML);
            }

            if (in != null) {
                Properties prop = new Properties();

                prop.load(in);

                appName = prop.getProperty("app.name");
                if (appName != null) {
                    logger.info(String.format("在app.properties中查找到域%s", appName));
                } else {
                    logger.info(String.format("在app.properties中未找到app.name!"));
                }
            } else {
                logger.info(String.format("在目录%s未找到app.properties!", PROPERTIES_CLIENT_XML));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return appName;
    }

}
