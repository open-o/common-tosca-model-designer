/**
 * Copyright 2016 [ZTE] and others.
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

package org.eclipse.winery.repository.ext.export.yaml.switcher;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.CapabilityTypesXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.GroupTypesXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.GroupsXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.IXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.ImportXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.InputsXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.MetaDatasXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.NodeTemplatesXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.NodeTypesXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.PlansXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.PolicyTemplatesXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.PolicyTypesXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.RelationshipTypesXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher.SubstitutionMappingsXml2YamlSubSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;

/**
 * This class can parse Definitions (XML bean) to ServiceTemplates (YAML bean).
 * 
 */
public class Xml2YamlSwitch {
  private final Definitions definitions;

  public Xml2YamlSwitch(Definitions definitions) {
    super();
    this.definitions = definitions;
  }

  public Definitions getDefinitions() {
    return this.definitions;
  }

  /**
   * The service template.
   */
  private ServiceTemplate serviceTemplate = new ServiceTemplate();


  public ServiceTemplate getServiceTemplate() {
    return this.serviceTemplate;
  }

  /**
   * 
   * @return .
   */
  public ServiceTemplate convert() {
    this.serviceTemplate.setTosca_definitions_version("tosca_simple_yaml_1_0");
    this.serviceTemplate.setTosca_default_namespace(this.getDefinitions().getTargetNamespace());

    IXml2YamlSubSwitch[] subSwitches = { new ImportXml2YamlSubSwitch(this),
        new MetaDatasXml2YamlSubSwitch(this),
        new NodeTypesXml2YamlSubSwitch(this),
        new CapabilityTypesXml2YamlSubSwitch(this),
        new RelationshipTypesXml2YamlSubSwitch(this),
        new NodeTemplatesXml2YamlSubSwitch(this),
        new InputsXml2YamlSubSwitch(this),
        new SubstitutionMappingsXml2YamlSubSwitch(this),
        new PolicyTypesXml2YamlSubSwitch(this),
        new PolicyTemplatesXml2YamlSubSwitch(this),
        new GroupTypesXml2YamlSubSwitch(this),
        new GroupsXml2YamlSubSwitch(this),
        new PlansXml2YamlSubSwitch(this)};
    for (IXml2YamlSubSwitch subSwitch : subSwitches) {
      subSwitch.process();
    }

    return this.serviceTemplate;
  }

}
