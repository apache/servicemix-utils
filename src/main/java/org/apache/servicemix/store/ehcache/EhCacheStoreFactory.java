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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.id.IdGenerator;
import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.StoreListener;
import org.apache.servicemix.store.base.BaseStoreFactory;

/**
 * @author n.dimos
 */
public class EhCacheStoreFactory extends BaseStoreFactory{

    private static final Log LOG = LogFactory.getLog(EhCacheStoreFactory.class);

    protected IdGenerator idGenerator = new IdGenerator();
    private Map<String, EhCacheStore> stores = new HashMap<String, EhCacheStore>();

    private CacheManagerFactory cacheManagerFactory = new CacheManagerFactory();
    private CacheManager cacheManager;

    public EhCacheStoreFactory() {

    }

    public synchronized Store open(String name) throws IOException {
        EhCacheStore store = stores.get(name);
        if (store == null) {

            if(cacheManager == null) {
                cacheManager = cacheManagerFactory.build();
            }

            Cache cache = cacheManager.getCache(name);
            if(cache == null) {
                cacheManager.addCache(name);
                cache = cacheManager.getCache(name);
            }
            store = new EhCacheStore(cache,idGenerator, name);

            for(StoreListener listener:storeListeners) {
                store.addListener(listener);
            }
            stores.put(name, store);
        }
        return store;
    }

    public synchronized void close(Store store) throws IOException {
        EhCacheStore ehCacheStore = (EhCacheStore) store;
        try {
            ehCacheStore.destroy();
        } catch (Exception e) {
            throw new IOException(e);
        }
        stores.remove(ehCacheStore.getName());
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    public CacheManagerFactory getCacheManagerFactory() {
        return cacheManagerFactory;
    }

    public void setCacheManagerFactory(CacheManagerFactory cacheManagerFactory) {
        this.cacheManagerFactory = cacheManagerFactory;
    }
}
