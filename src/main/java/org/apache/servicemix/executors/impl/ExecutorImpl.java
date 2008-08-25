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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.servicemix.executors.Executor;

/**
 * The default Executor implementation which uses a 
 * ThreadPoolExecutor underneath.
 * 
 * @author <a href="mailto:gnodet [at] gmail.com">Guillaume Nodet</a>
 */
public class ExecutorImpl implements Executor {

    private final ThreadPoolExecutor threadPool;

    private final long shutdownDelay;

    public ExecutorImpl(ThreadPoolExecutor threadPool, long shutdownDelay) {
        this.threadPool = threadPool;
        this.shutdownDelay = shutdownDelay;
    }

    public void execute(Runnable command) {
        threadPool.execute(command);
    }

    public void shutdown() {
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

}
