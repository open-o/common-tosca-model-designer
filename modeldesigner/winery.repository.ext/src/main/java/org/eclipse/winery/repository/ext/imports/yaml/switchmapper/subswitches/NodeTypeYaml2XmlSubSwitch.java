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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeType.CapabilityDefinitions;
import org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.CapabilityDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.NodeType;
import org.eclipse.winery.repository.ext.yamlmodel.RequirementDefinition;

public class NodeTypeYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {
  public NodeTypeYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
    super(parentSwitch);
  }

  @Override
  public void process() {
    if (getServiceTemplate().getNode_types() == null
        || getServiceTemplate().getNode_types().isEmpty()) {
      return;
    }

    for (Map.Entry<String, NodeType> entry : getServiceTemplate().getNode_types().entrySet()) {
      if (entry == null || entry.getKey() == null || entry.getKey().isEmpty()
          || entry.getValue() == null) {
        break;
      }

      String nodeTypeName = Yaml2XmlTypeMapper.mappingNodeType(entry.getKey());
      TNodeType tNodeType = constructNodeType(nodeTypeName, entry.getValue());

      getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(tNodeType);
    }
  }

  private TNodeType constructNodeType(String nodeTypeName, NodeType yNodeType) {
    TNodeType tNodeType = new TNodeType();
    // attribute-name,targetNameSpace,anyAttributes
    tNodeType.setName(nodeTypeName);
    tNodeType.setTargetNamespace(getParent().getUsedNamespace());
    // attribute-abstract,final-none

    // element-documentation-none

    // element-DerivedFrom
    String derivedFrom = Yaml2XmlTypeMapper.mappingNodeType(yNodeType.getDerived_from());
    tNodeType.setDerivedFrom(Yaml2XmlTypeMapper.buildDerivedFrom(getParent().getUsedNamespace(),
        derivedFrom));

    // element-PropertiesDefinition
    TEntityType.PropertiesDefinition tPropertiesDefinition = new TEntityType.PropertiesDefinition();
    tPropertiesDefinition.setType(new QName(getParent().getUsedNamespace() + "/"
        + CommonConst.PROPERTIES_DEFINITION, CommonConst.PROPERTIES_LOCALPART,
        CommonConst.PROPERTIES_DEFINITION_PREFIX));
    tNodeType.setPropertiesDefinition(tPropertiesDefinition);
    // element-PropertiesDefinition-any
    // properties
    WinerysPropertiesDefinition twpd =
        Yaml2XmlSwitchUtils.buildWinerysPropertiesDefinition(
            yNodeType.getProperties(), getParent().getUsedNamespace());
    // attributes
    twpd.getPropertyDefinitionKVList().addAll(
        Yaml2XmlSwitchUtils.buildAttributeDefinitionKVList(yNodeType.getAttributes()));
    tNodeType.getAny().add(twpd);

    // element-RequirementDefinitions-none
    tNodeType.setRequirementDefinitions(buildRequiremnets(yNodeType.getRequirements()));

    // element-CapabilityDefinitions-none
    tNodeType.setCapabilityDefinitions(buildCapabilityDefinitions(yNodeType.getCapabilities()));

    // element-InstanceStates-none

    // element-Interfaces-none

    return tNodeType;
  }

  private RequirementDefinitions buildRequiremnets(List<Map<String, RequirementDefinition>> requirements) {
    if (null == requirements || requirements.isEmpty()) {
      return null;
    }
    RequirementDefinitions tRequirementDefinitions = new RequirementDefinitions();
    List<TRequirementDefinition> tRequirementDefinitionList =
        tRequirementDefinitions.getRequirementDefinition();
    
    for (Map<String, RequirementDefinition> map : requirements) {
      for (Iterator<Entry<String, RequirementDefinition>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
        Entry<String, RequirementDefinition> entry = iterator.next();
        TRequirementDefinition tRequirement = new TRequirementDefinition();
        tRequirement.setName(entry.getKey());
        tRequirement.setRequirementType(getCapability(entry.getValue().getCapability()));
        // tRequirement.setLowerBound(null); TODO
        // tRequirement.setUpperBound(null); TODO

        tRequirementDefinitionList.add(tRequirement);
      }
    }
    return tRequirementDefinitions;
  }

  /**
   * @param capability
   * @return
   */
  private QName getCapability(String capability) {
    if (null != capability) {
      // temp namespace, updated when types are imported
      return new QName(getParent().getUsedNamespace(), capability);
    }
    return null;
  }


  private CapabilityDefinitions buildCapabilityDefinitions(Map<String, CapabilityDefinition> capabilities) {
    if (null == capabilities || capabilities.isEmpty()) {
      return null;
    }

    CapabilityDefinitions tCapabilityDefinitions = new CapabilityDefinitions();
    List<TCapabilityDefinition> capabilityDefinitionList =
        tCapabilityDefinitions.getCapabilityDefinition();
    for (Iterator<Entry<String, CapabilityDefinition>> iterator = capabilities.entrySet().iterator();
        iterator.hasNext();) {
      Entry<String, CapabilityDefinition> entry = iterator.next();
      TCapabilityDefinition tCapability = new TCapabilityDefinition();
      tCapability.setName(entry.getKey());
      tCapability.setCapabilityType(getTCapabilityType(entry.getValue().getType()));
      // tCapability.setLowerBound(null); TODO
      // tCapability.setUpperBound(null); TODO

      capabilityDefinitionList.add(tCapability);
    }

    return tCapabilityDefinitions;
  }

  private QName getTCapabilityType(String capType) {
    if (null != capType) {
      // temp namespace, updated when types are imported
      return new QName(getParent().getUsedNamespace(), capType);
    }
    return null;
  }

}
