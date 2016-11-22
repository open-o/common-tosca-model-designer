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
package org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
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
import org.eclipse.winery.repository.ext.export.yaml.switcher.PositionNamespaceHelper;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.ArtifactDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.AttributeDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.Capability;
import org.eclipse.winery.repository.ext.yamlmodel.CapabilityFilter;
import org.eclipse.winery.repository.ext.yamlmodel.ConstraintClause;
import org.eclipse.winery.repository.ext.yamlmodel.NodeFilter;
import org.eclipse.winery.repository.ext.yamlmodel.NodeTemplate;
import org.eclipse.winery.repository.ext.yamlmodel.NodeTemplatePosition;
import org.eclipse.winery.repository.ext.yamlmodel.NodeType;
import org.eclipse.winery.repository.ext.yamlmodel.PropertiesFilter;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyFilter;
import org.eclipse.winery.repository.ext.yamlmodel.Requirement;
import org.eclipse.winery.repository.ext.yamlmodel.RequirementDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.RequirementRelationship;

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

    TNodeType tnodeType = Xml2YamlSwitchUtils.getTNodeType(tnodeTemplate);
    NodeType ynodeType = Xml2YamlSwitchUtils.convert2NodeType(tnodeType);
    
    // properties & attributes
    Map<String, Object> ypropsAndAttrs =
        Xml2YamlSwitchUtils.convert2PropertiesOrAttributes(tnodeTemplate.getProperties());
    if (ypropsAndAttrs != null && !ypropsAndAttrs.isEmpty()) {
      Map<String, Object> yproperties = 
          getYProperties(ypropsAndAttrs, ynodeType.getProperties());
      ynodeTemplate.setProperties(yproperties);

      Map<String, Object> yattributes = 
          getYAttributes(ypropsAndAttrs, ynodeType.getAttributes());
      ynodeTemplate.setAttributes(yattributes);
    }
    // tosca_name
    ynodeTemplate.getAttributes().put("tosca_name", tnodeTemplate.getName());

    // capabilities
    TNodeTemplate.Capabilities tcapabilities = tnodeTemplate.getCapabilities();
    if (tcapabilities != null) {
      ynodeTemplate.setCapabilities(processCapabilities(tcapabilities));
    }
    
    // artifacts
    ynodeTemplate.getArtifacts().putAll(
        processArtifacts(tnodeTemplate.getDeploymentArtifacts()));

    // requirement
    TNodeTemplate.Requirements trequirements = tnodeTemplate.getRequirements();
    if (trequirements != null) {
      processRequirements(trequirements, tnodeTemplate, rstList, ynodeTemplate);
      addDefaultRequirementAssignments(ynodeTemplate, ynodeType);
    }

    // nodetemplate-position

    PositionNamespaceHelper.getInstance().addPosition(
        tnodeTemplate.getName(), getNodetemplatePosition(tnodeTemplate));

    return Xml2YamlSwitchUtils.buildEntry(
        Xml2YamlSwitchUtils.getYamlNodeTemplateName(tnodeTemplate), ynodeTemplate);
  }

  /**
   * @param ynodeTemplate
   * @param ynodeType 
   */
  private void addDefaultRequirementAssignments(NodeTemplate ynodeTemplate, NodeType ynodeType) {
    List<Map<String, RequirementDefinition>> requirementDefList = ynodeType.getRequirements();
    for (Map<String, RequirementDefinition> requirementDef : requirementDefList) {
      for (Entry<String, RequirementDefinition> reqDef : requirementDef.entrySet()) {
        String requirementName = reqDef.getKey();
        if (getRequirementByName(ynodeTemplate, requirementName) == null) {
          ynodeTemplate.getRequirements().add(
              buildDefaultRequirementAssignment(requirementName, reqDef.getValue()));
        }
      }
    }
  }


  /**
   * @param requirementName
   * @param requirementDef
   * @return
   */
  private Map<String, Object> buildDefaultRequirementAssignment(String requirementName,
      RequirementDefinition requirementDef) {
    String node = requirementDef.getNode();
    if (node == null || node.isEmpty()) {
      node = "tosca.nodes.Root";
    }
    
    Map<String, Object> requirement = new HashMap<>();
    requirement.put(requirementName, new Requirement(node));
    return requirement;
  }


  /**
   * @param ynodeTemplate
   * @param requirementName
   */
  private Object getRequirementByName(NodeTemplate ynodeTemplate, String requirementName) {
    if (ynodeTemplate.getRequirements() != null) {
      for (Map<String, Object> requirement : ynodeTemplate.getRequirements()) {
        for (Entry<String, Object> req : requirement.entrySet()) {
          if (req.getKey().equals(requirementName)) {
            return req.getValue();
          }
        }
      }
    }
    
    return null;
  }


  /**
   * @param ypropsAndAttrs .
   * @param yattributeDefs .
   * @return .
   */
  private Map<String, Object> getYAttributes(Map<String, Object> ypropsAndAttrs,
      Map<String, AttributeDefinition> yattributeDefs) {
    Map<String, Object> yattributes = new HashMap<>();
    for (String name : ypropsAndAttrs.keySet()) {
      if (yattributeDefs.containsKey(name)) {
        yattributes.put(name, ypropsAndAttrs.get(name));
      }
    }
    return yattributes;
  }

  /**
   * @param ypropsAndAttrs .
   * @param ypropertyDefs .
   * @return .
   */ 
  private Map<String, Object> getYProperties(Map<String, Object> ypropsAndAttrs,
      Map<String, PropertyDefinition> ypropertyDefs) {
    Map<String, Object> yproperties = new HashMap<>();
    for (String name : ypropsAndAttrs.keySet()) {
      if (ypropertyDefs.containsKey(name)) {
        yproperties.put(name, ypropsAndAttrs.get(name));
      }
    }

    return yproperties;
  }
  
  
  private void processRequirements(TNodeTemplate.Requirements trequirements, TNodeTemplate tnodeTemplate,
      ArrayList<TRelationshipTemplate> rstList, NodeTemplate ynodeTemplate) {
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
  
  private Requirement[] buildRequirement(TRequirement trequirement, TNodeTemplate tnodeTemplate,
      ArrayList<TRelationshipTemplate> rstList) {
    // Extension Relationship
    Requirement yRequirement = createExtRrequirement(trequirement);
    if (yRequirement != null) {
        return new Requirement[]{yRequirement};
    }
    // Manual Relationship
    Requirement[] yrequirements = createRrequirement(tnodeTemplate, trequirement, rstList);
    if (yrequirements.length > 0) {
      return yrequirements;
    }
    // Auto Relationship
    yRequirement = createFilterRequirement(trequirement);
    if (yRequirement != null) {
      return new Requirement[]{yRequirement};
    }
    return new Requirement[0];
  }

  private Requirement createExtRrequirement(TRequirement trequirement) {
    String destNodeName =
            trequirement.getOtherAttributes().get(new QName(Constants.REQUIREMENT_EXT_NODE));
    String capability =
            trequirement.getOtherAttributes().get(
                    new QName(Constants.REQUIREMENT_EXT_CAPABILITY));

    if (destNodeName == null || destNodeName.isEmpty()) {
        return null;
    }
    Requirement yrequirement = new Requirement();
    yrequirement.setNode(destNodeName);
    
    yrequirement.setCapability(capability);
    
    String rsType =
        trequirement.getOtherAttributes().get(new QName(Constants.REQUIREMENT_EXT_RELATIONSHIP));
    if (rsType != null && !rsType.isEmpty()) {
      yrequirement.setRelationship(new RequirementRelationship(rsType));
    }
    
    return yrequirement;
  }
    
  private Requirement[] createRrequirement(TNodeTemplate tNodeTemplate,
      TRequirement trequirement, List<TRelationshipTemplate> rstList) {
    List<Requirement> yrequirementList = new ArrayList<>();
    if (rstList != null && !rstList.isEmpty()) {
      for (TRelationshipTemplate rst : rstList){
        if(isRequirementEqualSourceNode(trequirement, rst)){
            TEntityTemplate target  = (TEntityTemplate) rst.getTargetElement().getRef();
            String rsType = Xml2YamlTypeMapper.mappingRelationshipType(
                Xml2YamlSwitchUtils.getNamefromQName(rst.getType()));
            Requirement yrequirement = buildRequirement(target, rsType);
            if (yrequirement != null) {
              yrequirementList.add(yrequirement);
            }
        }
      }
    }

    return yrequirementList.toArray(new Requirement[0]);
  }

  private Requirement buildRequirement(TEntityTemplate target, String rsType) {
    // when the target is a node
    if(target instanceof TNodeTemplate){
        TNodeTemplate tnode = (TNodeTemplate)target;
        Requirement yrequirement = new Requirement();
        yrequirement.setNode(Xml2YamlSwitchUtils.getYamlNodeTemplateName(tnode));
        yrequirement.setRelationship(new RequirementRelationship(rsType));
        return yrequirement;
    //when the target is a capability
    }else if(target instanceof TCapability){
        TCapability capability = (TCapability)target;
        TNodeTemplate tnode = findTNodeTemplatByCapability(capability);
        Requirement yrequirement = new Requirement();
        yrequirement.setCapability(capability.getName());
        yrequirement.setNode(Xml2YamlSwitchUtils.getYamlNodeTemplateName(tnode));
        yrequirement.setRelationship(new RequirementRelationship(rsType));
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

  /**
   * @param trequirement .
   * @return .
   */
  private Requirement createFilterRequirement(TRequirement trequirement) {
    if (trequirement.getProperties() == null) {
      return null;
    }

    NodeFilter nodeFilter = buildNodeFilter(trequirement);
    if (nodeFilter == null) {
      return null;
    }

    return new Requirement(nodeFilter);
  }

  private NodeFilter buildNodeFilter(TRequirement trequirement) {
    PropertiesFilter capabilityFilter = buildCapabilityFilter(trequirement);
    if (capabilityFilter == null) {
      return null;
    }
    
    NodeFilter nodeFilter = new NodeFilter();
    nodeFilter.getCapabilities().add(
        new CapabilityFilter(trequirement.getName(), capabilityFilter));

    return nodeFilter;
  }

  private PropertiesFilter buildCapabilityFilter(TRequirement trequirement) {
    Map<String, Object> yproperties =
        Xml2YamlSwitchUtils.convert2PropertiesOrAttributes(trequirement.getProperties());
    if (yproperties == null || yproperties.isEmpty()) {
      return null;
    }
    
    PropertiesFilter capabilityFilter = new PropertiesFilter();
    capabilityFilter.setProperties(buildPropertyFilters(yproperties));
    return capabilityFilter;
  }

  private List<PropertyFilter> buildPropertyFilters(Map<String, Object> yproperties) {
    List<PropertyFilter> ypropertyFilter = new ArrayList<>();
    for (Entry<String, Object> entry : yproperties.entrySet()) {
      ypropertyFilter.add(new PropertyFilter(entry.getKey(), new ConstraintClause(entry.getValue())));
    }
    
    return ypropertyFilter;
  }


  /**
   * @param tcapabilities .
   * @return .
   */
  private Map<String, Capability> processCapabilities(Capabilities tcapabilities) {
    Map<String, Capability> ycapabilities = new HashMap<>();
    if (tcapabilities.getCapability() != null && !tcapabilities.getCapability().isEmpty()) {
      for (TCapability tcapability : tcapabilities.getCapability()) {
        Capability yCapability = buildCapability(tcapability);
        if(isValidCapability(yCapability)) {
          ycapabilities.put(tcapability.getName(), yCapability);
        }
      }
    }

    return ycapabilities;

  }
  
  /**
   * @param tDeploymentArtifacts
   * @return
   */
  private Map<String, ArtifactDefinition> processArtifacts(TDeploymentArtifacts tDeploymentArtifacts) {
    Map<String, ArtifactDefinition> yartifacts = new HashMap<>();
    
    if (tDeploymentArtifacts != null && tDeploymentArtifacts.getDeploymentArtifact() != null && !tDeploymentArtifacts.getDeploymentArtifact().isEmpty()) {
      for (TDeploymentArtifact  tDeploymentArtifact: tDeploymentArtifacts.getDeploymentArtifact()) {
        ArtifactDefinition yArtifact= buildArtifact(tDeploymentArtifact);
        if(yArtifact != null) {
          yartifacts.put(tDeploymentArtifact.getName(), yArtifact);
        }
      }
    }
    
    return yartifacts;
  }

  /**
   * @param yCapability
   * @return
   */
  private boolean isValidCapability(Capability yCapability) {
    return yCapability.getProperties() != null && !yCapability.getProperties().isEmpty();
  }

  /**
   * @param tcapability .
   * @return .
   */
  private Capability buildCapability(TCapability tcapability) {
    return new Capability(Xml2YamlSwitchUtils.convert2PropertiesOrAttributes(tcapability.getProperties()));
  }
  
  private ArtifactDefinition buildArtifact(TDeploymentArtifact tDeploymentArtifact) {
    return Xml2YamlSwitchUtils.convert2ArtifactDefinition(tDeploymentArtifact);
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
}
