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

import junit.framework.TestCase;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.servicemix.id.IdGenerator;
import org.apache.servicemix.store.StoreListener;
import org.easymock.EasyMock;

import static org.easymock.EasyMock.*;

/**
 * @author: iocanel
 */
public class EhCacheStoreTest extends TestCase {

    private static final String TEST_CACHE_NAME = "TEST_CACHE_NAME";
    IdGenerator idGenerator = new IdGenerator();
    CacheManagerFactory cacheManagerFactory;
    CacheManager cacheManager;

    Cache cache = null;

    private StoreListener listener = createMock(StoreListener.class);

    public EhCacheStoreTest() {
        cacheManagerFactory = new CacheManagerFactory();
        cacheManagerFactory.setDiskStorePath("target");
        cacheManager = cacheManagerFactory.build();
        cacheManager.addCache(TEST_CACHE_NAME);
        cache = cacheManager.getCache(TEST_CACHE_NAME);
    }

    public void testStore() throws Exception {
        EhCacheStore store = new EhCacheStore(cache,idGenerator,"testStore");
        String id = "1";
        String data = "Test data ....";
        store.store(id,data);
        assertEquals(data,store.peek(id));
        assertEquals(data,store.load(id));
        assertEquals(null,store.load(id));
    }

    public void testEviction() throws Exception {
        cacheManagerFactory.setTimeToIdleSeconds(2);
        cacheManagerFactory.setTimeToLiveSeconds(2);
        cacheManager = cacheManagerFactory.build();
        cacheManager.addCache(TEST_CACHE_NAME);
        cache = cacheManager.getCache(TEST_CACHE_NAME);

        EhCacheStore store = new EhCacheStore(cache,idGenerator,"testStore");
        String id = "1";
        String data = "Test data ....";
        store.store(id,data);
        assertEquals(data,store.peek(id));
        Thread.sleep(3000);
        assertEquals(null,store.peek(id));
    }

    public void testStoreListeners() throws Exception {
        cacheManagerFactory.setTimeToIdleSeconds(2);
        cacheManagerFactory.setTimeToLiveSeconds(2);
        cacheManager = cacheManagerFactory.build();
        cacheManager.addCache(TEST_CACHE_NAME);
        cache = cacheManager.getCache(TEST_CACHE_NAME);

        EhCacheStore store = new EhCacheStore(cache,idGenerator,"testStore");
        store.addListener(listener);
        String id = "1";
        String data = "Test data ....";

         //Record behavior
        listener.onAdd(id,data);
        expectLastCall().times(1);
        listener.onEvict(id, data);
        expectLastCall().once();
        replay(listener);

        store.store(id,data);
        assertEquals(data,store.peek(id));
        Thread.sleep(3000);
        assertEquals(null,store.peek(id));
        verify(listener);
    }
}
