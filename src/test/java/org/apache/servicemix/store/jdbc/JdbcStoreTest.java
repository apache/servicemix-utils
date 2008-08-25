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
package org.apache.servicemix.store.jdbc;

import java.sql.Connection;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.StoreFactory;
import org.hsqldb.jdbc.jdbcDataSource;

public class JdbcStoreTest extends TestCase {

    private DataSource dataSource;
    private Connection connection;
    private StoreFactory factory;

    protected void setUp() throws Exception {
        jdbcDataSource ds = new jdbcDataSource();
        ds.setDatabase("jdbc:hsqldb:mem:aname");
        ds.setUser("sa");
        dataSource = ds;
        connection = dataSource.getConnection();
        JdbcStoreFactory f = new JdbcStoreFactory();
        f.setDataSource(dataSource);
        factory = f;
    }
    
    protected void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public void testStoreLoad() throws Exception {
        Store store = factory.open("store");
        String id = store.store(new Integer(10));
        Integer i = (Integer) store.load(id);
        assertEquals(10, i.intValue());
        assertNull(store.load("a"));
    }
}
