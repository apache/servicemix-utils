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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.servicemix.id.IdGenerator;
import org.apache.servicemix.store.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MemoryStore} which removes entries from the store after the specified timeout
 * to free memory.
 */
public class TimeoutMemoryStore extends MemoryStore {

    private static final Logger LOG = LoggerFactory.getLogger(TimeoutMemoryStore.class);
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
        fireAddedEvent(id,data);
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
        if (entry != null) {
            Object data = entry.getData();
            fireRemovedEvent(id,data);
            return data;
        } else return null;
    }

    /*
     * Remove timed out entries from the data map.
     */
    private void evict() {
        long now = System.currentTimeMillis();

        for (Map.Entry<String, Entry> entry : datas.entrySet()) {
            long age = now - entry.getValue().getTime();
            if (age > timeout) {
                LOG.debug("Removing object with id " + entry.getKey() + " from store after " + age + " ms");
                if(datas.remove(entry.getKey()) != null) {
                    fireEvictedEvent(entry.getKey(), entry.getValue().getData());
                }
            }
        }
    }
}
