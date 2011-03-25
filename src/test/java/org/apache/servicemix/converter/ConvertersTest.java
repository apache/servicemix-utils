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
package org.apache.servicemix.converter;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for {@link Converters}
 */
public class ConvertersTest {

    private final Converters converters = new Converters();

    @Test
    public void testIntegers() {
        assertEquals((Integer) 10, converters.as(10, Integer.class));
        assertEquals((Integer) 10, converters.as("10", Integer.class));
        assertEquals(null, converters.as("ILLEGAL_VALUE", Integer.class));
    }

    @Test
    public void testLongs() {
        assertEquals((Long) 10l, converters.as(10l, Long.class));
        assertEquals((Long) 10l, converters.as(10, Long.class));
        assertEquals((Long) 10l, converters.as("10", Long.class));
        assertEquals(null, converters.as("ILLEGAL_VALUE", Long.class));
    }

    @Test
    public void testBooleans() {
        assertEquals(true, converters.as(true, Boolean.class));
        assertEquals(false, converters.as("false", Boolean.class));
        assertEquals(null, converters.as("ILLEGAL_VALUE", Boolean.class));
    }
}
