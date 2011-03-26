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
package org.apache.servicemix.tck.mock;

import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOptionalOut;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.RobustInOnly;
import junit.framework.TestCase;

/**
 *
 * @author iocanel
 */
public class MockExchangeFactoryTest extends TestCase {

    public MockExchangeFactoryTest() {
    }
    
    /**
     * Test of createInOnlyExchange method, of class MockExchangeFactory.
     */
    public void testCreateInOnlyExchange() throws Exception {
        MockExchangeFactory instance = new MockExchangeFactory();
        InOnly exchange = instance.createInOnlyExchange();
        assertNotNull(exchange.getExchangeId());
        assertEquals(exchange.getPattern(), MockExchangeFactory.IN_ONLY);
    }

    /**
     * Test of createInOptionalOutExchange method, of class MockExchangeFactory.
     */
    public void testCreateInOptionalOutExchange() throws Exception {
        MockExchangeFactory instance = new MockExchangeFactory();
        InOptionalOut exchange = instance.createInOptionalOutExchange();
        assertNotNull(exchange.getExchangeId());
        assertEquals(exchange.getPattern(), MockExchangeFactory.IN_OPTIONAL_OUT);
    }

    /**
     * Test of createInOutExchange method, of class MockExchangeFactory.
     */
    public void testCreateInOutExchange() throws Exception {
        MockExchangeFactory instance = new MockExchangeFactory();
        InOut exchange = instance.createInOutExchange();
        assertNotNull(exchange.getExchangeId());
        assertEquals(exchange.getPattern(), MockExchangeFactory.IN_OUT);
    }

    /**
     * Test of createRobustInOnlyExchange method, of class MockExchangeFactory.
     */
    public void testCreateRobustInOnlyExchange() throws Exception {
        MockExchangeFactory instance = new MockExchangeFactory();
        RobustInOnly exchange = instance.createRobustInOnlyExchange();
        assertNotNull(exchange.getExchangeId());
        assertEquals(MockExchangeFactory.ROBUST_IN_ONLY,exchange.getPattern());
    }
}
