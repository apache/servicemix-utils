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
package org.apache.servicemix.jbi.transformer;

import java.io.Reader;
import java.io.StringReader;

import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

import org.apache.servicemix.jbi.helper.MessageUtil;
import org.apache.servicemix.jbi.jaxp.SourceTransformer;

public class CopyTransformerTest extends TestCase {

    private CopyTransformer transformer = CopyTransformer.getInstance();
    
    public void testWithSAXSource() throws Exception {
        Reader r = new StringReader("<hello>world</hello>");
        Source src = new SAXSource(new InputSource(r));
        NormalizedMessage msg = copyMessage(src);
        r.close();
        new SourceTransformer().contentToString(msg);
    }
    
    public void testWithStreamSource() throws Exception {
        Reader r = new StringReader("<hello>world</hello>");
        Source src = new StreamSource(r);
        NormalizedMessage msg = copyMessage(src);
        r.close();
        new SourceTransformer().contentToString(msg);
    }
    
    protected NormalizedMessage copyMessage(Source src) throws Exception {
        NormalizedMessage from = new MessageUtil.NormalizedMessageImpl();
        NormalizedMessage to = new MessageUtil.NormalizedMessageImpl();
        from.setContent(src);
        transformer.transform(null, from, to);
        return to;
    }
    
}
