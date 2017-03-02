/**
 * Copyright 2017 ZTE Corporation.
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
package org.openo.commontosca.catalog.verification.mdserver.cmd.copy;

import java.io.File;
import java.io.IOException;

import org.openo.commontosca.catalog.verification.mdserver.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VerifyUtils {

  private static final Logger logger = LoggerFactory.getLogger(VerifyUtils.class);
  
  public static boolean verifyCsarPackage(String inputDir, String packageName) throws Exception {
    String path = inputDir + File.separator + packageName + Constants.SUFFIX_CSAR;
//    File csarFile = new File(path);
//    if (!csarFile.exists()) {
//      return Response.status(Status.NOT_FOUND).build();
//    }
    String tempfolder = inputDir + File.separator + packageName;
    boolean defDirExist = false;
    boolean toscaMetaExist = false;
    try {
      CliUtil.unzip(path, tempfolder);
    } catch (IOException e1) {
      logger.error("Unzip package error ! " + e1.getMessage());
    }
    String definitionDir = tempfolder + File.separator + Constants.DEFINITIONS_PATH;
    defDirExist = verifyDir(definitionDir);
    String toscaMetaDir = tempfolder + File.separator + Constants.TOSCA_META;
    toscaMetaExist = verifyFile(toscaMetaDir);
    CliUtil.deleteDir(new File(tempfolder));
    if(defDirExist && toscaMetaExist) {
      return true;
    }
    return false;
  }
  
  private static boolean verifyDir(String dir) {
    File file = new File(dir);
    if (!file.exists() && !file.isDirectory()) {
      return false;
    } else {
      return true;
    }
  }
  
  private static boolean verifyFile(String dir) {
    File file = new File(dir);
    if (!file.exists()) {
      return false;
    }
    return true;
  }
}
