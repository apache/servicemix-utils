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

import java.io.IOException;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunctionResolver;

import org.xml.sax.SAXException;

import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.apache.servicemix.jbi.jaxp.StringSource;
import org.springframework.beans.factory.InitializingBean;

/**
 * Evalutes an XPath expression on the given message using JAXP
 * 
 * @org.apache.xbean.XBean element="xpath"
 * 
 * @version $Revision: 654087 $
 */
public class JAXPXPathExpression implements Expression, InitializingBean {
    
    private String xpath;

    private boolean useMessageContent = true;

    private SourceTransformer transformer = new SourceTransformer();

    private MessageVariableResolver variableResolver = new MessageVariableResolver();

    private XPathExpression xPathExpression;

    private XPathFunctionResolver functionResolver;

    private NamespaceContext namespaceContext;

    private XPathFactory factory;

    public JAXPXPathExpression() {
    }

    /**
     * A helper constructor to make a fully created expression.
     */
    public JAXPXPathExpression(String xpath) {
        this.xpath = xpath;
    }

    /**
     * Compiles the xpath expression.
     */
    public void afterPropertiesSet() throws XPathExpressionException {
        if (xPathExpression == null) {
            if (xpath == null) {
                throw new IllegalArgumentException("You must specify the xpath property");
            }

            if (factory == null) {
                factory = XPathFactory.newInstance();
            }
            XPath xpathObject = factory.newXPath();
            xpathObject.setXPathVariableResolver(variableResolver);
            if (functionResolver != null) {
                xpathObject.setXPathFunctionResolver(functionResolver);
            }
            if (namespaceContext != null) {
                xpathObject.setNamespaceContext(namespaceContext);
            }
            xPathExpression = xpathObject.compile(xpath);
        }
    }

    /**
     * Evaluates the XPath expression and returns the string values for the XML items described
     * by that expression.
     *
     * Before evaluating the xpath expression, it will be compiled by calling
     * the {@link #afterPropertiesSet()} method.
     *
     * @param exchange MessageExchange to use on MessageVariableResolver
     * @param message  NormalizedMessage to use on MessageVariableResolver
     *
     * @return Object  Contains the string values for the XML items described by the provided XPath
     *                 expression
     */
    public Object evaluate(MessageExchange exchange, NormalizedMessage message) throws MessagingException {
        return evaluate(exchange, message, XPathConstants.STRING);
    }

    /**
     * Evaluates the XPath expression and the XML items described by that expression. The type is
     * determined by the returnType parameter.  
     *
     * Before evaluating the xpath expression, it will be compiled by calling
     * the {@link #afterPropertiesSet()} method.
     *
     * @param exchange    MessageExchange to use on MessageVariableResolver
     * @param message     NormalizedMessage to use on MessageVariableResolver
     * @param returnType  QName as defined by javax.xml.xpath.XPathConstants that describes the
     *                    desired type of the object to be retuned
     *
     * @return Object    Contains the XML items described by the provided XPath expression. The type is
     *                   determined by the returnType parameter.
     */
    public Object evaluate(MessageExchange exchange, NormalizedMessage message, QName returnType) throws MessagingException {
        try {
            afterPropertiesSet();
            Object object = getXMLNode(exchange, message);
            synchronized (this) {
                variableResolver.setExchange(exchange);
                variableResolver.setMessage(message);
                return evaluateXPath(object, returnType);
            }
        } catch (TransformerException e) {
            throw new MessagingException(e);
        } catch (XPathExpressionException e) {
            throw new MessagingException(e);
        } catch (ParserConfigurationException e) {
            throw new MessagingException(e);
        } catch (IOException e) {
            throw new MessagingException(e);
        } catch (SAXException e) {
            throw new MessagingException(e);
        }
    }

    // Properties
    // -------------------------------------------------------------------------
    public String getXPath() {
        return xpath;
    }

    public void setXPath(String xp) {
        this.xpath = xp;
    }

    public boolean isUseMessageContent() {
        return useMessageContent;
    }

    /**
     * Specifies whether or not the XPath expression uses the message content.
     * 
     * By default, this property is <code>true</code>, but you can set it to
     * <code>false</code> to avoid that the message content is converted to
     * {@link StringSource}
     * 
     * @param useMessageContent
     *            specify <code>false</code> if this expression does not
     *            access the message content
     */
    public void setUseMessageContent(boolean useMessageContent) {
        this.useMessageContent = useMessageContent;
    }

    public SourceTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(SourceTransformer transformer) {
        this.transformer = transformer;
    }

    public MessageVariableResolver getVariableResolver() {
        return variableResolver;
    }

    public void setVariableResolver(MessageVariableResolver variableResolver) {
        this.variableResolver = variableResolver;
    }

    public XPathFactory getFactory() {
        return factory;
    }

    public void setFactory(XPathFactory factory) {
        this.factory = factory;
    }

    public XPathFunctionResolver getFunctionResolver() {
        return functionResolver;
    }

    public void setFunctionResolver(XPathFunctionResolver functionResolver) {
        this.functionResolver = functionResolver;
    }

    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    protected Object evaluateXPath(Object object) throws XPathExpressionException {
        return xPathExpression.evaluate(object);
    }

    protected Object evaluateXPath(Object object, QName returnType) throws XPathExpressionException {
        return xPathExpression.evaluate(object, returnType);
    }

    protected XPathExpression getXPathExpression() {
        return xPathExpression;
    }

    protected Object getXMLNode(MessageExchange exchange, NormalizedMessage message) throws TransformerException, MessagingException,
                    ParserConfigurationException, IOException, SAXException {
        // ensure re-readability of the content if the expression also needs to
        // access the content
        if (useMessageContent) {
            enableContentRereadability(message);
        }
        return transformer.toDOMNode(message);
    }

    /**
     * Convert the given {@link NormalizedMessage} instance's content to a re-readable {@link javax.xml.transform.Source} This allows the
     * content to be read more than once (e.g. for XPath evaluation or auditing).
     *
     * @param message
     *            the {@link NormalizedMessage} to convert the content for
     * @throws MessagingException
     */
    public void enableContentRereadability(NormalizedMessage message) throws MessagingException {
        if (message.getContent() instanceof StreamSource) {
            try {
                String content = transformer.contentToString(message);
                if (content != null) {
                    message.setContent(new StringSource(content));
                }
            } catch (TransformerException e) {
                throw new MessagingException("Unable to convert message content into StringSource", e);
            } catch (ParserConfigurationException e) {
                throw new MessagingException("Unable to convert message content into StringSource", e);
            } catch (IOException e) {
                throw new MessagingException("Unable to convert message content into StringSource", e);
            } catch (SAXException e) {
                throw new MessagingException("Unable to convert message content into StringSource", e);
            }
        }
    }

}
