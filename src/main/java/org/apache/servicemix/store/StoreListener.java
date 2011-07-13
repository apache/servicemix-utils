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

/**
 * A listener interface for {@link Store} implementations.
 * @author: iocanel
 */
public interface StoreListener {

    /**
     * Method that is called each time an item is added.
     * @param id
     * @param data
     */
    public void onAdd(String id, Object data);

    /**
     * Method that is called each time an item is removed.
     * @param id
     * @param data
     */
    public void onRemove(String id, Object data);

    /**
     * Method that is called each time an item is evicted.
     * Please note that not all {@link Store}s support eviction.
     * @param id
     * @param data
     */
    public void onEvict(String id, Object data);
}
