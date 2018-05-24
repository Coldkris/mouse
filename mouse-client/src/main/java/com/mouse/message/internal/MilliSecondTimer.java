/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.util.concurrent.locks.LockSupport;

/**
 * 该计时器提供精确到毫秒的系统时间。
 * @author kris
 * @version $Id: MilliSecondTimer.java, v 0.1 2018年5月24日 上午10:19:57 kris Exp $
 */
public class MilliSecondTimer {

    private static long    mBaseTime;

    private static long    mStartNanoTime;

    private static boolean mIsWindows = false;

    public static long currentTimeMillis() {

        if (mIsWindows) {
            if (mBaseTime == 0) {
                initialize();
            }

            long elipsed = (long) ((System.nanoTime() - mStartNanoTime) / 1e6);
            return mBaseTime + elipsed;
        } else {
            return System.currentTimeMillis();
        }

    }

    public static void initialize() {

        String os = System.getProperty("os.name");

        if (os.startsWith("Windows")) {
            mIsWindows = true;
            mBaseTime = System.currentTimeMillis();

            while (true) {
                LockSupport.parkNanos(100000);// 0.1ms
                long millis = System.currentTimeMillis();
                if (millis != mBaseTime) {
                    mBaseTime = millis;
                    mStartNanoTime = System.nanoTime();
                    break;
                }
            }
        } else {
            mBaseTime = System.currentTimeMillis();
            mStartNanoTime = System.nanoTime();
        }

    }
}
