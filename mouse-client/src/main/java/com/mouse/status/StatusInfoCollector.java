/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.status;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import com.mouse.message.spi.MessageStatistics;
import com.mouse.status.model.entity.DiskInfo;
import com.mouse.status.model.entity.DiskVolumeInfo;
import com.mouse.status.model.entity.Extension;
import com.mouse.status.model.entity.GcInfo;
import com.mouse.status.model.entity.MemoryInfo;
import com.mouse.status.model.entity.MessageInfo;
import com.mouse.status.model.entity.OsInfo;
import com.mouse.status.model.entity.RuntimeInfo;
import com.mouse.status.model.entity.StatusInfo;
import com.mouse.status.model.entity.ThreadsInfo;
import com.mouse.status.model.transform.BaseVisitor;

/**
 * 状态信息收集器
 * @author kris
 * @version $Id: StatusInfoCollector.java, v 0.1 2018年5月25日 下午7:57:33 kris Exp $
 */
public class StatusInfoCollector extends BaseVisitor {

    private MessageStatistics mStatistics;

    private boolean           dumpLocked;

    private String            jars;

    private String            dataPath = "/data";

    private StatusInfo        statusInfo;

    public StatusInfoCollector(MessageStatistics statistics, String jars) {
        this.mStatistics = statistics;
        this.jars = jars;
    }

    public StatusInfoCollector setDumpLocked(boolean dumpLocked) {
        this.dumpLocked = dumpLocked;
        return this;
    }

    @Override
    public void visitDisk(DiskInfo disk) {
        File[] roots = File.listRoots();

        if (roots != null) {
            for (File root : roots) {
                disk.addDiskVolume(new DiskVolumeInfo(root.getAbsolutePath()));
            }
        }

        File data = new File(dataPath);

        if (data.exists()) {
            disk.addDiskVolume(new DiskVolumeInfo(data.getAbsolutePath()));
        }

        super.visitDisk(disk);
    }

    @Override
    public void visitDiskVolume(DiskVolumeInfo diskVolume) {
        Extension diskExtension = statusInfo.findOrCreateExtension("Disk");
        File volume = new File(diskVolume.getId());

        diskVolume.setTotal(volume.getTotalSpace());
        diskVolume.setFree(volume.getFreeSpace());
        diskVolume.setUsable(volume.getUsableSpace());

        diskExtension.findOrCreateExtensionDetail(diskVolume.getId() + " Free").setValue(volume.getFreeSpace());
    }

    @Override
    public void visitMemory(MemoryInfo memory) {
        MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        Runtime runtime = Runtime.getRuntime();

        memory.setMax(runtime.maxMemory());
        memory.setTotal(runtime.totalMemory());
        memory.setFree(runtime.freeMemory());
        memory.setHeapUsage(bean.getHeapMemoryUsage().getUsed());
        memory.setNonHeapUsage(bean.getNonHeapMemoryUsage().getUsed());

        List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
        Extension gcExtension = statusInfo.findOrCreateExtension("GC");

        for (GarbageCollectorMXBean mxBean : beans) {
            if (mxBean.isValid()) {
                GcInfo gc = new GcInfo();
                String name = mxBean.getName();
                long count = mxBean.getCollectionCount();

                gc.setName(name);
                gc.setCount(count);
                gc.setTime(mxBean.getCollectionTime());
                memory.addGc(gc);

                gcExtension.findOrCreateExtensionDetail(name + "Count").setValue(count);
                gcExtension.findOrCreateExtensionDetail(name + "Time").setValue(mxBean.getCollectionTime());
            }
        }

        Extension heapUsage = statusInfo.findOrCreateExtension("JVMHeap");
        for (MemoryPoolMXBean mpBean : ManagementFactory.getMemoryPoolMXBeans()) {
            long count = mpBean.getUsage().getUsed();
            String name = mpBean.getName();

            heapUsage.findOrCreateExtensionDetail(name).setValue(count);
        }

        super.visitMemory(memory);
    }

    @Override
    public void visitMessage(MessageInfo message) {
        Extension mouseExtension = statusInfo.findOrCreateExtension("MouseUsage");

        if (mStatistics != null) {
            mouseExtension.findOrCreateExtensionDetail("Produced").setValue(mStatistics.getProdeced());
            mouseExtension.findOrCreateExtensionDetail("Overflowed").setValue(mStatistics.getOverflowed());
            mouseExtension.findOrCreateExtensionDetail("Bytes").setValue(mStatistics.getBytes());
        }
    }

    @Override
    @SuppressWarnings("restriction")
    public void visitOs(OsInfo os) {
        Extension systemExtension = statusInfo.findOrCreateExtension("System");
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();

        os.setArch(bean.getArch());
        os.setName(bean.getName());
        os.setVersion(bean.getVersion());
        os.setAvailableProcessors(bean.getAvailableProcessors());
        os.setSystemLoadAverage(bean.getSystemLoadAverage());

        systemExtension.findOrCreateExtensionDetail("LoadAverage").setValue(bean.getSystemLoadAverage());

        // for Sun JDK
        if (isInstanceOfInterface(bean.getClass(), "com.sun.management.OperationSystemMXBean")) {

            com.sun.management.OperatingSystemMXBean b = (com.sun.management.OperatingSystemMXBean) bean;

            os.setTotalPhysicalMemory(b.getTotalPhysicalMemorySize());
            os.setFreePhysicalMemory(b.getFreePhysicalMemorySize());
            os.setTotalSwapSpace(b.getTotalSwapSpaceSize());
            os.setFreeSwapSpace(b.getFreeSwapSpaceSize());
            os.setProcessTime(b.getProcessCpuTime());
            os.setCommittedVirtualMemory(b.getCommittedVirtualMemorySize());

            systemExtension.findOrCreateExtensionDetail("FreePhysicalMemory").setValue(b.getFreePhysicalMemorySize());
            systemExtension.findOrCreateExtensionDetail("FreeSwapSpaceSize").setValue(b.getFreeSwapSpaceSize());

        }
        statusInfo.addExtension(systemExtension);
    }

    @Override
    public void visitRuntime(RuntimeInfo runtime) {

        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();

        runtime.setStartTime(bean.getStartTime());
        runtime.setUpTime(bean.getUptime());
        runtime.setJavaClasspath(jars);
        runtime.setJavaVersion(System.getProperty("java.version"));
        runtime.setUserDir(System.getProperty("user.dir"));
        runtime.setUserName(System.getProperty("user.name"));

    }

    @Override
    public void visitStatus(StatusInfo status) {
        status.setTimestamp(new Date());
        status.setOs(new OsInfo());
        status.setDisk(new DiskInfo());
        status.setRuntime(new RuntimeInfo());
        status.setMemory(new MemoryInfo());
        status.setThread(new ThreadsInfo());
        status.setMessage(new MessageInfo());
        statusInfo = status;

        super.visitStatus(status);
    }

    @Override
    public void visitThread(ThreadsInfo thread) {
        Extension frameworkThread = statusInfo.findOrCreateExtension("FrameworkThread");
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();

        bean.setThreadContentionMonitoringEnabled(true);

        ThreadInfo[] threads;

        if (dumpLocked) {
            threads = bean.dumpAllThreads(true, true);
        } else {
            threads = bean.dumpAllThreads(false, false);
        }

        thread.setCount(bean.getThreadCount());
        thread.setDaemonCount(bean.getDaemonThreadCount());
        thread.setPeekCount(bean.getPeakThreadCount());
        thread.setTotalStartedCount((int) bean.getTotalStartedThreadCount());

        int jbossThreadsCount = countThreadsByPrefix(threads, "http-", "catalina-exec-");
        int jettyThreadsCount = countThreadsBySubstring(threads, "@qtp");

        thread.setDump(getThreadDump(threads));

        frameworkThread.findOrCreateExtensionDetail("HttpThread").setValue(jbossThreadsCount + jettyThreadsCount);
        frameworkThread.findOrCreateExtensionDetail("MouseThread").setValue(countThreadsByPrefix(threads, "Mouse-"));
        frameworkThread.findOrCreateExtensionDetail("PigeonThread").setValue(countThreadsByPrefix(threads, "Pigeon-", "DPSF-", "Netty-", "Client-ResponseProcessor"));
        frameworkThread.findOrCreateExtensionDetail("ActiveThread").setValue(bean.getThreadCount());
        frameworkThread.findOrCreateExtensionDetail("StartedThread").setValue(bean.getTotalStartedThreadCount());

        statusInfo.addExtension(frameworkThread);
    }

    private String getThreadDump(ThreadInfo[] threads) {
        StringBuilder sb = new StringBuilder(32768);
        int index = 1;

        TreeMap<String, ThreadInfo> sortedThreads = new TreeMap<>();

        for (ThreadInfo thread : threads) {
            sortedThreads.put(thread.getThreadName(), thread);
        }

        for (ThreadInfo thread : sortedThreads.values()) {
            sb.append(index++).append(": ").append(thread);
        }
        return sb.toString();
    }

    private int countThreadsBySubstring(ThreadInfo[] threads, String... substrings) {
        int count = 0;

        for (ThreadInfo thread : threads) {
            for (String str : substrings) {
                if (thread.getThreadName().contains(str)) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countThreadsByPrefix(ThreadInfo[] threads, String... prefixes) {
        int count = 0;

        for (ThreadInfo thread : threads) {
            for (String prefix : prefixes) {
                if (thread.getThreadName().startsWith(prefix)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isInstanceOfInterface(Class<?> clazz, String interfaceName) {
        if (clazz == Object.class) {
            return false;
        } else if (clazz.getName().equals(interfaceName)) {
            return true;
        }

        Class<?>[] interfaceclasses = clazz.getInterfaces();

        for (Class<?> interfaceclass : interfaceclasses) {
            if (isInstanceOfInterface(interfaceclass, interfaceName)) {
                return true;
            }
        }
        return isInstanceOfInterface(clazz.getSuperclass(), interfaceName);
    }

}
