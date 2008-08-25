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
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public abstract class AbstractStreamReaderTest extends TestCase {
    
    public void testSingleElement(XMLStreamReader reader) throws Exception {
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertTrue(reader.hasNext());
        assertEquals("root", reader.getLocalName());
        assertEquals(1, reader.getNamespaceCount());
        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("urn:test", reader.getNamespaceURI(0));

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }

    public void testTextChild(XMLStreamReader reader) throws Exception {
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());

        assertEquals(1, reader.getNamespaceCount());
        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("urn:test", reader.getNamespaceURI(0));

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());

        assertEquals("Hello World", reader.getText());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }

    public void testMixedContent(XMLStreamReader reader) throws Exception {
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());

        assertEquals("root", reader.getLocalName());
        assertEquals(1, reader.getNamespaceCount());
        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("urn:test", reader.getNamespaceURI(0));

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("Hello World", reader.getText());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("element", reader.getLocalName());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals(" more text", reader.getText());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }

    public void testAttributes(XMLStreamReader reader) throws Exception {
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.ATTRIBUTE, reader.next());

        // first attribute
        assertEquals(2, reader.getAttributeCount());
        assertTrue(reader.getAttributePrefix(0) == null || reader.getAttributePrefix(0).equals(""));
        assertEquals("att1", reader.getAttributeLocalName(0));
        assertTrue(reader.getAttributeNamespace(0) == null || reader.getAttributeNamespace(0).equals(""));
        assertEquals("value1", reader.getAttributeValue(0));
        assertEquals("value1", reader.getAttributeValue("", "att1"));

        QName q = reader.getAttributeName(0);
        assertEquals("", q.getNamespaceURI());
        assertEquals("", q.getPrefix());
        assertEquals("att1", q.getLocalPart());

        // second attribute
        assertEquals("p", reader.getAttributePrefix(1));
        assertEquals("att2", reader.getAttributeLocalName(1));
        assertEquals("urn:test2", reader.getAttributeNamespace(1));
        assertEquals("value2", reader.getAttributeValue(1));
        assertEquals("value2", reader.getAttributeValue("urn:test2", "att2"));

        q = reader.getAttributeName(1);
        assertEquals("urn:test2", q.getNamespaceURI());
        assertEquals("p", q.getPrefix());
        assertEquals("att2", q.getLocalPart());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.ATTRIBUTE, reader.next());

        assertEquals(2, reader.getNamespaceCount());
        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("urn:test", reader.getNamespaceURI(0));
        assertEquals("p", reader.getNamespacePrefix(1));
        assertEquals("urn:test2", reader.getNamespaceURI(1));

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }

    public void testElementChild(XMLStreamReader reader) throws Exception {
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());

        assertEquals("root", reader.getLocalName());
        assertEquals("urn:test", reader.getNamespaceURI());
        assertEquals("", reader.getPrefix());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());

        assertEquals("child", reader.getLocalName());
        assertEquals("urn:test2", reader.getNamespaceURI());
        assertEquals("a", reader.getPrefix());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }
}
