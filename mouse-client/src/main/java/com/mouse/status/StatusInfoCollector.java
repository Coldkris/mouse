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
import java.util.List;

import com.mouse.message.configuration.client.entity.Extension;
import com.mouse.message.configuration.client.entity.StatusInfo;
import com.mouse.message.spi.MessageStatistics;
import com.mouse.status.model.entity.DiskInfo;
import com.mouse.status.model.entity.DiskVolumeInfo;
import com.mouse.status.model.entity.GcInfo;
import com.mouse.status.model.entity.MemoryInfo;
import com.mouse.status.model.entity.MessageInfo;
import com.mouse.status.model.entity.OsInfo;
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
        Extension catExtension = statusInfo.findOrCreateExtension("CatUsage");

        if (mStatistics != null) {
            catExtension.findOrCreateExtensionDetail("Produced").setValue(mStatistics.getProdeced());
            catExtension.findOrCreateExtensionDetail("Overflowed").setValue(mStatistics.getOverflowed());
            catExtension.findOrCreateExtensionDetail("Bytes").setValue(mStatistics.getBytes());
        }
    }

    @Override
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

        }
    }

    private boolean isInstanceOfInterface(Class<? extends OperatingSystemMXBean> class1, String string) {
        // TODO Auto-generated method stub
        return false;
    }

}
