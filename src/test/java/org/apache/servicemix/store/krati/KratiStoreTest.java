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
import junit.framework.TestCase;
import krati.store.DataStore;

import org.slf4j.LoggerFactory;

public class KratiStoreTest extends TestCase {
	
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(KratiStoreTest.class);

	private static final long TIMEOUT = 1000L; 
    private static final String TEST_CACHE_NAME = "TEST_CACHE_NAME";
    private KratiStore store;
    private KratiStoreFactory storeFactory;

    DataStore<byte[], byte[]> dataStore = null;

    public KratiStoreTest() {
    	if(storeFactory == null){
    		storeFactory = new KratiStoreFactory();
    		storeFactory.setStoreDirectory("target/krati-store/");
    		storeFactory.setTimeout(TIMEOUT);
    	}
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if(store == null){
        	store = (KratiStore)storeFactory.open(TEST_CACHE_NAME);
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        storeFactory.close(store);
    }

    public void testStore() throws Exception {
        String id = "1";
        String data = "Test data";
        store.store(id,data);
        assertEquals(data,store.peek(id));
        assertEquals(data,store.load(id));
        assertEquals(null,store.load(id));
    }
    
    public void testTimeout() throws Exception {
        String id = store.store("Any kind of data...");
        Object data = store.load(id);
        assertNotNull(data);
        //now store it again and load it after the timeout
        store.store(id, data);
        synchronized (this) {
            wait(TIMEOUT * 2);
        }
        assertNull("Data should have been removed from store after timeout", store.load(id));
    }


}
