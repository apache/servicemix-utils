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

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;

import org.apache.servicemix.expression.PropertyExpression;

/**
 * Expression that returns the 'org.apache.servicemix.file.name' property on the
 * message added by a file extensions. Existing file extensions are by default
 * removed
 * 
 * @author Mayrbaeurl
 * @since 3.2
 */
public class FileExtensionPropertyExpression extends PropertyExpression {

    private final String extension;

    private boolean deleteExistingExtension = true;

    public FileExtensionPropertyExpression(String fileExtension) {
        super(DefaultFileMarshaler.FILE_NAME_PROPERTY);

        this.extension = fileExtension;
    }

    public FileExtensionPropertyExpression(String extension,
            boolean deleteExistingExtension) {
        super(DefaultFileMarshaler.FILE_NAME_PROPERTY);

        this.extension = extension;
        this.deleteExistingExtension = deleteExistingExtension;
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    public Object evaluate(MessageExchange exchange, NormalizedMessage message) throws MessagingException {
        Object result = super.evaluate(exchange, message);
        if ((result != null) && (result instanceof String)) {
            return this.removeExtension((String) result) + this.extension;
        } else {
            return result;
        }
    }

    private String removeExtension(String fileName) {
        String result = fileName;
        if (this.deleteExistingExtension && fileName != null && fileName.length() > 1) {
            int index = fileName.lastIndexOf('.');
            if (index != -1) {
                result = fileName.substring(0, index);
            }
        }
        return result;
    }
}
