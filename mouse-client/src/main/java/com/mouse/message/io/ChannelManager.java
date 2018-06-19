/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.io;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.unidal.helper.Files;
import org.unidal.helper.Splitters;
import org.unidal.helper.Threads.Task;
import org.unidal.helper.Urls;
import org.unidal.lookup.logging.Logger;
import org.unidal.lookup.util.StringUtils;
import org.unidal.tuple.Pair;

import com.mouse.configuration.ClientConfigManager;
import com.mouse.configuration.KVConfig;
import com.mouse.message.internal.MessageIdFactory;
import com.mouse.message.spi.MessageQueue;
import com.site.helper.JsonBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
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
            ChannelHolder holder = initChannel(configedAddresses, serverConfig);

            if (holder != null) {
                activeChannelHolder = holder;
            } else {
                activeChannelHolder = new ChannelHolder();
                activeChannelHolder.setServerAddress(configedAddresses);
            }
        } else {
            ChannelHolder holder = initChannel(serverAddresses, null);

            if (holder != null) {
                activeChannelHolder = holder;
            } else {
                activeChannelHolder = new ChannelHolder();
                activeChannelHolder.setServerAddress(serverAddresses);
                logger.error("error when init mouse module due to error config xml in /data/appdatas/cat/client.xml");
            }
        }

    }

    public ChannelFuture channel() {
        if (activeChannelHolder != null) {
            return activeChannelHolder.getActiveFuture();
        } else {
            return null;
        }
    }

    private ChannelHolder initChannel(List<InetSocketAddress> addresses, String serverConfig) {
        try {
            int len = addresses.size();

            for (int i = 0; i < len; i++) {
                InetSocketAddress address = addresses.get(i);
                String hostAddress = address.getAddress().getHostAddress();
                ChannelHolder holder = null;

                if (activeChannelHolder != null && hostAddress.equals(activeChannelHolder.getIp())) {
                    holder = new ChannelHolder();
                    holder.setActiveFuture(activeChannelHolder.getActiveFuture()).setConnectChanged(false);
                } else {
                    ChannelFuture future = createChannel(address);

                    if (future != null) {
                        holder = new ChannelHolder();
                        holder.setActiveFuture(future).setConnectChanged(true);
                    }
                }
                if (holder != null) {
                    holder.setActiveIndex(i).setIp(hostAddress);
                    holder.setActiveServerConfig(serverConfig).setServerAddress(addresses);

                    logger.info("success when init mouse server, new active holder" + holder.toString());
                    return holder;
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            StringBuilder sb = new StringBuilder();

            for (InetSocketAddress address : addresses) {
                sb.append(address.toString()).append(";");
            }
            logger.info("Error when init Mouse server " + sb.toString());
        } catch (Exception e) {

        }
        return null;
    }

    private ChannelFuture createChannel(InetSocketAddress address) {
        ChannelFuture future = null;

        try {
            future = bootstrap.connect(address);
            future.awaitUninterruptibly(100, TimeUnit.MILLISECONDS); // 100ms

            if (!future.isSuccess()) {
                logger.error("Error when try connectting to " + address);
                closeChannel(future);
            } else {
                logger.info("Connected to Mouse server at " + address);
                return future;
            }
        } catch (Exception e) {
            logger.error("Error when connect server " + address.getAddress(), e);

            if (future != null) {
                closeChannel(future);
            }
        }
        return null;
    }

    private void closeChannel(ChannelFuture channel) {
        try {
            if (channel != null) {
                SocketAddress ip = channel.channel().remoteAddress();

                if (ip != null) {
                    logger.info("close channel " + ip);
                }

                channel.channel().close();
            }
        } catch (Exception e) {

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
        while (active) {
            // 异步保存消息ID索引
            idFactory.saveMark();
            checkServerChanged();

            ChannelFuture activeFuture = activeChannelHolder.getActiveFuture();
            List<InetSocketAddress> serverAddresses = activeChannelHolder.getServerAddress();

            doubleCheckActiveServer(activeFuture);
            reconnectDefaultServer(activeFuture, serverAddresses);

            try {
                Thread.sleep(10 * 1000L); // 10秒检查一次
            } catch (InterruptedException e) {

            }
        }
    }

    private void reconnectDefaultServer(ChannelFuture activeFuture, List<InetSocketAddress> serverAddresses) {
        try {
            int reconnectServers = activeChannelHolder.getActiveIndex();

            if (reconnectServers == -1) {
                reconnectServers = serverAddresses.size();
            }
            for (int i = 0; i < reconnectServers; i++) {
                ChannelFuture future = createChannel(serverAddresses.get(i));

                if (future != null) {
                    ChannelFuture lastFuture = activeFuture;

                    activeChannelHolder.setActiveFuture(future);
                    activeChannelHolder.setActiveIndex(i);
                    closeChannel(lastFuture);
                    break;
                }
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void doubleCheckActiveServer(ChannelFuture activeFuture) {
        try {
            if (isChannelStalled(activeFuture) || isChannelDisabled(activeFuture)) {
                closeChannelHolder(activeChannelHolder);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean isChannelDisabled(ChannelFuture activeFuture) {
        return activeFuture != null && !activeFuture.channel().isOpen();
    }

    private boolean isChannelStalled(ChannelFuture activeFuture) {
        retriedTimes++;

        int size = queue.size();
        boolean stalled = activeFuture != null && size >= TcpSocketSender.getQueueSize() - 10;

        if (stalled) {
            if (retriedTimes >= 5) {
                retriedTimes = 0;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void checkServerChanged() {
        if (shouldCheckServerConfig(++count)) {
            Pair<Boolean, String> pair = routerConfigChanged();

            if (pair.getKey()) {
                String servers = pair.getValue();
                List<InetSocketAddress> serverAddresses = parseSocketAddress(servers);
                ChannelHolder newHolder = initChannel(serverAddresses, servers);

                if (newHolder != null) {
                    if (newHolder.isConnectChanged()) {
                        ChannelHolder last = activeChannelHolder;

                        activeChannelHolder = newHolder;
                        closeChannelHolder(last);
                        logger.info("switch active channel to " + activeChannelHolder);
                    } else {
                        activeChannelHolder = newHolder;
                    }
                }
            }
        }

    }

    private void closeChannelHolder(ChannelHolder channelHolder) {
        try {
            ChannelFuture channel = channelHolder.getActiveFuture();

            closeChannel(channel);
            channelHolder.setActiveIndex(-1);
        } catch (Exception e) {

        }
    }

    private Pair<Boolean, String> routerConfigChanged() {
        String current = loadServerConfig();

        if (!StringUtils.isEmpty(current) && !current.equals(activeChannelHolder.getActiveServerConfig())) {
            return new Pair<>(true, current);
        } else {
            return new Pair<>(false, current);
        }
    }

    private boolean shouldCheckServerConfig(int count) {
        int duration = 30;

        if (count % duration == 0 || activeChannelHolder.getActiveIndex() == -1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getName() {
        return "TcpSocketSender-ChannelManager";
    }

    public double getSample() {
        return sample;
    }

    @Override
    public void shutdown() {
        active = false;
    }

    public class ClientMessageHandle extends SimpleChannelInboundHandler<Object> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("receiver msg from server:" + msg);
        }

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
