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
import org.apache.servicemix.executors.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ObjectName;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default implementation of the ExecutorFactory.
 * <p/>
 * Configuration can be done hierachically.
 * When an executor is created with an id of <code>foo.bar</code>,
 * the factory will look for a configuration in the following
 * way:
 * <ul>
 * <li>configs.get("foo.bar")</li>
 * <li>configs.get("foo")</li>
 * <li>defaultConfig</li>
 * </ul>
 *
 * @author <a href="mailto:gnodet [at] gmail.com">Guillaume Nodet</a>
 */
public class ExecutorFactoryImpl implements ExecutorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutorFactoryImpl.class);

    private static final String OBJECT_NAME_PREFIX = "org.apache.servicemix:ContainerName=ServiceMix,Name=Executors,Type=";
    private static final String OBJECT_NAME_POSTFIX = ",SubType=";

    private ExecutorConfig defaultConfig = new ExecutorConfig(true, null);

    private javax.management.MBeanServer mbeanServer;
    private org.fusesource.commons.management.ManagementStrategy managementStrategy;

    private Map<String, ExecutorConfig> configs         = new HashMap<String, ExecutorConfig>();
    private Map<Executor, ObjectName>   executorNames   = new HashMap<Executor, ObjectName>();

    public Executor createExecutor(String id) {
        return doCreateExecutor(id, getConfig(id));
    }

    public Executor createExecutor(String id, Map<String, Object> configuration) {
        return doCreateExecutor(id, ExecutorConfig.create(configuration, getConfig(id)));
    }

    public Executor createDaemonExecutor(String id) {
        ExecutorConfig config = getConfig(id);
        config.setThreadDaemon(true);
        return doCreateExecutor(id, config);
    }

    /**
     * Create an executor with the given id and configuration
     */
    private Executor doCreateExecutor(String id, ExecutorConfig config) {
        ExecutorImpl executor = new ExecutorImpl(this, createService(id, config), config);
        try {
            registerMBean(id, executor, config);
        } catch (Exception ex) {
            LOG.error("Unable to register MBean for the executor with id " + id, ex);
        }
        return executor;
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

        RejectedExecutionHandler handler = (RejectedExecutionHandler) FactoryFinder.find(RejectedExecutionHandler.class.getName(),
                ThreadPoolExecutor.CallerRunsPolicy.class.getName());

        ThreadPoolExecutor service = new ThreadPoolExecutor(config.getCorePoolSize(),
                config.getMaximumPoolSize() < 0 ? Integer.MAX_VALUE : config.getMaximumPoolSize(), config
                .getKeepAliveTime(), TimeUnit.MILLISECONDS, queue, factory, handler);
        if (config.isAllowCoreThreadTimeOut()) {
            try {
                Method mth = service.getClass().getMethod("allowCoreThreadTimeOut", new Class[]{boolean.class});
                mth.invoke(service, new Object[]{Boolean.TRUE});
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

        final String id;

        final boolean daemon;

        final int priority;

        DefaultThreadFactory(String id, boolean daemon, int priority) {
            SecurityManager s = System.getSecurityManager();
            this.id = id;
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

        /**
         *
         * @return
         */
        public String toString() {
            return "DefaultThreadFactory{" +
                    "  id=" + id +
                    ", group=" + group +
                    ", threadNumber=" + threadNumber +
                    ", namePrefix='" + namePrefix + '\'' +
                    ", daemon=" + daemon +
                    ", priority=" + priority +
                    '}';
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

    public javax.management.MBeanServer getMbeanServer() {
        return mbeanServer;
    }

    public void setMbeanServer(javax.management.MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
        LOG.debug(">>>> SET MBEAN SERVER TO : " + mbeanServer);
    }

    public org.fusesource.commons.management.ManagementStrategy getManagementStrategy() {
        return managementStrategy;
    }

    public void setManagementStrategy(org.fusesource.commons.management.ManagementStrategy managementStrategy) {
        this.managementStrategy = managementStrategy;
        LOG.debug(">>>> SET MANAGEMENT STRATEGY TO : " + managementStrategy);
    }

    private void registerMBean(String id, ExecutorImpl executor, ExecutorConfig config) throws Exception {
        ManagedExecutor mbean = new ManagedExecutor(id, executor, config);
        ObjectName oName = null;
        if (this.managementStrategy != null) {
            // SMX 4 - ManagementStrategy
            if (hasSubType(id)) {
                oName = new javax.management.ObjectName(String.format("%s%s%s%s", OBJECT_NAME_PREFIX, sanitize(getType(id)), OBJECT_NAME_POSTFIX, sanitize(getSubType(id))));
                this.managementStrategy.manageNamedObject(mbean, oName);
            } else {
                oName = new javax.management.ObjectName(String.format("%s%s", OBJECT_NAME_PREFIX, sanitize(id)));
                this.managementStrategy.manageNamedObject(mbean, oName);
            }
        } else if (this.mbeanServer != null) {
            // SMX 3 - MBeanServer
            if (hasSubType(id)) {
                oName = new javax.management.ObjectName(String.format("%s%s%s%s", OBJECT_NAME_PREFIX, sanitize(getType(id)), OBJECT_NAME_POSTFIX, sanitize(getSubType(id))));
                this.mbeanServer.registerMBean(mbean, oName);
            } else {
                oName = new javax.management.ObjectName(String.format("%s%s", OBJECT_NAME_PREFIX, sanitize(id)));
                this.mbeanServer.registerMBean(mbean, oName);
            }
        } else {
            // no possibility to insert the mbean
        }
        this.executorNames.put(mbean.getInternalExecutor(), oName);
    }

    void unregisterMBean(Executor executor) throws Exception {
        ObjectName oName = this.executorNames.remove(executor);
        if (this.managementStrategy != null) {
            // SMX 4 - ManagementStrategy
            this.managementStrategy.unmanageNamedObject(oName);
        } else if (this.mbeanServer != null) {
            // SMX 3 - MBeanServer
            this.mbeanServer.unregisterMBean(oName);
        } else {
            // no possibility to remove the mbean
        }
    }

    private String sanitize(String in) {
        String result = null;
        if (in != null) {
            result = in.replace(':', '_');
            result = result.replace('/', '_');
            result = result.replace('\\', '_');
            result = result.replace('?', '_');
            result = result.replace('=', '_');
            result = result.replace(',', '_');
        }
        return result;
    }

    private boolean hasSubType(String id) {
        return id.toLowerCase().trim().endsWith(".consumer") || id.toLowerCase().trim().endsWith(".provider");
    }

    private String getType(String id) {
        return id.substring(0, id.lastIndexOf("."));
    }

    private String getSubType(String id) {
        return id.substring(id.lastIndexOf(".") +1);
    }
}
