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
import java.util.Map.Entry;

import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.RelationshipType;

/**
 * This class supports processing of relationship types of a YAML service template.
 */
public class RelationshipTypesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

	public RelationshipTypesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
		super(parentSwitch);
	}

    /**
	 * 
	 */
	@Override
	public void process() {
        List<?> nodeList = getDefinitions()
                .getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        if (nodeList != null) {
            for (Object node : nodeList) {
                if (node instanceof TRelationshipType) {
                    Entry<String, RelationshipType> yRelationshipType = createRelationshipType((TRelationshipType) node);
                    getServiceTemplate().getRelationship_types().put(
                            yRelationshipType.getKey(),
                            yRelationshipType.getValue());
                }

            }
        }
	}

    /**
     * @param tRelationshipType
     * @return
     */
    private Entry<String, RelationshipType> createRelationshipType(
            TRelationshipType tRelationshipType) {
        RelationshipType yRelationshipType = new RelationshipType();

        String name = Xml2YamlTypeMapper.mappingRelationshipType(tRelationshipType.getName());
        // derived_from
        if (tRelationshipType.getDerivedFrom() == null) {
            yRelationshipType.setDerived_from(
                Xml2YamlTypeMapper.mappingRelationshipTypeDerivedFrom(null, name));
        } else {
            yRelationshipType.setDerived_from(
                Xml2YamlTypeMapper.mappingRelationshipTypeDerivedFrom(
                    Xml2YamlSwitchUtils.getNamefromQName(tRelationshipType.getDerivedFrom().getTypeRef()), name));
        }

        // description
        yRelationshipType.setDescription(Xml2YamlSwitchUtils
                .convert2Description(tRelationshipType.getDocumentation()));

        // properties
        yRelationshipType.setProperties(Xml2YamlSwitchUtils
                .convert2PropertyDefinitions(tRelationshipType.getAny()));

        // interfaces
        
        // Valid_target_types
        TRelationshipType.ValidTarget validTarget = tRelationshipType
                .getValidTarget();
        if (validTarget != null) {
            String validTargetType = Xml2YamlSwitchUtils
                    .getNamefromQName(validTarget.getTypeRef());
            yRelationshipType
                    .setValid_target_types(new String[] { validTargetType });

        }

        return Xml2YamlSwitchUtils.buildEntry(name, yRelationshipType);
    }
}
