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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.security.auth.Subject;
import javax.xml.transform.Source;

public class MockNormalizedMessage implements NormalizedMessage {

    private Source content;
    private Map<String, Object> properties = new HashMap<String, Object>();
    private Map<String, DataHandler> attachments = new HashMap<String, DataHandler>();
    private Subject securitySubject;

    /**
     * @return the content
     */
    public Source getContent() {
        return this.content;
    }
    /**
     * @param content the content to set
     */
    public void setContent(Source content) {
        this.content = content;
    }
    /**
     * @return the securitySubject
     */
    public Subject getSecuritySubject() {
        return securitySubject;
    }
    /**
     * @param securitySubject the securitySubject to set
     */
    public void setSecuritySubject(Subject securitySubject) {
        this.securitySubject = securitySubject;
    }
    public void addAttachment(String id, DataHandler data) throws MessagingException {
        attachments.put(id, data);
    }
    public DataHandler getAttachment(String id) {
        return attachments.get(id);
    }
    public Set getAttachmentNames() {
        return attachments.keySet();
    }
    public Object getProperty(String name) {
        return properties.get(name);
    }
    public Set getPropertyNames() {
        return properties.keySet();
    }
    public void removeAttachment(String id) throws MessagingException {
        attachments.remove(id);
    }
    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }
    
}
