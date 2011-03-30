/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.executors.impl;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.apache.servicemix.executors.ExecutorFactory.*;
import static org.junit.Assert.*;

/**
 * Test cases for {@link ExecutorConfig}
 */
public class ExecutorConfigTest {

    @Test
    public void testCreateMethod() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(CORE_POOL_SIZE, 5);
        data.put(MAXIMUM_POOL_SIZE, 10);
        data.put(QUEUE_SIZE, 50);
        data.put(KEEP_ALIVE_TIME, 3000l);
        data.put(SHUTDOWN_DELAY, 9000l);
        data.put(THREAD_PRIORITY, 9);
        data.put(ALLOW_CORE_THREADS_TIMEOUT, true);
        data.put(BYPASS_IF_SYNCHRONOUS, false);
        data.put(THREAD_DAEMON, true);

        ExecutorConfig config = ExecutorConfig.create(data, null);
        assertEquals(new Integer(5), config.getCorePoolSize());
        assertEquals(new Integer(10), config.getMaximumPoolSize());
        assertEquals(new Integer(50), config.getQueueSize());
        assertEquals(new Long(3000), config.getKeepAliveTime());
        assertEquals(new Long(9000), config.getShutdownDelay());
        assertEquals(new Integer(9), config.getThreadPriority());
        assertEquals(true, config.isAllowCoreThreadTimeOut());
        assertEquals(false, config.isBypassIfSynchronous());
        assertEquals(true, config.isThreadDaemon());
    }

    @Test
    public void testCreateMethodWithStringValues() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(CORE_POOL_SIZE, "5");
        data.put(MAXIMUM_POOL_SIZE, "10");
        data.put(QUEUE_SIZE, "50");
        data.put(KEEP_ALIVE_TIME, "3000");
        data.put(SHUTDOWN_DELAY, "9000");
        data.put(THREAD_PRIORITY, "9");
        data.put(ALLOW_CORE_THREADS_TIMEOUT, "true");
        data.put(BYPASS_IF_SYNCHRONOUS, "false");
        data.put(THREAD_DAEMON, "true");

        ExecutorConfig config = ExecutorConfig.create(data, null);
        assertEquals(new Integer(5), config.getCorePoolSize());
        assertEquals(new Integer(10), config.getMaximumPoolSize());
        assertEquals(new Integer(50), config.getQueueSize());
        assertEquals(new Long(3000), config.getKeepAliveTime());
        assertEquals(new Long(9000), config.getShutdownDelay());
        assertEquals(new Integer(9), config.getThreadPriority());
        assertEquals(true, config.isAllowCoreThreadTimeOut());
        assertEquals(false, config.isBypassIfSynchronous());
        assertEquals(true, config.isThreadDaemon());
    }

    @Test
    public void testCreateMethodIllegalValuesIgnored() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(CORE_POOL_SIZE, "SOME");
        data.put(MAXIMUM_POOL_SIZE, "ILLEGAL");
        data.put(QUEUE_SIZE, "VALUE");
        data.put(KEEP_ALIVE_TIME, "3000");
        data.put(SHUTDOWN_DELAY, "9000");
        data.put(THREAD_PRIORITY, "9");
        data.put(ALLOW_CORE_THREADS_TIMEOUT, "true");
        data.put(BYPASS_IF_SYNCHRONOUS, "false");
        data.put(THREAD_DAEMON, "true");

        ExecutorConfig config = ExecutorConfig.create(data, new ExecutorConfig());
        assertEquals(ExecutorConfig.DEFAULT_CORE_POOL_SIZE, config.getCorePoolSize());
        assertEquals(ExecutorConfig.DEFAULT_MAXIMUM_POOL_SIZE, config.getMaximumPoolSize());
        assertEquals(ExecutorConfig.DEFAULT_QUEUE_SIZE, config.getQueueSize());
        assertEquals(new Long(3000), config.getKeepAliveTime());
        assertEquals(new Long(9000), config.getShutdownDelay());
        assertEquals(new Integer(9), config.getThreadPriority());
        assertEquals(true, config.isAllowCoreThreadTimeOut());
        assertEquals(false, config.isBypassIfSynchronous());
        assertEquals(true, config.isThreadDaemon());
    }
}
