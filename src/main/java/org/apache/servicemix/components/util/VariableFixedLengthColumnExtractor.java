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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Column extractor for SimpleFlatFileMarshaler that can extract columns from
 * fixed-length flat files that have a variable count of columns based on a
 * discriminator column value
 * 
 * @author Mayrbaeurl
 * @since 3.2
 */
public class VariableFixedLengthColumnExtractor implements ColumnExtractor {

    /**
     * Index of the discriminator column in the fixed length flat file
     */
    private int discriminatorIndex;

    /**
     * Index of the last column that every line of the flat file contains
     */
    private int lastFixedContentIndex;

    /**
     * Max count of columns
     */
    private int maximumColumnCount;

    /**
     * Fixed columns lengths
     */
    private int[] fixedColumnLengths;

    /**
     * Column lengths for discriminator values. Key is discriminator value.
     * Value is int[] for column lengths
     */
    private Map variableColumnLengths;

    // Implementation methods
    // -------------------------------------------------------------------------
    public String[] extractColumns(String lineText) {
        String[] result = new String[maximumColumnCount];
        int curIndex = 0;
        for (int i = 0; i <= lastFixedContentIndex; i++) {
            try {
                result[i] = lineText.substring(curIndex, curIndex + this.fixedColumnLengths[i]);
                curIndex += this.fixedColumnLengths[i];
            } catch (StringIndexOutOfBoundsException e) {
                return result;
            }
        }
        if (result.length > this.discriminatorIndex) {
            String discriminatorValue = result[this.discriminatorIndex];
            if (!StringUtils.isBlank(discriminatorValue) && (this.variableColumnLengths != null)
                            && (this.variableColumnLengths.containsKey(discriminatorValue))) {
                int[] variableLengths = (int[]) this.variableColumnLengths.get(discriminatorValue);

                int variableIndex = 0;
                for (int i = lastFixedContentIndex + 1; i < maximumColumnCount; i++, variableIndex++) {
                    try {
                        if (variableLengths[variableIndex] != -1) {
                            result[i] = lineText.substring(curIndex, curIndex + variableLengths[variableIndex]);

                            curIndex += variableLengths[variableIndex];
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        break;
                    }
                }
            }
        } else {
            throw new IllegalStateException("Discriminator Column could not be read");
        }
        return result;
    }

    // Properties
    // -------------------------------------------------------------------------
    public final void setFixedColumnLengths(int[] fixedColumnLengths) {
        this.fixedColumnLengths = fixedColumnLengths;
    }

    public final void setStringFixedColumnLengths(String[] lengths) {
        this.fixedColumnLengths = new int[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            this.fixedColumnLengths[i] = Integer.parseInt(lengths[i]);
        }
    }

    public final void setMaximumColumnCount(int maximumColumnCount) {
        this.maximumColumnCount = maximumColumnCount;
    }

    public final void setDiscriminatorIndex(int discriminatorIndex) {
        this.discriminatorIndex = discriminatorIndex;
    }

    public final void setLastFixedContentIndex(int lastFixedContentIndex) {
        this.lastFixedContentIndex = lastFixedContentIndex;
    }

    public final void setVariableColumnLengths(Map variableColumnLengths) {
        this.variableColumnLengths = variableColumnLengths;
    }

    @SuppressWarnings("unchecked")
    public final void setStringEncodedVariableColumnLengths(String columnLengths) {
        this.variableColumnLengths = null;
        String[] entries = StringUtils.splitWorker(columnLengths, ";", -1, false);
        if ((entries != null) && (entries.length > 0)) {
            this.variableColumnLengths = new LinkedHashMap();
            for (int i = 0; i < entries.length; i++) {
                String[] colLengths = StringUtils.splitWorker(entries[i], ",", -1, true);
                if ((colLengths != null) && (colLengths.length > 1)) {
                    int[] lengths = new int[colLengths.length - 1];
                    for (int j = 1; j < colLengths.length; j++) {
                        lengths[j - 1] = Integer.parseInt(colLengths[j]);
                    }
                    this.variableColumnLengths.put(colLengths[0], lengths);
                }
            }
        }
    }

}
