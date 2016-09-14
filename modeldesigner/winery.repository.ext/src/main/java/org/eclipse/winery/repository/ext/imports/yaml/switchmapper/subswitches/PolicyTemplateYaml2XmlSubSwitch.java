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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

import java.util.Map.Entry;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Policies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.utils.YamlBeansUtils;
import org.eclipse.winery.repository.ext.yamlmodel.PolicyTemplate;

/**
 * This class supports processing of policy types from a YAML service template.
 */
public class PolicyTemplateYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

    public PolicyTemplateYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
        super(parentSwitch);
    }

    @Override
    public void process() {
        if (getServiceTemplate().getPolicies() != null) {
            Policies policies = new Policies();
            for (final Entry<String, PolicyTemplate> entry : getServiceTemplate()
                    .getPolicies().entrySet()) {
                TPolicy tpolicy = YamlBeansUtils.convertTPolicy(entry.getKey(), entry.getValue());
                policies.getPolicy().add(tpolicy);
            }
            TServiceTemplate tServiceTemplate = getTServiceTemplate();
            tServiceTemplate.getBoundaryDefinitions().setPolicies(policies);
        }
    }
}
