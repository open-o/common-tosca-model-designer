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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.CapabilityType;

/**
 * This sub switch supports processing the capability types of a YAML service template.
 */
public class CapabilityTypesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

	public CapabilityTypesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
		super(parentSwitch);
	}

    /**
     * 
     */
    @Override
    public void process() {
        List<?> nodeList = getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        if (nodeList != null) {
            for (Object node : nodeList) {
                if (node instanceof TCapabilityType) {
                    Entry<String, CapabilityType> yCapabilityType =
                            createCapabilityType((TCapabilityType) node);
                    getServiceTemplate().getCapability_types().put(yCapabilityType.getKey(),
                            yCapabilityType.getValue());
                }

            }
        }
	}

    /**
     * @param tCapabilityType
     * @return
     */
    private Entry<String, CapabilityType> createCapabilityType(TCapabilityType tCapabilityType) {
        CapabilityType yCapabilityType = new CapabilityType();

        // derived_from
        if (tCapabilityType.getDerivedFrom() == null) {
            yCapabilityType.setDerived_from(Xml2YamlTypeMapper.mappingCapabilityType(null));
        } else {
            yCapabilityType.setDerived_from(Xml2YamlTypeMapper
                    .mappingCapabilityType(Xml2YamlSwitchUtils
                    .getNamefromQName(tCapabilityType
                    .getDerivedFrom().getTypeRef())));
        }
        
        // description
        yCapabilityType.setDescription(Xml2YamlSwitchUtils
                .convert2Description(tCapabilityType.getDocumentation()));

        // properties
        yCapabilityType.setProperties(Xml2YamlSwitchUtils
                .convert2PropertyDefinitions(tCapabilityType.getAny()));

        return buildEntry(
                Xml2YamlTypeMapper.mappingCapabilityType(tCapabilityType
                        .getName()), yCapabilityType);
    }

    private Entry<String, CapabilityType> buildEntry(
            String name, CapabilityType yCapabilityType) {
        Map<String, CapabilityType> map = new HashMap<>();
        map.put(name, yCapabilityType);

        return map.entrySet().iterator().next();
    }

}
