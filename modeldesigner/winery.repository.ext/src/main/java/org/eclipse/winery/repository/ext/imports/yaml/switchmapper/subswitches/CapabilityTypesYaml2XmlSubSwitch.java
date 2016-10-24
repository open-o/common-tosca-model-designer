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

import java.util.List;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.typemapper.ElementType;
import org.eclipse.winery.repository.ext.yamlmodel.CapabilityType;

/**
 * This sub switch supports processing the capability types of a YAML service template.
 */
public class CapabilityTypesYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

  public CapabilityTypesYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
    super(parentSwitch);
  }

  /**
   * For each capability type of the YAML service template, a corresponding
   * {@link org.opentosca.model.tosca.TCapabilityType} is created and added to the
   * {@link #getDefinitions()} object.
   */
  @Override
  public void process() {
    if (getServiceTemplate().getCapability_types() != null) {
      for (final Entry<String, CapabilityType> capType : getServiceTemplate().getCapability_types()
          .entrySet()) {
        final TCapabilityType capabilityType = createCapabilityType(capType);

        getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(capabilityType);
      }
    }
  }

  /**
   * Creates a Tosca capability type. Sets name and other properties which can be read from
   * {@link org.opentosca.yamlconverter.yamlmodel.yaml.element.CapabilityType} .
   * 
   * @param capType entry of a capability type in YAML service template
   * @return Tosca capability type
   */
  private TCapabilityType createCapabilityType(Entry<String, CapabilityType> capType) {
    final TCapabilityType result = new TCapabilityType();
    final CapabilityType capabilityType = capType.getValue();

    result.setName(capType.getKey());
    result.setTargetNamespace(getParent().getUsedNamespace());
    if (capabilityType.getDerived_from() != null && !capabilityType.getDerived_from().isEmpty()) {
      final QName derivedFrom =
          getTypeMapperUtil().getCorrectTypeReferenceAsQName(capabilityType.getDerived_from(),
              ElementType.CAPABILITY_TYPE);
      result.setDerivedFrom(parseDerivedFrom(derivedFrom));
    }
    result.getDocumentation().add(toDocumentation(capabilityType.getDescription()));

    if (capabilityType.getProperties() != null && !capabilityType.getProperties().isEmpty()) {
      // result.setPropertiesDefinition(parsePropertiesDefinition(
      // capabilityType.getProperties(), capType.getKey()));
      List<WinerysPropertiesDefinition> any =
          Yaml2XmlSwitchUtils.buildWinerysPropertiesDefinitionList(capabilityType.getProperties(), getParent().getUsedNamespace());
      result.getAny().addAll(any);
    }
    return result;
  }

}
