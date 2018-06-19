/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.unidal.helper.Splitters;

import com.mouse.configuration.NetworkInterfaceManager;

/**
 * 消息ID工厂
 * @author kris
 * @version $Id: MessageIdFactory.java, v 0.1 2018年6月1日 下午2:48:04 kris Exp $
 */
public class MessageIdFactory {

    private volatile long          timestamp = getTimestamp();

    private volatile AtomicInteger index;

    private String                 domain;

    private String                 ipAddress;

    private volatile boolean       isInitialized;

    private MappedByteBuffer       byteBuffer;

    private RandomAccessFile       markFile;

    private static final long      HOUR      = 3600 * 1000L;

    private BlockingQueue<String>  reusedIds = new LinkedBlockingQueue<>(100000);

    public void initialize(String domain) throws IOException {
        if (!isInitialized) {
            this.domain = domain;

            if (ipAddress == null) {
                String ip = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
                List<String> items = Splitters.by(".").noEmptyItem().split(ip);
                byte[] bytes = new byte[4];

                for (int i = 0; i < 4; i++) {
                    bytes[i] = (byte) Integer.parseInt(items.get(i));
                }

                StringBuilder sb = new StringBuilder(bytes.length / 2);

                for (byte b : bytes) {
                    sb.append(Integer.toHexString((b >> 4) & 0x0F));
                    sb.append(Integer.toHexString(b & 0x0F));
                }

                ipAddress = sb.toString();
            }

            File mark = createMarkFile(domain);

            markFile = new RandomAccessFile(mark, "rw");
            byteBuffer = markFile.getChannel().map(MapMode.READ_WRITE, 0, 20);

            if (byteBuffer.limit() > 0) {
                int index = byteBuffer.getInt();
                long lastTimestamp = byteBuffer.getLong();

                if (lastTimestamp == timestamp) {
                    this.index = new AtomicInteger(index + 10000);
                } else {
                    this.index = new AtomicInteger(0);
                }
            }
            isInitialized = true;
        }
        saveMark();
    }

    public String getNextId() {
        String id = reusedIds.poll();

        if (id != null) {
            return id;
        } else {
            long timestamp = getTimestamp();

            if (timestamp != this.timestamp) {
                index = new AtomicInteger(0);
                this.timestamp = timestamp;
            }

            int index = this.index.getAndIncrement();

            StringBuilder sb = new StringBuilder(domain.length() + 32);

            sb.append(domain);
            sb.append('-');
            sb.append(ipAddress);
            sb.append('-');
            sb.append(timestamp);
            sb.append('-');
            sb.append(index);

            return sb.toString();
        }
    }

    public void reuse(String id) {
        reusedIds.offer(id);
    }

    protected void resetIndex() {
        index.set(0);
    }

    public void close() {
        try {
            markFile.close();
        } catch (IOException e) {
        }
    }

    private long getTimestamp() {
        long timestamp = MilliSecondTimer.currentTimeMillis();

        return timestamp / HOUR;
    }

    private File createMarkFile(String domain) {

        File mark = new File("/data/appdatas/mouse/", "mouse-" + domain + ".mark");

        if (!mark.exists()) {
            boolean success = true;
            try {
                success = mark.createNewFile();
            } catch (Exception e) {
                success = false;
            }
            if (!success) {
                mark = createTempFile(domain);
            }
        } else if (!mark.canWrite()) {
            mark = createTempFile(domain);
        }

        return mark;

    }

    private File createTempFile(String domain) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        File mark = new File(tmpDir, "moust-" + domain + ".mark");
        return mark;
    }

    public void saveMark() {
        if (isInitialized) {
            try {
                byteBuffer.rewind();
                byteBuffer.putInt(index.get());
                byteBuffer.putLong(timestamp);
            } catch (Exception e) {
            }
        }
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}
