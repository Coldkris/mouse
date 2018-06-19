/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.io;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.mouse.message.spi.MessageQueue;
import com.mouse.message.spi.MessageTree;

/**
 * 默认消息队列实现类
 * @author kris
 * @version $Id: DefaultMessageQueue.java, v 0.1 2018年6月19日 下午8:48:00 kris Exp $
 */
public class DefaultMessageQueue implements MessageQueue {
    private BlockingQueue<MessageTree> queue;

    private Random                     rand = new Random();

    public DefaultMessageQueue(int size) {
        queue = new LinkedBlockingQueue<>(size);
    }

    @Override
    public boolean offer(MessageTree tree) {
        return queue.offer(tree);
    }

    @Override
    public boolean offer(MessageTree tree, double sampleRatio) {
        if (tree.isSample() && sampleRatio < 1.0) {
            if (sampleRatio > 0) {
                if (rand.nextInt(100) < 100 * sampleRatio) {
                    return offer(tree);
                }
            }
            return false;
        } else {
            return offer(tree);
        }
    }

    @Override
    public MessageTree peek() {
        return queue.peek();
    }

    @Override
    public MessageTree poll() {
        try {
            return queue.poll(5, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return queue.size();
    }

}
