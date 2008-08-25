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
 * A helper class for working with expressions.
 * 
 * @version $Revision: 564374 $
 */
public final class ExpressionHelper {
    
    private ExpressionHelper() {
    }

    /**
     * Evaluates the given expression as a string value.
     * 
     * @param expression the expression to evaluate
     * @param exchange the current exchange
     * @param message the current message
     * @param defaultValue the default value to use if the expression is null or the value of the expression is null
     * @return the value of the expression as a string if it is not null or the defaultValue
     * @throws MessagingException if the expression failed to be evaluated
     */
    public static String asString(Expression expression, MessageExchange exchange, 
                                  NormalizedMessage message, String defaultValue) throws MessagingException {
        if (expression != null) {
            Object answer = expression.evaluate(exchange, message);
            if (answer != null) {
                return answer.toString();
            }
        }
        return defaultValue;
    }
}
