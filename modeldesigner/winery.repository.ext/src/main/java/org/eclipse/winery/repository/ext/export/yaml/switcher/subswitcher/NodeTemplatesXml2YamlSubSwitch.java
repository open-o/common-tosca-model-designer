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

package org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.AttributeDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.Capability;
import org.eclipse.winery.repository.ext.yamlmodel.CapabilityFilter;
import org.eclipse.winery.repository.ext.yamlmodel.NodeFilter;
import org.eclipse.winery.repository.ext.yamlmodel.NodeTemplate;
import org.eclipse.winery.repository.ext.yamlmodel.NodeTemplatePosition;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.Requirement;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

/**
 * This class supports processing of node templates of a YAML service template.
 */
public class NodeTemplatesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

  public NodeTemplatesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
    super(parentSwitch);
  }


  @Override
  public void process() {
    TServiceTemplate tserviceTemplate = getTServiceTemplate();
    if (tserviceTemplate == null) {
      return;
    }
    getServiceTemplate().setDescription(
        Xml2YamlSwitchUtils.convert2Description(tserviceTemplate.getDocumentation()));

    TTopologyTemplate ttopologyTemplate = tserviceTemplate.getTopologyTemplate();
    if (ttopologyTemplate != null) {
      getTopology_template().setDescription(
          Xml2YamlSwitchUtils.convert2Description(ttopologyTemplate.getDocumentation()));

      parseTempateList(ttopologyTemplate.getNodeTemplateOrRelationshipTemplate());
    }
  }

  private void parseTempateList(List<TEntityTemplate> ttemplateList) {
    if (ttemplateList == null || ttemplateList.isEmpty()) {
      return;
    }

    ArrayList<TRelationshipTemplate> rstList = getRelationshipList(ttemplateList);

    // TNodeTemplate
    for (TEntityTemplate ttemplate : ttemplateList) {
      if (ttemplate instanceof TNodeTemplate) {
        Entry<String, NodeTemplate> ynodeTemplate =
            createNodeTemplate((TNodeTemplate) ttemplate, rstList);

        getTopology_template().getNode_templates().put(ynodeTemplate.getKey(),
            ynodeTemplate.getValue());
      }
    }
  }

  private ArrayList<TRelationshipTemplate> getRelationshipList(
      List<TEntityTemplate> ttemplateList) {
    ArrayList<TRelationshipTemplate> relationshipTemplateList =
        new ArrayList<TRelationshipTemplate>();
    for (TEntityTemplate ttemplate : ttemplateList) {
      if (ttemplate instanceof TRelationshipTemplate) {
        relationshipTemplateList.add((TRelationshipTemplate) ttemplate);
      }
    }
    return relationshipTemplateList;
  }

  /**
   * @param tNodeTemplate.
   * @return .
   */
  private Entry<String, NodeTemplate> createNodeTemplate(TNodeTemplate tnodeTemplate,
      ArrayList<TRelationshipTemplate> rstList) {
    NodeTemplate ynodeTemplate = new NodeTemplate();

    // description
    ynodeTemplate
        .setDescription(Xml2YamlSwitchUtils.convert2Description(tnodeTemplate.getDocumentation()));

    // type
    ynodeTemplate.setType(Xml2YamlTypeMapper
        .mappingNodeType(Xml2YamlSwitchUtils.getNamefromQName(tnodeTemplate.getType())));

    // properties & attributes
    Map<String, Object> ypropertiesAndAttributes =
        Xml2YamlSwitchUtils.convertTProperties(tnodeTemplate.getProperties());
    if (ypropertiesAndAttributes != null && !ypropertiesAndAttributes.isEmpty()) {
      TNodeType tnodeType = getTNodeType(tnodeTemplate);
      Map<String, PropertyDefinition> ypropertyDefs =
          Xml2YamlSwitchUtils.convert2PropertyDefinitions(tnodeType.getAny());
      Map<String, Object> yproperties = getYProperties(ypropertiesAndAttributes, ypropertyDefs);
      ynodeTemplate.setProperties(yproperties);

      Map<String, AttributeDefinition> yattributeDefs =
          Xml2YamlSwitchUtils.convert2AttributeDefinitions(tnodeType.getAny());
      Map<String, Object> yattributes = getYAttributes(ypropertiesAndAttributes, yattributeDefs);
      ynodeTemplate.setAttributes(yattributes);
    }

    // capabilities
    TNodeTemplate.Capabilities tcapabilities = tnodeTemplate.getCapabilities();
    if (tcapabilities != null) {
      ynodeTemplate.setCapabilities(processCapabilitiesInNodeTemplate(tcapabilities));
    }

    // requirement
    TNodeTemplate.Requirements trequirements = tnodeTemplate.getRequirements();
    if (trequirements != null) {
      List<TRequirement> trequirementList = trequirements.getRequirement();
      if (trequirementList != null && !trequirementList.isEmpty()) {
        for (TRequirement trequirement : trequirementList) {
          Requirement[] yrequirements = buildRequirement(trequirement, tnodeTemplate, rstList);
          for (Requirement yrequirement: yrequirements) {
            Map<String, Object> yrequirementMap = new HashMap<>();
            yrequirementMap.put(trequirement.getName(), yrequirement);
            ynodeTemplate.getRequirements().add(yrequirementMap);
          }
        }
      }
    }

    // nodetemplate-position
    NodeTemplatePosition position = getNodetemplatePosition(tnodeTemplate);
    ynodeTemplate.setPosition(position);

    return buildEntry(tnodeTemplate.getName(), ynodeTemplate);
  }

    private Requirement[] buildRequirement(TRequirement trequirement, TNodeTemplate tnodeTemplate,
            ArrayList<TRelationshipTemplate> rstList) {
        // Extension Relationship
        if (createExtRrequirement(trequirement) != null) {
            return new Requirement[]{createExtRrequirement(trequirement)};
        }
        // Manual Relationship
        Requirement[] yrequirements = createRrequirement(tnodeTemplate, trequirement, rstList);
        if (yrequirements.length > 0) {
          return yrequirements;
        }
        // Auto Relationship
        if (createFilterRequirement(trequirement) != null) {
          return new Requirement[]{createFilterRequirement(trequirement)};
        }
        return new Requirement[0];
    }

    private Requirement createExtRrequirement(TRequirement trequirement) {
        String destNodeName =
                trequirement.getOtherAttributes().get(new QName(Constants.REQUIREMENT_EXT_NODE));
        String capability =
                trequirement.getOtherAttributes().get(
                        new QName(Constants.REQUIREMENT_EXT_CAPABILITY));
        // String rsTemplate =
        // tRequirement.getOtherAttributes().get(
        // new QName(Constants.REQUIREMENT_EXT_RELATIONSHIP));
        if (destNodeName == null || destNodeName.isEmpty()) {
            return null;
        }

        Requirement yrequirement = new Requirement();
        yrequirement.setNode(destNodeName);
        yrequirement.setCapability(capability);

        return yrequirement;
    }
    
    private Requirement[] createRrequirement(TNodeTemplate tNodeTemplate,
            TRequirement trequirement, List<TRelationshipTemplate> rstList) {
      List<Requirement> yrequirementList = new ArrayList<>();
        if (rstList != null && !rstList.isEmpty()) {
            for (TRelationshipTemplate rst : rstList){
                if(isRequirementEqualSourceNode(trequirement, rst)){
                    TEntityTemplate target  = (TEntityTemplate) rst.getTargetElement().getRef();
                    // yRequirement.setRelationship(relationshipTemplate.getName());
                    if (buildRequirement(target) != null) {
                      yrequirementList.add(buildRequirement(target));
                    }
                }
            }
        }

        return yrequirementList.toArray(new Requirement[0]);
    }

    private Requirement buildRequirement(TEntityTemplate target) {
        // when the target is a node
        if(target instanceof TNodeTemplate){
            TNodeTemplate nodeTemplate = (TNodeTemplate)target;
            Requirement yrequirement = new Requirement();
            yrequirement.setNode(nodeTemplate.getName());
            return yrequirement;
        //when the target is a capability
        }else if(target instanceof TCapability){
            TCapability capability = (TCapability)target;
            TNodeTemplate nodeTemplate = findTNodeTemplatByCapability(capability);
            Requirement yrequirement = new Requirement();
            yrequirement.setCapability(capability.getName());
            yrequirement.setNode(nodeTemplate.getName());
            return yrequirement;
        }

        return null;
    }

    private boolean isRequirementEqualSourceNode(TRequirement trequirement, TRelationshipTemplate rst){
    TEntityTemplate source = (TEntityTemplate) rst.getSourceElement().getRef();
    if (trequirement != null && source != null) {
      String reqId = trequirement.getId();
      String soureId = source.getId();
            if(reqId != null && soureId != null)
                return reqId.equals(soureId);
        }
        return false;    
    }

  private TNodeTemplate findTNodeTemplatByCapability(TCapability capbility) {
    List<TEntityTemplate> entityTemplates =
        this.getTServiceTemplate().getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
    for (TEntityTemplate entyityTemplate : entityTemplates) {
      if (entyityTemplate instanceof TNodeTemplate) {
        TNodeTemplate node = (TNodeTemplate) entyityTemplate;
        Capabilities capabilities = node.getCapabilities();
        if (capabilities != null && capabilities.getCapability() != null) {
          for (TCapability cap : capabilities.getCapability()) {
                        if(cap.getId().equals(capbility.getId()))
                            return node;
                    }
          }
        }
      }
        return null;
    }


  private NodeTemplatePosition getNodetemplatePosition(TNodeTemplate tnodeTemplate) {
    NodeTemplatePosition position = new NodeTemplatePosition();
    for (QName key : tnodeTemplate.getOtherAttributes().keySet()) {
      if (key.getLocalPart().equals(CommonConst.NODETEMPLATE_POSITIONX_LOCALPART)) {
        position.setPositionX(tnodeTemplate.getOtherAttributes().get(key));
      }
      if (key.getLocalPart().equals(CommonConst.NODETEMPLATE_POSITIONY_LOCALPART)) {
        position.setPositionY(tnodeTemplate.getOtherAttributes().get(key));
      }
    }
    return position;
  }

  /**
   * @param ypropertiesAndAttributes .
   * @param yattributeDefs .
   * @return .
   */
  private Map<String, Object> getYAttributes(Map<String, Object> ypropertiesAndAttributes,
      Map<String, AttributeDefinition> yattributeDefs) {
    Map<String, Object> yattributes = new HashMap<>();
    for (String name : ypropertiesAndAttributes.keySet()) {
      if (yattributeDefs.containsKey(name)) {
        yattributes.put(name, ypropertiesAndAttributes.get(name));
      }
    }
    return yattributes;
  }

  /**
   * @param ypropertiesAndAttributes .
   * @param ypropertyDefs .
   * @return .
   */ 
  private Map<String, Object> getYProperties(Map<String, Object> ypropertiesAndAttributes,
      Map<String, PropertyDefinition> ypropertyDefs) {
    Map<String, Object> yproperties = new HashMap<>();
    for (String name : ypropertiesAndAttributes.keySet()) {
      if (ypropertyDefs.containsKey(name)) {
        yproperties.put(name, ypropertiesAndAttributes.get(name));
      }
    }

    return yproperties;
  }

  /**
   * @param tnodeTemplate .
   * @return .
   */
  private TNodeType getTNodeType(TNodeTemplate tnodeTemplate) {
    NodeTypesResource res = new NodeTypesResource();
    NodeTypeResource nodetypeRes =
        res.getComponentInstaceResource(Util.URLencode(tnodeTemplate.getType().getNamespaceURI()),
            tnodeTemplate.getType().getLocalPart());
    return (TNodeType) nodetypeRes.getEntityType();
  }

  /**
   * @param trequirement .
   * @return .
   */
  private Requirement createFilterRequirement(TRequirement trequirement) {
    if (trequirement.getProperties() == null) {
      return null;
    }

    Requirement yrequirement = new Requirement();
    yrequirement.setNode_filter(buildNodeFilter(trequirement));

    return yrequirement;
  }

  private NodeFilter buildNodeFilter(TRequirement trequirement) {
    NodeFilter nodeFilter = new NodeFilter();

    CapabilityFilter capabilityFilter = new CapabilityFilter();
    Map<String, Object> yproperties =
        Xml2YamlSwitchUtils.convertTProperties(trequirement.getProperties());
    List<Map<String, Object>> ypropertyFilter = Xml2YamlSwitchUtils.convertMap2ListMap(yproperties);
    capabilityFilter.setProperties(ypropertyFilter);

    Map<String, CapabilityFilter> ycapabilities = new HashMap<>();
    ycapabilities.put(trequirement.getName(), capabilityFilter);

    nodeFilter.setCapabilities(Xml2YamlSwitchUtils.convertMap2ListMap(ycapabilities));

    return nodeFilter;
  }


  private Entry<String, NodeTemplate> buildEntry(String name, NodeTemplate ynodeTemplate) {
    Map<String, NodeTemplate> map = new HashMap<>();
    map.put(name, ynodeTemplate);

    return map.entrySet().iterator().next();
  }

  /**
   * @param tcapabilities .
   * @return .
   */
  private Map<String, Capability> processCapabilitiesInNodeTemplate(Capabilities tcapabilities) {

    Map<String, Capability> ycapabilities = new HashMap<>();
    if (tcapabilities.getCapability() != null && !tcapabilities.getCapability().isEmpty()) {
      for (TCapability tcapability : tcapabilities.getCapability()) {
        Capability ycapability = buildCapability(tcapability);
        if (ycapability != null) {
          ycapabilities.put(tcapability.getName(), ycapability);
        }
      }
    }

    return ycapabilities;

  }

  /**
   * @param tcapability .
   * @return .
   */
  private Capability buildCapability(TCapability tcapability) {
    Map<String, Object> yproperties =
        Xml2YamlSwitchUtils.convertTProperties(tcapability.getProperties());
    if (yproperties == null || yproperties.isEmpty()) {
      return null;
    }

    return new Capability(yproperties);
  }

  // private void parseRelationshipTemplate(TRelationshipTemplate trt) {
  // String requirementName = trt.getName();
  // String relationshipType = Xml2YamlTypeMapper
  // .mappingRelationshipType(Xml2YamlSwitchUtils
  // .getNamefromQName(trt.getType()));
  //
  // TNodeTemplate sourceNodeTemplate = getNodeTemplateName(trt
  // .getSourceElement().getRef());
  // TNodeTemplate targetNodeTemplate = getNodeTemplateName(trt
  // .getTargetElement().getRef());
  //
  // if (sourceNodeTemplateName != null && targetNodeTemplateName != null) {
  // NodeTemplate ySourceNodeTemplate = getTopology_template()
  // .getNode_templates().get(sourceNodeTemplateName);
  //
  // NodeTemplate yTargetNodeTemplate = getTopology_template()
  // .getNode_templates().get(targetNodeTemplateName);
  //
  // if (ySourceNodeTemplate != null && yTargetNodeTemplate != null) {
  // ySourceNodeTemplate.getRequirements().add(
  // buildRequirement(requirementName,
  // targetNodeTemplateName, relationshipType));
  // }
  // }
  // }
  //
  // private Map<String, Object> buildRequirement(String requirementName,
  // String targetNodeTemplateName, String relationshipType) {
  // Map<String, Object> yRequirement = new HashMap<>();
  // yRequirement
  // .put(requirementName,
  // buildRequirementObject(targetNodeTemplateName,
  // relationshipType));
  // return yRequirement;
  // }
  //
  // private Requirement buildRequirementObject(
  // String targetNodeTemplateName, String relationshipType) {
  // Requirement yRequirementObject = new Requirement();
  //
  // yRequirementObject.setNode(targetNodeTemplateName);
  // yRequirementObject.setRelationship(relationshipType);
  //
  // return yRequirementObject;
  // }

  /**
   * @param object
   * @return
   */
  // private TNodeTemplate getNodeTemplateName(Object object) {
  // if (object instanceof TNodeTemplate) {
  // return ((TNodeTemplate) object);
  // }
  //
  // return null;
  // }

}
