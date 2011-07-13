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


import org.apache.servicemix.store.base.BaseStore;
import org.apache.servicemix.store.Entry;
import org.idevlab.rjc.RedisNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

public class RedisStore extends BaseStore {

    private static final Logger LOG = LoggerFactory.getLogger(RedisStore.class);

    private RedisNode redisNode;
    private String storeName;
    private String idgenName;
    private Long timeout = 0L;

    private BASE64Encoder encoder = new BASE64Encoder();
    private BASE64Decoder decoder = new BASE64Decoder();

    /**
     * Constructor
     *
     * @param redisNode
     * @param storeName
     */
    public RedisStore(RedisNode redisNode, String storeName) {
        this.redisNode = redisNode;
        this.storeName = storeName;
        this.idgenName = storeName + ".idgen";
    }

    public RedisStore(RedisNode redisNode, String storeName, Long timeout) {
        this.redisNode = redisNode;
        this.storeName = storeName;
        this.idgenName = storeName + ".idgen";
        this.timeout = timeout;
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
        if (CLUSTERED.equals(feature) || PERSISTENT.equals(feature))
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
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            out = new ObjectOutputStream(buffer);
            out.writeObject(new Entry(data));
            out.close();
            redisNode.set(id, encoder.encode(buffer.toByteArray()));
            fireAddedEvent(id,data);
        } catch (Exception e) {
            throw (IOException) new IOException("Error storing object").initCause(e);
        } finally {
            if(out != null) {
                out.close();
            }
        }
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
        Long id = redisNode.incr(idgenName);
        store(String.valueOf(id), data);
        return null;
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
        Entry result = removeEntry(id);
        if(result != null) {
            fireRemovedEvent(id,result.getData());
        }
        return result;
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
    public Object evict(String id) throws IOException {
        LOG.debug("Evicting object with id: " + id);
        Entry result = removeEntry(id);
        if(result != null) {
            fireEvictedEvent(id, result.getData());
        }
        return result;
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
        Object result = null;
        try {
            result = parseEntry(redisNode.get(id)).getData();
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not load object from store", e);
        }
        return result;
    }

    /**
     * Removes an object with the specified id.
     * @param id
     * @return
     * @throws IOException
     */
    private Entry removeEntry(String id) throws IOException {
        Entry result = null;
        if (timeout > 0) {
            evict();
        }
        try {
            result = parseEntry(redisNode.get(id));
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not load object from store", e);
        }
        redisNode.del(id);
        return result;
    }


    /**
     * Decodes a String to an Entry.
     * @param string
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Entry parseEntry(String string) throws IOException, ClassNotFoundException {
        ByteArrayInputStream buffer = new ByteArrayInputStream(decoder.decodeBuffer(string));
        ObjectInputStream out = new ObjectInputStream(buffer);
        return (Entry) out.readObject();
    }

    /**
     * Check for Entries that have timed out.
     * @throws IOException
     */
    private void evict() throws IOException {
        long now = System.currentTimeMillis();
        for (String key : redisNode.keys("*")) {
            long age = 0;
            try {
                age = now - parseEntry(redisNode.get(key)).getTime();
            } catch (ClassNotFoundException e) {
                throw new IOException("Could not load object from store", e);
            }
            if (age > timeout) {
                LOG.debug("Removing object with id " + key + " from store after " + age + " ms");
                evict(key);
            }
        }
    }
}
