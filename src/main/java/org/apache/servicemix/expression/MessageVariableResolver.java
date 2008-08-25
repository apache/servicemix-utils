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
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

/**
 * A variable resolver for XPath expressions which support properties on the messge, exchange as well
 * as making system properties and environment properties available.
 *
 * @version $Revision: 564374 $
 */
public class MessageVariableResolver implements XPathVariableResolver {
    public static final String SYSTEM_PROPERTIES_NAMESPACE = "http://servicemix.org/xml/variables/system-properties";
    public static final String ENVIRONMENT_VARIABLES = "http://servicemix.org/xml/variables/environment-variables";

    private MessageExchange exchange;
    private NormalizedMessage message;

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

    public Object resolveVariable(QName name) {
        // should we use other namespaces maybe?
        String uri = name.getNamespaceURI();
        String localPart = name.getLocalPart();

        Object answer = null;

        if (uri == null || uri.length() == 0) {
            answer = message.getProperty(localPart);
            if (answer == null) {
                answer = exchange.getProperty(localPart);
            }
        } else if (uri.equals(SYSTEM_PROPERTIES_NAMESPACE)) {
            answer = System.getProperty(localPart);
        } else if (uri.equals(ENVIRONMENT_VARIABLES)) {
            answer = System.getProperty(System.getProperty(localPart));
        }
        return answer;
    }
}
