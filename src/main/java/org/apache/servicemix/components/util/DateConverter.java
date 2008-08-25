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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Column converter for SimpleFlatFileMarshaler that converts date contents
 * using defined date formats
 * 
 * @author Mayrbaeurl
 * @since 3.2
 */
public class DateConverter implements ContentConverter {

    public static final String DEFAULT_OUTPUT_FORMATSTRING = "yyyy-MM-dd'T'HH:mm:ss'.'SSS";

    /**
     * Flat file date format
     */
    private DateFormat inputformat;

    /**
     * XML Element date format to use
     */
    private DateFormat outputformat = new SimpleDateFormat(DEFAULT_OUTPUT_FORMATSTRING);

    private boolean blankValueIsNull = true;

    public DateConverter(DateFormat inputformat) {
        super();

        this.inputformat = inputformat;
    }

    public DateConverter(DateFormat inputformat, DateFormat outputformat) {
        super();

        this.inputformat = inputformat;
        this.outputformat = outputformat;
    }

    public DateConverter(String inputformat) {
        super();

        this.inputformat = new SimpleDateFormat(inputformat);
    }

    public DateConverter(String inputformat, String outputformat) {
        super();

        this.inputformat = new SimpleDateFormat(inputformat);
        this.outputformat = new SimpleDateFormat(outputformat);
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    public String convertToXml(String contents) {
        if (contents != null) {
            if (!StringUtils.isBlank(contents)) {
                try {
                    return this.outputformat.format(this.inputformat
                            .parse(contents));
                } catch (ParseException e) {
                    return contents;
                }
            } else {
                if (this.blankValueIsNull) {
                    return "";
                } else {
                    return contents;
                }
            }
        } else {
            return null;
        }
    }

    public String convertToFlatFileContent(String contents) {
        if (contents != null) {
            try {
                return this.inputformat.format(this.outputformat.parse(contents));
            } catch (ParseException e) {
                return contents;
            }
        } else {
            return null;
        }
    }

    // Properties
    // -------------------------------------------------------------------------
    public final void setInputformat(String format) {
        this.inputformat = new SimpleDateFormat(format);
    }

    public final void setOutpuformat(String format) {
        this.outputformat = new SimpleDateFormat(format);
    }

    public boolean isBlankValueIsNull() {
        return blankValueIsNull;
    }

    public void setBlankValueIsNull(boolean blankValueIsNull) {
        this.blankValueIsNull = blankValueIsNull;
    }

}
