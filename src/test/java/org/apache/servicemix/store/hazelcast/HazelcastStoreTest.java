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

import java.util.Map;

import junit.framework.TestCase;

import org.apache.servicemix.store.Entry;
import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.krati.KratiPersistenceProvider;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 *
 * @author iocanel
 */
public class HazelcastStoreTest extends TestCase {

    private static final long TIMEOUT = 250L; 

    private Store store;
    private Config config = new Config();


    private final HazelcastStoreFactory factory = new HazelcastStoreFactory();
    private HazelcastInstance hazelcastInstance=null;
    
    public HazelcastStoreTest() {
        super();
        config.getGroupConfig().setName("testgroup");
        config.getGroupConfig().setPassword("testpwd");
        this.hazelcastInstance =  Hazelcast.newHazelcastInstance(config);
        factory.setTimeout(TIMEOUT);
        factory.setHazelcastInstance(hazelcastInstance);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        store = factory.open("test");
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        factory.close(store);
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

    public void testDistributesStoreAndLoad() throws Exception {        
        //Create a new Hazelcast instance
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
        Map map = instance.getMap(HazelcastStoreFactory.STORE_PREFIX+".test");
        
        String id = "testId";
        String data = "testData";
        map.put(id,new Entry(data));
        Object result = store.load(id);
        assertNotNull(data);
        assertEquals(data, result);        
    }
    
    public void testInstanceWithPersistenceProvider() throws Exception {

		// Create a new HazelCast Instance configured to have persistence
		// provider
		Config config = new Config();
		config.getGroupConfig().setName("testgroup-peristence");
		config.getGroupConfig().setPassword("testpwd");
		MapStoreConfig mapConfig = new MapStoreConfig();
		mapConfig.setEnabled(true);
		mapConfig.setWriteDelaySeconds(0);
		//Instantiate the implementation provider
		KratiPersistenceProvider provider = new KratiPersistenceProvider("target/krati");

		mapConfig.setImplementation(provider);
		config.getMapConfig(
				HazelcastStoreFactory.STORE_PREFIX + ".test.persistence").setMapStoreConfig(mapConfig);
		HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);

		String id = "testId";
		String data = "testData";
		Map map = instance.getMap(HazelcastStoreFactory.STORE_PREFIX + ".test.persistence");
		map.put(id, data);

		Object retObj = provider.load(id);
		assertEquals(data, retObj);

		map.remove(id);
		assertNull(provider.load(id));
		
		provider.destroy();

	}
    
    
}
