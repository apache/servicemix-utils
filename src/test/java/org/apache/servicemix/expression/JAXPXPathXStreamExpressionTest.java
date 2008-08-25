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
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;

import junit.framework.TestCase;

import com.thoughtworks.xstream.XStream;

import org.apache.servicemix.jbi.jaxp.StringSource;
import org.apache.servicemix.tck.mock.MockMessageExchange;


/**
 * @version $Revision: 659786 $
 */
public class JAXPXPathXStreamExpressionTest extends TestCase {
    XStream xStream = new XStream();
    
    public void testMap() throws Exception {
        JAXPXPathXStreamExpression exp = new JAXPXPathXStreamExpression();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("key1", "value1");
        exp.setXPath("/header/map");
        assertExpression(exp, params, "<header>" + xStream.toXML(params) + "</header>");
    }

    protected void assertExpression(Expression expression, Object expected, String xml) throws MessagingException {
        MessageExchange exchange = new MockMessageExchange();
        NormalizedMessage message = exchange.createMessage();
        message.setContent(new StringSource(xml));
        Object value = expression.evaluate(exchange, message);
        assertEquals("Expression: " + expression, expected, value);
    }

}
