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
package org.apache.servicemix.jbi.jaxp;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

/**
 * @version $Revision: 1.1 $
 */
public final class QNameHelper {

    private QNameHelper() {
    }

    public static String getQualifiedName(QName qname) {
        String prefix = qname.getPrefix();
        String localPart = qname.getLocalPart();
        if (prefix != null && prefix.length() > 0) {
            return prefix + ":" + localPart;
        }
        return localPart;
    }

    /**
     * Turns the given String into a QName using the current namespace context
     */
    public static QName asQName(NamespaceContext context, String text) {
        int idx = text.indexOf(':');
        if (idx >= 0) {
            String prefix = text.substring(0, idx);
            String localPart = text.substring(idx + 1);
            String uri = context.getNamespaceURI(prefix);
            return new QName(uri, localPart, prefix);
        } else {
            return new QName(text);
        }
    }
}
