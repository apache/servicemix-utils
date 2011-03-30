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

import org.apache.servicemix.executors.ExecutorFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test cases for {@link ExecutorFactoryImpl}
 */
public class ExecutorFactoryImplTest {

    private ExecutorConfig defaultConfig;
    private ExecutorFactoryImpl factory;

    @Before
    public void setupExecutorFactory() {
        factory = new ExecutorFactoryImpl();

        defaultConfig = new ExecutorConfig();
        defaultConfig.setCorePoolSize(1);
        defaultConfig.setMaximumPoolSize(2);
        defaultConfig.setQueueSize(3);
        defaultConfig.setAllowCoreThreadTimeOut(true);
        factory.setDefaultConfig(defaultConfig);
    }

    @Test
    public void testAdditionalConfig() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ExecutorFactory.MAXIMUM_POOL_SIZE, 10);
        ExecutorImpl impl = (ExecutorImpl) factory.createExecutor("test", config);
        assertEquals("Core pool size is the default",
                     defaultConfig.getCorePoolSize(), impl.getConfig().getCorePoolSize());
        assertEquals("Maximum pool size has been altered",
                     new Integer(10), impl.getConfig().getMaximumPoolSize());

    }

}
