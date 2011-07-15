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

import org.apache.servicemix.executors.Executor;
import org.apache.servicemix.executors.ExecutorAwareRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The default Executor implementation which uses a
 * ThreadPoolExecutor underneath.
 *
 * @author <a href="mailto:gnodet [at] gmail.com">Guillaume Nodet</a>
 */
public class ExecutorImpl implements Executor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorImpl.class);

    private final ThreadPoolExecutor threadPool;

    private ExecutorFactoryImpl executorFactory;

    private final ExecutorConfig config;

    public ExecutorImpl(ExecutorFactoryImpl executorFactory, ThreadPoolExecutor threadPool, ExecutorConfig config) {
        this.executorFactory = executorFactory;
        this.threadPool = threadPool;
        this.config = config;
    }

    public void execute(Runnable command) {
        if (config.isBypassIfSynchronous() && command instanceof ExecutorAwareRunnable) {
            if (((ExecutorAwareRunnable) command).shouldRunSynchronously()) {
                wrap(command).run();
                return;
            }
        }
        threadPool.execute(wrap(command));
    }

    private Runnable wrap(final Runnable wrapped) {
        return new Runnable() {
            public void run() {
                try {
                    wrapped.run();
                } catch (Throwable t) {
                    LOGGER.error("Exception caught while executing submitted job", t);
                    throw new RuntimeException("Exception caught while executing in submitted job", t);
                }
            }
        };
    }

    public void shutdown() {
        try {
            this.executorFactory.unregisterMBean(this);
        } catch (Exception ex) {
            // ignored
        }
        threadPool.shutdown();
        if (!threadPool.isTerminated() && config.getShutdownDelay() > 0) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        if (!threadPool.awaitTermination(config.getShutdownDelay(), TimeUnit.MILLISECONDS)) {
                            threadPool.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                }
            }).start();
        }
    }

    public int capacity() {
        BlockingQueue queue = threadPool.getQueue();
        return queue.remainingCapacity() + queue.size();
    }

    public int size() {
        BlockingQueue queue = threadPool.getQueue();
        return queue.size();
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return this.threadPool;
    }

    /**
     * The configuration used for creating this executor instance
     *
     * @return the configuration object
     */
    protected ExecutorConfig getConfig() {
        return config;
    }
}
