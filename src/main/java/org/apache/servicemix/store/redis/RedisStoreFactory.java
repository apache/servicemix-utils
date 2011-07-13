/*
 * Copyright 2011 iocanel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
package org.apache.servicemix.store.redis;


import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.StoreListener;
import org.apache.servicemix.store.base.BaseStoreFactory;
import org.idevlab.rjc.RedisNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RedisStoreFactory extends BaseStoreFactory {

    private Map<String, RedisStore> stores = new HashMap<String, RedisStore>();

    private RedisNode redisNode;
    private long timeout = -1;

    public static final String STORE_PREFIX = "org.apache.servicemix.stores";

    public RedisStoreFactory(RedisNode redisNode) {
        this.redisNode = redisNode;
    }

    public synchronized Store open(String name) throws IOException {
        RedisStore store = stores.get(name);
        String storeName = STORE_PREFIX + "." + name;
        if (store == null) {
            if (timeout <= 0) {
                store = new RedisStore(redisNode, storeName);
            } else {
                store = new RedisStore(redisNode, storeName, timeout);
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

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}