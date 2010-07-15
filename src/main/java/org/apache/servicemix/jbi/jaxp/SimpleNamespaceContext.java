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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SimpleNamespaceContext implements ExtendedNamespaceContext {

    private SimpleNamespaceContext parent;

    private Map namespaces;

    public SimpleNamespaceContext() {
        namespaces = new HashMap();
    }

    public SimpleNamespaceContext(SimpleNamespaceContext parent, Map namespaces) {
        this.parent = parent;
        this.namespaces = namespaces;
    }

    public SimpleNamespaceContext getParent() {
        return parent;
    }

    @SuppressWarnings("unchecked")
    public Iterator getPrefixes() {
        Set prefixes = new HashSet();
        for (SimpleNamespaceContext context = this; context != null; context = context.parent) {
            prefixes.addAll(context.namespaces.keySet());
        }
        return prefixes.iterator();
    }

    public String getNamespaceURI(String prefix) {
        String uri = (String) namespaces.get(prefix);
        if (uri == null && parent != null) {
            uri = parent.getNamespaceURI(prefix);
        }
        return uri;
    }

    public String getPrefix(String namespaceURI) {
        for (SimpleNamespaceContext context = this; context != null; context = context.parent) {
            for (Iterator it = context.namespaces.keySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getValue().equals(namespaceURI)) {
                    return (String) entry.getKey();
                }
            }
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        Set<String> prefixes = new HashSet<String>();
        for (SimpleNamespaceContext context = this; context != null; context = context.parent) {
            for (Iterator it = context.namespaces.keySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getValue().equals(namespaceURI)) {
                    prefixes.add((String)entry.getKey());
                }
            }
        }
        return prefixes.iterator();
    }

    @SuppressWarnings("unchecked")
    public void add(String prefix, String uri) {
        namespaces.put(prefix, uri);
    }
}
