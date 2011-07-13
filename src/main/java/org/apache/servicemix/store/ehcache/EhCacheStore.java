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
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.servicemix.id.IdGenerator;
import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.base.BaseStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * EhCache Store.
 * <p>Note: The current implementation always "removes" expired items.</p>
 * @author n.dimos
 */
public class EhCacheStore extends BaseStore implements CacheEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(EhCacheStore.class);

    private String name;
    private Cache cache;
    private IdGenerator idGenerator;

    public EhCacheStore(Cache cache, IdGenerator idGenerator, String name) {
        super();
        this.cache=cache;
        this.idGenerator=idGenerator;
        this.name = name;
        cache.getCacheEventNotificationService().registerListener(this);
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
        if(Store.PERSISTENT.equals(feature)) return cache.getCacheConfiguration().isDiskPersistent();
        else return false;
    }


    public synchronized void destroy() throws Exception {
           cache.flush();
    }

    /**
     * <p>
     * Put an object in the store under the given id. This method must be used
     * with caution and the behavior is unspecified if an object already exist
     * for the same id.
     * </p>
     *
     * @param id   the id of the object to store
     * @param data the object to store
     * @throws IOException if an error occurs
     */
    public void store(String id, Object data) throws IOException {
        LOG.debug("Storing object with id: " + id);
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(data);
            out.close();

            Element element = new Element(id, buffer.toByteArray());
            cache.put(element);
        } catch (Exception e) {
            throw new IOException(e);
        }

    }

    /**
     * <p>
     * Put an object into the store and return the unique id that may be used at
     * a later time to retrieve the object.
     * </p>
     *
     * @param data the object to store
     * @return the id of the object stored
     * @throws IOException if an error occurs
     */
    public String store(Object data) throws IOException {
        String id = idGenerator.generateId();
        store(id, data);
        return id;
    }

    /**
     * <p>
     * Loads an object that has been previously stored under the specified key.
     * The object is removed from the store.
     * </p>
     *
     * @param id the id of the object
     * @return the object, or <code>null></code> if the object could not be found
     * @throws IOException if an error occurs
     */
    public Object load(String id) throws IOException {
        LOG.debug("Loading object with id: " + id);
        try {
            Object result = null;
            Element element = cache.get(id);
            if (element != null) {
                byte[] data = (byte[]) (element.getValue());
                result = readObject(data);
                cache.remove(id);
            }
            return result;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * <p>
     * Loads an object that has been previously stored under the specified key.
     * The object is not removed from the store.
     * </p>
     *
     * @param id the id of the object
     * @return the object, or <code>null</code> if the object could not be found
     * @throws IOException if an error occurs
     */
    public Object peek(String id) throws IOException {
        LOG.debug("Peeking object with id: " + id);
        try {
            Object result = null;
            Element element = cache.get(id);
            if (element != null) {
                byte[] data = (byte[]) (element.getValue());
                result = readObject(data);
            }
            return result;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private Object readObject(byte[] data) throws IOException, ClassNotFoundException {
        Object result = null;
        if (data != null) {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            try {
                result = ois.readObject();
            } finally {
                ois.close();
            }
        }
        return result;
    }


    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        String id = (String) element.getKey();
        byte[] data = (byte[]) element.getObjectValue();
        try {
            fireAddedEvent(id, readObject(data));
        } catch (IOException e) {
            throw new CacheException(e);
        } catch (ClassNotFoundException e) {
            throw new CacheException(e);
        }
    }

    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        notifyElementPut(cache,element);
    }

    public void notifyElementExpired(Ehcache cache, Element element) {

        String id = (String) element.getKey();
        byte[] bytes = (byte[]) element.getObjectValue();
        Object data = null;
        try {
            data = readObject(bytes);
            fireEvictedEvent(id, readObject(bytes));
        } catch (IOException e) {
            LOG.error("Error reading expired element",e);
        } catch (ClassNotFoundException e) {
           LOG.error("Error reading expired element",e);
        }
        cache.removeQuiet(id);
    }

    public void notifyElementEvicted(Ehcache cache, Element element) {
            String id = (String) element.getKey();
            byte[] data = (byte[]) element.getObjectValue();
            try {
                fireEvictedEvent(id, readObject(data));
            } catch (IOException e) {
                throw new CacheException(e);
            } catch (ClassNotFoundException e) {
                throw new CacheException(e);
            }
    }

    public void notifyRemoveAll(Ehcache cache) {
    }

    public void dispose() {

    }

   public Object clone() throws CloneNotSupportedException {
       throw new CloneNotSupportedException();
   }

    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        String id = (String) element.getKey();
        byte[] data = (byte[]) element.getObjectValue();
        try {
            //I am doing this to work around an issue in EhCache quiet removal.
            //This cause problems since elements are always removed on expiration.
            if (data != null) {
                fireRemovedEvent(id, readObject(data));
            }
        } catch (IOException e) {
           throw new CacheException(e);
        } catch (ClassNotFoundException e) {
            throw new CacheException(e);
        }
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
