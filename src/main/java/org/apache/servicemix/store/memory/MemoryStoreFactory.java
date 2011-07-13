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
package org.apache.servicemix.store.memory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.servicemix.id.IdGenerator;
import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.StoreFactory;
import org.apache.servicemix.store.StoreListener;
import org.apache.servicemix.store.base.BaseStoreFactory;

/**
 * {@link StoreFactory} for creating memory-based {@link Store} implementations
 * 
 * If a timeout has been specified, a {@link TimeoutMemoryStore} will be created,
 * otherwise the factory will build a plain {@link MemoryStore}
 */
public class MemoryStoreFactory extends BaseStoreFactory {

    private IdGenerator idGenerator = new IdGenerator();
    private Map<String, MemoryStore> stores = new HashMap<String, MemoryStore>();
    private long timeout = -1;
    
    /* (non-Javadoc)
     * @see org.apache.servicemix.store.ExchangeStoreFactory#get(java.lang.String)
     */
    public synchronized Store open(String name) throws IOException {
        MemoryStore store = stores.get(name);
        if (store == null) {
            if (timeout <= 0) {
                store = new MemoryStore(idGenerator);
            } else {
                store = new TimeoutMemoryStore(idGenerator, timeout);
            }

            for(StoreListener listener:storeListeners) {
                store.addListener(listener);
            }
            stores.put(name, store);
        }
        return store;
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.store.ExchangeStoreFactory#release(org.apache.servicemix.store.ExchangeStore)
     */
    public synchronized void close(Store store) throws IOException {
        stores.remove(store);
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
