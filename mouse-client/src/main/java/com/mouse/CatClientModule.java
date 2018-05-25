/**
 * CAT的小老鼠
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

import com.mouse.message.configuration.ClientConfigManager;
import com.mouse.message.internal.MilliSecondTimer;
import com.mouse.message.io.TransportManager;
import com.mouse.status.StatusUpdateTask;

/**
 * 
 * @author kris
 * @version $Id: CatClientModule.java, v 0.1 2018年5月24日 上午10:14:38 kris Exp $
 */
public class CatClientModule extends AbstractModule {

    public static final String ID = "cat-client";

    @Override
    public Module[] getDependencies(ModuleContext ctx) {
        return null;// 无依赖关系
    }

    @Override
    protected void execute(ModuleContext ctx) throws Exception {

        ctx.info("当前工作目录：" + System.getProperty("user.dir"));

        // 初始化毫秒分辨率级定时器
        MilliSecondTimer.initialize();

        // 跟踪线程启动/停止
        Threads.addListener(new CatThreadListener(ctx));

        // CAT启动准备
        Mouse.getInstance().setContainer(((DefaultModuleContext) ctx).getContainer());

        // 
        ctx.lookup(TransportManager.class);

        ClientConfigManager clientConfigManager = ctx.lookup(ClientConfigManager.class);

        if (clientConfigManager.isCatEnabled()) {

            // 启动状态更新任务
            StatusUpdateTask statusUpdateTask = ctx.lookup(StatusUpdateTask.class);

            Threads.forGroup("cat").start(statusUpdateTask);

            // 等待10ms
            LockSupport.parkNanos(10 * 1000 * 1000L);
        }

    }

    public static final class CatThreadListener extends AbstractThreadListener {

        private final ModuleContext mCtx;

        private CatThreadListener(ModuleContext ctx) {
            mCtx = ctx;
        }

        @Override
        public void onThreadGroupCreated(ThreadGroup group, String name) {
            mCtx.info(String.format("线程组(%s)被创建", name));
        }

        @Override
        public void onThreadPoolCreated(ExecutorService pool, String name) {
            mCtx.info(String.format("线程池(%s)被创建", name));
        }

        @Override
        public void onThreadStarting(Thread thread, String name) {
            mCtx.info(String.format("启动线程(%s)...", name));
        }

        @Override
        public void onThreadStopping(Thread thread, String name) {
            mCtx.info(String.format("停止线程(%s)...", name));
        }

        @Override
        public boolean onUncaughtException(Thread thread, Throwable e) {
            mCtx.error(String.format("由线程(%s)抛出的未被捕获的异常", thread.getName(), e));
            return true;
        }

    }

}
