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

import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.MetaData;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.Input;

public class BoundaryDefinitionsYaml2XmlSubSwitch extends
		AbstractYaml2XmlSubSwitch {

	private final TServiceTemplate tServiceTemplate;
	
    public BoundaryDefinitionsYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch,TServiceTemplate tServiceTemplate) {
    	super(parentSwitch);
    	this.tServiceTemplate = tServiceTemplate; 
    }
    
	@Override
	public void process() {
		// TODO Auto-generated method stub
		TBoundaryDefinitions tBoundaryDefinitions = new TBoundaryDefinitions();
		
		//elements-properties
		tBoundaryDefinitions.setProperties(constructProperties());
		
		//elements-propertyConstraints
		
		//elements-Requirements
		
		//elements-Capabilities
		
		//elements-Policies
		
		//elements-Interfaces		
		
		tServiceTemplate.setBoundaryDefinitions(tBoundaryDefinitions);
	}
	
	private TBoundaryDefinitions.Properties constructProperties()
	{
		TBoundaryDefinitions.Properties tProperties = new TBoundaryDefinitions.Properties();
		//any element
		tProperties.setAny(constructBoundaryPropertyDefinition());
		
		return tProperties;
	}
	
	private BoundaryPropertyDefinition constructBoundaryPropertyDefinition()
	{
		BoundaryPropertyDefinition boundaryPropertyDefinition=new BoundaryPropertyDefinition();

		//element-Inputs
		Map<String, Input> yinputs=getServiceTemplate().getTopology_template().getInputs();
		if(yinputs!=null && yinputs.size() >0)
		{
			for(Map.Entry<String, Input> entry : yinputs.entrySet())
			{
				org.eclipse.winery.common.boundaryproperty.Input tInput = new org.eclipse.winery.common.boundaryproperty.Input();
				tInput.setName(entry.getKey());
				tInput.setType(entry.getValue().getType());
				tInput.setValue(entry.getValue().getDefault());
				tInput.setDesc(entry.getValue().getDescription());
				boundaryPropertyDefinition.addInput(tInput);
			}
		}
		
        // element-MetaDatas
        Map<String, Object> yMetadatas = getServiceTemplate().getMetadata();
        if (yMetadatas != null && yMetadatas.size() > 0) {
            for (Map.Entry<String, Object> yMetaData : yMetadatas.entrySet()) {
                MetaData tMetaData = new MetaData();
                tMetaData.setKey(yMetaData.getKey());
                tMetaData.setValue(yMetaData.getValue().toString());
                boundaryPropertyDefinition.addMetaData(tMetaData);
            }

        }
		
		return boundaryPropertyDefinition;
	}
}
