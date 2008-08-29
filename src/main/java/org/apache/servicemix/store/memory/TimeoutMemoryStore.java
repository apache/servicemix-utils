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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.id.IdGenerator;

/**
 * {@link MemoryStore} which removes entries from the store after the specified timeout
 * to free memory.
 */
public class TimeoutMemoryStore extends MemoryStore {

    private static final Log LOG = LogFactory.getLog(TimeoutMemoryStore.class);
    private ConcurrentMap<String, Entry> datas = new ConcurrentHashMap<String, Entry>();
    private final long timeout;

    protected TimeoutMemoryStore(IdGenerator idGenerator, long timeout) {
        super(idGenerator);
        this.timeout = timeout;
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(String id, Object data) throws IOException {
        LOG.debug("Storing object with id: " + id);
        datas.put(id, new Entry(data));
    }
    
    /**
     * {@inheritDoc}
     * 
     * Before attempting to load the object, all data older than the specified timeout will first be 
     * removed from the store.
     */
    public Object load(String id) throws IOException {
        evict();
        LOG.debug("Loading object with id:" + id);
        Entry entry = datas.remove(id);
        return entry == null ? null : entry.data;
    }
    
    private void evict() {
        long now = System.currentTimeMillis();
        for (String key : datas.keySet()) {
            long age = now - datas.get(key).time;
            if (age > timeout) {
                LOG.debug("Removing object with id " + key + " from store after " + age + " ms");
                datas.remove(key);
            }
        }
    }

    /*
     * A single store entry
     */
    private final class Entry {
        private final long time = System.currentTimeMillis();
        private final Object data;
        
        private Entry(Object data) {
            this.data = data;
        }
    }
}
