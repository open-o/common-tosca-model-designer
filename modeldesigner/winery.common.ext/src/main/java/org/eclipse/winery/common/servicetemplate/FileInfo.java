/**
 * Copyright 2016 [ZTE] and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.winery.common.servicetemplate;

import org.eclipse.winery.common.RepositoryFileReference;


public class FileInfo implements Comparable<FileInfo> {
    private String path;
    private String fileName;
    private RepositoryFileReference ref;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public RepositoryFileReference getRef() {
        return ref;
    }

    public void setRef(RepositoryFileReference ref) {
        this.ref = ref;
    }

    public FileInfo() {
        super();
    }

    public FileInfo(String path, String fileName) {
        super();
        this.path = path;
        this.fileName = fileName;
    }

    @Override
    public int compareTo(FileInfo o) {
        int res;
        res = this.path.compareTo(o.path);
        if (res == 0) {
            res = this.fileName.compareTo(o.fileName);
        }
        return res;
    }
}
