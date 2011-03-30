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

import org.apache.servicemix.converter.Converters;
import org.apache.servicemix.executors.ExecutorFactory;

import java.util.Map;

import static org.apache.servicemix.executors.ExecutorFactory.*;

/**
 * This bean holds configuration attributes for a given Executor.
 * 
 * @author <a href="mailto:gnodet [at] gmail.com">Guillaume Nodet</a>
 */
public class ExecutorConfig {

    public static final Integer DEFAULT_CORE_POOL_SIZE = 4;
    public static final Integer DEFAULT_MAXIMUM_POOL_SIZE = -1;
    public static final Long DEFAULT_KEEP_ALIVE_TIME = 60000l;
    public static final Boolean DEFAULT_THREAD_DAEMON = false;
    public static final Integer DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY;
    public static final Integer DEFAULT_QUEUE_SIZE = 1024;
    public static final Long DEFAULT_SHUTDOWN_DELAY = 1000l;
    public static final Boolean DEFAULT_ALLOW_CORE_THREAD_TIMEOUT = true;
    public static final Boolean DEFAULT_BYPASS_IF_SYNCHRONOUS = false;

    private ExecutorConfig parent;

    private Integer corePoolSize;

    private Integer maximumPoolSize;

    private Long keepAliveTime;

    private Boolean threadDaemon;

    private Integer threadPriority = Thread.NORM_PRIORITY;

    private Integer queueSize;

    private Long shutdownDelay;

    private Boolean allowCoreThreadTimeOut;

    private Boolean bypassIfSynchronous;

    /**
     * default constructor needed by spring beans
     */
    public ExecutorConfig() {
        this(true, null);
    }

    /**
     * creates a new executor config using the given parent
     *
     * @param parent the parent config
     */
    public ExecutorConfig(boolean isDefaultConfig, ExecutorConfig parent) {
        this.parent = parent;
        // if this is the default config we don't want undefined values
        if (isDefaultConfig) {
            setQueueSize(DEFAULT_QUEUE_SIZE);
            setShutdownDelay(DEFAULT_SHUTDOWN_DELAY);
            setThreadDaemon(DEFAULT_THREAD_DAEMON);
            setThreadPriority(DEFAULT_THREAD_PRIORITY);
            setAllowCoreThreadTimeOut(DEFAULT_ALLOW_CORE_THREAD_TIMEOUT);
            setBypassIfSynchronous(DEFAULT_BYPASS_IF_SYNCHRONOUS);
            setCorePoolSize(DEFAULT_CORE_POOL_SIZE);
            setKeepAliveTime(DEFAULT_KEEP_ALIVE_TIME);
            setMaximumPoolSize(DEFAULT_MAXIMUM_POOL_SIZE);
        }
    }

    /**
     * @return the corePoolSize
     */
    public Integer getCorePoolSize() {
        return getParent() != null && corePoolSize == null ? getParent().getCorePoolSize() : corePoolSize;
    }

    /**
     * @param corePoolSize
     *            the corePoolSize to set
     */
    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * @return the keepAlive
     */
    public Long getKeepAliveTime() {
        return getParent() != null && keepAliveTime == null ? getParent().getKeepAliveTime() : keepAliveTime;
    }

    /**
     * @param keepAlive
     *            the keepAlive to set
     */
    public void setKeepAliveTime(Long keepAlive) {
        this.keepAliveTime = keepAlive;
    }

    /**
     * @return the maximumPoolSize
     */
    public Integer getMaximumPoolSize() {
        return getParent() != null && maximumPoolSize == null ? getParent().getMaximumPoolSize() : maximumPoolSize;
    }

    /**
     * @param maximumPoolSize
     *            the maximumPoolSize to set
     */
    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    /**
     * @return the queueSize
     */
    public Integer getQueueSize() {
        return getParent() != null && queueSize == null ? getParent().getQueueSize() : queueSize;
    }

    /**
     * @param queueSize
     *            the queueSize to set
     */
    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    /**
     * @return the threadDaemon
     */
    public Boolean isThreadDaemon() {
        return getParent() != null && threadDaemon == null ? getParent().isThreadDaemon() : threadDaemon;
    }

    /**
     * @param threadDaemon
     *            the threadDaemon to set
     */
    public void setThreadDaemon(Boolean threadDaemon) {
        this.threadDaemon = threadDaemon;
    }

    /**
     * @return the threadPriority
     */
    public Integer getThreadPriority() {
        return getParent() != null && threadPriority == null ? getParent().getThreadPriority() : threadPriority;
    }

    /**
     * @param threadPriority
     *            the threadPriority to set
     */
    public void setThreadPriority(Integer threadPriority) {
        this.threadPriority = threadPriority;
    }

    /**
     * @return the shutdownDelay
     */
    public Long getShutdownDelay() {
        return getParent() != null && shutdownDelay == null ? getParent().getShutdownDelay() : shutdownDelay;
    }

    /**
     * @param shutdownDelay
     *            the shutdownDelay to set
     */
    public void setShutdownDelay(Long shutdownDelay) {
        this.shutdownDelay = shutdownDelay;
    }

    /**
     * @return the allowCoreThreadTimeOut
     */
    public Boolean isAllowCoreThreadTimeOut() {
        return getParent() != null && allowCoreThreadTimeOut == null ? getParent().isAllowCoreThreadTimeOut() : allowCoreThreadTimeOut;
    }

    /**
     * @param allowCoreThreadTimeOut
     *            the allowCoreThreadTimeOut to set
     */
    public void setAllowCoreThreadTimeOut(Boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    /**
     * @return if synchronous tasks should bypass the executor
     */
    public Boolean isBypassIfSynchronous() {
        return getParent() != null && bypassIfSynchronous == null ? getParent().isBypassIfSynchronous() : bypassIfSynchronous;
    }

    /**
     * @param bypassIfSynchronous if synchronous tasks should bypass the executor
     */
    public void setBypassIfSynchronous(Boolean bypassIfSynchronous) {
        this.bypassIfSynchronous = bypassIfSynchronous;
    }

    public ExecutorConfig getParent() {
        return parent;
    }

    public void setParent(ExecutorConfig parent) {
        this.parent = parent;
    }

    /**
     * Create an ExecutorConfig instance based on the information in the options map.
     *
     * @param options the map of executor configuration options that will get set on the ExecutorConfig instance
     * @param parent (optionally) the parent ExecutorConfig instance
     * @return the configured instance
     */
    public static ExecutorConfig create(Map<String, Object> options, ExecutorConfig parent) {
        Converters converter = new Converters();

        ExecutorConfig result = new ExecutorConfig(false, parent);
        result.setCorePoolSize(converter.as(options.get(CORE_POOL_SIZE), Integer.class));
        result.setMaximumPoolSize(converter.as(options.get(MAXIMUM_POOL_SIZE), Integer.class));
        result.setQueueSize(converter.as(options.get(QUEUE_SIZE), Integer.class));
        result.setThreadPriority(converter.as(options.get(THREAD_PRIORITY), Integer.class));

        result.setKeepAliveTime(converter.as(options.get(KEEP_ALIVE_TIME), Long.class));
        result.setShutdownDelay(converter.as(options.get(SHUTDOWN_DELAY), Long.class));

        result.setAllowCoreThreadTimeOut(converter.as(options.get(ALLOW_CORE_THREADS_TIMEOUT), Boolean.class));
        result.setBypassIfSynchronous(converter.as(options.get(BYPASS_IF_SYNCHRONOUS), Boolean.class));
        result.setThreadDaemon(converter.as(options.get(THREAD_DAEMON), Boolean.class));

        return result;
    }
}
