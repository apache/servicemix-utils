/*
 * Copyright 2011 iocanel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
package org.apache.servicemix.store;

import java.io.Serializable;

/**
 *
 * @author iocanel
 */
public class Entry implements Serializable {

    private final long time = System.currentTimeMillis();
    private final Object data;

    public Entry(Object data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Entry{" + "time=" + time + ", data=" + data + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entry other = (Entry) obj;
        if (this.time != other.time) {
            return false;
        }
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.time ^ (this.time >>> 32));
        hash = 53 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }
    
    
}
