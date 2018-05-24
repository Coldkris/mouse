/**
 * CAT��С����
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.util.concurrent.locks.LockSupport;

/**
 * �ü�ʱ���ṩ��ȷ�������ϵͳʱ�䡣
 * @author kris
 * @version $Id: MilliSecondTimer.java, v 0.1 2018��5��24�� ����10:19:57 kris Exp $
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
