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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.DomReader;


/**
 *
 * @author Andrew Skiba <skibaa@gmail.com>
 */
public class JAXPXPathXStreamExpression extends JAXPXPathExpression {

    protected Logger logger = LoggerFactory.getLogger(JAXPXPathXStreamExpression.class);
    private XStream xStream;

    public JAXPXPathXStreamExpression() {
        super();
    }

    /**
     * A helper constructor to make a fully created expression.
     * @param xpath 
     */
    public JAXPXPathXStreamExpression(String xpath) {
        super(xpath);
    }


    @Override
    public Object evaluate(MessageExchange exchange, NormalizedMessage message) throws MessagingException {
        Object node = super.evaluate(exchange, message, XPathConstants.NODE);
        HierarchicalStreamReader streamReader;
        if (node instanceof Document) {
            streamReader = new DomReader((Document) node);
        } else if (node instanceof Element) {
            streamReader = new DomReader((Element) node);
        } else {
            throw new IllegalArgumentException("DOMResult contains neither Document nor Element: " + node.getClass().getName());
        }
        return getXStream().unmarshal(streamReader);
    }

    public XStream getXStream() {
        if (xStream == null) {
            xStream = createXStream();
        }
        return xStream;
    }

    public void setXStream(XStream xStream) {
        this.xStream = xStream;
    }

    //FIXME: copied as-is from XStreamMarshaler
    private XStream createXStream() {
        XStream answer = new XStream();
        try {
            answer.alias("invoke", Class.forName("org.logicblaze.lingo.LingoInvocation"));
        } catch (ClassNotFoundException e) {
            // Ignore
        }
        return answer;
    }
}
