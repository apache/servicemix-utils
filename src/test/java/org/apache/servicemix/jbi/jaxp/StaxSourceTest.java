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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import junit.framework.TestCase;

public class StaxSourceTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(StaxSourceTest.class);

    public void testStaxSourceOnStream() throws Exception {
        InputStream is = getClass().getResourceAsStream("test.xml");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        XMLStreamReader xsr = factory.createXMLStreamReader(is);
        StaxSource ss = new StaxSource(xsr);
        StringWriter buffer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(ss, new StreamResult(buffer));
        LOG.info(buffer.toString());

        /*
         * Attribute ordering is not preserved, so we can not compare the
         * strings
         * 
         * is = getClass().getResourceAsStream("test.xml");
         * ByteArrayOutputStream baos = new ByteArrayOutputStream();
         * FileUtil.copyInputStream(is, baos);
         * compare(baos.toString().replaceAll("\r", ""),
         * buffer.toString().replaceAll("\r", ""));
         */

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(buffer.toString().getBytes()));
        checkDomResult(doc);

        StringWriter buffer2 = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(buffer2));
        LOG.info(buffer2.toString());
    }

    public void testStaxSourceOnDOM() throws Exception {
        InputStream is = getClass().getResourceAsStream("test.xml");
        XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(is);
        StaxSource ss = new StaxSource(xsr);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMResult result = new DOMResult();
        transformer.transform(ss, result);
        assertNotNull(result.getNode());
        checkDomResult((Document) result.getNode());
    }

    public void testStaxToDOM() throws Exception {
        InputStream is = getClass().getResourceAsStream("test.xml");
        XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(is);
        StaxSource ss = new StaxSource(xsr);
        DOMSource src = (DOMSource) new SourceTransformer().toDOMSource(ss);
        assertNotNull(src);
        assertNotNull(src.getNode());
        checkDomResult((Document) src.getNode());
    }

    public void testEncoding() throws Exception {
        final String msg = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><hello>���</hello>";
        StringSource src = new StringSource(msg);
        DOMSource dom = new SourceTransformer().toDOMSource(src);
        StreamSource stream = new SourceTransformer().toStreamSource(dom);
        LOG.info(new SourceTransformer().toString(stream));
        SAXSource sax = new SourceTransformer().toSAXSource(dom);
        LOG.info(new SourceTransformer().toString(sax));
    }

    protected void checkDomResult(Document doc) {
        // Whitespace only elements must be preserved
        NodeList l = doc.getElementsByTagName("child4");
        assertEquals(1, l.getLength());
        assertEquals(1, l.item(0).getChildNodes().getLength());
        Text txt = (Text) l.item(0).getFirstChild();
        assertEquals("   ", txt.getData());

        // Check long string
        l = doc.getDocumentElement().getElementsByTagName("long");
        assertEquals(1, l.getLength());
        assertEquals(1, l.item(0).getChildNodes().getLength());
        txt = (Text) l.item(0).getFirstChild();
        StringBuffer expected = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    expected.append((char) ('0' + j));
                    expected.append((char) ('0' + k));
                    if (k != 9) {
                        expected.append(' ');
                    }
                }
                expected.append("\n");
            }
        }
        assertEquals(expected.toString(), txt.getData());
    }

    protected void compare(String s1, String s2) {
        char[] c1 = s1.toCharArray();
        char[] c2 = s2.toCharArray();
        for (int i = 0; i < c1.length; i++) {
            if (c1[i] != c2[i]) {
                fail("Expected '" + (int) c2[i] + "' but found '" + (int) c1[i] + "' at index " + i + ". Expected '" + build(c2, i)
                                + "' but found '" + build(c1, i) + "'.");
            }
        }
    }

    protected String build(char[] c, int i) {
        int min = Math.max(0, i - 10);
        int cnt = Math.min(20, c.length - min);
        return new String(c, min, cnt);
    }

}
