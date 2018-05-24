/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse;

import java.util.concurrent.ExecutorService;

import org.unidal.helper.Threads;
import org.unidal.helper.Threads.AbstractThreadListener;
import org.unidal.initialization.AbstractModule;
import org.unidal.initialization.DefaultModuleContext;
import org.unidal.initialization.Module;
import org.unidal.initialization.ModuleContext;

import com.mouse.message.internal.MilliSecondTimer;
import com.mouse.message.io.TransportManager;

/**
 * 
 * @author kris
 * @version $Id: CatClientModule.java, v 0.1 2018��5��24�� ����10:14:38 kris Exp $
 */
public class CatClientModule extends AbstractModule {

    public static final String ID = "cat-client";

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
        Threads.addListener(new CatThreadListener(ctx));

        // CAT����׼��
        Mouse.getInstance().setContainer(((DefaultModuleContext) ctx).getContainer());

        // 
        ctx.lookup(TransportManager.class);

    }

    public static final class CatThreadListener extends AbstractThreadListener {

        private final ModuleContext mCtx;

        private CatThreadListener(ModuleContext ctx) {
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
