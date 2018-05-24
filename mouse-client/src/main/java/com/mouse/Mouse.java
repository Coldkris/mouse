/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse;

import java.io.File;

import org.unidal.helper.Properties;
import org.unidal.initialization.DefaultModuleContext;
import org.unidal.initialization.Module;
import org.unidal.initialization.ModuleContext;
import org.unidal.lookup.ComponentLookupException;
import org.unidal.lookup.ContainerLoader;
import org.unidal.lookup.PlexusContainer;

import com.mouse.message.MessageProducer;
import com.mouse.message.Transaction;
import com.mouse.message.spi.MessageManager;

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

    public static void checkAndInitialize() {

        if (!isInit) {
            synchronized (instance) {
                if (!isInit) {
                    initialize(new File(getCatHome(), "client.xml"));

                }

            }

        }

    }

    private static String getCatHome() {
        return Properties.forString().fromEnv().fromSystem().getProperty("CAT_HOME", "/data/appdatas/cat");
    }

    public static Mouse getInstance() {
        return instance;
    }

    // ��Ӧ����Ӧ�ó����ʼ���ڼ���á�
    private static void initialize(File configFile) {

        PlexusContainer container = ContainerLoader.getDefaultContainer();

        initialize(container, configFile);

    }

    private static void initialize(PlexusContainer container, File configFile) {

        ModuleContext ctx = new DefaultModuleContext(container);

        Module module = ctx.lookup(Module.class, "");

    }

    public static MessageProducer getProducer() {

        checkAndInitialize();

        return instance.mProducer;

    }

    public static Transaction newTransaction(String type, String name) {
        return null;
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

}
