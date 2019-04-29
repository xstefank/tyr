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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.UnaryOperator;
import org.jboss.logging.Logger;

public class PersistentList extends ArrayList<String> {

    private static final Logger log = Logger.getLogger(PersistentList.class);

    private File file;

    public PersistentList(File file) {
        super();
        this.file = file;
        if (file.exists()) {
            loadFileContent(file);
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create new file with name " + file.getName(), e);
        }
    }

    public PersistentList(String dirName, String fileName) {
        this(new File(dirName, fileName));
    }

    @Override
    public boolean add(String s) {
        if (!super.add(s)) {
            return false;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println(s);
        } catch (IOException e) {
            log.error(e);
            super.remove(s);
            return false;
        }
        return true;
    }

    @Override
    public void add(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(UnaryOperator<String> operator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String set(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super String> c) {
        throw new UnsupportedOperationException();
    }

    private void loadFileContent(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    super.add(line);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot access file " + file.getPath(), e);
        }
    }
}
