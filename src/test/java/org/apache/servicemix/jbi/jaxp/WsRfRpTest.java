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

import javax.xml.transform.Source;
import javax.xml.bind.util.JAXBSource;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

public class WsRfRpTest extends TestCase {

    public void test() throws Exception {
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        Source source = new JAXBSource(context, new ObjectFactory().createGetResourceProperty(new QName("urn", "local")));
        XMLStreamReader reader = new SourceTransformer().toXMLStreamReader(source);
        reader.nextTag();
        source = new StaxSource(new FragmentStreamReader(reader));

        @SuppressWarnings("unchecked")
        JAXBElement<QName> e = (JAXBElement<QName>) context.createUnmarshaller().unmarshal(source);
        assertNotNull(e.getValue());
        System.out.println(e.getValue());
    }

    @XmlRegistry
    public static class ObjectFactory {

        private final static QName _GetResourceProperty_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "GetResourceProperty");

        @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "GetResourceProperty")
        public JAXBElement<QName> createGetResourceProperty(QName value) {
            return new JAXBElement<QName>(_GetResourceProperty_QNAME, QName.class, null, value);
        }

    }
}
