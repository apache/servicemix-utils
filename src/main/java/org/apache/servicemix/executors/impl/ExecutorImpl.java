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

    private final ThreadPoolExecutor threadPool;

    private final long shutdownDelay;

    private final boolean bypassIfSynchronous;

    private ExecutorFactoryImpl executorFactory;

    public ExecutorImpl(ExecutorFactoryImpl executorFactory, ThreadPoolExecutor threadPool, long shutdownDelay, boolean bypassIfSynchronous) {
        this.executorFactory = executorFactory;
        this.threadPool = threadPool;
        this.shutdownDelay = shutdownDelay;
        this.bypassIfSynchronous = bypassIfSynchronous;
    }

    public void execute(Runnable command) {
        if (bypassIfSynchronous && command instanceof ExecutorAwareRunnable) {
            if (((ExecutorAwareRunnable) command).shouldRunSynchronously()) {
                command.run();
                return;
            }
        }
        threadPool.execute(command);
    }

    public void shutdown() {
        try {
            this.executorFactory.unregisterMBean(this);
        } catch (Exception ex) {
            // ignored
        }
        threadPool.shutdown();
        if (!threadPool.isTerminated() && shutdownDelay > 0) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        if (!threadPool.awaitTermination(shutdownDelay, TimeUnit.MILLISECONDS)) {
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
}
