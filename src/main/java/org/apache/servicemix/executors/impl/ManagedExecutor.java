/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.servicemix.executors.impl;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.servicemix.executors.Executor;

public class ManagedExecutor extends javax.management.StandardMBean implements Executor, ManagedExecutorMBean {

    private String id;
    private ExecutorImpl internalExecutor;
    private ExecutorConfig config;
    private AtomicLong rejectedExecutions;


    public ManagedExecutor(String id, ExecutorImpl internalExecutor, ExecutorConfig config) throws javax.management.NotCompliantMBeanException {
        super(ManagedExecutorMBean.class);
        this.id = id;
        this.internalExecutor = internalExecutor;
        this.config = config;
        this.rejectedExecutions = new AtomicLong(0L);
        if (this.internalExecutor != null) {
            setupWrapper();
        }
    }

    public String getId() {
        return this.id;
    }

    public int getActiveCount() {
        return this.internalExecutor != null ? this.internalExecutor.getThreadPoolExecutor().getActiveCount() : 0;
    }

    public long getCompletedTaskCount() {
        return this.internalExecutor != null ? this.internalExecutor.getThreadPoolExecutor().getCompletedTaskCount() : 0;
    }

    public int getCorePoolSize() {
        return this.internalExecutor != null ? this.internalExecutor.getThreadPoolExecutor().getCorePoolSize() : 0;
    }

    public void setCorePoolSize(int size) {
        if (this.internalExecutor != null) {
            this.internalExecutor.getThreadPoolExecutor().setCorePoolSize(size);
        }
    }

    public long getKeepAliveTimeInMillis() {
        return this.internalExecutor != null ? this.internalExecutor.getThreadPoolExecutor().getKeepAliveTime(TimeUnit.MILLISECONDS) : 0L;
    }

    public void setKeepAliveTimeInMillis(long timeInMillis) {
        if (this.internalExecutor != null) {
            this.internalExecutor.getThreadPoolExecutor().setKeepAliveTime(timeInMillis, TimeUnit.MILLISECONDS);
        }
    }

    public int getLargestPoolSize() {
        return this.internalExecutor != null ? this.internalExecutor.getThreadPoolExecutor().getLargestPoolSize() : 0;
    }

    public int getMaximumPoolSize() {
        return this.internalExecutor != null ? this.internalExecutor.getThreadPoolExecutor().getMaximumPoolSize() : 0;
    }

    public void setMaximumPoolSize(int size) {
        if (this.internalExecutor != null) {
            this.internalExecutor.getThreadPoolExecutor().setMaximumPoolSize(size);
        }
    }

    public int getPoolSize() {
        return this.internalExecutor != null ? this.internalExecutor.getThreadPoolExecutor().getPoolSize() : 0;
    }

    public long getTaskCount() {
        return this.internalExecutor != null ? this.internalExecutor.getThreadPoolExecutor().getTaskCount() : 0;
    }

    public int getQueueSize() {
        return this.internalExecutor != null ? this.internalExecutor.size() : 0;
    }

    public boolean isAllowCoreThreadTimeOut() {
        if (this.internalExecutor != null) {
            ThreadPoolExecutor executor = this.internalExecutor.getThreadPoolExecutor();
            try {
                Method m = ThreadPoolExecutor.class.getMethod("allowsCoreThreadTimeOut", null);
                try {
                    return (Boolean) m.invoke(executor, null);
                } catch (Exception ex) {
                    // ignore
                }
            } catch (NoSuchMethodException ex) {
                // ignore
            }
        }
        return false;
    }

    public long getNumberOfRejectedExecutions() {
        return this.rejectedExecutions.get();
    }

    public void reset() {
        this.rejectedExecutions.set(0L);
    }

    public long getShutdownDelay() {
        return this.config != null ? this.config.getShutdownDelay() : 0L;
    }

    public boolean isBypassIfSynchronous() {
        return this.config != null && this.config.isBypassIfSynchronous();
    }

    public void increaseRejectedExecutions() {
        this.rejectedExecutions.incrementAndGet();
    }

    private void setupWrapper() {
        this.internalExecutor.getThreadPoolExecutor().setRejectedExecutionHandler(new WrappedRejectedExecutionHandler(this));
    }

    public ExecutorImpl getInternalExecutor() {
        return this.internalExecutor;
    }

    public void execute(Runnable command) {
        if (this.internalExecutor != null) {
            this.internalExecutor.execute(command);
        }
    }

    public void shutdown() {
        if (this.internalExecutor != null) {
            this.internalExecutor.shutdown();
        }
    }

    public int capacity() {
        return this.internalExecutor != null ? this.internalExecutor.capacity() : 0;
    }

    public int size() {
        return this.internalExecutor != null ? this.internalExecutor.size() : 0;
    }

    @Override
    protected String getDescription(javax.management.MBeanInfo info) {
        return "Managed Executor";
    }

    @Override
    protected String getDescription(javax.management.MBeanFeatureInfo info) {
        if ("name".equalsIgnoreCase(info.getName())) {
            return "Name of the endpoint";
        }
        if ("properties".equalsIgnoreCase(info.getName())) {
            return "Properties associated to this endpoint";
        }
        if ("inboundExchangeCount".equalsIgnoreCase(info.getName())) {
            return "Number of exchanges received";
        }
        if ("inboundExchangeCount".equalsIgnoreCase(info.getName())) {
            return "Number of exchanges received";
        }
        if ("inboundExchangeRate".equalsIgnoreCase(info.getName())) {
            return "Exchanges received per second";
        }
        if ("outboundExchangeCount".equalsIgnoreCase(info.getName())) {
            return "Number of exchanges sent";
        }
        if ("outboundExchangeRate".equalsIgnoreCase(info.getName())) {
            return "Exchanges sent per second";
        }
        if ("reset".equalsIgnoreCase(info.getName())) {
            return "Reset statistics";
        }
        return null;
    }
}
