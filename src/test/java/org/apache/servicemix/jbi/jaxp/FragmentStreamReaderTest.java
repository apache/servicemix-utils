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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class FragmentStreamReaderTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(FragmentStreamReaderTest.class);

    public void testStaxSource() throws Exception {
        InputStream is = getClass().getResourceAsStream("test.xml");
        XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(is);
        xsr = new ExtendedXMLStreamReader(xsr);
        xsr.nextTag();
        LOG.info(xsr.getName().toString());
        xsr.nextTag();
        LOG.info(xsr.getName().toString());
        XMLStreamReader fsr = new FragmentStreamReader(xsr);
        StaxSource ss = new StaxSource(fsr);
        StringWriter buffer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(ss, new StreamResult(buffer));
        LOG.info(buffer.toString());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(buffer.toString().getBytes()));
        StringWriter buffer2 = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(buffer2));
        LOG.info(buffer2.toString());
    }

}
