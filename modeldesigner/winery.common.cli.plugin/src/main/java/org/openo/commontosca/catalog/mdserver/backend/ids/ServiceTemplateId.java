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
package org.openo.commontosca.catalog.mdserver.backend.ids;

import java.util.ArrayList;
import java.util.List;

public class ServiceTemplateId extends GenericId {

  private String version;

  public ServiceTemplateId() {}

  public ServiceTemplateId(String id) {
    super(id);
  }

  public ServiceTemplateId(String id, String version) {
    super(id);
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public List<String> getPaths() {
    List<String> paths = new ArrayList<String>();
    paths.addAll(super.getPaths());
    if (null != this.version) {
      paths.add(0, version);
    }
    return paths;
  }
}
