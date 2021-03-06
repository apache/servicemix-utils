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
package org.apache.servicemix.expression;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
 
/**
 * An expression strategy for extracting or calculating some value from a message.
 *
 * @version $Revision: 564374 $
 */
public interface Expression {

    /**
     * Evaluates the expression on the given exchange and message.
     *
     * @param exchange the message exchange
     * @param message the message, typically an inbound message
     * @return the value of the expression
     */
    Object evaluate(MessageExchange exchange, NormalizedMessage message) throws MessagingException;
}
