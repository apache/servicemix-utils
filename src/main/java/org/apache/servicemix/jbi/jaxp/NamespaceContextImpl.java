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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * A simple namespace context with a clean xbean configuration.
 *
 * @org.apache.xbean.XBean element="namespace-context"
 *                         description="A NamespaceContext implementation"
 * @author gnodet
 * @version $Revision: 397796 $
 */
public class NamespaceContextImpl implements NamespaceContext {

    /**
     * map containing bound namespaces, keyed by their prefix. A LinkedHashMap
     * is used to ensure that {@link #getPrefix(String)} always returns the same
     * prefix, unless that prefix is removed.
     */
    private Map<String, String> namespaces;
    
    /**
     * Constructs a SimpleNamespaceContext with no parent context or namespace
     * declarations.
     */
    public NamespaceContextImpl() {
        this.namespaces = new LinkedHashMap<String, String>();
    }
    
    /**
     * Constructs a SimpleNamespaceContext with no parent context that contains
     * the specified prefixes.
     * 
     * @param namespaces A Map of namespace URIs, keyed by their prefixes.
     */
    public NamespaceContextImpl(Map<String, String> namespaces) {
        this.namespaces = new LinkedHashMap<String, String>(namespaces);
    }
    
    /**
     * @org.apache.xbean.Map entryName="namespace" keyName="prefix"
     * @return Returns the namespaces.
     */
    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    /**
     * @param namespaces The namespaces to set.
     */
    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces.clear();
        if (namespaces != null) {
            this.namespaces.putAll(namespaces);
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix argument was null");
        } else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
            return XMLConstants.XML_NS_URI;
        } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else if (namespaces.containsKey(prefix)) {
            String uri = namespaces.get(prefix);
            if (uri.length() == 0) {
                return null;
            } else {
                return uri;
            }
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
     */
    public String getPrefix(String nsURI) {
        if (nsURI == null) {
            throw new IllegalArgumentException("nsURI was null");
        } else if (nsURI.length() == 0) {
            throw new IllegalArgumentException("nsURI was empty");
        } else if (nsURI.equals(XMLConstants.XML_NS_URI)) {
            return XMLConstants.XML_NS_PREFIX;
        } else if (nsURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        }
        Iterator iter = namespaces.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String uri = (String) entry.getValue();
            if (uri.equals(nsURI)) {
                return (String) entry.getKey();
            }
        }
        if (nsURI.length() == 0) {
            return "";
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
     */
    public Iterator<String> getPrefixes(String nsURI) {
        if (nsURI == null) {
            throw new IllegalArgumentException("nsURI was null");
        } else if (nsURI.length() == 0) {
            throw new IllegalArgumentException("nsURI was empty");
        } else if (nsURI.equals(XMLConstants.XML_NS_URI)) {
            return Collections.singleton(XMLConstants.XML_NS_PREFIX).iterator();
        } else if (nsURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return Collections.singleton(XMLConstants.XMLNS_ATTRIBUTE).iterator();
        }
        Set<String> prefixes = null;
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            String uri = entry.getValue();
            if (uri.equals(nsURI)) {
                if (prefixes == null) {
                    prefixes = new HashSet<String>();
                }
                prefixes.add(entry.getKey());
            }
        }
        if (prefixes != null) {
            return Collections.unmodifiableSet(prefixes).iterator();
        } else if (nsURI.length() == 0) {
            return Collections.singleton("").iterator();
        } else {
            List<String> l = Collections.emptyList();
            return l.iterator();
        }
    }
    
}
