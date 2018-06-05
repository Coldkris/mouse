/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse;

import org.junit.Assert;
import org.junit.Test;

import com.mouse.message.MessageProducer;
import com.mouse.message.Transaction;

/**
 * 
 * @author kris
 * @version $Id: MouseEnvironmentTest.java, v 0.1 2018年5月31日 下午2:38:33 kris Exp $
 */
public class MouseEnvironmentTest {

    @Test
    public void testWithoutInitialize() throws InterruptedException {
        MessageProducer mouse = Mouse.getProducer();
        Transaction transaction = mouse.newTransaction("TestType", "TestName");

        transaction.addData("data here");
        transaction.setStatus("TestStatus");
        transaction.complete();

        Thread.sleep(100);
        Assert.assertEquals(true, Mouse.isInitialized());
        Mouse.destroy();
    }

}
