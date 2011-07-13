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
package org.apache.servicemix.store;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * A Store is an interface representing a storage where objects can be
 * put and retrieved.  A store can support different features, mainly
 * persistence, clustered or transactional.
 * 
 *  A store is not designed to be a thread-safe map.  If a user tries to
 *  store an object with an existing id, the behavior is undefined.
 *  
 * @author gnodet
 */
public interface Store  {

    String PERSISTENT = "Persistent";
    
    String CLUSTERED = "Clustered";
    
    String TRANSACTIONAL = "Transactional";
    
    /**
     * Returns true if the store implementation supports the given feature.
     * @param name the feature to check
     * @return <code>true</code> if the feature is supported
     */
    boolean hasFeature(String name);
    
    /**
     * Put an object in the store under the given id.
     * This method must be used with caution and the behavior is
     * unspecified if an object already exist for the same id.
     *  
     * @param id the id of the object to store
     * @param data the object to store
     * @throws IOException if an error occurs
     */
    void store(String id, Object data) throws IOException;
    
    /**
     * Put an object into the store and return the unique id that
     * may be used at a later time to retrieve the object.
     * 
     * @param data the object to store
     * @return the id of the object stored
     * @throws IOException if an error occurs
     */
    String store(Object data) throws IOException;
    
    /**
     * Loads an object that has been previously stored under the specified key.
     * The object is removed from the store.
     * 
     * @param id the id of the object
     * @return the object, or <code>null></code> if the object could not be found
     * @throws IOException if an error occurs
     */
    Object load(String id) throws IOException;

    /**
     * Loads an object that has been previously stored under the specified key.
     * The object is not removed from the store.
     * 
     * @param id the id of the object
     * @return the object, or <code>null</code> if the object could not be found
     * @throws IOException if an error occurs
     */
    Object peek(String id) throws IOException;



    /***
     * Registers a {@link StoreListener}.
     * @param listener
     */
    void addListener(StoreListener listener);


    /***
     * Unregisters a {@link StoreListener}.
     * @param listener
     */
    void removeListener(StoreListener listener);

    /***
     * Lists all {@link StoreListener}s.
     */
    public Set<StoreListener> getStoreListeners();

}
