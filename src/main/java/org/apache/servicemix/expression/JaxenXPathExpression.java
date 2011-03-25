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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.apache.servicemix.jbi.jaxp.StringSource;
import org.jaxen.FunctionContext;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.springframework.beans.factory.InitializingBean;

/**
 * Evalutes an XPath expression on the given message using <a
 * href="http://jaxen.org/"/>Jaxen</a>
 * 
 * @version $Revision: 564900 $
 */
public class JaxenXPathExpression implements Expression, InitializingBean {
    
    private static final transient Logger LOG = LoggerFactory.getLogger(JaxenXPathExpression.class);

    private String xpath;

    private boolean useMessageContent = true;

    private SourceTransformer transformer = new SourceTransformer();

    private JaxenVariableContext variableContext = new JaxenVariableContext();

    private XPath xpathObject;

    private NamespaceContext namespaceContext;

    private FunctionContext functionContext;

    public JaxenXPathExpression() {
    }

    /**
     * A helper constructor to make a fully created expression. This constructor
     * will call the {@link #afterPropertiesSet()} method to ensure this POJO is
     * properly constructed.
     */
    public JaxenXPathExpression(String xpath) throws Exception {
        this.xpath = xpath;
        init();
    }

    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() throws JaxenException {
        if (xpathObject == null) {
            if (xpath == null) {
                throw new IllegalArgumentException("You must specify the xpath property");
            }
            xpathObject = createXPath(xpath);
            xpathObject.setVariableContext(variableContext);
            if (namespaceContext != null) {
                xpathObject.setNamespaceContext(namespaceContext);
            }
            if (functionContext != null) {
                xpathObject.setFunctionContext(functionContext);
            }
        }
    }

    public Object evaluate(MessageExchange exchange, NormalizedMessage message) throws MessagingException {
        try {
            Object object = getXMLNode(exchange, message);
            if (object == null) {
                return null;
            }
            synchronized (this) {
                variableContext.setExchange(exchange);
                variableContext.setMessage(message);
                return evaluateXPath(object);
            }
        } catch (TransformerException e) {
            throw new MessagingException(e);
        } catch (JaxenException e) {
            throw new MessagingException(e);
        } catch (ParserConfigurationException e) {
            throw new MessagingException(e);
        } catch (IOException e) {
            throw new MessagingException(e);
        } catch (SAXException e) {
            throw new MessagingException(e);
        }
    }

    public boolean matches(MessageExchange exchange, NormalizedMessage message) throws MessagingException {
        try {
            Object object = getXMLNode(exchange, message);
            if (object == null) {
                return false;
            }
            synchronized (this) {
                variableContext.setExchange(exchange);
                variableContext.setMessage(message);
                return evaluateXPathAsBoolean(object);
            }
        } catch (TransformerException e) {
            throw new MessagingException(e);
        } catch (JaxenException e) {
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
    public XPath getXpathObject() {
        return xpathObject;
    }

    public void setXpathObject(XPath xpathObject) {
        this.xpathObject = xpathObject;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
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

    public JaxenVariableContext getVariableContext() {
        return variableContext;
    }

    public void setVariableContext(JaxenVariableContext variableContext) {
        this.variableContext = variableContext;
    }

    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    public FunctionContext getFunctionContext() {
        return functionContext;
    }

    public void setFunctionContext(FunctionContext functionContext) {
        this.functionContext = functionContext;
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    protected final XPath createXPath(String xp) throws JaxenException {
        return new DOMXPath(xp);
    }

    protected Object evaluateXPath(Object object) throws JaxenException {
        return xpathObject.evaluate(object);
    }

    protected boolean evaluateXPathAsBoolean(Object object) throws JaxenException {
        return xpathObject.booleanValueOf(object);
    }

    protected Object getXMLNode(MessageExchange exchange, NormalizedMessage message) throws TransformerException, MessagingException,
                    ParserConfigurationException, IOException, SAXException {
        Node node = null;
        // ensure re-readability of the content if the expression also needs to
        // access the content
        if (useMessageContent) {
            enableContentRereadability(message);
        }
        if (message != null) {
            node = transformer.toDOMNode(message);
        } else {
            LOG.warn("Null message for exchange: " + exchange);
        }
        if (node == null) {
            // lets make an empty document to avoid Jaxen throwing a
            // NullPointerException
            node = transformer.createDocument();
        }
        return node;
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
