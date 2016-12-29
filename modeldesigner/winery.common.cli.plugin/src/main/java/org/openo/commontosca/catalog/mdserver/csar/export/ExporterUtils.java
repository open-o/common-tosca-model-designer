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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import org.apache.commons.compress.archivers.ArchiveException;
import org.openo.commontosca.catalog.mdserver.backend.common.Constants;
import org.openo.commontosca.catalog.mdserver.backend.common.MdCommonException;
import org.openo.commontosca.catalog.mdserver.backend.ids.ServiceTemplateId;
import org.openo.commontosca.catalog.mdserver.csar.entity.CsarMetaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExporterUtils {

  private static final Logger logger = LoggerFactory.getLogger(ExporterUtils.class);
  
  public static boolean createCsarPackage(CsarMetaEntity csarInfo, String inputDir,
      String outPutDir, String serviceTemplateName) throws Exception {
    logger.info("start create csar package");
    ServiceTemplateId resource = new ServiceTemplateId(serviceTemplateName, csarInfo.getVersion());
    ZipExporter exporter = new ZipExporter(resource);
    OutputStream out = null;
    try {
      out = new FileOutputStream(
          Paths.get(outPutDir, serviceTemplateName + Constants.SUFFIX_CSAR).toFile());
      exporter.createZip(out, csarInfo, inputDir);
      exporter.closeZipArchive();
    } catch (ArchiveException | MdCommonException | IOException e) {
      throw e;
    } finally {
      if (out != null)
        out.close();
    }
    return true;
  }
}
