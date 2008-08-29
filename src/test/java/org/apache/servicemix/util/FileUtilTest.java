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
package org.apache.servicemix.util;

import java.io.File;

import junit.framework.TestCase;

public class FileUtilTest extends TestCase {

    private static final File WORKDIR = new File("target/servicemix-test");

    protected void setUp() throws Exception {
        FileUtil.deleteFile(WORKDIR);
        WORKDIR.mkdirs();
    }

    public void testDeleteFile() throws Exception {
        File f = new File(WORKDIR, "test.txt");
        assertFalse(f.exists());
        assertTrue(f.createNewFile());
        assertTrue(f.exists());
        assertFalse(f.isDirectory());
        assertTrue(f.isFile());
        assertTrue(FileUtil.deleteFile(f));
        assertFalse(f.exists());
    }

    /*
     * This test only works on windows, as writing to a file does not prevent
     * its deletion on unix systems.
     * 
     * public void testDeleteLockedFile() throws Exception { File f = new
     * File(WORKDIR, "test.txt"); assertFalse(f.exists()); OutputStream os = new
     * FileOutputStream(f); try { Writer w = new OutputStreamWriter(os);
     * w.write("hello"); w.flush(); assertTrue(f.exists());
     * assertFalse(FileUtil.deleteFile(f)); assertTrue(f.exists()); } finally {
     * os.close(); } assertTrue(f.exists()); assertTrue(FileUtil.deleteFile(f));
     * assertFalse(f.exists()); }
     */

    public void testDeleteDir() throws Exception {
        File f = new File(WORKDIR, "testdir");
        assertFalse(f.exists());
        assertTrue(f.mkdir());
        assertTrue(f.exists());
        assertTrue(f.isDirectory());
        assertFalse(f.isFile());
        assertTrue(FileUtil.deleteFile(f));
        assertFalse(f.exists());
    }

    /*
     * This test only works on windows, as writing to a file does not prevent
     * its deletion on unix systems.
     * 
     * public void testDeleteDirWithLockedFile() throws Exception { File f = new
     * File(WORKDIR, "testdir"); assertFalse(f.exists()); assertTrue(f.mkdir());
     * assertTrue(f.exists()); assertTrue(f.isDirectory());
     * assertFalse(f.isFile()); File f2 = new File(f, "test.txt");
     * assertFalse(f2.exists()); File f3 = new File(f, "test2.txt");
     * assertFalse(f3.exists()); assertTrue(f3.createNewFile());
     * assertTrue(f3.exists()); OutputStream os = new FileOutputStream(f2); try {
     * Writer w = new OutputStreamWriter(os); w.write("hello"); w.flush();
     * assertTrue(f2.exists()); assertFalse(FileUtil.deleteFile(f));
     * assertTrue(f.exists()); assertTrue(f2.exists()); } finally { os.close(); }
     * assertFalse(f3.exists()); assertTrue(f2.exists());
     * assertTrue(f.exists()); assertTrue(FileUtil.deleteFile(f));
     * assertFalse(f.exists()); }
     */

}
