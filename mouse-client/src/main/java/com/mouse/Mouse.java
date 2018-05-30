/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;

import org.unidal.helper.Properties;
import org.unidal.initialization.DefaultModuleContext;
import org.unidal.initialization.Module;
import org.unidal.initialization.ModuleContext;
import org.unidal.initialization.ModuleInitializer;
import org.unidal.lookup.ComponentLookupException;
import org.unidal.lookup.ContainerLoader;
import org.unidal.lookup.PlexusContainer;

import com.mouse.message.Event;
import com.mouse.message.ForkedTransaction;
import com.mouse.message.Heartbeat;
import com.mouse.message.MessageProducer;
import com.mouse.message.TaggedTransaction;
import com.mouse.message.Trace;
import com.mouse.message.Transaction;
import com.mouse.message.spi.MessageManager;
import com.mouse.message.spi.MessageTree;

/**
 * ϵͳ���
 * @author kris
 * @version $Id: Mouse.java, v 0.1 2018��5��23�� ����11:40:38 kris Exp $
 */
public class Mouse {

    private static Mouse            instance = new Mouse();

    private static volatile boolean isInit   = false;

    private MessageManager          mManager;

    private MessageProducer         mProducer;

    private PlexusContainer         pContainer;

    private Mouse() {
    }

    public static void checkAndInitialize() {
        if (!isInit) {
            synchronized (instance) {
                if (!isInit) {
                    initialize(new File(getMouseHome(), "client.xml"));
                    log("WARN", "Mouse����ʼ����");
                    isInit = true;
                }
            }
        }
    }

    private static String getMouseHome() {
        return Properties.forString().fromEnv().fromSystem().getProperty("MOUSE_HOME", "/data/appdatas/mouse");
    }

    public static Mouse getInstance() {
        return instance;
    }

    public static void destroy() {
        instance.pContainer.dispose();
        instance = new Mouse();
    }

    // Ӧ�ó����ʼ���ڼ���á�
    private static void initialize(File configFile) {

        PlexusContainer container = ContainerLoader.getDefaultContainer();

        initialize(container, configFile);

    }

    private static void initialize(PlexusContainer container, File configFile) {
        ModuleContext ctx = new DefaultModuleContext(container);

        Module module = ctx.lookup(Module.class, MouseClientModule.ID);

        if (!module.isInitialized()) {
            ModuleInitializer initializer = ctx.lookup(ModuleInitializer.class);

            ctx.setAttribute("mouse-client-config-file", configFile);
            initializer.execute(ctx, module);
        }
    }

    public static MessageProducer getProducer() {
        checkAndInitialize();

        return instance.mProducer;
    }

    public static MessageManager getManager() {
        checkAndInitialize();

        return instance.mManager;
    }

    public static Transaction newTransaction(String type, String name) {
        return Mouse.getProducer().newTransaction(type, name);
    }

    public static ForkedTransaction newForkedTransaction(String type, String name) {
        return Mouse.getProducer().newForkedTransaction(type, name);
    }

    public static TaggedTransaction newTaggedTransaction(String type, String name, String tag) {
        return Mouse.getProducer().newTaggedTransaction(type, name, tag);
    }

    public static Event newEvent(String type, String name) {
        return Mouse.getProducer().newEvent(type, name);
    }

    public static Heartbeat newHeartbeat(String type, String name) {
        return Mouse.getProducer().newHeartbeat(type, name);
    }

    public static Trace newTrace(String type, String name) {
        return Mouse.getProducer().newTrace(type, name);
    }

    void setContainer(PlexusContainer container) {
        try {
            pContainer = container;
            mManager = container.lookup(MessageManager.class);
            mProducer = container.lookup(MessageProducer.class);
        } catch (ComponentLookupException e) {
            throw new RuntimeException("�޷���ȡMessageManager��ʵ������ȷ������������ȷ��", e);
        }
    }

    static void log(String severity, String message) {
        MessageFormat format = new MessageFormat("[{0,date,MM-dd HH:mm:ss.sss}] [{1}] [{2}] {3}");

        System.out.println(format.format(new Object[] { new Date(), severity, "mouse", message }));
    }

    public static void logError(String message, Throwable cause) {
        Mouse.getProducer().logError(message, cause);
    }

    public static void logError(Throwable cause) {
        Mouse.getProducer().logError(cause);
    }

    public static void logEvent(String type, String name) {
        Mouse.getProducer().logEvent(type, name);
    }

    public static void logEvent(String type, String name, String status, String nameValuePairs) {
        Mouse.getProducer().logEvent(type, name, status, nameValuePairs);
    }

    public static void logHeartbeat(String type, String name, String status, String nameValuePairs) {
        Mouse.getProducer().logHeartbeat(type, name, status, nameValuePairs);
    }

    public static void logMetric(String name, Object... keyValues) {
        // ��ɾ��
    }

    /**
     * ��nameָ���ļ���������1��
     * @param name Ĭ�ϼ�����ֵΪ1
     */
    public static void logMetricForCount(String name) {
        logMetricInternal(name, "C", "1");
    }

    /**
     * ��nameָ���ļ���������1��
     * @param name
     * @param quantity
     */
    public static void logMetricForCount(String name, int quantity) {
        logMetricInternal(name, "C", String.valueOf(quantity));
    }

    /**
     * ��nameָ���ļ���������durationInMillis���롣
     * @param name
     * @param durationInMillis �Ժ���Ϊ��λ��ʱ����ӵ�Metric
     */
    public static void logMetricForDuration(String name, long durationInMillis) {
        logMetricInternal(name, "T", String.valueOf(durationInMillis));
    }

    /**
     * ��nameָ���ļ������ۼ���value
     * @param name
     * @param value
     */
    public static void logMetricForSum(String name, double value) {
        logMetricInternal(name, "S", String.format("%.2f", value));
    }

    /**
     * ��nameָ���ļ������ۼӶ���
     * @param name     Metric��
     * @param sum      �ۼ�ֵ
     * @param quantity �ۼ���
     */
    public static void logMetricForSum(String name, double sum, int quantity) {
        logMetricInternal(name, "S,C", String.format("%s,%.2f", quantity, sum));
    }

    private static void logMetricInternal(String name, String status, String keyValuePairs) {
        Mouse.getProducer().logMetric(name, status, keyValuePairs);
    }

    public static void logRemoteCallClient(Context ctx) {
        MessageTree tree = Mouse.getManager().getThreadLocalMessageTree();
        String messageId = tree.getMessageId();

        if (messageId == null) {
            messageId = Mouse.createMessageId();
            tree.setMessageId(messageId);
        }

        String childId = Mouse.createMessageId();
        Mouse.logEvent(MouseConstants.TYPE_REMOTE_CALL, "", Event.SUCCESS, childId);

        String root = tree.getRootMessageId();

        if (root == null) {
            root = messageId;
        }

        ctx.addProperty(Context.ROOT, root);
        ctx.addProperty(Context.PARENT, messageId);
        ctx.addProperty(Context.CHILD, childId);
    }

    public static void logRemoteCallServer(Context ctx) {
        MessageTree tree = Mouse.getManager().getThreadLocalMessageTree();
        String messageId = ctx.getProperty(Context.CHILD);
        String rootId = ctx.getProperty(Context.ROOT);
        String parentId = ctx.getProperty(Context.PARENT);

        if (messageId != null) {
            tree.setMessageId(messageId);
        }
        if (parentId != null) {
            tree.setParentMessageId(parentId);
        }
        if (rootId != null) {
            tree.setRootMessageId(rootId);
        }
    }

    public static String createMessageId() {
        return Mouse.getProducer().createMessageId();
    }

    // �߳̿�ʼʱ���ô���һЩ�̱߳�������
    public static void setup(String sessionToken) {
        Mouse.getManager().setup();
    }

    // �߳̽���ʱ���������ĳЩ�̱߳�������
    public static void reset() {
        // remove
    }

    public static interface Context {

        public final String ROOT   = "_mouseRootMessageId";

        public final String PARENT = "_mouseParentMessageId";

        public final String CHILD  = "_mouseChildMessageId";

        public void addProperty(String key, String value);

        public String getProperty(String key);
    }

}
