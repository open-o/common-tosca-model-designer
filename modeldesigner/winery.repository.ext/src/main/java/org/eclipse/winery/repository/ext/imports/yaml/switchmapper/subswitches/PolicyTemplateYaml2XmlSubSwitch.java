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

import java.util.Arrays;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;

import org.eclipse.winery.common.boundaryproperty.PolicyExtInfo;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Policies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.utils.YamlBeansUtils;
import org.eclipse.winery.repository.ext.yamlmodel.Policy;
import org.eclipse.winery.repository.resources.entitytypes.policytypes.PolicyTypesResource;

/**
 * This class supports processing of policy types from a YAML service template.
 */
public class PolicyTemplateYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {
    public PolicyTemplateYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
        super(parentSwitch);
    }

    @Override
    public void process() {
        if (getServiceTemplate().getTopology_template().getPolicies() != null) {
            Policies tPolicies = new Policies();
            
            for (Map<String, Policy> policies : getServiceTemplate().getTopology_template().getPolicies()) {
              for (Entry<String, Policy> policy : policies.entrySet()) {
                TPolicy tpolicy = convertTPolicy(policy.getKey(), policy.getValue());
                
                tPolicies.getPolicy().add(tpolicy);
              }
            }
            
            getTServiceTemplate().getBoundaryDefinitions().setPolicies(tPolicies);
        }
    }
    
    private TPolicy convertTPolicy(String name, Policy policy) {
      if (null == policy) {
          return null;
      }
      PolicyExtInfo policyInfo = new PolicyExtInfo();
      policyInfo.setName(name);
      policyInfo.setDesc(policy.getDescription());
      policyInfo.setTarget(Arrays.asList(policy.getTargets()));
      policyInfo.setProperties(policy.getProperties());

      Class<? extends TOSCAComponentId> idClass =
              Utils.getComponentIdClassForComponentContainer(PolicyTypesResource.class);
      SortedSet<? extends TOSCAComponentId> allTOSCAcomponentIds =
              Repository.INSTANCE.getAllTOSCAComponentIds(idClass);
      for (TOSCAComponentId id : allTOSCAcomponentIds) {
          if (id.getXmlId().equals(policy.getType())) {
              policyInfo.setType(id.getQName());
          }
      }

      return Utils.buildPolicyInfo(policyInfo);
  }
}
