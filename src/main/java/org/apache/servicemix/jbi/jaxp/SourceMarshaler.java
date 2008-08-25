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

import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

/**
 * @version $Revision: 564374 $
 */
public class SourceMarshaler {
    
    private SourceTransformer transformer = new SourceTransformer();

    public Source asSource(String text) {
        return new StringSource(text);
    }

    public String asString(Source source) throws TransformerException {
        StringWriter buffer = new StringWriter();
        transformer.toResult(source, new StreamResult(buffer));
        return buffer.toString();
    }

    public SourceTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(SourceTransformer transformer) {
        this.transformer = transformer;
    }
}
