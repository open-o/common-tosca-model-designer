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
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityTemplate.Properties;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.utils.AnyMap;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.utils.Global;
import org.eclipse.winery.repository.ext.yamlmodel.Capability;
import org.eclipse.winery.repository.ext.yamlmodel.NodeTemplate;

public class NodeTemplateYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

  private TTopologyTemplate tTopologyTemplate;

  public NodeTemplateYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch,
      TTopologyTemplate tTopologyTemplate) {
    super(parentSwitch);
    this.tTopologyTemplate = tTopologyTemplate;
  }

  @Override
  public void process() {
    if (getServiceTemplate().getTopology_template().getNode_templates() == null
        || getServiceTemplate().getTopology_template().getNode_templates().isEmpty()) {
      return;
    }

    Map<String, NodeTemplate> yNodeTemplates =
        getServiceTemplate().getTopology_template().getNode_templates();

    int positionXValue = CommonConst.NODETEMPLATE_POSITIONX_DEFAULTVALUE;
    int positionYValue = CommonConst.NODETEMPLATE_POSITIONY_DEFAULTVALUE;
    for (Map.Entry<String, NodeTemplate> entry : yNodeTemplates.entrySet()) {
//      if (entry.getValue().getPosition() != null) {
//        positionXValue =
//            convertPosition(entry.getValue().getPosition().getPositionX(), positionXValue);
//        positionYValue =
//            convertPosition(entry.getValue().getPosition().getPositionY(), positionYValue);
//      } else {
        positionXValue += CommonConst.NODETEMPLATE_POSITION_INTERVAL;
//      }

      TNodeTemplate tNodeTemplate =
          constructNodeTemplate(entry.getValue(), entry.getKey(), positionXValue, positionYValue);
      tTopologyTemplate.getNodeTemplateOrRelationshipTemplate().add(tNodeTemplate);

    }
  }

  private int convertPosition(String position, int defaultValue) {
    try {
      return Integer.valueOf(position);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  private TNodeTemplate constructNodeTemplate(NodeTemplate nodeTemplate, String nodeTemplateName,
      int positionXValue, int positionYValue) {
    TNodeTemplate tNodeTemplate = new TNodeTemplate();

    // attribute-name,minInstances,maxInstances,id,type
    tNodeTemplate.setId(Global.unique(name2id(nodeTemplateName).replace('.', '_')));
    tNodeTemplate.setName(nodeTemplateName);
    // temp node type namespace, it will updated namespace after importing all definitions
//    QName type =
//        new QName(getServiceTemplate().getTosca_default_namespace(), nodeTemplate.getType());
    QName type = new QName(CommonConst.TOSCA_NS, nodeTemplate.getType());
    tNodeTemplate.setType(type);

    // element-documentation
    if (nodeTemplate.getDescription() != null && !nodeTemplate.getDescription().isEmpty()) {
      tNodeTemplate.getDocumentation().add(toDocumentation(nodeTemplate.getDescription()));
    }

    // element-Properties,PropertyConstraints,Requirements,Capabilities,Policies,DeploymentArtifacts
    if (nodeTemplate.getProperties() != null && !nodeTemplate.getProperties().isEmpty()) {
      tNodeTemplate.setProperties(constructProperties(nodeTemplate.getProperties()));
    }
    // element-OtherAttribute
    setOtherAttribute(positionXValue, positionYValue, tNodeTemplate);

    // element-PropertyConstraints-none
    // element-Requirements-yaml treatment wrong
    if (nodeTemplate.getRequirements() != null && !nodeTemplate.getRequirements().isEmpty()) {
      tNodeTemplate.setRequirements(constructRequirements(nodeTemplate.getRequirements()));
    }
    // element-Capabilities-yaml treatment wrong
    if (nodeTemplate.getCapabilities() != null && !nodeTemplate.getCapabilities().isEmpty()) {
      tNodeTemplate.setCapabilities(constructCapabilities(nodeTemplate.getCapabilities()));
    }
    // element-Policies-none

    // element-DeploymentArtifacts-none

    return tNodeTemplate;
  }

  private void setOtherAttribute(int positionXValue, int positionYValue, TNodeTemplate tNodeTemplate) {
    tNodeTemplate.getOtherAttributes().put(
        new QName(CommonConst.NODETEMPLATE_POSITION_NAMESPACE,
            CommonConst.NODETEMPLATE_POSITIONX_LOCALPART, CommonConst.WINERY_PREFIX),
        String.valueOf(positionXValue));
    tNodeTemplate.getOtherAttributes().put(
        new QName(CommonConst.NODETEMPLATE_POSITION_NAMESPACE,
            CommonConst.NODETEMPLATE_POSITIONY_LOCALPART, CommonConst.WINERY_PREFIX),
        String.valueOf(positionYValue));
  }

  private TEntityTemplate.Properties constructProperties(Map<String, Object> yProperties) {
    final TEntityTemplate.Properties prop = new TEntityTemplate.Properties();
    final JAXBElement<AnyMap> jaxbprop =
        getAnyMapForProperties(yProperties, new QName("http://www.zte.com.cn/paas" + "/"
            + CommonConst.PROPERTIES_DEFINITION, "", CommonConst.PROPERTIES_DEFINITION_PREFIX));

    prop.setAny(jaxbprop);
    return prop;
  }

  private TNodeTemplate.Requirements constructRequirements(List<Map<String, Object>> requirements) {
    final TNodeTemplate.Requirements tRrequirements = new TNodeTemplate.Requirements();
    return tRrequirements;
  }

  private TNodeTemplate.Capabilities constructCapabilities(Map<String, Capability> yCapablities) {
    final TNodeTemplate.Capabilities capabilities = new TNodeTemplate.Capabilities();
    for (Iterator<Entry<String, Capability>> iterator = yCapablities.entrySet().iterator(); iterator
        .hasNext();) {
      Entry<String, Capability> entry = iterator.next();
      TCapability tCapability = buildTCapability(entry);
      capabilities.getCapability().add(tCapability);
    }

    return capabilities;
  }

  private TCapability buildTCapability(Entry<String, Capability> entry) {
    TCapability tCapability = new TCapability();
    tCapability.setId(UUID.randomUUID().toString());
    tCapability.setName(entry.getKey());
    // temp type, it will updated after importing all definitions
//    tCapability
//        .setType(new QName(getServiceTemplate().getTosca_default_namespace(), entry.getKey()));
    tCapability.setType(new QName(CommonConst.TOSCA_NS, entry.getKey()));
    
    Map<String, Object> yProperties = entry.getValue().getProperties();
    if (null != yProperties && !yProperties.isEmpty()) {
      Properties tProperties = constructProperties(yProperties);
      tCapability.setProperties(tProperties);
    }
    return tCapability;
  }
}
