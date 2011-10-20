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
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStore;
import krati.core.StoreConfig;
import krati.core.StoreFactory;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.Segment;
import krati.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KratiPersistenceProvider implements MapLoader<String, Object>, MapStore<String, Object>{
	
	private static final Logger LOG = LoggerFactory.getLogger(KratiPersistenceProvider.class);
	
	private static final String DEFAULT_STORE_DIRECTORY = "/tmp/krati/";
	private static final Integer INIT_CAPACITY = 10000;
	
	private DataStore<byte[], byte[]> datas;
	private String storeDirectory = DEFAULT_STORE_DIRECTORY;
    private int initCapacity = INIT_CAPACITY; 
    private int segmentFileSizeMB = Segment.minSegmentFileSizeMB;
    
    public KratiPersistenceProvider(){
    	try {
			init();
		} catch (Exception e) {
			LOG.error("Error during initialization",e);
		}
    }
    
	
	public KratiPersistenceProvider(String storeDirectory) {
		super();
		this.storeDirectory = storeDirectory;
		try {
			init();
		} catch (Exception e) {
			LOG.error("Error during initialization",e);
		}
	}


	public KratiPersistenceProvider(String storeDirectory, int initCapacity) {
		super();
		this.storeDirectory = storeDirectory;
		this.initCapacity = initCapacity;
		try {
			init();
		} catch (Exception e) {
			LOG.error("Error during initialization",e);
		}
	}

	

	public KratiPersistenceProvider(String storeDirectory,
                                    int initCapacity, int segmentFileSizeMB) {
		super();
		this.storeDirectory = storeDirectory;
		this.initCapacity = initCapacity;
		this.segmentFileSizeMB = segmentFileSizeMB;
		try {
			init();
		} catch (Exception e) {
			LOG.error("Error during initialization",e);
		}
	}


	public void init() throws Exception{
		if(datas == null){
			StoreConfig config = new StoreConfig(new File(storeDirectory), initCapacity);
			config.setSegmentFactory(new MemorySegmentFactory());
			config.setSegmentFileSizeMB(segmentFileSizeMB);
			datas = StoreFactory.createDynamicDataStore(config);
		}
	}

	public void destroy() throws Exception{
		if(datas!=null && datas.isOpen()){
			datas.close();
		}
	}
	
	public void delete(String key) {
		try {
			datas.delete(key.getBytes());
		} catch (Exception e) {
			LOG.error("Error deleting object with id:{}", key, e);
		}
		
	}

	public void deleteAll(Collection<String> entries) {
		for(String key:entries){
			try {
				datas.delete(key.getBytes());
			} catch (Exception e) {
				LOG.error("Error deleting object with id:{}", key, e);
			}
		}
		
	}

	public void store(String key, Object value) {
		try {
			LOG.debug("Storing key: {}", key);
			datas.put(key.getBytes(), writeObject(value));
		} catch (Exception e) {
			LOG.error("Error storing object with id:{}", key, e);
		}
		
	}

	public void storeAll(Map<String, Object> entries) {
		for(String key: entries.keySet()){
			store(key, entries.get(key));
		}
		
	}

	public Object load(String key) {
		Object result = null;
		try {
			result = readObject(datas.get(key.getBytes()));
		} catch (Exception e) {
			LOG.error("Error loading object with id: {}", key, e);
		}
		return result;
	}

	public Map<String, Object> loadAll(Collection<String> keys) {
		Map<String, Object> result = null;
		for(String key : keys){
			if(result == null){
				result = new HashMap<String, Object>();
			}
			result.put(key, load(key));
		}
		return result;
	}

	public Set<String> loadAllKeys() {
		Set<String> keys = null;
		while(datas.keyIterator().hasNext()){
			String key = new String(datas.keyIterator().next());
			if(keys == null){
				keys = new HashSet<String>();
			}
			keys.add(key);
		}
		return keys;
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
	
	private byte[] writeObject(Object object) throws IOException{
		byte[] result = null;
		ByteArrayOutputStream buffer = null;
		ObjectOutputStream out = null;
		if (object != null) {
			try {
				buffer = new ByteArrayOutputStream();
				out = new ObjectOutputStream(buffer);
				out.writeObject(object);
			} finally{
				out.close();
			}	
			result = buffer.toByteArray();
        }
		return result;
	}

	public int getSegmentFileSizeMB() {
		return segmentFileSizeMB;
	}

	public void setSegmentFileSizeMB(int segmentFileSizeMB) {
		this.segmentFileSizeMB = segmentFileSizeMB;
	}

	public String getStoreDirectory() {
		return storeDirectory;
	}

	public void setStoreDirectory(String storeDirectory) {
		this.storeDirectory = storeDirectory;
	}

	public int getInitCapacity() {
		return initCapacity;
	}

	public void setInitCapacity(int initCapacity) {
		this.initCapacity = initCapacity;
	}

}
