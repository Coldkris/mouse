/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.configuration.client.entity;

import java.util.Date;

import com.mouse.status.StatusInfoCollector;
import com.mouse.status.model.entity.DiskInfo;
import com.mouse.status.model.entity.MemoryInfo;
import com.mouse.status.model.entity.MessageInfo;
import com.mouse.status.model.entity.OsInfo;
import com.mouse.status.model.entity.RuntimeInfo;
import com.mouse.status.model.entity.ThreadsInfo;

/**
 * 
 * @author kris
 * @version $Id: StatusInfo.java, v 0.1 2018年5月25日 下午7:51:38 kris Exp $
 */
public class StatusInfo {

    public Extension findOrCreateExtension(String string) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addExtension(Extension systemExtension) {
        // TODO Auto-generated method stub

    }

    public void setTimestamp(Date date) {
        // TODO Auto-generated method stub

    }

    public void setOs(OsInfo osInfo) {
        // TODO Auto-generated method stub

    }

    public void setDisk(DiskInfo diskInfo) {
        // TODO Auto-generated method stub

    }

    public void setRuntime(RuntimeInfo runtimeInfo) {
        // TODO Auto-generated method stub

    }

    public void setMemory(MemoryInfo memoryInfo) {
        // TODO Auto-generated method stub

    }

    public void setMessage(MessageInfo messageInfo) {
        // TODO Auto-generated method stub

    }

    public void setThread(ThreadsInfo threadsInfo) {
        // TODO Auto-generated method stub

    }

    public void accept(StatusInfoCollector setDumpLocked) {
        // TODO Auto-generated method stub

    }

}
