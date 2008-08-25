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
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;

import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlSaxHandler;

/**
 * An {@link Expression} which evaluates an XPath expression using <a href="http://xmlbeans.apache.org/">XMLBeans</a>
 *
 * @version $Revision: 564374 $
 */
public class XMLBeansXPathExpression implements Expression {
    
    private String xpath;

    private XmlOptions options = new XmlOptions();

    private SourceTransformer transformer = new SourceTransformer();

    public XMLBeansXPathExpression(String xp) {
        this.xpath = xp;
    }

    public Object evaluate(MessageExchange exchange, NormalizedMessage message) throws MessagingException {
        try {
            XmlSaxHandler handler = XmlObject.Factory.newXmlSaxHandler();
            SAXResult result = new SAXResult(handler.getContentHandler());
            transformer.toResult(message.getContent(), result);
            XmlObject object = handler.getObject();
            return evaluateXPath(object, xpath, options);
        } catch (TransformerException e) {
            throw new MessagingException(e);
        } catch (XmlException e) {
            throw new MessagingException(e);
        }
    }

    protected Object evaluateXPath(XmlObject object, String xp, XmlOptions opts) {
        XmlObject[] xmlObjects = object.selectPath(xp, opts);
        if (xmlObjects.length == 1) {
            return xmlObjects[0];
        }
        return xmlObjects;
    }
}
