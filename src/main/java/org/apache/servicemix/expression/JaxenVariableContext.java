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

import java.util.HashMap;
import java.util.Map;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;

import org.jaxen.UnresolvableException;
import org.jaxen.VariableContext;

/**
 * A variable resolver for XPath expressions which support properties on the messge, exchange as well
 * as making system properties and environment properties available.
 *
 * @version $Revision: 564374 $
 */
public class JaxenVariableContext implements VariableContext {
    public static final String MESSAGE_NAMESPACE = "http://servicemix.org/xml/variables/message";

    public static final String EXCHANGE_NAMESPACE = "http://servicemix.org/xml/variables/exchange";

    public static final String SYSTEM_PROPERTIES_NAMESPACE = "http://servicemix.org/xml/variables/system-properties";

    public static final String ENVIRONMENT_VARIABLES_NAMESPACE = "http://servicemix.org/xml/variables/environment-variables";

    private MessageExchange exchange;

    private NormalizedMessage message;

    private Map<String, Object> variables;

    public MessageExchange getExchange() {
        return exchange;
    }

    public void setExchange(MessageExchange exchange) {
        this.exchange = exchange;
    }

    public NormalizedMessage getMessage() {
        return message;
    }

    public void setMessage(NormalizedMessage message) {
        this.message = message;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * Allows other variables to be added to the variable scope
     *
     * @param variables
     */
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Object getVariableValue(String uri, String prefix, String localPart) throws UnresolvableException {
        Object answer = null;

        if (uri == null || uri.length() == 0) {
            answer = message.getProperty(localPart);
            if (answer == null) {
                answer = exchange.getProperty(localPart);
            }
        } else if (uri.equals(MESSAGE_NAMESPACE)) {
            answer = message.getProperty(localPart);
        } else if (uri.equals(EXCHANGE_NAMESPACE)) {
            answer = message.getProperty(localPart);
        } else if (uri.equals(SYSTEM_PROPERTIES_NAMESPACE)) {
            answer = System.getProperty(localPart);
        } else if (uri.equals(ENVIRONMENT_VARIABLES_NAMESPACE)) {
            answer = System.getProperty(System.getProperty(localPart));
        }
        return answer;
    }

    /**
     * Allows a variable to be specified
     *
     * @param localPart
     * @param value
     */
    public void setVariableValue(String localPart, Object value) {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        variables.put(localPart, value);
    }

}
