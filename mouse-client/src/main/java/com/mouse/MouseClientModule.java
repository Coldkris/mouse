/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.LockSupport;

import org.unidal.helper.Threads;
import org.unidal.helper.Threads.AbstractThreadListener;
import org.unidal.initialization.AbstractModule;
import org.unidal.initialization.DefaultModuleContext;
import org.unidal.initialization.Module;
import org.unidal.initialization.ModuleContext;

import com.mouse.configuration.ClientConfigManager;
import com.mouse.message.internal.MilliSecondTimer;
import com.mouse.message.io.TransportManager;
import com.mouse.status.StatusUpdateTask;

/**
 * 
 * @author kris
 * @version $Id: MouseClientModule.java, v 0.1 2018��5��24�� ����10:14:38 kris Exp $
 */
public class MouseClientModule extends AbstractModule {

    public static final String ID = "mouse-client";

    @Override
    public Module[] getDependencies(ModuleContext ctx) {
        return null;// ��������ϵ
    }

    @Override
    protected void execute(ModuleContext ctx) throws Exception {

        ctx.info("��ǰ����Ŀ¼��" + System.getProperty("user.dir"));

        // ��ʼ������ֱ��ʼ���ʱ��
        MilliSecondTimer.initialize();

        // �����߳�����/ֹͣ
        Threads.addListener(new MouseThreadListener(ctx));

        // Mouse����׼��
        Mouse.getInstance().setContainer(((DefaultModuleContext) ctx).getContainer());

        // 
        ctx.lookup(TransportManager.class);

        ClientConfigManager clientConfigManager = ctx.lookup(ClientConfigManager.class);

        if (clientConfigManager.isMouseEnabled()) {

            // ����״̬��������
            StatusUpdateTask statusUpdateTask = ctx.lookup(StatusUpdateTask.class);

            Threads.forGroup("mouse").start(statusUpdateTask);

            // �ȴ�10ms
            LockSupport.parkNanos(10 * 1000 * 1000L);
        }

    }

    public static final class MouseThreadListener extends AbstractThreadListener {

        private final ModuleContext mCtx;

        private MouseThreadListener(ModuleContext ctx) {
            mCtx = ctx;
        }

        @Override
        public void onThreadGroupCreated(ThreadGroup group, String name) {
            mCtx.info(String.format("�߳���(%s)������", name));
        }

        @Override
        public void onThreadPoolCreated(ExecutorService pool, String name) {
            mCtx.info(String.format("�̳߳�(%s)������", name));
        }

        @Override
        public void onThreadStarting(Thread thread, String name) {
            mCtx.info(String.format("�����߳�(%s)...", name));
        }

        @Override
        public void onThreadStopping(Thread thread, String name) {
            mCtx.info(String.format("ֹͣ�߳�(%s)...", name));
        }

        @Override
        public boolean onUncaughtException(Thread thread, Throwable e) {
            mCtx.error(String.format("���߳�(%s)�׳���δ��������쳣", thread.getName(), e));
            return true;
        }

    }

}
