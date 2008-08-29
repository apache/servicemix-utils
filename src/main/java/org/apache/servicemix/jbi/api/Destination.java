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
package org.apache.servicemix.jbi.api;

import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOptionalOut;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.RobustInOnly;

/**
 * Represents a JBI endpoint you can communicate with
 * 
 * @version $Revision: $
 */
public interface Destination {

    /**
     * Creates an {@link InOnly} (one way) message exchange.
     * 
     * @return the newly created message exchange
     * @throws MessagingException
     */
    InOnly createInOnlyExchange() throws MessagingException;

    /**
     * Creates an {@link InOut} (request-reply) message exchange.
     * 
     * @return the newly created message exchange
     * @throws MessagingException
     */
    InOut createInOutExchange() throws MessagingException;

    /**
     * Creates an {@link InOptionalOut} (optional request-reply) message
     * exchange.
     * 
     * @return the newly created message exchange
     * @throws MessagingException
     */
    InOptionalOut createInOptionalOutExchange() throws MessagingException;

    /**
     * Creates an {@link RobustInOnly} (one way) message exchange.
     * 
     * @return the newly created message exchange
     * @throws MessagingException
     */
    RobustInOnly createRobustInOnlyExchange() throws MessagingException;

    /**
     * Allows a Message to be created for an {@link InOnly} exchange for simpler one-way messaging.
     * @throws MessagingException 
     */
    Message createInOnlyMessage() throws MessagingException;

}
