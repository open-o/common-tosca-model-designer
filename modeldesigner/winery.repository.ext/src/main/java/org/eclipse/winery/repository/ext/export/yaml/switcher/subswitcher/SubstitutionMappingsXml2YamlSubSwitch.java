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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityRef;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;

/**
 * This class supports processing of node types from a YAML service template.
 */
public class SubstitutionMappingsXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

    public SubstitutionMappingsXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
        super(parentSwitch);
    }

    /**
     * Processes every in-memory node type and creates a corresponding YAML node type.
     */
    @Override
    public void process() {
        TServiceTemplate tServiceTemplate = getTServiceTemplate();
        if (tServiceTemplate == null) {
            return;
        }

        if (tServiceTemplate.getSubstitutableNodeType() == null) {
            return;
        }

        Map<String, Object> substitution_mappings = new HashMap<>();
        substitution_mappings.put("type", Xml2YamlSwitchUtils
                .getNamefromQName(tServiceTemplate.getSubstitutableNodeType()));

        List<TCapabilityRef> tCapabilityList = getTCapabilityList();

        if (tCapabilityList != null && !tCapabilityList.isEmpty()) {
            Map<String, String[]> yCapabilities = buildYCapabilities(tCapabilityList);
            substitution_mappings.put("capabilities", yCapabilities);
        }

        List<TRequirementRef> tRequirementList = getTRequirementList();
        if (tCapabilityList != null && !tCapabilityList.isEmpty()) {
            Map<String, String[]> yRequirements = buildYRequirements(tRequirementList);
            substitution_mappings.put("requirements", yRequirements);
        }

        getTopology_template().setSubstitution_mappings(substitution_mappings);
    }

    private Map<String, String[]> buildYRequirements(List<TRequirementRef> tRequirementList) {
        Map<String, String[]> yRequirements = new HashMap<>();
        for (TRequirementRef tRequirement : tRequirementList) {
            if (tRequirement.getRef() instanceof TRequirement) {
                String yRequirementName = tRequirement.getName();
                yRequirements.put(yRequirementName,
                        buildYRequirementValue((TRequirement) tRequirement
                                .getRef()));
            }

        }
        return yRequirements;
    }

    private Map<String, String[]> buildYCapabilities(
            List<TCapabilityRef> tCapabilityList) {
        Map<String, String[]> yCapabilities = new HashMap<>();
        for (TCapabilityRef tCapability : tCapabilityList) {
            if (tCapability.getRef() instanceof TCapability) {
                String yCapabilityName = tCapability.getName();
                yCapabilities.put(yCapabilityName,
                        buildYCapabilityValue((TCapability) tCapability
                                .getRef()));
            }
        }

        return yCapabilities;
    }


    /**
     * @param tRequirement
     * @return
     */
    private String[] buildYRequirementValue(TRequirement tRequirement) {
        TNodeTemplate tNodeTemplate = getRequirementBelongTNodeTemplate(tRequirement.getId());

        if (tNodeTemplate == null) {
            return new String[] { tRequirement.getId() };
        }

        return new String[] { tNodeTemplate.getName(), tRequirement.getName() };
    }

    private TNodeTemplate getRequirementBelongTNodeTemplate(String tRequirementId) {
        for (TEntityTemplate tTmpl : getTServiceTemplate().getTopologyTemplate()
                .getNodeTemplateOrRelationshipTemplate()) {
            if (tTmpl instanceof TNodeTemplate) {
                TNodeTemplate tNodeTmpl = (TNodeTemplate) tTmpl;
                if (tNodeTmpl.getRequirements() != null
                        && tNodeTmpl.getRequirements().getRequirement() != null) {
                    for (TRequirement tRequirement : tNodeTmpl
                            .getRequirements().getRequirement()) {
                        if (tRequirement.getId().equals(tRequirementId)) {
                            return tNodeTmpl;
                        }
                    }
                }
            }
        }
        return null;
    }

    private String[] buildYCapabilityValue(TCapability tCapability) {
        String tCapabilityId = tCapability.getId();

        TNodeTemplate tmp = getCapabilityBelongTNodeTemplate(tCapabilityId);

        if (tmp == null) {
            return new String[] { tCapability.getId() };
        }

        return new String[] { tmp.getName(), tCapability.getName() };
    }

    private TNodeTemplate getCapabilityBelongTNodeTemplate(String tCapabilityId) {
        for (TEntityTemplate tTmpl : getTServiceTemplate().getTopologyTemplate()
                .getNodeTemplateOrRelationshipTemplate()) {
            if (tTmpl instanceof TNodeTemplate) {
                TNodeTemplate tNodeTmpl = (TNodeTemplate) tTmpl;
                if (tNodeTmpl.getCapabilities() != null
                        && tNodeTmpl.getCapabilities().getCapability() != null) {
                    for (TCapability tCapability : tNodeTmpl.getCapabilities().getCapability()) {
                        if (tCapability.getId().equals(tCapabilityId)) {
                            return tNodeTmpl;
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<TCapabilityRef> getTCapabilityList() {
        TBoundaryDefinitions tbd = getTBoundaryDefinitions();
        if (tbd != null) {
            TBoundaryDefinitions.Capabilities tCapabilities = tbd
                    .getCapabilities();
            if (tCapabilities != null) {
                return tCapabilities.getCapability();
            }

        }
        return null;
    }

    private List<TRequirementRef> getTRequirementList() {
        TBoundaryDefinitions tbd = getTBoundaryDefinitions();
        if (tbd != null) {
            TBoundaryDefinitions.Requirements tRequirements = tbd
                    .getRequirements();

            if (tRequirements != null) {
                return tRequirements.getRequirement();
            }

        }

        return null;
    }

}
