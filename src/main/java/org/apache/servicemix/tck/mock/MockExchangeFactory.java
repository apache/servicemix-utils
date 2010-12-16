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
package org.apache.servicemix.tck.mock;

import java.net.URI;

import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOptionalOut;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.RobustInOnly;
import javax.xml.namespace.QName;

public class MockExchangeFactory implements MessageExchangeFactory {
    
    public static final URI IN_ONLY = URI.create("http://www.w3.org/2004/08/wsdl/in-only");
    public static final URI IN_OUT = URI.create("http://www.w3.org/2004/08/wsdl/in-out");
    public static final URI IN_OPTIONAL_OUT = URI.create("http://www.w3.org/2004/08/wsdl/in-opt-out");
    public static final URI ROBUST_IN_ONLY = URI.create("http://www.w3.org/2004/08/wsdl/robust-in-only");
    
    public MessageExchange createExchange(QName serviceName, QName operationName) throws MessagingException {
        throw new UnsupportedOperationException();
    }
    public MessageExchange createExchange(URI pattern) throws MessagingException {
        String str = pattern.toString();
        if (str.startsWith("http://www.w3.org/2006/01/wsdl/")) {
            str = str.replace("http://www.w3.org/2006/01/wsdl/", "http://www.w3.org/2004/08/wsdl/");
            pattern = URI.create(str);
        }
        MessageExchange me;
        if (IN_ONLY.equals(pattern)) {
            me = createInOnlyExchange();
        } else if (IN_OUT.equals(pattern)) {
            me = createInOutExchange();
        } else if (IN_OPTIONAL_OUT.equals(pattern)) {
            me = createInOptionalOutExchange();
        } else if (ROBUST_IN_ONLY.equals(pattern)) {
            me = createRobustInOnlyExchange();
        } else {
            throw new IllegalArgumentException("Unhandled pattern: " + pattern);
        }
        ((MockMessageExchange) me).setPattern(pattern);
        return me;
    }
    public InOnly createInOnlyExchange() throws MessagingException {
        return new MockInOnly();
    }
    public InOptionalOut createInOptionalOutExchange() throws MessagingException {
        return new MockInOptionalOut();
    }
    public InOut createInOutExchange() throws MessagingException {
        return new MockInOut();
    }
    public RobustInOnly createRobustInOnlyExchange() throws MessagingException {
        return new MockRobustInOnly();
    }
    
    public static class MockInOnly extends MockMessageExchange implements InOnly {

        public MockInOnly() {
            super();
            setPattern(IN_ONLY);
        }
    }

    public static class MockInOut extends MockMessageExchange implements InOut {

        public MockInOut() {
            super();
            setPattern(IN_OUT);
        }
    }

    public static class MockInOptionalOut extends MockMessageExchange implements InOptionalOut {

        public MockInOptionalOut() {
            super();
            setPattern(IN_OPTIONAL_OUT);
        }
    }

    public static class MockRobustInOnly extends MockMessageExchange implements RobustInOnly {

        public MockRobustInOnly() {
            super();
            setPattern(ROBUST_IN_ONLY);
        }
    }
}
