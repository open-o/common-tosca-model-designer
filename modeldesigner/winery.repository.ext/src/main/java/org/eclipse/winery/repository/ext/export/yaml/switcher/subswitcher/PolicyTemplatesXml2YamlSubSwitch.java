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

import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Policies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.utils.YamlBeansUtils;
import org.eclipse.winery.repository.ext.yamlmodel.PolicyTemplate;

/**
 * This class supports processing of node templates of a YAML service template.
 */
public class PolicyTemplatesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

	public PolicyTemplatesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
		super(parentSwitch);
	}

    /**
	 * 
	 */
	@Override
	public void process() {
        TServiceTemplate tServiceTemplate = getTServiceTemplate();
        if (tServiceTemplate == null || tServiceTemplate.getBoundaryDefinitions() == null) {
            return;
        }
        
        Policies policies = tServiceTemplate.getBoundaryDefinitions().getPolicies();
        if (policies != null) {
            HashMap<String, PolicyTemplate> policyTemplateMap = xmlPolicies2YamlPolicies(policies);
            getServiceTemplate().setPolicies(policyTemplateMap);
        }
	}

    private HashMap<String, PolicyTemplate> xmlPolicies2YamlPolicies(Policies policies) {
        HashMap<String, PolicyTemplate> policyTemplateMap = new HashMap<String, PolicyTemplate>();

        for (TPolicy tPolicy : policies.getPolicy()) {
            PolicyTemplate policyTemplate = YamlBeansUtils.convertPolicyTemplate(tPolicy);
            policyTemplateMap.put(tPolicy.getName(), policyTemplate);
        }
        return policyTemplateMap;
    }
}
