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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;

import org.apache.servicemix.store.base.BaseStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcStore extends BaseStore {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcStore.class);

    private JdbcStoreFactory factory;
    private String name;
    
    public JdbcStore(JdbcStoreFactory factory, String name) {
        this.factory = factory;
        this.name = name;
    }

    public boolean hasFeature(String feature) {
        return PERSISTENT.equals(feature) 
            || (CLUSTERED.equals(feature) && factory.isClustered())
            || (TRANSACTIONAL.equals(feature) && factory.isTransactional());
    }

    public void store(String id, Object data) throws IOException {
        LOG.debug("Storing object with id: " + id);
        Connection connection = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(data);
            out.close();
            connection = factory.getDataSource().getConnection();
            factory.getAdapter().doStoreData(connection, name + ":" + id, buffer.toByteArray());
            fireAddedEvent(id,data);
        } catch (Exception e) {
            throw (IOException) new IOException("Error storing object").initCause(e);
        } finally {
            close(connection);
        }
    }

    public String store(Object data) throws IOException {
        String id = factory.getIdGenerator().generateId();
        store(id, data);
        return id;
    }

    public Object load(String id) throws IOException {
        LOG.debug("Loading object with id: " + id);
        Connection connection = null;
        try {
            connection = factory.getDataSource().getConnection();
            byte[] data = factory.getAdapter().doLoadData(connection, name + ":" + id);
            Object result = null;
            if (data != null) {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                result = ois.readObject();
                factory.getAdapter().doRemoveData(connection, name + ":" + id);
                fireRemovedEvent(id, data);
            }
            return result;
        } catch (Exception e) {
            throw (IOException) new IOException("Error loading object").initCause(e);
        } finally {
            close(connection);
        }
    }

    public Object peek(String id) throws IOException {
        LOG.debug("Peeking object with id: " + id);
        Connection connection = null;
        try {
            connection = factory.getDataSource().getConnection();
            byte[] data = factory.getAdapter().doLoadData(connection, name + ":" + id);
            Object result = null;
            if (data != null) {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                result = ois.readObject();
            }
            return result;
        } catch (Exception e) {
            throw (IOException) new IOException("Error loading object").initCause(e);
        } finally {
            close(connection);   
        }
    }

    protected void close(Connection connection) throws IOException {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                throw (IOException) new IOException("Error closing connection").initCause(e);
            }
        }
    }

}
