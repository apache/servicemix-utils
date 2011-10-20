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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import krati.core.StoreConfig;
import krati.core.StoreFactory;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.Segment;
import krati.store.DataStore;
import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.StoreListener;
import org.apache.servicemix.store.base.BaseStoreFactory;
import org.apache.servicemix.store.mongo.MongoStore;
import org.slf4j.LoggerFactory;

public class KratiStoreFactory extends BaseStoreFactory{
	
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MongoStore.class);
	
	private static final String DEFAULT_STORE_DIRECTORY = "/tmp/krati/";
	private static final Integer INIT_CAPACITY = 10000;
	
	private Map<String, KratiStore> stores = new HashMap<String, KratiStore>();

    private long timeout = -1;
    private String storeDirectory = DEFAULT_STORE_DIRECTORY;
    private int initCapacity = INIT_CAPACITY;

    public static final String STORE_PREFIX = "org.apache.servicemix.stores";

	public synchronized Store open(String name) throws IOException {

		KratiStore store = stores.get(name);
		if (store == null) {
			DataStore<byte[], byte[]> dataStore = null;
			try {
				String storeName = STORE_PREFIX + "." + name;
				StringBuilder sb = new StringBuilder(storeDirectory);
				sb.append("/");

                File storeFolder = new File(sb.toString());
                storeFolder.mkdir();
                File file = new File(storeFolder,name);

				StoreConfig config = new StoreConfig(file, initCapacity);
				config.setSegmentFactory(new MemorySegmentFactory());
				config.setSegmentFileSizeMB(Segment.minSegmentFileSizeMB);
				dataStore = StoreFactory.createDynamicDataStore(config);

				if (timeout <= 0) {
					store = new KratiStore(dataStore);
				} else {
					store = new KratiStore(dataStore, timeout);
				}
				stores.put(name, store);
				for (StoreListener listener : storeListeners) {
					store.addListener(listener);
				}
			} catch (Exception e) {
				LOG.error("Error during store initialization, {}", e, e);
			}
		}

		return store;
	}

    /* (non-Javadoc)
    * @see org.apache.servicemix.store.ExchangeStoreFactory#release(org.apache.servicemix.store.ExchangeStore)
    */
    public synchronized void close(Store store) throws IOException {
    	KratiStore kratiStore = (KratiStore)store;
    	if(kratiStore.getDatas() != null && kratiStore.getDatas().isOpen()){
    		kratiStore.getDatas().close();
    	}
        stores.remove(store);
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
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
