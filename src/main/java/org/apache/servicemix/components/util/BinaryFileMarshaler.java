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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.jbi.JBIException;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;

import org.apache.servicemix.util.FileUtil;
import org.apache.servicemix.util.jaf.StreamDataSource;

/**
 * A FileMarshaler that converts the given input stream into a binary
 * attachment.
 * 
 * @org.apache.xbean.XBean
 * @author Guillaume Nodet
 * @since 3.0
 */
public class BinaryFileMarshaler extends DefaultFileMarshaler {

    private String attachment = FILE_CONTENT;
    private String contentType = "application/octet-stream";

    /**
     * returns the key used to add the attachment to the message
     * 
     * @return the attachments name / key
     */
    public String getAttachment() {
        return this.attachment;
    }

    /**
     * sets the key of the attachment to use for adding a attachment to the
     * normalized message
     * 
     * @param attachment the new key to use
     */
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    /**
     * returns the content type to use / expect
     * 
     * @return the content type
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * sets the content type to use / expect
     * 
     * @param contentType the new content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void readMessage(MessageExchange exchange, NormalizedMessage message, InputStream in, String path)
        throws IOException, JBIException {
        File polledFile = new File(path);
        DataHandler handler = new DataHandler(new StreamDataSource(in, getContentType(), polledFile.getName()));
        message.addAttachment(attachment, handler);
        message.setProperty(FILE_NAME_PROPERTY, polledFile.getName());
        message.setProperty(FILE_PATH_PROPERTY, path);
    }

    public void writeMessage(MessageExchange exchange, NormalizedMessage message, OutputStream out,
                             String path) throws IOException, JBIException {
        DataHandler handler = message.getAttachment(attachment);
        if (handler == null) {
            throw new MessagingException("Could not find attachment: " + attachment);
        }
        InputStream is = handler.getInputStream();
        copyInputStream(is, out);
    }

    /**
     * Copy in stream to an out stream
     *
     * @param in
     * @param out
     * @throws IOException
     */
    protected static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        FileUtil.fastCopy(in, out);
    }
}
