/**
 * Copyright 2016 ZTE Corporation.
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
package org.openo.commontosca.catalog.mdserver.csar.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.IOUtils;
import org.openo.commontosca.catalog.mdserver.backend.common.Constants;
import org.openo.commontosca.catalog.mdserver.backend.common.MdCommonException;
import org.openo.commontosca.catalog.mdserver.backend.common.MimeTypes;
import org.openo.commontosca.catalog.mdserver.backend.ids.ServiceTemplateId;
import org.openo.commontosca.catalog.mdserver.csar.entity.CsarMetaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipExporter {

  private static final Logger logger = LoggerFactory.getLogger(ZipExporter.class);
  private ServiceTemplateId serviceTemplateId;
  private ArchiveOutputStream zos;

  public ZipExporter(ServiceTemplateId id) {
    this.serviceTemplateId = id;
  } 

  public void createZip(OutputStream out, CsarMetaEntity csarInfo, String inputDir)
      throws ArchiveException, MdCommonException, IOException {
    zos = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);
    File file = Paths.get(inputDir, Constants.TOSCA_META).toFile();
    if (!file.exists()) {
      logger.info("start add manifest data");
      try {
        addManifest();
      } catch (IOException e) {
        throwMdCommonException("add manifest faild! errorMsg:" + e.getMessage());
      }
    }
    logger.info("start other files");
    try {
      addDirToZipArchive(inputDir, "");
    } catch (IOException e) {
      throwMdCommonException("add manifest faild! errorMsg:" + e.getMessage());
    }
    file = Paths.get(new File(inputDir).getPath(), Constants.CSAR_META).toFile();
    if (!file.exists()) {
      logger.info("start add csar meta data");
      try {
        addCsarMeta(csarInfo);
      } catch (IOException e) {
        throwMdCommonException("add csar meta faild! errorMsg:" + e.getMessage());
      }
    }
  }

  private void addManifest() throws IOException {
    zos.putArchiveEntry(new ZipArchiveEntry(Constants.TOSCA_META));
    PrintWriter pw = new PrintWriter(zos);
    pw.println("TOSCA-Meta-Version: 1.0");
    pw.println("CSAR-Version: 1.0");
    String versionString = "Created-By: Winery " + "zte";
    pw.println(versionString);
    pw.println("Entry-Definitions: " + "Definitions/" + serviceTemplateId.getId() + "."
        + MimeTypes.YAML.getValue());
    pw.println();
    // add definition
    pw.println(
        "Name: " + "Definitions/" + serviceTemplateId.getId() + "." + MimeTypes.YAML.getValue());
    pw.println("Content-Type: " + HttpMimeTypes.MIMETYPE_TOSCA_DEFINITIONS);
    pw.println();
    pw.flush();
    pw.close();
    zos.closeArchiveEntry();
  }

  private void addCsarMeta(CsarMetaEntity csarInfo) throws IOException {
    zos.putArchiveEntry(new ZipArchiveEntry(Constants.CSAR_META));
    StringBuffer buffer = new StringBuffer();
    buffer.append("Type:");
    buffer.append(csarInfo.getType());
    buffer.append("\n");

    buffer.append("Version:");
    buffer.append(csarInfo.getVersion());
    buffer.append("\n");

    buffer.append("Provider:");
    buffer.append(csarInfo.getProvider());

    PrintWriter pw = new PrintWriter(zos);
    pw.print(buffer.toString());
    pw.flush();
    pw.close();
    zos.closeArchiveEntry();
  }

  
  
  
  

  /**
   * add dir to zip
   * 
   * @param dir source dir
   * @param basePath relative Path of the casr package root
   * @throws IOException
   * @throws MdCommonException
   */
  private void addDirToZipArchive(String dir, String basePath)
      throws IOException, MdCommonException {
    File fileDir = new File(dir);
    if (!fileDir.exists()) {
      logger.warn("file not exist." + dir);
      return;
    }
    File[] files = fileDir.listFiles();
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      if (file.isDirectory()) {
        String subDir = basePath + file.getName() + "/";
        logger.info("add dir to archive:" + subDir);
        addFileToZipArchive(null, subDir);
        addDirToZipArchive(file.getPath(), subDir);
      } else {
        logger.info("add file to archive:" + basePath + file.getName());
        InputStream input = new FileInputStream(file);
        addFileToZipArchive(input, basePath + file.getName());
      }
    }
  }

  /**
   * add File to zip
   * 
   * @param inputStream input file stream
   * @param dest file
   * @throws MdCommonException
   */
  private void addFileToZipArchive(InputStream inputStream, String file) throws MdCommonException {
    try {
      zos.putArchiveEntry(new ZipArchiveEntry(file));
      if (inputStream != null)
        IOUtils.copy(inputStream, zos);
      zos.closeArchiveEntry();
    } catch (IOException e) {
      throwMdCommonException(
          "add file {} to zipArchive faild.file:" + file + "errorMsg:" + e.getMessage());
    } finally {
      if (inputStream != null)
        closeInputStream(inputStream);
    }
  }

  private void throwMdCommonException(String errorMsg) throws MdCommonException {
    logger.error(errorMsg);
    throw new MdCommonException(errorMsg);
  }

  private void closeInputStream(InputStream in) throws MdCommonException {
    try {
      in.close();
    } catch (IOException e) {
      throwMdCommonException("close InputStream faild.errorMsg:" + e.getMessage());
    }
  }

  public void closeZipArchive() throws IOException {
    zos.flush();
    zos.close();
  }

}
