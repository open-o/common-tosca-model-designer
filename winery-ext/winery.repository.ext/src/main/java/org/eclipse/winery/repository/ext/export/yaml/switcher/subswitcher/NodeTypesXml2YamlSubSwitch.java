/**
 * Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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

import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeType.CapabilityDefinitions;
import org.eclipse.winery.model.tosca.TNodeType.Interfaces;
import org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.NodeType;
import org.eclipse.winery.repository.resources.entitytypes.requirementtypes.RequirementTypesResource;

/**
 * This class supports processing of node types from a YAML service template.
 */
public class NodeTypesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

    public NodeTypesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
        super(parentSwitch);
    }

    /**
     * Processes every in-memory node type and creates a corresponding YAML node type.
     */
    @Override
    public void process() {
        List<?> tNodeList = getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        if (tNodeList != null) {
            for (Object tNode : tNodeList) {
                if (tNode instanceof TNodeType) {
                    Entry<String, NodeType> yNodeType = createNodeType((TNodeType) tNode);
                    getServiceTemplate().getNode_types().put(
                            yNodeType.getKey(), yNodeType.getValue());
                }

            }
        }
    }

    /**
     * @param tNodeType
     * @return
     */
    private Entry<String, NodeType> createNodeType(TNodeType tNodeType) {
        NodeType yNodeType = new NodeType();

        // derived_from
        if (tNodeType.getDerivedFrom() == null) {
            yNodeType.setDerived_from(Xml2YamlTypeMapper.mappingNodeType(null));
        } else {
            yNodeType.setDerived_from(Xml2YamlTypeMapper
                    .mappingNodeType(Xml2YamlSwitchUtils
                            .getNamefromQName(tNodeType.getDerivedFrom()
                                    .getTypeRef())));
        }

        // description
        yNodeType.setDescription(Xml2YamlSwitchUtils
                .convert2Description(tNodeType.getDocumentation()));

        // properties
        yNodeType.setProperties(Xml2YamlSwitchUtils
                .convert2PropertyDefinitions(tNodeType.getAny()));

        // attributes
        yNodeType.setAttributes(Xml2YamlSwitchUtils
                .convert2AttributeDefinitions(tNodeType.getAny()));
        
        // capabilities
        CapabilityDefinitions tCapabilities = tNodeType
                .getCapabilityDefinitions();
        if (tCapabilities != null) {
            yNodeType
                    .setCapabilities(parseNodeTypeCapabilities(tCapabilities));
        }

        // requirements
        RequirementDefinitions tRequirements = tNodeType
                .getRequirementDefinitions();
        if (tRequirements != null) {
            List<Map<String, Object>> yamlRequirementList = parseNodeTypeRequirementDefinitions(tRequirements);
            yNodeType.setRequirements(yamlRequirementList);

        }

        // interfaces
        Interfaces tInterfaces = tNodeType.getInterfaces();
        if (tInterfaces != null) {
            Map<String, Map<String, Map<String, String>>> yamlInterfaces = parseNodeTypeInterfaces(tInterfaces);
            yNodeType.setInterfaces(yamlInterfaces);
        }

        // TODO artifacts

        String name = Xml2YamlTypeMapper.mappingNodeType(tNodeType.getName());
        return buildEntry(name, yNodeType);
    }

    private Entry<String, NodeType> buildEntry(String name, NodeType yNodeType) {
        Map<String, NodeType> map = new HashMap<>();
        map.put(name, yNodeType);
        
        return map.entrySet().iterator().next();
    }

    /**
     * @param tInterfaces
     * @return
     */
    private Map<String, Map<String, Map<String, String>>> parseNodeTypeInterfaces(
            Interfaces tInterfaces) {
        // TODO Auto-generated method stub
        return null;
    }

    // private Interfaces parseNodeTypeInterfaces(Map<String, Map<String, Map<String, String>>>
    // interfaces) {
    // Interfaces result = new Interfaces();
    // for (Entry<String, Map<String, Map<String, String>>> entry : interfaces.entrySet()) {
    // TInterface inf = getInterfaceWithOperations(entry);
    // result.getInterface().add(inf);
    // }
    // return result;
    // }

    /**
     * @param tRequirements
     * @return
     */
    private List<Map<String, Object>> parseNodeTypeRequirementDefinitions(
            RequirementDefinitions tRequirements) {
        List<Map<String, Object>> yRequirementList = new ArrayList<>();

        List<TRequirementDefinition> tRequirementList = tRequirements.getRequirementDefinition();
        for (TRequirementDefinition tRequirement : tRequirementList) {
            Map<String, Object> yamlRequirement = new HashMap<>();
            yamlRequirement.put(tRequirement.getName(), convert2YamlRequirementObject(tRequirement));
            
            yRequirementList.add(yamlRequirement);
        }

        return yRequirementList;
    }

    private Map<String, String> convert2YamlRequirementObject(TRequirementDefinition tRequirement) {
        Map<String, String> yRequirementObject = new HashMap<>(); // TODO
                                                                     // yamlRequirementDefinition瀵硅薄鐨勭被寰呭畾涔夛紝鏆傛椂浠ap浠ｆ浛銆�
        String tRequirementType = Xml2YamlSwitchUtils
                .getNamefromQName(tRequirement
                .getRequirementType());

        QName tRequiredCapabilityType = new RequirementTypesResource()
                .getComponentInstaceResource(
                        Util.URLencode(tRequirement.getRequirementType()
                                .getNamespaceURI()), tRequirementType)
                .getRequiredCapabilityTypeResource().getRequirementType()
                .getRequiredCapabilityType();

        if (tRequiredCapabilityType != null) {
            String yCapabilityType = Xml2YamlSwitchUtils
                    .getNamefromQName(tRequiredCapabilityType);
            yRequirementObject.put("capability",
                    Xml2YamlTypeMapper.mappingCapabilityType(yCapabilityType));
        } else {
            yRequirementObject.put("capability", Xml2YamlTypeMapper
                    .mappingTRequirement2yCapabilityType(tRequirementType));
        }


        return yRequirementObject;
    }



    private Map<String, Object> parseNodeTypeCapabilities(CapabilityDefinitions tCapabilities) {
        Map<String, Object> yCapabilities = new HashMap<>();
        if (tCapabilities.getCapabilityDefinition() != null
                && !tCapabilities.getCapabilityDefinition().isEmpty()) {
            for (TCapabilityDefinition tCapability : tCapabilities.getCapabilityDefinition()) {
                yCapabilities.put(tCapability.getName(), convert2YamlCapabilityObject(tCapability));
            }
        }

        return yCapabilities;
    }

    private Map<String, String> convert2YamlCapabilityObject(TCapabilityDefinition tCapability) {
        Map<String, String> yCapabilityObject = new HashMap<>(); // TODO
        // yamlCapabilityDefinition瀵硅薄鐨勭被寰呭畾涔夛紝鏆傛椂浠ap浠ｆ浛銆�
        yCapabilityObject.put("type", Xml2YamlTypeMapper
                .mappingCapabilityType(Xml2YamlSwitchUtils
                        .getNamefromQName(tCapability.getCapabilityType())));

        return yCapabilityObject;
    }

}
