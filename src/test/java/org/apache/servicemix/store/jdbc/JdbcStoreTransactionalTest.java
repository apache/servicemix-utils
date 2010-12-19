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

import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import junit.framework.TestCase;

import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.StoreFactory;
import org.jencks.GeronimoPlatformTransactionManager;
import org.jencks.factory.ConnectionManagerFactoryBean;
import org.tranql.connector.AllExceptionsAreFatalSorter;
import org.tranql.connector.jdbc.AbstractXADataSourceMCF;

public class JdbcStoreTransactionalTest extends TestCase {

    private DataSource dataSource;
    private Connection connection;
    private StoreFactory factory;
    private GeronimoPlatformTransactionManager tm;

    protected void setUp() throws Exception {
        tm = new GeronimoPlatformTransactionManager();
        
        // Create an embedded database for testing tx results when commit / rollback
        ConnectionManagerFactoryBean cmFactory = new ConnectionManagerFactoryBean();
        cmFactory.setTransactionManager(tm);
        cmFactory.setTransaction("xa");
        cmFactory.afterPropertiesSet();
        ConnectionManager cm = (ConnectionManager) cmFactory.getObject();
        ManagedConnectionFactory mcf = new DerbyDataSourceMCF("target/testdb");
        dataSource = (DataSource) mcf.createConnectionFactory(cm);
        JdbcStoreFactory f = new JdbcStoreFactory();
        f.setTransactional(true);
        f.setDataSource(dataSource);
        factory = f;
    }
    
    protected void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
    
    public void testStoreAndLoad() throws Exception {
        Store store = factory.open("store");
        String id = store.store(new Integer(10));
        Integer i = (Integer) store.load(id);
        assertEquals(10, i.intValue());
        assertNull(store.load(id));
        assertNull(store.load("a"));
    }

    public void testStoreAndPeek() throws Exception {
        Store store = factory.open("store");
        String id = store.store(new Integer(10));
        Integer i = (Integer) store.peek(id);
        assertEquals(10, i.intValue());
        assertNotNull(store.peek(id));
        assertNull(store.load("a"));
    }

    public void testStoreAndLoadInOneTx() throws Exception {
        Store store = factory.open("store");
        tm.begin();
        String id = store.store(new Integer(10));
        Integer i = (Integer) store.load(id);
        assertEquals(10, i.intValue());
        assertNull(store.load(id));
        assertNull(store.load("a"));
        tm.commit();
    }

    public void testStoreAndLoadInTwoTx() throws Exception {
        Store store = factory.open("store");
        tm.begin();
        String id = store.store(new Integer(10));
        tm.commit();
        tm.begin();
        Integer i = (Integer) store.load(id);
        assertEquals(10, i.intValue());
        assertNull(store.load(id));
        tm.commit();
        assertNull(store.load("a"));
    }

    public void testStoreRollbackAndLoad() throws Exception {
        Store store = factory.open("store");
        tm.begin();
        String id = store.store(new Integer(10));
        tm.rollback();
        tm.begin();
        assertNull(store.load(id));
        tm.commit();
    }

    public void testStoreRollbackAndLoadNonTx() throws Exception {
        Store store = factory.open("store");
        tm.begin();
        String id = store.store(new Integer(10));
        tm.rollback();
        assertNull(store.load(id));
    }

    public static class DerbyDataSourceMCF extends AbstractXADataSourceMCF {
        private static final long serialVersionUID = 7971682207810098396L;
        protected DerbyDataSourceMCF(String dbName) {
            super(createXADS(dbName), new AllExceptionsAreFatalSorter());
        }
        public String getPassword() {
            return null;
        }
        public String getUserName() {
            return null;
        }
        protected static XADataSource createXADS(String dbName) {
            EmbeddedXADataSource xads = new EmbeddedXADataSource();
            xads.setDatabaseName(dbName);
            xads.setCreateDatabase("create");
            return xads;
        }
    }
    
}
