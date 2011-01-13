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

package org.apache.servicemix.executors;

import junit.framework.TestCase;
import org.apache.servicemix.executors.impl.ExecutorConfig;
import org.apache.servicemix.executors.impl.ExecutorFactoryImpl;
import org.apache.servicemix.executors.impl.ExecutorImpl;
import org.apache.servicemix.executors.impl.ManagedExecutor;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

public class RejectedExecutionTest extends TestCase {

    private ExecutorFactoryImpl factory;
    private ExecutorImpl executor;
    private ExecutorConfig config;
    private ManagedExecutor managedExecutor;

    @Override
    protected void tearDown() throws Exception {
        managedExecutor.getInternalExecutor().getThreadPoolExecutor().shutdown();
    }

    @Override
    protected void setUp() throws Exception {
        config = new ExecutorConfig(true, null);
        config.setCorePoolSize(1);
        config.setMaximumPoolSize(1);
        config.setQueueSize(2);

        factory = new ExecutorFactoryImpl();
        factory.setDefaultConfig(config);
        executor = (ExecutorImpl) factory.createExecutor("myExecutor");
        executor.getThreadPoolExecutor().setRejectedExecutionHandler(new AbortPolicy());
        managedExecutor = new ManagedExecutor("myExecutor", executor, config);
    }

    public void testExecutionRejectionIncrement() {
        int counter = 0;
        for (int i = 0; i < 10; i++) {
            final int x = i;
            try {
                managedExecutor.execute(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException ex) {
                            // ignore
                        }
                    }
                });
            } catch (RejectedExecutionException ex) {
                counter++;
            }
        }

        managedExecutor.getInternalExecutor().getThreadPoolExecutor().shutdownNow();

        assertEquals("The number of rejected execution exceptions does not fit.", counter, managedExecutor.getNumberOfRejectedExecutions());
    }

    public void testExecutionRejectionReset() {
        int counter = 0;
        for (int i = 0; i < 10; i++) {
            final int x = i;
            try {
                managedExecutor.execute(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException ex) {
                            // ignore
                        }
                    }
                });
            } catch (RejectedExecutionException ex) {
                counter++;
            }
        }

        managedExecutor.getInternalExecutor().getThreadPoolExecutor().shutdownNow();

        assertEquals("The number of rejected execution exceptions does not fit.", counter, managedExecutor.getNumberOfRejectedExecutions());

        managedExecutor.reset();

        assertEquals("The number of rejected execution exceptions after reset does not fit zero.", 0, managedExecutor.getNumberOfRejectedExecutions());
    }
}
