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

import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.PolicyType;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;

public class PolicyTypeYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

    public PolicyTypeYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
        super(parentSwitch);
    }
    
	@Override
    public void process() {
        if (getServiceTemplate().getPolicy_types() == null
                || getServiceTemplate().getPolicy_types().isEmpty()) {
            return;
        }

        for (Map.Entry<String, PolicyType> entry : getServiceTemplate().getPolicy_types().entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getKey().isEmpty()
                    || entry.getValue() == null) {
                break;
            }

            String policyTypeName = entry.getKey();
            PolicyType yPolicyType = entry.getValue();

            TPolicyType tPolicyType = constructPolicyType(yPolicyType, policyTypeName);

            getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(tPolicyType);
        }
    }

    private TPolicyType constructPolicyType(PolicyType yPolicyType, String policyTypeName) {
        TPolicyType tPolicyType = new TPolicyType();
        // attribute-name,targetNameSpace,anyAttributes
        tPolicyType.setName(policyTypeName);
        tPolicyType.setTargetNamespace(getParent().getUsedNamespace());
        // attribute-abstract,final-none

        // element-documentation-none

        // element-DerivedFrom
        TEntityType.DerivedFrom tDerivedFrom = new TEntityType.DerivedFrom();
        tDerivedFrom.setTypeRef(new QName(getParent().getUsedNamespace(), yPolicyType
                .getDerived_from(), CommonConst.TARGET_PREFIX));
        tPolicyType.setDerivedFrom(tDerivedFrom);

        // element-PropertiesDefinition
        TEntityType.PropertiesDefinition tPropertiesDefinition =
                new TEntityType.PropertiesDefinition();
        tPropertiesDefinition.setType(new QName(getParent().getUsedNamespace()+ "/" + CommonConst.PROPERTIES_DEFINITION,
            CommonConst.PROPERTIES_LOCALPART, CommonConst.PROPERTIES_DEFINITION_PREFIX));
        tPolicyType.setPropertiesDefinition(tPropertiesDefinition);
        // element-PropertiesDefinition-any
        WinerysPropertiesDefinition tWinerysPropertiesDefinition =
                new WinerysPropertiesDefinition();
        tWinerysPropertiesDefinition.setElementName(CommonConst.PROPERTIES_LOCALPART);
        tWinerysPropertiesDefinition.setNamespace(getParent().getUsedNamespace());
        PropertyDefinitionKVList tKVList = new PropertyDefinitionKVList();
        for (Map.Entry<String, PropertyDefinition> entry : yPolicyType.getProperties().entrySet()) {
            PropertyDefinitionKV tKV = new PropertyDefinitionKV();
            tKV.setKey(entry.getKey());
            tKV.setType("xsd:" + entry.getValue().getType());
            tKVList.add(tKV);
        }
        tWinerysPropertiesDefinition.setPropertyDefinitionKVList(tKVList);
        tPolicyType.getAny().add(tWinerysPropertiesDefinition);

        return tPolicyType;
    }

}
