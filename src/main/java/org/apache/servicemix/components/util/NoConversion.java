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
package org.apache.servicemix.components.util;

/**
 * Column contents converter for SimpleFlatFileMarshaler that does 
 * no conversion at all
 * @author Juergen Mayrbaeurl
 * @since 3.2
 */
public class NoConversion implements ContentConverter {

    /**
     * No conversion of the column contents
     * @param contents Column contents
     * @return parameter contents
     */
    public final String convertToXml(String contents) {
        return contents;
    }

    /**
     * No conversion of the column contents
     * @param contents Column contents
     * @return parameter contents
     */
    public String convertToFlatFileContent(String contents) {
        return contents;
    }
}
