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

import org.apache.servicemix.id.IdGenerator;
import org.apache.servicemix.store.base.BaseStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple memory store implementation based on a simple map.
 * This store is neither clusterable, nor persistent, nor transactional.
 * 
 * @author gnodet
 */
public class MemoryStore extends BaseStore {

    private static final Logger LOG = LoggerFactory.getLogger(MemoryStore.class);

    private Map<String, Object> datas = new ConcurrentHashMap<String, Object>();

    private IdGenerator idGenerator;

    public MemoryStore(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public boolean hasFeature(String name) {
        return false;
    }

    public void store(String id, Object data) throws IOException {
        LOG.debug("Storing object with id: " + id);
        datas.put(id, data);
        fireAddedEvent(id,data);
    }

    public String store(Object data) throws IOException {
        String id = idGenerator.generateId();
        store(id, data);
        return id;
    }

    public Object load(String id) throws IOException {
        LOG.debug("Loading/Removing object with id: " + id);
        Object data = datas.remove(id);
        fireEvictedEvent(id,data);
        return data;
    }

    public Object peek(String id) throws IOException {
        LOG.debug("Peeking object with id: " + id);
        return datas.get(id);
    }

}
