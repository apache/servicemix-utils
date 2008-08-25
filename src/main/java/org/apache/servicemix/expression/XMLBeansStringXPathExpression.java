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
package org.apache.servicemix.expression;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

/**
 * An {@link Expression} which evaluates an XPath expression using <a href="http://xmlbeans.apache.org/">XMLBeans</a> and
 * returns the String value.
 *
 * @version $Revision: 564374 $
 */
public class XMLBeansStringXPathExpression extends XMLBeansXPathExpression {
    public XMLBeansStringXPathExpression(String xpath) {
        super(xpath);
    }

    protected Object evaluateXPath(XmlObject object, String xpath, XmlOptions options) {
        XmlObject[] xmlObjects = object.selectPath(xpath);
        if (xmlObjects == null || xmlObjects.length == 0) {
            return "";
        } else if (xmlObjects.length == 1) {
            return asString(xmlObjects[0]);
        } else {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < xmlObjects.length; i++) {
                XmlObject xmlObject = xmlObjects[i];
                buffer.append(asString(xmlObject));
            }
            return buffer.toString();
        }
    }

    protected String asString(XmlObject xmlObject) {
        XmlCursor cursor = xmlObject.newCursor();
        return cursor.getTextValue();
    }
}
