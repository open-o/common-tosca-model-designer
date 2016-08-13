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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.NodeType;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;

public class NodeTypeYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

    public NodeTypeYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
        super(parentSwitch);
    }
    
	@Override
    public void process() {
        if (getServiceTemplate().getNode_types() == null
                || getServiceTemplate().getNode_types().isEmpty()) {
            return;
        }

        for (Map.Entry<String, NodeType> entry : getServiceTemplate().getNode_types().entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getKey().isEmpty()
                    || entry.getValue() == null) {
                break;
            }

            String nodeTypeName = entry.getKey();
            NodeType yNodeType = entry.getValue();

            TNodeType tNodeType = constructNodeType(yNodeType, nodeTypeName);

            getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(tNodeType);
        }
    }

    private TNodeType constructNodeType(NodeType yNodeType, String nodeTypeName) {
        TNodeType tNodeType = new TNodeType();
        // attribute-name,targetNameSpace,anyAttributes
        tNodeType.setName(nodeTypeName);
        tNodeType.setTargetNamespace(getParent().getUsedNamespace());
        // attribute-abstract,final-none

        // element-documentation-none

        // element-DerivedFrom
        TEntityType.DerivedFrom tDerivedFrom = new TEntityType.DerivedFrom();
        tDerivedFrom.setTypeRef(new QName(getParent().getUsedNamespace(), yNodeType
                .getDerived_from(), CommonConst.TARGET_PREFIX));
        tNodeType.setDerivedFrom(tDerivedFrom);

        // element-PropertiesDefinition
        TEntityType.PropertiesDefinition tPropertiesDefinition =
                new TEntityType.PropertiesDefinition();
        tPropertiesDefinition.setType(new QName(getParent().getUsedNamespace()+ "/" + CommonConst.PROPERTIES_DEFINITION,
            CommonConst.PROPERTIES_LOCALPART, CommonConst.PROPERTIES_DEFINITION_PREFIX));
        tNodeType.setPropertiesDefinition(tPropertiesDefinition);
        // element-PropertiesDefinition-any
        WinerysPropertiesDefinition tWinerysPropertiesDefinition =
                new WinerysPropertiesDefinition();
        tWinerysPropertiesDefinition.setElementName(CommonConst.PROPERTIES_LOCALPART);
        tWinerysPropertiesDefinition.setNamespace(getParent().getUsedNamespace());
        PropertyDefinitionKVList tKVList = new PropertyDefinitionKVList();
        for (Map.Entry<String, PropertyDefinition> entry : yNodeType.getProperties().entrySet()) {
            PropertyDefinitionKV tKV = new PropertyDefinitionKV();
            tKV.setKey(entry.getKey());
            tKV.setType("xsd:" + entry.getValue().getType());
            tKVList.add(tKV);
        }
        tWinerysPropertiesDefinition.setPropertyDefinitionKVList(tKVList);
        tNodeType.getAny().add(tWinerysPropertiesDefinition);

        // element-RequirementDefinitions-none
        tNodeType.setRequirementDefinitions(null);

        // element-CapabilityDefinitions-none

        // element-InstanceStates-none

        // element-Interfaces-none

        return tNodeType;
    }

}
