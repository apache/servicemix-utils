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
package org.apache.servicemix.store.base;

import org.apache.servicemix.store.Store;
import org.apache.servicemix.store.StoreListener;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author: iocanel
 */
public abstract class BaseStore implements Store, Serializable {

    protected final Set<StoreListener> storeListeners = new LinkedHashSet<StoreListener>();

    /**
     * Notify all registered {@link StoreListener}s that an item has been added.
     * @param id
     * @param data
     */
    public void fireAddedEvent(String id, Object data) {
        for(StoreListener listener:storeListeners) {
            listener.onAdd(id,data);
        }
    }

    /**
     * Notify all registered {@link StoreListener}s that an item has been removed.
     * @param id
     * @param data
     */
    public void fireRemovedEvent(String id, Object data) {
        for(StoreListener listener:storeListeners) {
            listener.onRemove(id, data);
        }
    }

    /**
     * Notify all registered {@link StoreListener}s that an item has been evicted.
     * @param id
     * @param data
     */
    public void fireEvictedEvent(String id, Object data) {
        for(StoreListener listener:storeListeners) {
            listener.onEvict(id, data);
        }
    }

    /***
     * Registers a {@link StoreListener}.
     * @param listener
     */
    public void addListener(StoreListener listener) {
        storeListeners.add(listener);
    }

    /***
     * Unregisters a {@link StoreListener}.
     * @param listener
     */
    public void removeListener(StoreListener listener) {
        storeListeners.remove(listener);
    }

    /***
     * Lists all {@link StoreListener}s.
     */
    public Set<StoreListener> getStoreListeners() {
        return storeListeners;
    }
}
