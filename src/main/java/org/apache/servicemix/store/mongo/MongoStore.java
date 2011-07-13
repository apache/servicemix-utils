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

import com.mongodb.*;
import org.apache.servicemix.store.base.BaseStore;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * <p>
 * A store which uses MongoDB.
 * </p>
 *
 * @author iocanel
 * @author jbonofre
 */
public class MongoStore extends BaseStore {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MongoStore.class);

    private static final String ID = "_id";
    private static final String DATA = "data";
    private static final String TIMESTAMP = "_timestamp";

    private DB db;
    DBCollection collection;

    private Long timeout;

    /**
     * <p>
     * Constructor with the Mongo DB and collection name to use.
     * </p>
     *
     * @param db the Mongo DB.
     * @param collectionName the Mongo collection name.
     */
    public MongoStore(DB db, String collectionName) {
        this.db = db;
        this.collection = db.getCollection(collectionName);
    }

    /**
     * <p>
     * Constructor with the Mongo DB and collection name to use.
     * This constructor defines a connection timeout too.
     * </p>
     *
     * @param db the Mongo DB.
     * @param collectionName the Mongo collection name.
     * @param timeout the connection timeout.
     */
    public MongoStore(DB db, String collectionName, Long timeout) {
        this.db = db;
        this.collection = db.getCollection(collectionName);
        this.timeout = timeout;
    }

    /**
     * <p>
     * Returns true if feature is provided by the store, false else.
     * </p>
     *
     * @param feature the feature.
     * @return true if the given feature is provided by the store, false else.
     */
    public boolean hasFeature(String feature) {
        if (PERSISTENT.equals(feature) || CLUSTERED.equals(feature))
            return true;
        return false;
    }

    /**
     * <p>
     * Stores {@param data} to a {@link DBObject} with the given {@param id}.
     * </p>
     *
     * @param id the id of the object to store
     * @param data the object to store
     * @throws IOException
     */
    public void store(String id, Object data) throws IOException {
        DBObject object = new BasicDBObject();
        ObjectOutputStream out=null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            out = new ObjectOutputStream(buffer);
            out.writeObject(data);

            object.put(ID, id);
            object.put(DATA, buffer.toByteArray());
            object.put(TIMESTAMP, System.currentTimeMillis());
        } catch (Exception e) {
            throw (IOException) new IOException("Error storing object").initCause(e);
        } finally {
            if(out != null) {
                out.close();
            }
        }
        WriteResult result = collection.insert(object);
        // check result for errors
        if (result.getError() != null) {
            throw new IOException(result.getError());
        }

        fireAddedEvent(id,data);
    }

    /**
     * <p>
     * Stores {@param data} to a {@link DBObject} and return the generated ID.
     * </p>
     * 
     * @param data the object to store
     * @return the generated ID.
     * @throws IOException
     */
    public String store(Object data) throws IOException {
        DBObject object = new BasicDBObject();
        object.put(DATA, data);
        WriteResult result = collection.insert(object);
        // check result for errors
        if (result.getError() != null) {
            throw new IOException(result.getError());
        }
        return String.valueOf(result.getField(ID));
    }

    /**
     * <p>
     * Retrieves the data of the object with the given {@param id}.
     * </p>
     *
     * @param id the id of the object
     * @return the data object
     * @throws IOException
     */
    public Object load(String id) throws IOException {
        evict();
        Object obj = null;
        try {
            DBObject object = new BasicDBObject();
            object.put(ID, id);
            DBObject item = collection.findOne(object);
            WriteResult result = collection.remove(object);
            if (item == null) {
                throw new IOException("Could not find item with id " + id);
            }
            byte[] data = (byte[]) item.get(DATA);
            if (data != null) {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                try {
                    obj = ois.readObject();
                } finally {
                    ois.close();
                }
            }
            fireRemovedEvent(id,data);
        } catch (Exception e) {
            throw (IOException) new IOException("Error loading object").initCause(e);
        }
        return obj;
    }

    /**
     * <p>
     * Retrieves the data of the object with the given {@param id} without removing it.
     * </p>
     *
     * @param id the id of the object
     * @return the data object
     * @throws IOException
     */
    public Object peek(String id) throws IOException {
        evict();
        Object obj = null;
        try {
            DBObject object = new BasicDBObject();
            object.put(ID, id);
            DBObject item = collection.findOne(object);
            byte[] data = (byte[]) item.get(DATA);
            if (data != null) {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                try {
                    obj = ois.readObject();
                } finally {
                ois.close();
                }
            }
        } catch (Exception e) {
            throw (IOException) new IOException("Error loading object").initCause(e);
        }
        return obj;
    }

    /**
     * <p>
     * Removes objects that have been expired.
     * </p>
     */
    protected void evict() {
        if (timeout != null) {
            DBObject object = new BasicDBObject();
            object.put(TIMESTAMP, new BasicDBObject("&lt", System.currentTimeMillis() - timeout));
            DBCursor items = collection.find(object);
            WriteResult result = collection.remove(object);

            for (DBObject item : items) {
                String id = null;
                Object data = null;
                if (item != null) {

                    byte[] idBytes = (byte[]) item.get(ID);
                    byte[] dataBytes = (byte[]) item.get(DATA);

                    if (data != null) {
                        try {
                            ObjectInputStream ois = null;

                            ois = new ObjectInputStream(new ByteArrayInputStream(idBytes));
                            try {
                                id = (String) ois.readObject();
                            } finally {
                                ois.close();
                            }

                            ois = new ObjectInputStream(new ByteArrayInputStream(dataBytes));
                            try {
                                data = ois.readObject();
                                ois.close();
                            } finally {
                                ois.close();
                            }

                            if(id != null) {
                                fireEvictedEvent(id,data);
                            }

                        } catch (IOException e) {
                            LOG.error("Error evicting object from store",e);
                        } catch (ClassNotFoundException e) {
                            LOG.error("Error evicting object from store",e);
                        }
                    }
                }
            }
        }
    }
}
