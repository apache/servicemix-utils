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
package org.apache.servicemix.store.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;

/**
 * @author: iocanel
 */
public class CacheManagerFactory {

    private Boolean diskPersistent = Boolean.TRUE;
    private Boolean eternal = Boolean.FALSE;
    private int maxElementsInMemory=10000;
    private Boolean overflowToDisk= Boolean.TRUE;
    private long timeToIdleSeconds=300;
    private long timeToLiveSeconds=300;
    private String memoryStoreEvictionPolicy="LRU";

    private String diskStorePath=".";


    /**
     * Builds the default {@ling CacheManager} instace
     * @return
     */
    public CacheManager build() {
        Configuration configuration = new Configuration();
        CacheConfiguration defaultCacheConfiguration = new CacheConfiguration();
        defaultCacheConfiguration.setMaxElementsInMemory(maxElementsInMemory);
        defaultCacheConfiguration.setEternal(eternal);
        defaultCacheConfiguration.setTimeToIdleSeconds(timeToIdleSeconds);
        defaultCacheConfiguration.setTimeToLiveSeconds(timeToLiveSeconds);
        defaultCacheConfiguration.setOverflowToDisk(overflowToDisk);
        defaultCacheConfiguration.setDiskPersistent(diskPersistent);
        defaultCacheConfiguration.setMemoryStoreEvictionPolicy(memoryStoreEvictionPolicy);

        DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
        diskStoreConfiguration.setPath(diskStorePath);
        configuration.addDiskStore(diskStoreConfiguration);

        configuration.addDefaultCache(defaultCacheConfiguration);


        CacheManager cacheManager = new CacheManager(configuration);
        return cacheManager;
    }

    public String getDiskStorePath() {
        return diskStorePath;
    }

    public void setDiskStorePath(String diskStorePath) {
        this.diskStorePath = diskStorePath;
    }

    public Boolean getDiskPersistent() {
        return diskPersistent;
    }

    public void setDiskPersistent(Boolean diskPersistent) {
        this.diskPersistent = diskPersistent;
    }

    public Boolean getEternal() {
        return eternal;
    }

    public void setEternal(Boolean eternal) {
        this.eternal = eternal;
    }

    public int getMaxElementsInMemory() {
        return maxElementsInMemory;
    }

    public void setMaxElementsInMemory(int maxElementsInMemory) {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    public Boolean getOverflowToDisk() {
        return overflowToDisk;
    }

    public void setOverflowToDisk(Boolean overflowToDisk) {
        this.overflowToDisk = overflowToDisk;
    }

    public long getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(long timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public String getMemoryStoreEvictionPolicy() {
        return memoryStoreEvictionPolicy;
    }

    public void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
    }
}
