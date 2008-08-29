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
package org.apache.servicemix.jbi.api;

import java.util.Map;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOptionalOut;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.RobustInOnly;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;


/**
 * Represents a client  API which allows users to programatically send messages into the JBI
 * container or to receive them using the regular JBI API together with a collection of helper methods making it
 * easier to interact with the JBI API.
 *
 * @version $Revision: 564374 $
 */
public interface ServiceMixClient {


    // Core JBI methods
    //-------------------------------------------------------------------------

    /**
     * Sends the message exchange to the endpoint.
     *
     * @param exchange
     * @throws MessagingException
     */
    void send(MessageExchange exchange) throws MessagingException;

    /**
     * Sends an In-Only message
     * 
     * @param message
     */
    void send(Message message) throws MessagingException;
    
    /**
     * Sends the message exchange to the endpoint, blocking until the send has completed.
     *
     * @param exchange
     * @throws MessagingException
     * @return true if the exchange has been processed and returned by the
     *  servicing component, false otherwise.
     */
    boolean sendSync(MessageExchange exchange) throws MessagingException;

    /**
     * Sends the message exchange to the endpoint, blocking until the send has completed
     * or the specified timeout has elapsed.
     *
     * @param exchange
     * @param timeout
     * @throws MessagingException
     * @return true if the exchange has been processed and returned by the
     *  servicing component, false otherwise.
     */
    boolean sendSync(MessageExchange exchange, long timeout) throws MessagingException;

    /**
     * Receives an inbound message exchange, blocking forever until one is available.
     *
     * @return the received message exchange
     * @throws MessagingException
     */
    MessageExchange receive() throws MessagingException;

    /**
     * Receives an inbound message exchange, blocking until the given timeout period.
     *
     * @param timeout the maximum amount of time to wait for a message
     * @return the received message exchange or null if the timeout occurred.
     * @throws MessagingException
     */
    MessageExchange receive(long timeout) throws MessagingException;


    /**
     * Performs a request-response (using an {@link InOut} to the endpoint denoted by the given resolver,
     * blocking until the response is received and then returning the result.
     *
     * @param resolver            the resolver used to resolve and choose the endpoint, which if null is used
     *                            then the container configured routing rules are used to dispatch the message to the destination
     * @param exchangeProperties  the properties used for the exchange or null if no properties are required
     * @param inMessageProperties the properties used for the in message or null if no properties are required
     * @param content             the body of the message
     * @throws JBIException if the message could not be dispatched for some reason.
     */
    Object request(EndpointResolver resolver, Map exchangeProperties, Map inMessageProperties, Object content) throws JBIException;

    /**
     * Sends a one way message exchange to the endpoint denoted by the given resolver
     *
     * @param resolver            the resolver used to resolve and choose the endpoint, which if null is used
     *                            then the container configured routing rules are used to dispatch the message to the destination
     * @param exchangeProperties  the properties used for the exchange or null if no properties are required
     * @param inMessageProperties the properties used for the in message or null if no properties are required
     * @param content             the body of the message
     * @throws JBIException if the message could not be dispatched for some reason.
     */
    void send(EndpointResolver resolver, Map exchangeProperties, Map inMessageProperties, Object content) throws JBIException;

    /**
     * Sends a one way message exchange to the endpoint denoted by the given resolver and blocks until the send is completed.
     *
     * @param resolver            the resolver used to resolve and choose the endpoint, which if null is used
     *                            then the container configured routing rules are used to dispatch the message to the destination
     * @param exchangeProperties  the properties used for the exchange or null if no properties are required
     * @param inMessageProperties the properties used for the in message or null if no properties are required
     * @param content             the body of the message
     * @return true if the exchange has been processed and returned by the
     *  servicing component, false otherwise.
     * @throws JBIException if the message could not be dispatched for some reason.
     */
    boolean sendSync(EndpointResolver resolver, Map exchangeProperties, Map inMessageProperties, Object content) throws JBIException;


    /**
     * Performs a request-response (using an {@link InOut} to the endpoint denoted by the given resolver,
     * blocking until the response is received and then returning the result.
     *
     * @param inMessageProperties the properties used for the in message or null if no properties are required
     * @param content             the body of the message
     * @throws JBIException if the message could not be dispatched for some reason.
     */
    Object request(Map inMessageProperties, Object content) throws JBIException;

    /**
     * Sends a one way message exchange to the endpoint denoted by the given resolver
     *
     * @param inMessageProperties the properties used for the in message or null if no properties are required
     * @param content             the body of the message
     * @throws JBIException if the message could not be dispatched for some reason.
     */
    void send(Map inMessageProperties, Object content) throws JBIException;

    /**
     * Sends a one way message exchange to the endpoint denoted by the given resolver and blocks until the send is completed.
     *
     * @param inMessageProperties the properties used for the in message or null if no properties are required
     * @param content             the body of the message
     * @return true if the exchange has been processed and returned by the
     *  servicing component, false otherwise.
     * @throws JBIException if the message could not be dispatched for some reason.
     */
    boolean sendSync(Map inMessageProperties, Object content) throws JBIException;



    // Factory methods to make MessageExchange instances
    //-------------------------------------------------------------------------

    /**
     * Creates an {@link InOnly} (one way) message exchange.
     *
     * @return the newly created message exchange
     * @throws MessagingException
     */
    InOnly createInOnlyExchange() throws MessagingException;

    /**
     * Creates an {@link InOnly} (one way) message exchange with the given resolver.
     *
     * @return the newly created message exchange
     * @throws MessagingException
     */
    InOnly createInOnlyExchange(EndpointResolver resolver) throws JBIException;

    /**
     * Creates an {@link InOut} (request-reply) message exchange.
     *
     * @return the newly created message exchange
     * @throws MessagingException
     */
    InOut createInOutExchange() throws MessagingException;

    /**
     * Creates an {@link InOut} (request-reply) message exchange with the given resolver.
     *
     * @return the newly created message exchange
     * @throws MessagingException
     */
    InOut createInOutExchange(EndpointResolver resolver) throws JBIException;

    /**
     * Creates an {@link InOptionalOut} (optional request-reply) message exchange.
     *
     * @return the newly created message exchange
     * @throws MessagingException
     */
    InOptionalOut createInOptionalOutExchange() throws MessagingException;

    /**
     * Creates an {@link InOptionalOut} (optional request-reply) message exchange with the given resolver.
     *
     * @return the newly created message exchange
     * @throws MessagingException
     */
    InOptionalOut createInOptionalOutExchange(EndpointResolver resolver) throws JBIException;

    /**
     * Creates an {@link RobustInOnly} (one way) message exchange.
     *
     * @return the newly created message exchange
     * @throws MessagingException
     */
    RobustInOnly createRobustInOnlyExchange() throws MessagingException;

    /**
     * Creates an {@link RobustInOnly} (one way) message exchange with the given resolver.
     *
     * @return the newly created message exchange
     * @throws MessagingException
     */
    RobustInOnly createRobustInOnlyExchange(EndpointResolver resolver) throws JBIException;




    /**
     * Resolves a WS-Addressing endpoint reference String into a JBI {@link javax.jbi.servicedesc.ServiceEndpoint}
     * reference so that message exchanges can be directed to an endpoint
     *
     * @param uri the WS-Addressing endpoint reference string
     */
    ServiceEndpoint resolveEndpointReference(String uri);


    // Helper methods to get an endpoint resolver
    //-------------------------------------------------------------------------

    /**
     * Creates an endpoint resolver for the given service name
     *
     * @param service
     * @return the newly created entity resolver
     */
    EndpointResolver createResolverForService(QName service);

    /**
     * Creates an endpoint resolver for the given interface name
     *
     * @param interfaceName
     * @return the newly created entity resolver
     */
    EndpointResolver createResolverInterface(QName interfaceName);

    /**
     * Creates an endpoint for the given external service name
     *
     * @param service
     * @return the newly created entity resolver
     */
    EndpointResolver createResolverForExternalService(QName service);

    /**
     * Creates an endpoint for the given external interface
     *
     * @param interfaceName
     * @return the newly created entity resolver
     */
    EndpointResolver createResolverForExternalInterface(QName interfaceName);

    /**
     * Creates an endpoint resolver for the given service and endpoint name
     *
     * @param service
     * @param endpoint
     * @return the newly created entity resolver
     */
    EndpointResolver createResolverForExternalInterface(QName service, String endpoint);


    // Create a destination
    //-------------------------------------------------------------------------
    
    /**
     * Creates a destination which represents some JBI endpoint that message exchanges can be created with.
     * @throws MessagingException 
     */
    Destination createDestination(String uri) throws MessagingException;
    

    // Helper methods and access to the JBI context information
    //-------------------------------------------------------------------------


    /**
     * A helper method to indicate that the message exchange is complete
     * which will set the status to {@link ExchangeStatus#DONE} and send the message
     * on the delivery channel.
     *
     * @param exchange
     * @throws MessagingException
     */
    void done(MessageExchange exchange) throws MessagingException;

    /**
     * A helper method which fails and completes the given exchange with the specified fault
     */
    void fail(MessageExchange exchange, Fault fault) throws MessagingException;

    /**
     * A helper method which fails and completes the given exchange with the specified exception
     */
    void fail(MessageExchange exchange, Exception error) throws MessagingException;

        
        /**
     * Returns the current component context which can be used to activate endpoints, components and
     * query the available service endpoints.
     *
     * @return the component context
     */
    ComponentContext getContext();

    /**
     * Returns the delivery channel for this client's message exchanges
     *
     * @return the delivery channel on which all this clients exchanges will occur.
     * @throws MessagingException
     */
    DeliveryChannel getDeliveryChannel() throws MessagingException;

    /**
     * Returns the default message exchange factory.
     *
     * @return the default message exchange factory.
     * @throws MessagingException
     */
    MessageExchangeFactory getExchangeFactory() throws MessagingException;


    /**
     * Close this client.
     * 
     * @throws JBIException
     */
    void close() throws JBIException;


}
