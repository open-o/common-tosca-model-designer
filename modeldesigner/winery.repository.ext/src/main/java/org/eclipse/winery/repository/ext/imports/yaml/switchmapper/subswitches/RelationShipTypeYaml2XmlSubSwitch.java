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

import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.RelationshipType;

public class RelationShipTypeYaml2XmlSubSwitch extends
		AbstractYaml2XmlSubSwitch {

	public RelationShipTypeYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
		super(parentSwitch);
	}

	@Override
    public void process() {
        if (getServiceTemplate().getRelationship_types() == null
                || getServiceTemplate().getRelationship_types().isEmpty()) {
            return;
        }

        for (Map.Entry<String, RelationshipType> entry : getServiceTemplate()
                .getRelationship_types().entrySet()) {
            String name = entry.getKey();
            RelationshipType yRelationshipType = entry.getValue();

            if (name == null || name.isEmpty() || yRelationshipType == null) {
                continue;
            }

            TRelationshipType tRelationshipType = constructRelationship(yRelationshipType, name);
            getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(
                    tRelationshipType);
        }

    }

    private TRelationshipType constructRelationship(RelationshipType yRelationshipType,
            String TypeName) {
        TRelationshipType tRelationshipType = new TRelationshipType();

        // attribute-other,

        // attribute-name,target name space,abstract,final
        tRelationshipType.setName(TypeName);
        tRelationshipType.setTargetNamespace(getParent().getUsedNamespace());

        // attribute-

        // element-documentation,any(properties)

        // element-tags,derived from,properties definition

        // element-instanceStates,SourceInterfaces,TargetInterfaces,ValidSource,ValidTarget

        return tRelationshipType;
    }

}
