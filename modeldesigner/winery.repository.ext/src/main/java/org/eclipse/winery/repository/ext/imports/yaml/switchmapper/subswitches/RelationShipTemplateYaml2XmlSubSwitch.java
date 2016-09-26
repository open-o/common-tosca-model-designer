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

import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;

public class RelationShipTemplateYaml2XmlSubSwitch extends
		AbstractYaml2XmlSubSwitch {

	private TTopologyTemplate tTopologyTemplateInstance; 
    public RelationShipTemplateYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch,TTopologyTemplate tTopologyTemplateInstance) {
        super(parentSwitch);
        this.tTopologyTemplateInstance = tTopologyTemplateInstance;
    }
    
	@Override
    public void process() {
        if (getServiceTemplate().getTopology_template().getRelationship_templates() == null
                || getServiceTemplate().getTopology_template().getRelationship_templates()
                        .isEmpty()) {
            return;
        }

        // yaml not handle
        TRelationshipTemplate rsTemplate = new TRelationshipTemplate();


        tTopologyTemplateInstance.getNodeTemplateOrRelationshipTemplate().add(rsTemplate);
    }

}
