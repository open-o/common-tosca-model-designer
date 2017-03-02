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
package org.openo.commontosca.catalog.verification.mdserver.cmd.entity;

import de.tototec.cmdoption.CmdOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 10159474
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseParam {
  @CmdOption(names = {"--help", "-h"}, description = "Show this help.", isHelp = true)
  public boolean help;
  @CmdOption(names = {"--verify", "-v"}, description = "verify the validation of package")
  private boolean verify;
//  @CmdOption(names = {"--create", "-c"}, description = "create new csar package")
//  private boolean create;
//  @CmdOption(names = {"--import", "-i"}, description = "import new csar package")
//  private boolean importing; 


}
