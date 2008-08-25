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

import junit.framework.TestCase;

import org.apache.servicemix.jbi.jaxp.StringSource;
import org.apache.servicemix.tck.mock.MockMessageExchange;

/**
 * @version $Revision: 564607 $
 */
public class XPathExpressionTest extends TestCase {

    public void testXPathUsingJaxen() throws Exception {
        assertExpression(new JaxenStringXPathExpression("foo/bar"), "cheese", "<foo><bar>cheese</bar></foo>");
        assertExpression(new JaxenStringXPathExpression("foo/bar/@xyz"), "cheese", "<foo><bar xyz='cheese'/></foo>");
        assertExpression(new JaxenStringXPathExpression("$name"), "James", "<foo><bar xyz='cheese'/></foo>");
        assertExpression(new JaxenStringXPathExpression("foo/bar/text()"), "cheese", "<foo><bar>cheese</bar></foo>");
    }

    public void testXPathUsingXMLBeans() throws Exception {
        assertExpression(new XMLBeansStringXPathExpression("foo/bar"), "cheese", "<foo><bar>cheese</bar></foo>");
        assertExpression(new XMLBeansStringXPathExpression("foo/bar/@xyz"), "cheese", "<foo><bar xyz='cheese'/></foo>");

        // These are way too complex for XMLBeans! :)
        //assertExpression(new XMLBeansStringXPathExpression("$name"), "James", "<foo><bar xyz='cheese'/></foo>");
        //assertExpression(new XMLBeansStringXPathExpression("foo/bar/text()"), "cheese", "<foo><bar>cheese</bar></foo>");
    }

    protected void assertExpression(Expression expression, String expected, String xml) throws MessagingException {
        MessageExchange exchange = new MockMessageExchange();
        NormalizedMessage message = exchange.createMessage();
        message.setProperty("name", "James");
        message.setContent(new StringSource(xml));
        Object value = expression.evaluate(exchange, message);
        assertEquals("Expression: " + expression, expected, value);
    }

}
