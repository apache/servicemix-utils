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

package org.apache.servicemix.store.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IdGenerator;
import java.io.IOException;
import java.util.Map;

import org.apache.servicemix.store.base.BaseStore;
import org.apache.servicemix.store.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author iocanel
 */
public class HazelcastStore extends BaseStore {

    private static final Logger LOG = LoggerFactory.getLogger(HazelcastStore.class);

    private Map<String, Entry> datas;

    private HazelcastInstance hazelcastInstance;
    private IdGenerator idGenerator;
    private final long timeout;

    /**
     * Constructor
     * @param name
     */
    public HazelcastStore(String name) {
        this.hazelcastInstance = Hazelcast.newHazelcastInstance(null);
        this.idGenerator = hazelcastInstance.getIdGenerator(name);
        this.datas = hazelcastInstance.getMap(name);
        this.timeout=-1;
    }
    /**
     * Constructor
     * @param hazelcastInstance
     * @param name
     */
    public HazelcastStore(HazelcastInstance hazelcastInstance,String name) {
        this.hazelcastInstance = hazelcastInstance;
        this.idGenerator = hazelcastInstance.getIdGenerator(name);
        this.datas = hazelcastInstance.getMap(name);
        this.timeout=-1;        
    }

    /**
     * Constructor
     * @param hazelcastInstance
     * @param name
     * @param timemout
     */
    public HazelcastStore(HazelcastInstance hazelcastInstance,String name, long timemout) {
        this.hazelcastInstance = hazelcastInstance;
        this.idGenerator = hazelcastInstance.getIdGenerator(name);
        this.datas = hazelcastInstance.getMap(name);
        this.timeout=timemout;
    }


    /**
     * <p>
     * Returns true if feature is provided by the store (clustered), false else.
     * </p>
     *
     * @param feature the feature.
     * @return true if the given feature is provided by the store, false else.
     */
    public boolean hasFeature(String feature) {
        if (CLUSTERED.equals(feature))
            return true;
        return false;
    }


    /**
     * <p>
     * Put an object in the store under the given id.
     * This method must be used with caution and the behavior is
     * unspecified if an object already exist for the same id.
     * </p>
     * @param id the id of the object to store
     * @param data the object to store
     * @throws IOException if an error occurs
     */
    public void store(String id, Object data) throws IOException {
        LOG.debug("Storing object with id: " + id);
        datas.put(id, new Entry(data));
        fireAddedEvent(id,data);
    }

    /**
     * <p>
     * Put an object into the store and return the unique id that
     * may be used at a later time to retrieve the object.
     * </p>
     * @param data the object to store
     * @return the id of the object stored
     * @throws IOException if an error occurs
     */
    public String store(Object data) throws IOException {
        String id = String.valueOf(idGenerator.newId());
        store(id, data);
        return id;
    }

    /**
     * <p>
     * Loads an object that has been previously stored under the specified key.
     * The object is removed from the store.
     * </p>
     * @param id the id of the object
     * @return the object, or <code>null></code> if the object could not be found
     * @throws IOException if an error occurs
     */
    public Object load(String id) throws IOException {
        LOG.debug("Loading/Removing object with id: " + id);
        if(timeout > 0) {
            evict();
        }
        Entry entry = datas.remove(id);
        if(entry != null) {
          fireRemovedEvent(id,entry.getData());
            return entry.getData();
        } else return null;
    }

    /**
     * <p>
     * Loads an object that has been previously stored under the specified key.
     * The object is not removed from the store.
     * </p>
     * @param id the id of the object
     * @return the object, or <code>null</code> if the object could not be found
     * @throws IOException if an error occurs
     */

    public Object peek(String id) throws IOException {
        LOG.debug("Peeking object with id: " + id);
        Entry entry = datas.get(id);
        return entry != null ? entry.getData() : null;
    }
    
    private void evict() {
        long now = System.currentTimeMillis();
        for (String key : datas.keySet()) {
            long age = now - datas.get(key).getTime();
            if (age > timeout) {
                LOG.debug("Removing object with id " + key + " from store after " + age + " ms");
                Entry entry = datas.get(key);
                datas.remove(key);

                if(entry != null) {
                    fireEvictedEvent(key,entry.getData());
                }
            }
        }
    }
}
