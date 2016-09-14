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


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.winery.model.tosca.TGroupTemplate;
import org.eclipse.winery.model.tosca.TGroupTemplates;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.Group;

/**
 * 
 * @author 10090474
 * 
 */
public class GroupsXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

	public GroupsXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
		super(parentSwitch);
	}

    /**
     * 
     */
	@Override
	public void process() {
        TServiceTemplate tServiceTemplate = getTServiceTemplate();
        if (tServiceTemplate == null) {
              return;
        }
        
        TGroupTemplates tGroups = tServiceTemplate.getGroupTemplates();
        if (tGroups == null || tGroups.getGroupTemplates() == null
                || tGroups.getGroupTemplates().isEmpty()) {
            return;
        }

        List<TGroupTemplate> tGroupList = tGroups.getGroupTemplates();
        for (TGroupTemplate tGroup : tGroupList) {
            Entry<String, Group> yGroup = convert2Group(tGroup);
            getTopology_template().getGroups().put(yGroup.getKey(), yGroup.getValue());
        }
	}

    /**
     * @param tGroup
     * @return
     */
    private Entry<String, Group> convert2Group(TGroupTemplate tGroup) {
        Group yGroup = new Group();

        // description
        yGroup.setDescription(Xml2YamlSwitchUtils.convert2Description(tGroup.getDocumentation()));
        // type
        yGroup.setType(Xml2YamlTypeMapper.mappingGroupType(Xml2YamlSwitchUtils.getNamefromQName(tGroup.getType())));
        // properties
        Map<String, Object> yProperties =
                Xml2YamlSwitchUtils.convertTProperties(tGroup.getProperties());
        yGroup.setProperties(yProperties);
        // targets
        if (tGroup.getTargets() != null && tGroup.getTargets().getTarget() != null) {
            yGroup.setMembers(tGroup.getTargets().getTarget().toArray(new String[0]));
        }

        return Xml2YamlSwitchUtils.buildEntry(tGroup.getName(), yGroup);
    }

}
