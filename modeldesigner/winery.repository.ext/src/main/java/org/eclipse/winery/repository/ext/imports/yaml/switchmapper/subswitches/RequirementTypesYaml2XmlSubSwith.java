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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;

/**
 * @author 10186401
 *
 */
public class RequirementTypesYaml2XmlSubSwith extends AbstractYaml2XmlSubSwitch {

  public RequirementTypesYaml2XmlSubSwith(Yaml2XmlSwitch parentSwitch) {
    super(parentSwitch);
  }

  @Override
  public void process() {
    String template_name = getServiceTemplate().getTemplate_name();
    if (!template_name.contains(".requirements.")) {
      return;
    }

    TRequirementType reqType = new TRequirementType();
    reqType.setName(template_name);
    reqType.setTargetNamespace(getParent().getUsedNamespace());
    getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(reqType);
  }

}
