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

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import junit.framework.TestCase;

public class SourceTransformerTest extends TestCase {

    private SourceTransformer transformer = new SourceTransformer();
    
    /*
     * Test method for 'org.apache.servicemix.jbi.jaxp.SourceTransformer.toDOMNode(Source)'
     */
    public void testToDOMNodeSource() throws Exception {
        Node node = transformer.toDOMNode(new StringSource(
                "<definition xmlns:tns='http://foo.bar.com'><value>tns:bar</value></definition>"));
        
        assertNotNull(node);
        assertTrue(node instanceof Document);
        Document doc = (Document) node;
        Element e = (Element) doc.getDocumentElement().getFirstChild();
        QName q = createQName(e, e.getFirstChild().getNodeValue());
        assertEquals("http://foo.bar.com", q.getNamespaceURI());
    }
    
    public void testToDOMSourceFromStream() throws Exception {
        DOMSource domsource = transformer.toDOMSourceFromStream(new StringSource(
            "<definition xmlns:tns='http://foo.bar.com'><value>J�rgen</value></definition>"));
        assertNotNull(domsource);
    }

    /**
     * Creates a QName instance from the given namespace context for the given qualifiedName
     *
     * @param element       the element to use as the namespace context
     * @param qualifiedName the fully qualified name
     * @return the QName which matches the qualifiedName
     */
    public static QName createQName(Element element, String qualifiedName) {
        int index = qualifiedName.indexOf(':');
        if (index >= 0) {
            String prefix = qualifiedName.substring(0, index);
            String localName = qualifiedName.substring(index + 1);
            String uri = recursiveGetAttributeValue(element, "xmlns:" + prefix);
            return new QName(uri, localName, prefix);
        } else {
            String uri = recursiveGetAttributeValue(element, "xmlns");
            if (uri != null) {
                return new QName(uri, qualifiedName);
            }
            return new QName(qualifiedName);
        }
    }

    /**
     * Recursive method to find a given attribute value
     */
    public static String recursiveGetAttributeValue(Element element, String attributeName) {
        String answer = null;
        try {
            answer = element.getAttribute(attributeName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (answer == null || answer.length() == 0) {
            Node parentNode = element.getParentNode();
            if (parentNode instanceof Element) {
                return recursiveGetAttributeValue((Element) parentNode, attributeName);
            }
        }
        return answer;
    }

}
