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
package org.apache.servicemix.tck.mock;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

public class MockMessageExchange implements MessageExchange {

    private ServiceEndpoint endpoint;
    private Exception error;
    private String exchangeId;
    private QName interfaceName;
    private QName operation;
    private URI pattern;
    private QName service;
    private MessageExchange.Role role;
    private ExchangeStatus status;
    private NormalizedMessage inMessage;
    private NormalizedMessage outMessage;
    private Fault fault;
    private Map<String, Object> properties = new HashMap<String, Object>();

    public MockMessageExchange() {
        exchangeId = UUID.randomUUID().toString();
    }

    /**
     * @return the endpoint
     */
    public ServiceEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(ServiceEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return the error
     */
    public Exception getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(Exception error) {
        this.error = error;
    }

    /**
     * @return the exchangeId
     */
    public String getExchangeId() {
        return exchangeId;
    }

    /**
     * @param exchangeId the exchangeId to set
     */
    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    /**
     * @return the fault
     */
    public Fault getFault() {
        return fault;
    }

    /**
     * @param fault the fault to set
     */
    public void setFault(Fault fault) {
        this.fault = fault;
    }

    /**
     * @return the in
     */
    public NormalizedMessage getInMessage() {
        return inMessage;
    }

    /**
     * @param in the in to set
     */
    public void setInMessage(NormalizedMessage in) {
        this.inMessage = in;
    }
    
    /**
     * @return the interfaceName
     */
    public QName getInterfaceName() {
        return interfaceName;
    }

    /**
     * @param interfaceName the interfaceName to set
     */
    public void setInterfaceName(QName interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * @return the operation
     */
    public QName getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(QName operation) {
        this.operation = operation;
    }

    /**
     * @return the out
     */
    public NormalizedMessage getOutMessage() {
        return outMessage;
    }

    /**
     * @param out the out to set
     */
    public void setOutMessage(NormalizedMessage out) {
        this.outMessage = out;
    }

    /**
     * @return the pattern
     */
    public URI getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(URI pattern) {
        this.pattern = pattern;
    }

    /**
     * @return the role
     */
    public MessageExchange.Role getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(MessageExchange.Role role) {
        this.role = role;
    }

    /**
     * @return the service
     */
    public QName getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(QName service) {
        this.service = service;
    }

    /**
     * @return the status
     */
    public ExchangeStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ExchangeStatus status) {
        this.status = status;
    }

    public Fault createFault() throws MessagingException {
        return new MockFault();
    }

    public NormalizedMessage createMessage() throws MessagingException {
        return new MockNormalizedMessage();
    }

    public NormalizedMessage getMessage(String name) {
        if ("in".equalsIgnoreCase(name)) {
            return getInMessage();
        } else if ("out".equalsIgnoreCase(name)) {
            return getOutMessage();
        } else if ("fault".equalsIgnoreCase(name)) {
            return getFault();
        }
        
        return null;
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public Set getPropertyNames() {
        return properties.keySet();
    }

    public boolean isTransacted() {
        return false;
    }

    public void setMessage(NormalizedMessage msg, String name) throws MessagingException {
        if ("in".equalsIgnoreCase(name)) {
            setInMessage(msg);
        } else if ("out".equalsIgnoreCase(name)) {
            setOutMessage(msg);
        } else if ("fault".equalsIgnoreCase(name)) {
            setFault((Fault) msg);
        }
    }

    public void setProperty(String name, Object obj) {
        properties.put(name, obj);
    }
    
    public static class MockFault extends MockNormalizedMessage implements Fault {
    }

}
