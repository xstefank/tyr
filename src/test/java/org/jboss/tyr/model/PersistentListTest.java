/*
 * Copyright 2019 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.tyr.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jboss.tyr.TestUtils;

public class PersistentListTest {

    private static final String FILE_NAME = "list.txt";
    private static File persistentListFile = new File(TestUtils.TARGET_DIR, FILE_NAME);

    private final String ELEMENT = "element";

    private PersistentList persistentList;

    @BeforeClass
    public static void beforeClass() {
        TestUtils.deleteFileIfExists(persistentListFile);
    }

    @Before
    public void before() {
        persistentList = new PersistentList(persistentListFile);
    }

    @Test
    public void testIfFileWasCreated() {
        Assert.assertTrue(persistentListFile.exists());
    }

    @Test
    public void testFileContentLoading() throws IOException {
        FileWriter fw = new FileWriter(persistentListFile);
        fw.write(ELEMENT);
        fw.close();

        persistentList = new PersistentList(persistentListFile);
        Assert.assertTrue(persistentList.contains(ELEMENT));
    }

    @Test
    public void testAddElement() {
        persistentList.add(ELEMENT);

        Assert.assertTrue(persistentList.contains(ELEMENT));
        Assert.assertTrue(TestUtils.fileContainsLine(persistentListFile, ELEMENT));
    }

    @After
    public void after() {
        TestUtils.deleteFileIfExists(persistentListFile);
    }
}
