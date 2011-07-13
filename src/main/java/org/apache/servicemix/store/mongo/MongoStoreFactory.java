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
package org.apache.servicemix.store.mongo;

import com.mongodb.DB;
import com.mongodb.Mongo;
import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.StoreFactory;
import org.apache.servicemix.store.StoreListener;
import org.apache.servicemix.store.base.BaseStoreFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A store factory which use MongoDB.
 * </p>
 *
 * @author iocanel
 * @author jbonofre
 */
public class MongoStoreFactory extends BaseStoreFactory {

    private Mongo mongo;
    private DB db;

    private String host;
    private Integer port;

    private String username;
    private String password;

    private String database;

    private Long timeout;

    private Map<String, MongoStore> stores = new HashMap<String, MongoStore>();

    /**
     * <p>
     * Open a {@link MongoStore} using a new {@param collection}.
     * </p>
     *
     * @param collection the Mongo store collection.
     * @return the opened Mongo store.
     * @throws IOException
     */
    public synchronized Store open(String collection) throws IOException {
        String key = database + "/" + collection;
        MongoStore store = stores.get(key);
        if (store == null) {
            if (mongo == null) {
                if (host == null || port == null)
                    throw new IOException("MongoDB host and port are required.");
                mongo = new Mongo(host, port);
            }
            if (db == null) {
                if (database == null)
                    throw new IOException("MongoDB database name is required.");
                db = mongo.getDB(database);
            }
            // if credentials are provided
            if (username != null && password != null) {
                boolean authenticated = db.authenticate(username, password.toCharArray());
                if (!authenticated)
                    throw new IOException("MongoDB authentication failed.");
            }
            if (timeout != null)
                store = new MongoStore(db, collection, timeout);
            else store = new MongoStore(db, collection);

             for(StoreListener listener:storeListeners) {
                store.addListener(listener);
            }
            stores.put(key, store);
        }
        return store;
    }

    public synchronized void close(Store store) throws IOException {
        if (mongo != null)
            mongo.close();
    }

    public Mongo getMongo() {
        return mongo;
    }

    public void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
    
}
