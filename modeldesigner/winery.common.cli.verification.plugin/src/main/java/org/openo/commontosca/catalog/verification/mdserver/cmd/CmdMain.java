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

import org.openo.commontosca.catalog.verification.mdserver.cmd.entity.BaseParam;

import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;

public class CmdMain {
  private CmdlineParser cp;
  private BaseParam config;
  private String[] args;

  public CmdMain(String[] args) {
    this.args = args;
  }

  public static void main(String[] args) {
    if (args.length <= 0) {
      System.err.println("Error: parameter error\nRun  --help for help.");
      System.exit(1);
    }
    CmdMain cmd = new CmdMain(args);
    cmd.init();
    cmd.execute();
  }
  private void init() {
    config = new BaseParam();
    cp = new CmdlineParser(config);
    cp.setAboutLine("model designer v1.0");
    try {
      cp.parse(args[0]);
    } catch (CmdlineParserException e) {
      error(e.getLocalizedMessage());
      System.exit(1);
    }
  }
  private void execute() {
    CliCommand clis = null;
    if (config.isVerify()) {
      clis = new VerifyCsarCommand();
    } else if (config.isHelp()) {
      cp.usage();
      System.exit(0);
    }
    if (clis == null) {
      error("parameter error");
    }
    clis.execute(args);
  }

  private void error(String errorMsg) {
    System.err.println("Error: " + errorMsg + "\nRun  --help for help.");
    System.exit(1);
  }
}
