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

/**
 * Test case for {@link MemoryStoreFactory}
 */
public class MemoryStoreFactoryTest extends TestCase {
    
    private MemoryStoreFactory factory;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        factory = new MemoryStoreFactory();
    }
    
    public void testOpen() throws Exception {
        assertTrue(factory.open("store1") instanceof MemoryStore);
        factory.setTimeout(500);
        assertTrue(factory.open("store1") instanceof MemoryStore);
        assertTrue(factory.open("store2") instanceof TimeoutMemoryStore);
    }

}
