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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;

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

/**
 * This class supports processing of node templates of a YAML service template.
 */
public class NodeTemplatesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

	public NodeTemplatesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
		super(parentSwitch);
	}

    /**
	 * 
	 */
	@Override
	public void process() {
        TServiceTemplate tServiceTemplate = getTServiceTemplate();
        if (tServiceTemplate == null) {
            return;
        }
        getServiceTemplate().setDescription(
                Xml2YamlSwitchUtils.convert2Description(tServiceTemplate
                        .getDocumentation()));

        TTopologyTemplate tTopologyTemplate = tServiceTemplate
                .getTopologyTemplate();
        if (tTopologyTemplate != null) {
            getTopology_template().setDescription(
                    Xml2YamlSwitchUtils.convert2Description(tTopologyTemplate
                            .getDocumentation()));

            parseTempateList(tTopologyTemplate
                    .getNodeTemplateOrRelationshipTemplate());
        }
	}

    private void parseTempateList(List<TEntityTemplate> tTemplateList) {
        if (tTemplateList == null || tTemplateList.isEmpty()) {
            return;
        }

        // TNodeTemplate
        for (TEntityTemplate tTemplate : tTemplateList) {
            if (tTemplate instanceof TNodeTemplate) {
                Entry<String, NodeTemplate> yNodeTemplate = createNodeTemplate((TNodeTemplate) tTemplate);

                getTopology_template().getNode_templates().put(
                        yNodeTemplate.getKey(), yNodeTemplate.getValue());
            }
        }
        // TRelationshipTemplate
        for (TEntityTemplate tTemplate : tTemplateList) {
            if (tTemplate instanceof TRelationshipTemplate) {
                // parseRelationshipTemplate((TRelationshipTemplate) tTemplate);
            }
        }
    }


    /**
     * @param tNodeTemplate
     * @return
     */
    private Entry<String, NodeTemplate> createNodeTemplate(
            TNodeTemplate tNodeTemplate) {
        NodeTemplate yNodeTemplate = new NodeTemplate();
        
        // description
        yNodeTemplate.setDescription(Xml2YamlSwitchUtils
                .convert2Description(tNodeTemplate.getDocumentation()));
        
        // type
        yNodeTemplate.setType(Xml2YamlTypeMapper.mappingNodeType(
                Xml2YamlSwitchUtils.getNamefromQName(tNodeTemplate.getType())));
        
        // properties & attributes
        Map<String, Object> yPropertiesAndAttributes = Xml2YamlSwitchUtils
                .convertTProperties(tNodeTemplate.getProperties());
        if (yPropertiesAndAttributes != null
                && !yPropertiesAndAttributes.isEmpty()) {
            TNodeType tNodeType = getTNodeType(tNodeTemplate);
            Map<String, PropertyDefinition> yPropertyDefs = Xml2YamlSwitchUtils
                    .convert2PropertyDefinitions(tNodeType.getAny());
            Map<String, Object> yProperties = getYProperties(
                    yPropertiesAndAttributes, yPropertyDefs);
            yNodeTemplate.setProperties(yProperties);

            Map<String, AttributeDefinition> yAttributeDefs = Xml2YamlSwitchUtils
                    .convert2AttributeDefinitions(tNodeType.getAny());
            Map<String, Object> yAttributes = getYAttributes(
                    yPropertiesAndAttributes, yAttributeDefs);
            yNodeTemplate.setAttributes(yAttributes);
        }
        
        // capabilities
        TNodeTemplate.Capabilities tCapabilities = tNodeTemplate.getCapabilities();
        if (tCapabilities != null) {
            yNodeTemplate
                    .setCapabilities(processCapabilitiesInNodeTemplate(tCapabilities));
        }
        
        // requirement
        TNodeTemplate.Requirements tRequirements = tNodeTemplate.getRequirements();
        if (tRequirements != null) {
            List<TRequirement> tRequirementList = tRequirements.getRequirement();
            if (tRequirementList != null && !tRequirementList.isEmpty()) {
                for (TRequirement tRequirement : tRequirementList) {
                    Requirement yRequirement = buildRequirement(tRequirement);
                    Map<String, Object> yRequirementMap = new HashMap<>();
                    yRequirementMap.put(tRequirement.getName(), yRequirement);
                    yNodeTemplate.getRequirements().add(yRequirementMap);
                }
            }

        }

        //nodetemplate-position
        NodeTemplatePosition position = getNodetemplatePosition(tNodeTemplate);
        yNodeTemplate.setPosition(position);
        
        return buildEntry(tNodeTemplate.getName(), yNodeTemplate);
    }

    private NodeTemplatePosition getNodetemplatePosition(TNodeTemplate tNodeTemplate) {
        NodeTemplatePosition position = new NodeTemplatePosition();
        for(QName key : tNodeTemplate.getOtherAttributes().keySet()){
            if(key.getLocalPart().equals(CommonConst.NODETEMPLATE_POSITIONX_LOCALPART)){
                position.setPositionX(tNodeTemplate.getOtherAttributes().get(key));
            }
            if(key.getLocalPart().equals(CommonConst.NODETEMPLATE_POSITIONY_LOCALPART)){
                position.setPositionY(tNodeTemplate.getOtherAttributes().get(key));
            }
        }
        return position;
    }
    
    /**
     * @param yPropertiesAndAttributes
     * @param yAttributeDefs
     * @return
     */
    private Map<String, Object> getYAttributes(
            Map<String, Object> yPropertiesAndAttributes,
            Map<String, AttributeDefinition> yAttributeDefs) {
        Map<String, Object> yAttributes = new HashMap<>();
        for (String name : yPropertiesAndAttributes.keySet()) {
            if (yAttributeDefs.containsKey(name)) {
                yAttributes.put(name, yPropertiesAndAttributes.get(name));
            }
        }
        return yAttributes;
    }

    /**
     * @param yPropertiesAndAttributes
     * @param yPropertyDefs
     * @return
     */
    private Map<String, Object> getYProperties(
            Map<String, Object> yPropertiesAndAttributes,
            Map<String, PropertyDefinition> yPropertyDefs) {
        Map<String, Object> yProperties = new HashMap<>();
        for (String name : yPropertiesAndAttributes.keySet()) {
            if (yPropertyDefs.containsKey(name)) {
                yProperties.put(name, yPropertiesAndAttributes.get(name));
            }
        }

        return yProperties;
    }

    /**
     * @param tNodeTemplate
     * @return
     */
    private TNodeType getTNodeType(TNodeTemplate tNodeTemplate) {
        NodeTypesResource res = new NodeTypesResource();
        NodeTypeResource nodetypeRes = res.getComponentInstaceResource(
                Util.URLencode(tNodeTemplate.getType().getNamespaceURI()),
                tNodeTemplate
                        .getType().getLocalPart());
        return (TNodeType) nodetypeRes.getEntityType();
    }

    /**
     * @param tRequirement
     * @return
     */
    private Requirement buildRequirement(TRequirement tRequirement) {
        if (tRequirement.getProperties() == null) {
            return null;
        }

        Requirement yRequirement = new Requirement();
        yRequirement.setNode_filter(buildNodeFilter(tRequirement));

        return yRequirement;
    }

    private NodeFilter buildNodeFilter(TRequirement tRequirement) {
        NodeFilter node_filter = new NodeFilter();

        CapabilityFilter capabilityFilter = new CapabilityFilter();
        Map<String, Object> yProperties = Xml2YamlSwitchUtils
                .convertTProperties(tRequirement.getProperties());
        List<Map<String, Object>> yPropertyFilter = Xml2YamlSwitchUtils
                .convertMap2ListMap(yProperties);
        capabilityFilter.setProperties(yPropertyFilter);

        Map<String, CapabilityFilter> yCapabilities = new HashMap<>();
        yCapabilities.put(tRequirement.getName(), capabilityFilter);

        node_filter.setCapabilities(Xml2YamlSwitchUtils
                .convertMap2ListMap(yCapabilities));

        return node_filter;
    }


    private Entry<String, NodeTemplate> buildEntry(String name,
            NodeTemplate yNodeTemplate) {
        Map<String, NodeTemplate> map = new HashMap<>();
        map.put(name, yNodeTemplate);

        return map.entrySet().iterator().next();
    }

    /**
     * @param tCapabilities
     * @return
     */
    private Map<String, Capability> processCapabilitiesInNodeTemplate(
            Capabilities tCapabilities) {

        Map<String, Capability> yCapabilities = new HashMap<>();
        if (tCapabilities.getCapability() != null
                && !tCapabilities.getCapability().isEmpty()) {
            for (TCapability tCapability : tCapabilities.getCapability()) {
                yCapabilities.put(tCapability.getName(), convert2YamlCapabilityObject(tCapability));
            }
        }

        return yCapabilities;
    
    }

    /**
     * @param tCapability
     * @return
     */
    private Capability convert2YamlCapabilityObject(
            TCapability tCapability) {
        Capability yCapabilityObject = new Capability();

        // yCapabilityObject.put("type", Xml2YamlTypeMapper
        // .mappingCapabilityType(Xml2YamlSwitchUtils
        // .getNamefromQName(tCapability.getType())));

        Map<String, Object> yProperties = Xml2YamlSwitchUtils
                .convertTProperties(tCapability.getProperties());
        yCapabilityObject.setProperties(yProperties);

        return yCapabilityObject;
    }

    private void parseRelationshipTemplate(TRelationshipTemplate trt) {
        String requirementName = trt.getName();
        String relationshipType = Xml2YamlTypeMapper
                .mappingRelationshipType(Xml2YamlSwitchUtils
                        .getNamefromQName(trt.getType()));

        String sourceNodeTemplateName = getNodeTemplateName(trt
                .getSourceElement().getRef());
        String targetNodeTemplateName = getNodeTemplateName(trt
                .getTargetElement().getRef());

        if (sourceNodeTemplateName != null && targetNodeTemplateName != null) {
            NodeTemplate ySourceNodeTemplate = getTopology_template()
                    .getNode_templates().get(sourceNodeTemplateName);

            NodeTemplate yTargetNodeTemplate = getTopology_template()
                    .getNode_templates().get(targetNodeTemplateName);

            if (ySourceNodeTemplate != null && yTargetNodeTemplate != null) {
                ySourceNodeTemplate.getRequirements().add(
                        buildRequirement(requirementName,
                                targetNodeTemplateName, relationshipType));
            }
        }
    }

    private Map<String, Object> buildRequirement(String requirementName,
            String targetNodeTemplateName, String relationshipType) {
        Map<String, Object> yRequirement = new HashMap<>();
        yRequirement
                .put(requirementName,
                        buildRequirementObject(targetNodeTemplateName,
                                relationshipType));
        return yRequirement;
    }

    private Requirement buildRequirementObject(
            String targetNodeTemplateName, String relationshipType) {
        Requirement yRequirementObject = new Requirement();

        yRequirementObject.setNode(targetNodeTemplateName);
        yRequirementObject.setRelationship(relationshipType);

        return yRequirementObject;
    }

    /**
     * @param object
     * @return
     */
    private String getNodeTemplateName(Object object) {
        if (object instanceof TNodeTemplate) {
            return ((TNodeTemplate) object).getName();
        }

        return null;
    }

}
