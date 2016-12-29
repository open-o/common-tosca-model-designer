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
package org.openo.commontosca.catalog.mdserver.cmd.entity;

import de.tototec.cmdoption.CmdOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonParam {
  @CmdOption(names = {"--name"}, args = {"name"}, description = "service Template name")
  private String name;
  @CmdOption(names = {"--version"}, args = {"version"}, description = "service Template version default v1.0")
  private String provider="zte";
  @CmdOption(names = {"--provider"}, args = {"provider"}, description = "service Template provider default openo")
  private String type="NSAR";
  @CmdOption(names = {"--type"}, args = {"type"}, description = "service Template type default NSAR,option NSAR NFAR")
  private String version="v1.0";
  @CmdOption(names = {"--help", "-h"}, description = "Show this help.", isHelp = true)
  private boolean help;
}
