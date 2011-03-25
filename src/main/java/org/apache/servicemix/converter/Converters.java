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
package org.apache.servicemix.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * A set of helper methods and classes to convert a few basic types
 * NOTE: if this grows any bigger, we might want to consider moving to Camel @Converter's for this
 */
public class Converters {

    private Map<Class<?>, Converter<?>> converters = new HashMap<Class<?>, Converter<?>>();

    public Converters() {
        converters.put(Boolean.class, new BooleanConverter());
        converters.put(Integer.class, new IntegerConverter());
        converters.put(Long.class, new LongConverter());
    }

    /**
     * Convert an object to another type.  If the object is <code>null</code>, this method will always return
     * <code>null</code>.
     *
     * @param value the object to be converted
     * @param target the target object type
     * @return the converted object value or <code>null</code> if no suitable conversion was found
     */
    public<T> T as(Object value, Class<T> target) {
        if (value == null) {
            return null;
        }
        return getConverter(target).convert(value);

    }

    private<T> Converter<T> getConverter(Class<T> target) {
        return (Converter<T>) converters.get(target);
    }


    private interface Converter<T> {

        public T convert(Object value);

    }

    /*
     * {@link Converter} implementation for converting objects to Boolean
     */
    private class BooleanConverter implements Converter<Boolean> {

        public Boolean convert(Object value) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else {
                String string = value.toString();
                if ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string)) {
                    return Boolean.parseBoolean(string);
                } else {
                    return null;
                }
            }
        }
    }


    /*
     * {@link Converter} implementation for converting objects to Integer
     */
    private class IntegerConverter implements Converter<Integer> {

        public Integer convert(Object value) {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /*
     * {@link Converter} implementation for converting objects to Long
     */
    private class LongConverter implements Converter<Long> {

        public Long convert(Object value) {
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else if (value instanceof String) {
                try {
                    return Long.parseLong((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
