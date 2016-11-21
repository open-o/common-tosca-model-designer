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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.common.boundaryproperty.PolicyExtInfo;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Policies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.Policy;

/**
 * This class supports processing of node templates of a YAML service template.
 */
public class PoliciesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {
  public PoliciesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
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
    
    Policies tPolicies = tServiceTemplate.getBoundaryDefinitions().getPolicies();
    if (tPolicies != null) {
      getServiceTemplate().getTopology_template().setPolicies(xmlPolicies2YamlPolicies(tPolicies));
    }
  }

  private List<Map<String, Policy>> xmlPolicies2YamlPolicies(Policies tPolicies) {
    List<Map<String, Policy>> policies = new ArrayList<>();

    for (TPolicy tPolicy : tPolicies.getPolicy()) {
      Policy tmppolicy = convert2Policy(tPolicy);
      policies.add(Xml2YamlSwitchUtils.convert2MapObject(tPolicy.getName(), tmppolicy));
    }
    
    return policies;
  }
  
  private Policy convert2Policy(TPolicy tPolicy) {
    PolicyExtInfo policyExt = Utils.buildTPolicy(tPolicy);
    if (null == policyExt) {
        return null;
    }

    Policy policy = new Policy();

    policy.setType(Xml2YamlTypeMapper.mappingPolicyType(
        Xml2YamlSwitchUtils.getNamefromQName(policyExt.getType())));
    policy.setDescription(policyExt.getDesc());
    policy.setProperties(policyExt.getProperties());

    List<String> target = policyExt.getTarget();
    if (null != target && !target.isEmpty()) {
      policy.setTargets(target.toArray(new String[target.size()]));
    }

    return policy;
  }

}
