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
package org.openo.commontosca.catalog.verification.mdserver.cmd;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import org.openo.commontosca.catalog.verification.mdserver.cmd.entity.VerifyParam;

import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;

public class VerifyCsarCommand implements CliCommand{
  private VerifyParam param;
  @Override
  public void execute(String[] args) {
    
    checkParam(args);
    boolean verificationResult = false;
    try {
      verificationResult = VerifyUtils.verifyCsarPackage(param.getInput(), param.getName());
    } catch (Exception e) {
      System.out.println("verify csar package faild.errorMsg:" + e.getMessage());
    }
    if(verificationResult) {
      System.out.println("the verification result is: This is a qualified package!");
    } else {
      System.out.println("the verification result is: This is not a qualified package!");
    }
  }
  
  private void checkParam(String[] args) {
    if (args.length <= 1) {
      error("parameter error");
    }
    param = new VerifyParam();
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
  }
  
  private void error(String errorMsg) {
    System.err.println("Error: " + errorMsg + "\nRun -c --help for help.");
    System.exit(1);
  }

}
