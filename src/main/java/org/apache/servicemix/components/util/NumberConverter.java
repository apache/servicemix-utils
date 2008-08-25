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
 * Column converter for SimpleFlatFileMarshaler that converts string
 * representations of numbers. Zero values and blank columns in flat files can
 * be converted to empty strings (default setting)
 * 
 * @author Mayrbaeurl
 * @since 3.2
 */
public class NumberConverter implements ContentConverter {

    private boolean zeroValueIsNull = true;

    private boolean blankValueIsNull = true;

    // Implementation methods
    // -------------------------------------------------------------------------
    public String convertToXml(String contents) {
        if (contents != null) {
            int number = 0;
            if (!StringUtils.isBlank(contents)) {
                try {
                    number = Integer.parseInt(contents);
                } catch (NumberFormatException e) {
                    return contents;
                }
            } else {
                if (this.blankValueIsNull) {
                    return StringUtils.EMPTY;
                } else {
                    return contents;
                }
            }
            if ((this.zeroValueIsNull) && (number == 0)) {
                return StringUtils.EMPTY;
            } else {
                return String.valueOf(number);
            }
        } else {
            return null;
        }
    }

    public String convertToFlatFileContent(String contents) {
        return contents;
    }

    // Properties
    // -------------------------------------------------------------------------
    public final void setZeroValueIsNull(boolean zeroValueIsNull) {
        this.zeroValueIsNull = zeroValueIsNull;
    }

    public final void setBlankValueIsNull(boolean blankValueIsNull) {
        this.blankValueIsNull = blankValueIsNull;
    }

}
