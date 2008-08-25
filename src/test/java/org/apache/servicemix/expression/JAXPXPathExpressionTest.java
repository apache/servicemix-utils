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

import org.apache.servicemix.jbi.jaxp.SimpleNamespaceContext;
import org.apache.xalan.extensions.XPathFunctionResolverImpl;


/**
 * @version $Revision: 564607 $
 */
public class JAXPXPathExpressionTest extends XPathExpressionTest {

    /**
     * Note that this test only works on Java 5
     *
     * @throws Exception
     */
    public void testXPathUsingJAXP() throws Exception {
        boolean test = false;

        try {
            Class.forName("java.lang.annotation.AnnotationTypeMismatchException");
            test = true;
        } catch (ClassNotFoundException doNothing) {
            // Expected if not java 5
        }

        if (test) {
            assertExpression(new JAXPStringXPathExpression("/foo/bar/@xyz"), "cheese", "<foo><bar xyz='cheese'/></foo>");
            assertExpression(new JAXPStringXPathExpression("$name"), "James", "<foo><bar xyz='cheese'/></foo>");
        }
    }

    public void testUsingJavaExtensions() throws Exception {
        JAXPStringXPathExpression exp = new JAXPStringXPathExpression();
        exp.setXPath("java:org.apache.servicemix.expression.JAXPXPathExpressionTest.func(string(/header/value))");
        SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext();
        namespaceContext.add("java", "http://xml.apache.org/xalan/java");
        exp.setNamespaceContext(namespaceContext);
        exp.setFunctionResolver(new XPathFunctionResolverImpl());
        assertExpression(exp, "modified12", "<header><value>12</value></header>");
    }

    public static String func(String s) {
        return "modified" + s;
    }

}
