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
 * Interface for the conversion of columns content to XML and vice versa 
 * used by the SimpleFlatFileMarshaler
 * @author Mayrbaeurl
 * @since 3.2
 */
public interface ContentConverter {
    
    /**
     * Converts the contents of column to the contents of the XML element
     * @param contents contents of the flat file column
     * @return contents for the XML element
     */
    String convertToXml(String contents);

    /**
     * Converts the contents of a XML element to the contents of the flat file column
     * @param contents contents of the XML element
     * @return contents for the flat file
     */
    String convertToFlatFileContent(String contents);
}
