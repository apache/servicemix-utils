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

import junit.framework.TestCase;

import org.apache.servicemix.store.Store;

/**
 * Test case for {@link TimeoutMemoryStore} 
 */
public class TimeoutMemoryStoreTest extends TestCase {
    
    private static final long TIMEOUT = 250L; 
    
    private Store store;
    private final MemoryStoreFactory factory = new MemoryStoreFactory();
    
    public TimeoutMemoryStoreTest() {
        super();
        factory.setTimeout(TIMEOUT);
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

}
