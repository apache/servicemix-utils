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
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

/**
 * Evaluates an XPath expression and coerces the result into a String.
 * 
 * @org.apache.xbean.XBean element="xpathBoolean"
 * 
 * @version $Revision: 359151 $
 */
public class JAXPBooleanXPathExpression extends JAXPXPathExpression {

    public JAXPBooleanXPathExpression() {
    }

    public JAXPBooleanXPathExpression(String xpath) throws Exception {
        super(xpath);
    }

    public Object evaluateXPath(Object object) throws XPathExpressionException {
        return getXPathExpression().evaluate(object, XPathConstants.BOOLEAN);
    }

    public Object evaluate(MessageExchange exchange, NormalizedMessage message) throws MessagingException {
        return evaluate(exchange, message, XPathConstants.BOOLEAN);
    }
}
