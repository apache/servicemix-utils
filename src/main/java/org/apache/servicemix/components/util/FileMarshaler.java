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
package org.apache.servicemix.components.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jbi.JBIException;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;

/**
 * A pluggable strategy for turning a file or URL input source into
 * a normalized message.
 *
 * @version $Revision: 673769 $
 */
public interface FileMarshaler {

    /**
     * Converts the file stream to a normalized message.
     *
     * @param exchange the message exchange
     * @param message  the message to populate
     * @param in       the input stream
     * @param path     the name of the file, URI or URL
     */
    void readMessage(MessageExchange exchange, NormalizedMessage message, InputStream in, String path) throws IOException, JBIException;

    /**
     * Creates a output file name for the given exchange when reading an inbound
     * message.
     *
     * @param exchange the inbound message exchange
     * @param message the inbound message
     * @return the file name or null if a file name could not be found or calculated
     */
    String getOutputName(MessageExchange exchange, NormalizedMessage message) throws MessagingException;

    /**
     * creates a temporary file name for the given exchange when reading an inbound
     * message. This name is a (hopefully) unique name used for doing a first writing.
     * It will be renamed to the real output name after successfully finished transmission.
     * 
     * @param exchange the inbound message exchange
     * @param message the inbound message
     * @return the file name or null if a file name could not be found or calculated
     */
    String getTempOutputName(MessageExchange exchange, NormalizedMessage message) throws MessagingException;
    
    /**
     * Writes the inbound message to the destination stream of the given name
     *
     * @param exchange the inbound message exchange
     * @param message the inbound message
     * @param out the output stream to write to
     * @param path
     */ 
    void writeMessage(MessageExchange exchange, NormalizedMessage message, OutputStream out, String path) throws IOException, JBIException;
}
