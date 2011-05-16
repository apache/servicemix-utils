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

import javax.jbi.JBIException;


/**
 * An interface that defines a factory to create ServiceMixClient.
 * An implementation should be bound in the JNDI context
 * 
 * @author <a href="mailto:gnodet [at] apache.org">Guillaume Nodet</a>
 */
public interface ClientFactory {

    /**
     * Default location to where the object should be looked for in JNDI
     */
    String DEFAULT_JNDI_NAME = "org.apache.servicemix.jbi.ClientFactory";
    
    /**
     * Create a new client to interact with the JBI bus
     * 
     * @return a client
     * @throws JBIException if an error occurs
     */
    ServiceMixClient createClient() throws JBIException;
    
}
