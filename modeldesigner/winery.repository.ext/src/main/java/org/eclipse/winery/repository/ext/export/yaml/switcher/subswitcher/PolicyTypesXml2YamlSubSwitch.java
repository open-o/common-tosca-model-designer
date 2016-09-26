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
import java.util.Map.Entry;

import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.PolicyType;

/**
 * This class supports processing of node types from a YAML service template.
 */
public class PolicyTypesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

    public PolicyTypesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
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
                if (tNode instanceof TPolicyType) {
                    Entry<String, PolicyType> yPolicyType = createPolicyType((TPolicyType) tNode);
                    getServiceTemplate().getPolicy_types().put(yPolicyType.getKey(),
                            yPolicyType.getValue());
                }

            }
        }
    }

    /**
     * @param tPolicyType
     * @return
     */
    private Entry<String, PolicyType> createPolicyType(TPolicyType tPolicyType) {
        PolicyType yPolicyType = new PolicyType();

        // derived_from
        if (tPolicyType.getDerivedFrom() == null) {
            yPolicyType.setDerived_from("tosca.policies.Root");
        } else {
            yPolicyType.setDerived_from(Xml2YamlTypeMapper.mappingNodeType(Xml2YamlSwitchUtils
                    .getNamefromQName(tPolicyType.getDerivedFrom().getTypeRef())));
        }

        // description
        String description =
                Xml2YamlSwitchUtils.convert2Description(tPolicyType.getDocumentation());
        if (null != description) {
            yPolicyType.setDescription(description);
        }

        // properties
        yPolicyType.setProperties(Xml2YamlSwitchUtils.convert2PolicyPropertyDefinitions(tPolicyType
                .getAny()));

        return buildEntry(tPolicyType.getName(), yPolicyType);
    }

    private Entry<String, PolicyType> buildEntry(String name, PolicyType yPolicyType) {
        Map<String, PolicyType> map = new HashMap<>();
        map.put(name, yPolicyType);

        return map.entrySet().iterator().next();
    }
}
