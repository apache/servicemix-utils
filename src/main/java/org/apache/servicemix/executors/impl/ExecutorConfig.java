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

/**
 * This bean holds configuration attributes for a given Executor.
 * 
 * @author <a href="mailto:gnodet [at] gmail.com">Guillaume Nodet</a>
 */
public class ExecutorConfig {

    private int corePoolSize = 4;

    private int maximumPoolSize = -1;

    private long keepAliveTime = 60000;

    private boolean threadDaemon;

    private int threadPriority = Thread.NORM_PRIORITY;

    private int queueSize = 1024;

    private long shutdownDelay = 1000;

    private boolean allowCoreThreadsTimeout = true;

    /**
     * @return the corePoolSize
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * @param corePoolSize
     *            the corePoolSize to set
     */
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * @return the keepAlive
     */
    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * @param keepAlive
     *            the keepAlive to set
     */
    public void setKeepAliveTime(long keepAlive) {
        this.keepAliveTime = keepAlive;
    }

    /**
     * @return the maximumPoolSize
     */
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    /**
     * @param maximumPoolSize
     *            the maximumPoolSize to set
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    /**
     * @return the queueSize
     */
    public int getQueueSize() {
        return queueSize;
    }

    /**
     * @param queueSize
     *            the queueSize to set
     */
    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    /**
     * @return the threadDaemon
     */
    public boolean isThreadDaemon() {
        return threadDaemon;
    }

    /**
     * @param threadDaemon
     *            the threadDaemon to set
     */
    public void setThreadDaemon(boolean threadDaemon) {
        this.threadDaemon = threadDaemon;
    }

    /**
     * @return the threadPriority
     */
    public int getThreadPriority() {
        return threadPriority;
    }

    /**
     * @param threadPriority
     *            the threadPriority to set
     */
    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    /**
     * @return the shutdownDelay
     */
    public long getShutdownDelay() {
        return shutdownDelay;
    }

    /**
     * @param shutdownDelay
     *            the shutdownDelay to set
     */
    public void setShutdownDelay(long shutdownDelay) {
        this.shutdownDelay = shutdownDelay;
    }

    /**
     * @return the allowCoreThreadsTimeout
     */
    public boolean isAllowCoreThreadsTimeout() {
        return allowCoreThreadsTimeout;
    }

    /**
     * @param allowCoreThreadsTimeout
     *            the allowCoreThreadsTimeout to set
     */
    public void setAllowCoreThreadsTimeout(boolean allowCoreThreadsTimeout) {
        this.allowCoreThreadsTimeout = allowCoreThreadsTimeout;
    }

}
