/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.io;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import org.unidal.helper.Files;
import org.unidal.helper.Splitters;
import org.unidal.helper.Threads.Task;
import org.unidal.helper.Urls;
import org.unidal.lookup.logging.Logger;
import org.unidal.lookup.util.StringUtils;

import com.mouse.configuration.ClientConfigManager;
import com.mouse.configuration.KVConfig;
import com.mouse.message.internal.MessageIdFactory;
import com.mouse.message.spi.MessageQueue;
import com.site.helper.JsonBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * @author kris
 * @version $Id: ChannelManager.java, v 0.1 2018年6月14日 下午5:44:08 kris Exp $
 */
public class ChannelManager implements Task {

    private ClientConfigManager configManager;

    private Bootstrap           bootstrap;

    private Logger              logger;

    private boolean             active       = true;

    private int                 retriedTimes = 0;

    private int                 count        = -10;

    private volatile double     sample       = 1d;

    private MessageQueue        queue;

    private ChannelHolder       activeChannelHolder;

    private MessageIdFactory    idFactory;

    private JsonBuilder         jsonBuilder  = new JsonBuilder();

    public ChannelManager(Logger logger, List<InetSocketAddress> serverAddresses, MessageQueue queue, ClientConfigManager configManager, MessageIdFactory idFactory) {
        this.logger = logger;
        this.queue = queue;
        this.configManager = configManager;
        this.idFactory = idFactory;

        EventLoopGroup group = new NioEventLoopGroup(1, new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {

            }
        });
        this.bootstrap = bootstrap;

        String serverConfig = loadServerConfig();

        if (StringUtils.isNotEmpty(serverConfig)) {
            List<InetSocketAddress> configedAddresses = parseSocketAddress(serverConfig);
        }

    }

    private List<InetSocketAddress> parseSocketAddress(String content) {
        try {
            List<String> strs = Splitters.by(";").noEmptyItem().split(content);
            List<InetSocketAddress> address = new ArrayList<>();

            for (String str : strs) {
                List<String> items = Splitters.by(":").noEmptyItem().split(str);

                address.add(new InetSocketAddress(items.get(0), Integer.parseInt(items.get(1))));
            }
            return address;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    private String loadServerConfig() {
        try {
            String url = configManager.getServerConfigUrl();
            InputStream inputStream = Urls.forIO().readTimeout(2000).connectTimeout(1000).openStream(url);
            String content = Files.forIO().readFrom(inputStream, "utf-8");

            KVConfig routerConfig = (KVConfig) jsonBuilder.parse(content.trim(), KVConfig.class);
            String current = routerConfig.getValue("routers");
            sample = Double.valueOf(routerConfig.getValue("sample").trim());

            return current.trim();
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    /** 
     * @see org.unidal.helper.Threads.Task#getName()
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * @see org.unidal.helper.Threads.Task#shutdown()
     */
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

    public static class ChannelHolder {
        private ChannelFuture           activeFuture;

        private int                     activeIndex = -1;

        private String                  activeServerConfig;

        private List<InetSocketAddress> serverAddress;

        private String                  ip;

        private boolean                 connectChanged;

        public ChannelFuture getActiveFuture() {
            return activeFuture;
        }

        public ChannelHolder setActiveFuture(ChannelFuture activeFuture) {
            this.activeFuture = activeFuture;
            return this;
        }

        public int getActiveIndex() {
            return activeIndex;
        }

        public ChannelHolder setActiveIndex(int activeIndex) {
            this.activeIndex = activeIndex;
            return this;
        }

        public String getActiveServerConfig() {
            return activeServerConfig;
        }

        public ChannelHolder setActiveServerConfig(String activeServerConfig) {
            this.activeServerConfig = activeServerConfig;
            return this;
        }

        public List<InetSocketAddress> getServerAddress() {
            return serverAddress;
        }

        public ChannelHolder setServerAddress(List<InetSocketAddress> serverAddress) {
            this.serverAddress = serverAddress;
            return this;
        }

        public String getIp() {
            return ip;
        }

        public ChannelHolder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public boolean isConnectChanged() {
            return connectChanged;
        }

        public ChannelHolder setConnectChanged(boolean connectChanged) {
            this.connectChanged = connectChanged;
            return this;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("active future :").append(activeFuture.channel().remoteAddress());
            sb.append(" index:").append(activeIndex);
            sb.append(" ip:").append(ip);
            sb.append(" server config:").append(activeServerConfig);
            return sb.toString();
        }

    }

}
