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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.winery.model.tosca.TGroupType;
import org.eclipse.winery.model.tosca.TGroupType.Interfaces;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.GroupType;

/**
 * This class supports processing of node types from a YAML service template.
 */
public class GroupTypesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

    public GroupTypesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
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
                if (tNode instanceof TGroupType) {
                    Entry<String, GroupType> yGroupType = createGroupType((TGroupType) tNode);
                    getServiceTemplate().getGroup_types().put(yGroupType.getKey(),
                            yGroupType.getValue());
                }

            }
        }
    }

    /**
     * @param tType
     * @return
     */
    private Entry<String, GroupType> createGroupType(TGroupType tType) {
        GroupType yType = new GroupType();
        
        String name = Xml2YamlTypeMapper.mappingGroupType(tType.getName());
        // derived_from
        if (tType.getDerivedFrom() == null) {
            yType.setDerived_from(Xml2YamlTypeMapper.mappingGroupTypeDerivedFrom(null, name));
        } else {
            yType.setDerived_from(Xml2YamlTypeMapper.mappingGroupTypeDerivedFrom(
                Xml2YamlSwitchUtils.getNamefromQName(tType.getDerivedFrom().getTypeRef()), name));
        }
        // description
        yType.setDescription(Xml2YamlSwitchUtils.convert2Description(tType.getDocumentation()));
        // properties
        yType.setProperties(Xml2YamlSwitchUtils.convert2PropertyDefinitions(tType.getAny()));
        // targets
        if (tType.getTargets() != null && tType.getTargets().getTarget() != null) {
            List<String> list = tType.getTargets().getTarget();
            yType.setMembers(list.toArray(new String[0]));
        }

        // interfaces
        Interfaces tInterfaces = tType.getInterfaces();
        if (tInterfaces != null) {
            Map<String, Map<String, Map<String, String>>> yamlInterfaces =
                    parseInterfaces(tInterfaces);
            yType.setInterfaces(yamlInterfaces);
        }

        return Xml2YamlSwitchUtils.buildEntry(name, yType);
    }


    /**
     * @param tInterfaces
     * @return
     */
    private Map<String, Map<String, Map<String, String>>> parseInterfaces(
            Interfaces tInterfaces) {
        // TODO Auto-generated method stub
        return null;
    }

}
