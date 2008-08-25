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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.servicemix.executors.Executor;
import org.apache.servicemix.executors.ExecutorFactory;

/**
 * Default implementation of the ExecutorFactory.
 * 
 * Configuration can be done hierachically.
 * When an executor is created with an id of <code>foo.bar</code>,
 * the factory will look for a configuration in the following
 * way:
 * <ul>
 *    <li>configs.get("foo.bar")</li>
 *    <li>configs.get("foo")</li>
 *    <li>defaultConfig</li>
 * </ul>
 * 
 * @author <a href="mailto:gnodet [at] gmail.com">Guillaume Nodet</a>
 */
public class ExecutorFactoryImpl implements ExecutorFactory {

    private ExecutorConfig defaultConfig = new ExecutorConfig();

    private Map<String, ExecutorConfig> configs = new HashMap<String, ExecutorConfig>();

    public Executor createExecutor(String id) {
        ExecutorConfig config = getConfig(id);
        return new ExecutorImpl(createService(id, config), config.getShutdownDelay());
    }

    protected ExecutorConfig getConfig(String id) {
        ExecutorConfig config = null;
        if (configs != null) {
            config = configs.get(id);
            while (config == null && id.indexOf('.') > 0) {
                id = id.substring(0, id.lastIndexOf('.'));
                config = configs.get(id);
            }
        }
        if (config == null) {
            config = defaultConfig;
        }
        return config;
    }

    protected ThreadPoolExecutor createService(String id, ExecutorConfig config) {
        if (config.getQueueSize() != 0 && config.getCorePoolSize() == 0) {
            throw new IllegalArgumentException("CorePoolSize must be > 0 when using a capacity queue");
        }
        BlockingQueue<Runnable> queue;
        if (config.getQueueSize() == 0) {
            queue = new SynchronousQueue<Runnable>();
        } else if (config.getQueueSize() < 0 || config.getQueueSize() == Integer.MAX_VALUE) {
            queue = new LinkedBlockingQueue<Runnable>();
        } else {
            queue = new ArrayBlockingQueue<Runnable>(config.getQueueSize());
        }
        ThreadFactory factory = new DefaultThreadFactory(id, config.isThreadDaemon(), config.getThreadPriority());
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        ThreadPoolExecutor service = new ThreadPoolExecutor(config.getCorePoolSize(),
                config.getMaximumPoolSize() < 0 ? Integer.MAX_VALUE : config.getMaximumPoolSize(), config
                        .getKeepAliveTime(), TimeUnit.MILLISECONDS, queue, factory, handler);
        if (config.isAllowCoreThreadsTimeout()) {
            try {
                Method mth = service.getClass().getMethod("allowCoreThreadTimeOut", new Class[] {boolean.class });
                mth.invoke(service, new Object[] {Boolean.TRUE });
            } catch (Throwable t) {
                // Do nothing
            }
        }
        return service;
    }

    /**
     * The default thread factory
     */
    static class DefaultThreadFactory implements ThreadFactory {
        final ThreadGroup group;

        final AtomicInteger threadNumber = new AtomicInteger(1);

        final String namePrefix;

        final boolean daemon;

        final int priority;

        DefaultThreadFactory(String id, boolean daemon, int priority) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + id + "-thread-";
            this.daemon = daemon;
            this.priority = priority;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon() != daemon) {
                t.setDaemon(daemon);
            }
            if (t.getPriority() != priority) {
                t.setPriority(priority);
            }
            return t;
        }
    }

    /**
     * @return the configs
     */
    public Map<String, ExecutorConfig> getConfigs() {
        return configs;
    }

    /**
     * @param configs the configs to set
     */
    public void setConfigs(Map<String, ExecutorConfig> configs) {
        this.configs = configs;
    }

    /**
     * @return the defaultConfig
     */
    public ExecutorConfig getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * @param defaultConfig the defaultConfig to set
     */
    public void setDefaultConfig(ExecutorConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

}
