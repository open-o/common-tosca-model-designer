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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.typemapper.ElementType;
import org.eclipse.winery.repository.ext.yamlmodel.RelationshipType;

/**
 * This class supports processing of relationship types of a YAML service template.
 */
public class RelationshipTypesYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

	public RelationshipTypesYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
		super(parentSwitch);
	}

    /**
     * Processes all YAML relationship types and creates a Tosca
     * {@link org.opentosca.model.tosca.TRelationshipType} and adds it to
     * {@link #getDefinitions()} object.
     */
    @Override
    public void process() {
        if (getServiceTemplate().getRelationship_types() != null) {
            for (final Entry<String, RelationshipType> yamlRelationshipType : getServiceTemplate()
                    .getRelationship_types().entrySet()) {
                final TRelationshipType relationshipType = createRelationshipType(yamlRelationshipType);
                relationshipType.setName(yamlRelationshipType.getKey());
                getDefinitions()
                        .getServiceTemplateOrNodeTypeOrNodeTypeImplementation()
                        .add(relationshipType);
            }
        }
    }

    private TRelationshipType createRelationshipType(
            Entry<String, RelationshipType> relType) {
        final TRelationshipType result = new TRelationshipType();
        final RelationshipType relationshipType = relType.getValue();
        result.setName(relType.getKey());

        if (relationshipType.getDerived_from() != null
                && !relationshipType.getDerived_from().isEmpty()) {
            final QName derivedFrom = getTypeMapperUtil()
                    .getCorrectTypeReferenceAsQName(
                            relationshipType.getDerived_from(),
                            ElementType.RELATIONSHIP_TYPE);
            result.setDerivedFrom(parseDerivedFrom(derivedFrom));
        }

        // set interfaces
        final TRelationshipType.TargetInterfaces targetInterfaces = new TRelationshipType.TargetInterfaces();
        for (final Entry<String, Map<String, Map<String, String>>> ifaceEntry : relationshipType
                .getInterfaces().entrySet()) {
            if (ifaceEntry.getValue() instanceof HashMap) {
                final TInterface tInterface = getInterfaceWithOperations(ifaceEntry);
                targetInterfaces.getInterface().add(tInterface);
            }
        }
        result.setTargetInterfaces(targetInterfaces);

        // set properties
        if (relationshipType.getProperties() != null
                && !relationshipType.getProperties().isEmpty()) {
            // result.setPropertiesDefinition(parsePropertiesDefinition(
            // relationshipType.getProperties(), relType.getKey()));
            List<WinerysPropertiesDefinition> any = Yaml2XmlSwitchUtils
                    .convert2PropertyDefinitions(relationshipType
                            .getProperties());
            result.getAny().addAll(any);
        }

        // set valid target (only one possible, thus choose first one)
        // if (relationshipType.getValid_targets().length > 0 &&
        // relationshipType.getValid_targets()[0] != null) {
        // final TRelationshipType.ValidTarget validTarget = new
        // TRelationshipType.ValidTarget();
        // validTarget.setTypeRef(getNamespaceUtil().toTnsQName(relationshipType.getValid_targets()[0]));
        // result.setValidTarget(validTarget);
        // }
        if (relationshipType.getValid_target_types().length > 0
                && relationshipType.getValid_target_types()[0] != null) {
            final TRelationshipType.ValidTarget validTarget = new TRelationshipType.ValidTarget();
            validTarget.setTypeRef(getNamespaceUtil().toTnsQName(
                    relationshipType.getValid_target_types()[0]));
            result.setValidTarget(validTarget);
        }
        result.getDocumentation().add(
                toDocumentation(relationshipType.getDescription()));
        return result;
    }

}
