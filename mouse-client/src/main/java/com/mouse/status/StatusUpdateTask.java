/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.status;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;

import com.mouse.Mouse;
import com.mouse.message.Heartbeat;
import com.mouse.message.Message;
import com.mouse.message.MessageProducer;
import com.mouse.message.Transaction;
import com.mouse.message.configuration.ClientConfigManager;
import com.mouse.message.configuration.NetworkInterfaceManager;
import com.mouse.message.configuration.client.entity.Extension;
import com.mouse.message.configuration.client.entity.StatusInfo;
import com.mouse.message.internal.MilliSecondTimer;
import com.mouse.message.spi.MessageStatistics;

/**
 * 状态更新任务
 * @author kris
 * @version $Id: StatusUpdateTask.java, v 0.1 2018年5月25日 下午3:13:34 kris Exp $
 */
public class StatusUpdateTask implements Task, Initializable {

    @Inject
    private MessageStatistics   mStatistics;

    @Inject
    private ClientConfigManager cManager;

    private boolean             active   = true;

    private String              ipAddress;

    private long                interval = 60 * 1000; //60秒

    private String              jars;

    @Override
    public void run() {

        // 等待mouse client初始化成功
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            return;
        }

        while (true) {
            Calendar calendar = Calendar.getInstance();
            int second = calendar.get(Calendar.SECOND);

            // 避免在59-01秒区间发送心跳
            if (second < 2 || second > 58) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //忽略
                }
            } else {
                break;
            }
        }

        try {
            buildClasspath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MessageProducer mouse = Mouse.getProducer();
        Transaction reboot = mouse.newTransaction("System", "Reboot");

        reboot.setStatus(Message.SUCCESS);
        mouse.logEvent("Reboot", NetworkInterfaceManager.INSTANCE.getLocalHostAddress(), Message.SUCCESS, null);
        reboot.complete();

        while (active) {
            long start = MilliSecondTimer.currentTimeMillis();

            if (cManager.isMouseEnabled()) {
                Transaction t = mouse.newTransaction("System", "Status");
                Heartbeat h = mouse.newHeartbeat("Heartbeat", ipAddress);
                StatusInfo status = new StatusInfo();

                t.addData("dumpLocked", cManager.isDumpLocked());

                try {
                    StatusInfoCollector statusInfoCollector = new StatusInfoCollector(mStatistics, jars);

                    status.accept(statusInfoCollector.setDumpLocked(cManager.isDumpLocked()));

                    buildExtensionData(status);
                    h.addData(status.toString());
                    h.setStatus(Message.SUCCESS);
                } catch (Throwable e) {
                    h.setStatuc(e);
                    mouse.logError(e);
                } finally {
                    h.complete();
                }
                t.setStatus(Message.SUCCESS);
                t.complete();
            }
            long elapsed = MilliSecondTimer.currentTimeMillis() - start;

            if (elapsed < interval) {
                try {
                    Thread.sleep(interval - elapsed);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

    }

    private void buildExtensionData(StatusInfo status) {
        StatusExtensionRegister register = StatusExtensionRegister.getInstance();
        List<StatusExtension> extensions = register.getStatusExtension();

        for (StatusExtension extension : extensions) {
            String id = extension.getId();
            String des = extension.getDescription();
            Map<String, String> properties = extension.getProperties();
            Extension item = status.findOrCreateExtension(id).setDescription(des);

            for (Entry<String, String> entry : properties.entrySet()) {
                try {
                    double value = Double.parseDouble(entry.getValue());
                    item.findOrCreateExtensionDetail(entry.getKey()).setValue(value);
                } catch (Exception e) {
                    Mouse.logError("状态扩展只能是double类型", e);
                }
            }
        }

    }

    private void buildClasspath() {
        ClassLoader loader = StatusUpdateTask.class.getClassLoader();
        StringBuilder sb = new StringBuilder();

        buildClasspath(loader, sb);
        if (sb.length() > 0) {
            jars = sb.substring(0, sb.length() - 1);
        }
    }

    private void buildClasspath(ClassLoader loader, StringBuilder sb) {
        if (loader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) loader).getURLs();
            for (URL url : urls) {
                String jar = parseJar(url.toExternalForm());

                if (jar != null) {
                    sb.append(jar).append(",");
                }
            }
            ClassLoader parent = loader.getParent();

            buildClasspath(parent, sb);
        }
    }

    private String parseJar(String path) {
        if (path.endsWith(".jar")) {
            int index = path.lastIndexOf("/");

            if (index > -1) {
                return path.substring(index + 1);
            }
        }
        return null;
    }

    @Override
    public void initialize() throws InitializationException {
        ipAddress = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();

    }

    @Override
    public String getName() {
        return "StatusUpdateTask";
    }

    @Override
    public void shutdown() {
        active = true;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

}
