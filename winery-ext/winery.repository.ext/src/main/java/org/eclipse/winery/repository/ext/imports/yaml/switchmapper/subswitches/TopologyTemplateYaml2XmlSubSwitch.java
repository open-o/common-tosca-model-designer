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

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;

public class TopologyTemplateYaml2XmlSubSwitch extends
		AbstractYaml2XmlSubSwitch {
	private final TServiceTemplate tServiceTemplateInstance;
	
    public TopologyTemplateYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch,TServiceTemplate tServiceTemplateInstance) {
        super(parentSwitch);
        this.tServiceTemplateInstance = tServiceTemplateInstance;
    }
    
	@Override
	public void process() {
		// TODO Auto-generated method stub
		TTopologyTemplate tTopologyTemplate = new TTopologyTemplate();
			
		//element-node template
		new NodeTemplateYaml2XmlSubSwitch(getParent(),tTopologyTemplate).process();
		
		//element-relationship template
		new RelationShipTemplateYaml2XmlSubSwitch(getParent(),tTopologyTemplate).process();
			
		tServiceTemplateInstance.setTopologyTemplate(tTopologyTemplate);
	}

}
