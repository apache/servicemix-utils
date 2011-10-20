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
package org.apache.servicemix.store.krati;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import krati.store.DataStore;
import org.apache.servicemix.id.IdGenerator;
import org.apache.servicemix.store.Entry;
import org.apache.servicemix.store.base.BaseStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KratiStore extends BaseStore{


    private static final Logger LOG = LoggerFactory.getLogger(KratiStore.class);

    private DataStore<byte[], byte[]> datas;
    private IdGenerator idGenerator = new IdGenerator();
    private final long timeout;

    /**
     * Constructor
     * @param store
     */
    public KratiStore(DataStore<byte[], byte[]> store) {
        this.datas = store;
        this.timeout=-1;   
    }

    /**
     * Constructor
     * @param store
     * @param timeout
     */
    public KratiStore(DataStore<byte[], byte[]> store, long timeout) {
        this.datas = store;
        this.timeout = timeout;   
    }
    
    /**
     * Constructor
     * @param store
     * @param timeout
     */
    public KratiStore(DataStore<byte[], byte[]> store, long timeout, IdGenerator idGenerator) {
        this.datas = store;
        this.timeout = timeout;   
        this.idGenerator = idGenerator;
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
	public void store(String key, Object value) {
		LOG.debug("Storing object with key:{}, value:{}",key, value);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
        	Entry entry = new Entry(value);
			ObjectOutputStream out = new ObjectOutputStream(buffer);
			out.writeObject(entry);
			out.close();
			
			datas.put(key.getBytes(), buffer.toByteArray());
			fireAddedEvent(key, value);
		} catch (IOException e) {
			LOG.error("Error storing key:{}", key, e);
		} catch (Exception e) {
			LOG.error("Error storing key:{}", key, e);
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
        String id = idGenerator.generateId();
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
        LOG.debug("Loading/Removing object with id: {}", id);
        Object result = null;
        if(timeout > 0) {
            evict();
        }
        result = peek(id);
        try {
			if(result != null && datas.delete(id.getBytes())) {
			  fireRemovedEvent(id,result);
			}
		} catch (Exception e) {
			LOG.error("Error deleting object with id: {}", id);
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
        LOG.debug("Peeking object with id: {}", id);
        byte[] value = datas.get(id.getBytes());
        Entry result = null;
        try {
			result = (Entry)readObject(value);
		} catch (ClassNotFoundException e) {
			LOG.error("Error reading object with id: {}", id, e);
		}
		return result != null ? result.getData() : null;
    }
    
    /**
     * <p>
     * Deletes an object that has been previously stored under the specified key.
     * </p>
     * @param id the id of the object
     * @throws Exception if an error occurs
     */
    public void delete(String id) throws Exception{
    	LOG.debug("Deleting object with id: {}", id);
    	datas.delete(id.getBytes());
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
    
    private void evict() {
        long now = System.currentTimeMillis();
        Iterator<byte[]> iter = datas.keyIterator();
        while(iter.hasNext()){
        	byte[] key = iter.next();
        	String keyValue = null;
        	Entry value = null;
        	try {
        		keyValue = new String(key);
    			value = (Entry)readObject(datas.get(key));
    		} catch (Exception e) {
    			LOG.error("Error reading object with id: {}", key, e);
    		}
        	long age = now - value.getTime();
        	if (age > timeout) {
        		
        		LOG.debug("Removing object with id {} from store after {} ms", keyValue, age);
        		try {
					if(datas.delete(key)){
						fireEvictedEvent(keyValue,value.getData());
					}
				} catch (Exception e) {
					LOG.error("Error deleting object with id {}", keyValue);
				}
        	}
        }
    }

	public DataStore<byte[], byte[]> getDatas() {
		return datas;
	}

	public void setDatas(DataStore<byte[], byte[]> datas) {
		this.datas = datas;
	}

	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}
    
}
