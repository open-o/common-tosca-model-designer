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
package org.openo.commontosca.catalog.mdserver.cmd;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import org.openo.commontosca.catalog.entity.EnumType;
import org.openo.commontosca.catalog.mdserver.backend.common.Constants;
import org.openo.commontosca.catalog.mdserver.cmd.entity.CreateParam;
import org.openo.commontosca.catalog.mdserver.csar.entity.CsarMetaEntity;
import org.openo.commontosca.catalog.mdserver.csar.export.ExporterUtils;

import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;

public class CreateCsarCommand implements CliCommand {
  private CreateParam param;
  @Override
  public void execute(String[] args) {
    checkParam(args);
    CsarMetaEntity csarInfo = new CsarMetaEntity();
    csarInfo.setProvider(param.getProvider());
    csarInfo.setVersion(param.getVersion());
    csarInfo.setType(EnumType.valueOf(param.getType()));
    try {
      ExporterUtils.createCsarPackage(csarInfo, param.getInput(), param.getOutput(),
          param.getName());
    } catch (Exception e) {
      System.out.println("create csar package faild.errorMsg:" + e.getMessage());
    }
    System.out.println("output csar package path:"
        + Paths.get(param.getOutput(), param.getName() + Constants.SUFFIX_CSAR));   
  }

  private void checkParam(String[] args) {
    if (args.length <= 1) {
      error("parameter error");
    }
    param = new CreateParam();
    CmdlineParser cp = new CmdlineParser(param);
    try {
      cp.parse(Arrays.copyOfRange(args, 1, args.length));
    } catch (CmdlineParserException e) {
      error(e.getLocalizedMessage());
      System.exit(1);
    }
    if (param.isHelp()) {
      cp.usage();
      System.exit(0);
    }
    if (CliUtil.isEmpty(param.getInput()) || CliUtil.isEmpty(param.getName())) {
      error("parameter error");
    }
    if (CliUtil.isEmpty(param.getInput()) || !new File(param.getInput()).exists()) {
      error("input path not exist!");
    } else {
      param.setInput(new File(param.getInput()).getPath());
    }
    if (CliUtil.isEmpty(param.getOutput()))
      param.setOutput(new File(CreateCsarCommand.class.getResource("/").getPath()).getPath());
    else if (!new File(param.getOutput()).exists()) {
      error("out path not exist!");
    }

  }

  private void error(String errorMsg) {
    System.err.println("Error: " + errorMsg + "\nRun -c --help for help.");
    System.exit(1);
  }
}
