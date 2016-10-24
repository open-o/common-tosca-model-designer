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
import java.util.Map;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Capabilities;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityRef;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;

public class SubstitutionMappingsYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {
	private final TServiceTemplate tServiceTemplate;

	public SubstitutionMappingsYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch, TServiceTemplate tServiceTemplate) {
		super(parentSwitch);
		this.tServiceTemplate = tServiceTemplate;
	}

	@Override
	public void process() {
		if (getServiceTemplate().getTopology_template() == null) {
			return;
		}
		Map<String, Object> substitutionMappings = getServiceTemplate().getTopology_template()
				.getSubstitution_mappings();
		if (substitutionMappings == null || substitutionMappings.isEmpty()) {
			return;
		}
		if (substitutionMappings.containsKey("node_type")) {
			String node_type = (String) substitutionMappings.get("node_type");
			tServiceTemplate
					.setSubstitutableNodeType(Yaml2XmlDataHelper.newQName(getParent().getUsedNamespace(), node_type));
		}
		TBoundaryDefinitions tBoundaryDefinitions = new TBoundaryDefinitions();
		// elements-Requirements
		if (substitutionMappings.containsKey("requirements")){
			tBoundaryDefinitions.setRequirements(constructRequirements(substitutionMappings));
		}
		// elements-Capabilities
		if (substitutionMappings.containsKey("capabilities")){
			tBoundaryDefinitions.setCapabilities(constructCapabilities(substitutionMappings));
		}
		if(tBoundaryDefinitions != null){
			tServiceTemplate.setBoundaryDefinitions(tBoundaryDefinitions);
		}
	}
	//create requirements
	@SuppressWarnings("unchecked")
	private TBoundaryDefinitions.Requirements constructRequirements(Map<String, Object> substitutionMappings) {
		TBoundaryDefinitions.Requirements requirements = new TBoundaryDefinitions.Requirements();
		Map<String,Object> object = (Map<String, Object>) substitutionMappings.get("requirements");
		for (Map.Entry<String, Object> yRequirementData : object.entrySet()) {
			TRequirementRef tRequirementRef = new TRequirementRef();
			//name
			tRequirementRef.setName(yRequirementData.getKey());
			//ref
			Object value = yRequirementData.getValue();
			if(value instanceof List){
				for (String name : (List<String>)value) {
					if(getRequirementsIdByName(name)!=null){
						tRequirementRef.setRef(getRequirementsIdByName(name));
					}
				}
			}
			requirements.getRequirement().add(tRequirementRef);
		}
		return requirements;
	}

	//create Capabilities
	@SuppressWarnings("unchecked")
	private Capabilities constructCapabilities(Map<String, Object> substitutionMappings) {
		TBoundaryDefinitions.Capabilities capabilities = new TBoundaryDefinitions.Capabilities();
		Map<String,Object> object = (Map<String, Object>) substitutionMappings.get("capabilities");
		for (Map.Entry<String, Object> yCapabilitiesData : object.entrySet()) {
			TCapabilityRef capability = new TCapabilityRef();
			//name
			capability.setName(yCapabilitiesData.getKey());
			//ref
			Object value = yCapabilitiesData.getValue();
			if(value instanceof List){
				for (String name :(List<String>) value) {
					if(getCapabilityIdByName(name)!=null){
						capability.setRef(getCapabilityIdByName(name));
					}
				}
			}
			capabilities.getCapability().add(capability);
		}
		return capabilities;
	}
	private String getRequirementsIdByName(String requirementName){
		List<TEntityTemplate> template = tServiceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
		for (TEntityTemplate tt : template) {
            if (tt instanceof TNodeTemplate) {
            	TNodeTemplate tNodeTemplate = (TNodeTemplate)tt;
            	org.eclipse.winery.model.tosca.TNodeTemplate.Requirements requirements = tNodeTemplate.getRequirements();
            	if(requirements!=null){
	            	List<TRequirement> requirement = requirements.getRequirement();
	            	for (TRequirement tRequirement : requirement) {
	            		if(tRequirement.getName().equals(requirementName)){
	            			return tRequirement.getId();
	            		}
					}
            	}
            }
        }
		return null;
	}
	private String getCapabilityIdByName(String capabilityName){
		List<TEntityTemplate> template = tServiceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
		for (TEntityTemplate tt : template) {
			if (tt instanceof TNodeTemplate) {
				TNodeTemplate tNodeTemplate = (TNodeTemplate)tt;
				org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities capabilities = tNodeTemplate.getCapabilities();
				if(capabilities!=null){
					List<TCapability> capability = capabilities.getCapability();
					for (TCapability tCapability : capability) {
						if(tCapability.getName().equals(capabilityName)){
							return tCapability.getId();
						}
					}
				}
			}
		}
		return null;
	}
}
