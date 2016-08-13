/**
 * Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.winery.repository.ext.export.custom;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.eclipse.winery.model.tosca.Definitions;

public abstract class ExportFileGenerator {
    
    private static final String DEFAULTETYPE = "CSAR"; 
    
    /**
     * 
     * @param definition the TOSCA definition object defined by winery
     * @param out  zip outputStream
     * @return  array of file names
     */
    abstract public String[] makeFile(Definitions definition, OutputStream out);

    /**
     * 
     * @param zos zip outputStream
     * @param fileName 
     * @throws IOException
     */
    public void createEntry(ArchiveOutputStream zos, String fileName) throws IOException {
        zos.putArchiveEntry(new ZipArchiveEntry(fileName));
    }

    /**
     * 
     * @param zos
     * @throws IOException
     */
    public void closeEntry(ArchiveOutputStream zos) throws IOException {
        zos.closeArchiveEntry();
    }
    
    /**
     * 
     * @return file type
     */
    public String getType(){
        return ExportFileGenerator.DEFAULTETYPE;
    }

}
