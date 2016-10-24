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
package org.eclipse.winery.repository.importing;

import java.util.List;
import java.util.SortedSet;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityType.DerivedFrom;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeType.CapabilityDefinitions;
import org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.utils.AnyMap;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.requirementtypes.RequirementTypeResource;

/**
 * @author 10186401
 *
 */
public class NameSpaceHelper {

  private SortedSet<NodeTypeId> allNodeTypes;
  private SortedSet<RelationshipTypeId> allRelationTypes;
  private SortedSet<RequirementTypeId> allRequiremnetTypes;
  private SortedSet<CapabilityTypeId> allCapabilityTypes;

  public void init() {
    allNodeTypes = Repository.INSTANCE.getAllTOSCAComponentIds(NodeTypeId.class);
    allRelationTypes = Repository.INSTANCE.getAllTOSCAComponentIds(RelationshipTypeId.class);
    allRequiremnetTypes = Repository.INSTANCE.getAllTOSCAComponentIds(RequirementTypeId.class);
    allCapabilityTypes = Repository.INSTANCE.getAllTOSCAComponentIds(CapabilityTypeId.class);
  }

  public void clear() {
    allNodeTypes = null;
    allRelationTypes = null;
    allRequiremnetTypes = null;
    allCapabilityTypes = null;
  }

  public QName getNodeType(String key) {
    for (NodeTypeId id : allNodeTypes) {
      if (id.getQName().getLocalPart().equals(key)) {
        return id.getQName();
      }
    }
    return null;
  }

  public QName getRelationshipType(String key) {
    for (RelationshipTypeId id : allRelationTypes) {
      if (id.getQName().getLocalPart().equals(key)) {
        return id.getQName();
      }
    }
    return null;
  }

  public QName getRequirementType(String key) {
    for (RequirementTypeId id : allRequiremnetTypes) {
      if (id.getQName().getLocalPart().equals(key)) {
        return id.getQName();
      }
    }
    return null;
  }

  public QName getCapabilityType(String key) {
    for (CapabilityTypeId id : allCapabilityTypes) {
      if (id.getQName().getLocalPart().equals(key)) {
        return id.getQName();
      }
    }
    return null;
  }

  public void updateNameSpace(TExtensibleElements ci) {
    init();
    if (ci instanceof TServiceTemplate) {
      updateNodeTemplatesOrRelationshipTemplatesNamespace((TServiceTemplate) ci);
    } else if (ci instanceof TEntityType) {
      updateEntityTypeNameSpace((TEntityType) ci);
    }
    clear();
  }

  private void updateEntityTypeNameSpace(TEntityType ci) {
    updateDeriverdFromNameSapce(ci);
    if (ci instanceof TNodeType) {
      updateNodeTypeNameSpace((TNodeType) ci);
    }
  }


  private void updateNodeTypeNameSpace(TNodeType ci) {
    CapabilityDefinitions capabilityDefinitions = ci.getCapabilityDefinitions();
    if (null != capabilityDefinitions && null != capabilityDefinitions.getCapabilityDefinition()
        && !capabilityDefinitions.getCapabilityDefinition().isEmpty()) {
      for (TCapabilityDefinition tCapabilityDefinition : capabilityDefinitions
          .getCapabilityDefinition()) {
        QName type = getCapabilityType(tCapabilityDefinition.getCapabilityType().getLocalPart());
        if (null != type) {
          tCapabilityDefinition.setCapabilityType(type);
        }
      }
    }

    RequirementDefinitions requirementDefinitions = ci.getRequirementDefinitions();
    if (null != requirementDefinitions && null != requirementDefinitions.getRequirementDefinition()
        && !requirementDefinitions.getRequirementDefinition().isEmpty()) {
      for (TRequirementDefinition tRequirementDefinition : requirementDefinitions
          .getRequirementDefinition()) {
        
        QName type = getReqTypeByCap(tRequirementDefinition.getRequirementType().getLocalPart());
        if (null != type) {
          tRequirementDefinition.setRequirementType(type);
        }
      }
    }

  }

  private void updateDeriverdFromNameSapce(TEntityType ci) {
    DerivedFrom derivedFrom = ci.getDerivedFrom();
    if (null == derivedFrom || null == derivedFrom.getTypeRef()) {
      return;
    }
    String localPart = derivedFrom.getTypeRef().getLocalPart();
    QName type = null;
    if (ci instanceof TNodeType) {
      type = getNodeType(localPart);
    } else if (ci instanceof TRelationshipType) {
      type = getRelationshipType(localPart);
    } else if (ci instanceof TRequirementType) {
      type = getRequirementType(localPart);
    } else if (ci instanceof TCapabilityType) {
      type = getCapabilityType(localPart);
    }

    if (null != type) {
      derivedFrom.setTypeRef(type);
    }
  }

  private void updateNodeTemplatesOrRelationshipTemplatesNamespace(TServiceTemplate serviceTemplate) {
    List<TEntityTemplate> nodeOrRelationList =
        serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
    for (TEntityTemplate tt : nodeOrRelationList) {
      if (tt instanceof TNodeTemplate) {
        updateNodeTemplateNameSpace((TNodeTemplate) tt);
      } else if (tt instanceof TRelationshipTemplate) {
        updateRelationshipTemplateNameSpace((TRelationshipTemplate) tt);
      }
    }
  }

  private void updateRelationshipTemplateNameSpace(TRelationshipTemplate template) {
    QName type = getRelationshipType(template.getType().getLocalPart());
    if (null != type) {
      template.setType(type);
    }
  }

  private void updateNodeTemplateNameSpace(TNodeTemplate template) {
    QName type = getNodeType(template.getType().getLocalPart());
    if (null != type) {
      template.setType(type);
      updateNodeTemplatePropertyNameSpace(type.getNamespaceURI(), template);
    }

    updateNodeTemplateReqCapNameSapce(template, type);
  }

  private void updateNodeTemplateReqCapNameSapce(TNodeTemplate template, QName nodeType) {
    TNodeType nodetype =
        new NodeTypesResource().getComponentInstaceResource(
            Util.URLencode(nodeType.getNamespaceURI()), nodeType.getLocalPart()).getNodeType();

    updateCapType4NodeTemplate(template, nodetype.getCapabilityDefinitions());
    updateReqType4NodeTemplate(template, nodetype.getRequirementDefinitions());
  }

  private void updateReqType4NodeTemplate(TNodeTemplate template, RequirementDefinitions reqDefs) {
    if (null == template.getRequirements() || null == template.getRequirements().getRequirement()
        || template.getRequirements().getRequirement().isEmpty()) {
      return;
    }

    if (null == reqDefs || null == reqDefs.getRequirementDefinition()) {
      return;
    }

    // find reqtype in nodetype definition
    for (TRequirement tReqInTemplate : template.getRequirements().getRequirement()) {
      for (TRequirementDefinition tReqInType : reqDefs.getRequirementDefinition()) {
        if (tReqInType.getName().equals(tReqInTemplate.getName())) {
          // captype def in nodetype's reqs definistions, so Traversal all reqtype to find which one
          // is connected to the cap
          QName reqType = getReqTypeByCap(tReqInType.getRequirementType().getLocalPart());
          if (null != reqType) {
            tReqInTemplate.setType(reqType);
          }
        }
      }
    }
  }

  private QName getReqTypeByCap(String localPart) {
    for (RequirementTypeId id : allRequiremnetTypes) {
      TRequirementType requirementType = getRequirementTypeById(id);
      if (requirementType.getRequiredCapabilityType() != null && localPart.equals(requirementType.getRequiredCapabilityType().getLocalPart())) {
        return new QName(requirementType.getTargetNamespace(), requirementType.getName());
      }
    }
    return null;
  }

  private TRequirementType getRequirementTypeById(RequirementTypeId id) {
    return new RequirementTypeResource(id).getRequirementType();
  }

  private void updateCapType4NodeTemplate(TNodeTemplate template, CapabilityDefinitions capDefs) {
    if (null == template.getCapabilities() || null == template.getCapabilities().getCapability()
        || template.getCapabilities().getCapability().isEmpty()) {
      return;
    }

    if (null == capDefs || null == capDefs.getCapabilityDefinition()) {
      return;
    }

    // find captype in nodetype definition
    for (TCapability tCapInTemplate : template.getCapabilities().getCapability()) {
      for (TCapabilityDefinition tCapInType : capDefs.getCapabilityDefinition()) {
        if (tCapInType.getName().equals(tCapInTemplate.getName())) {
          tCapInTemplate.setType(tCapInType.getCapabilityType());
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void updateNodeTemplatePropertyNameSpace(String namespaceURI, TNodeTemplate nodeTemplate) {
    if (nodeTemplate.getProperties() == null || nodeTemplate.getProperties().getAny() == null) {
      return;
    }

    if (nodeTemplate.getProperties().getAny() instanceof JAXBElement) {
      QName newQname =
          new QName(namespaceURI + "/" + CommonConst.PROPERTIES_DEFINITION,
              CommonConst.PROPERTIES_LOCALPART);
      AnyMap properties = ((JAXBElement<AnyMap>) nodeTemplate.getProperties().getAny()).getValue();
      JAXBElement<AnyMap> jaxbprop = new JAXBElement<AnyMap>(newQname, AnyMap.class, properties);
      nodeTemplate.getProperties().setAny(jaxbprop);
    }
  }
}
