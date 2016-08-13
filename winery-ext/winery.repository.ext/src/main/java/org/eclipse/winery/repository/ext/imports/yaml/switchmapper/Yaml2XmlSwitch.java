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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches.ImportYaml2XmlSubSwitch;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches.NodeTypeYaml2XmlSubSwitch;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches.RelationShipTypeYaml2XmlSubSwitch;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches.ServiceTemplateYaml2XmlSubSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;

/**
 * This class can parse Definitions (XML bean) to ServiceTemplates (YAML bean).
 * 
 */
public class Yaml2XmlSwitch {

	//
    private ServiceTemplate serviceTemplate = new ServiceTemplate();

    private Definitions definitions;
    
    private String usedNamespace = CommonConst.TARGET_NS;
    

	
    
    public Yaml2XmlSwitch(ServiceTemplate st) {
        this.serviceTemplate = st;
    }

    public ServiceTemplate getServiceTemplate() {
        return this.serviceTemplate;
    }

	public Definitions getDefinitions() {
        return this.definitions;
    }
	
    public String getUsedNamespace() {
		return usedNamespace;
	}

	public void setUsedNamespace(String usedNamespace) {
		this.usedNamespace = usedNamespace;
	}

    public Definitions convert() {
    	
		if (this.serviceTemplate.getTosca_default_namespace() != null && !this.serviceTemplate.getTosca_default_namespace().isEmpty()) {
			this.usedNamespace = this.serviceTemplate.getTosca_default_namespace();
		}
		
        this.definitions = new Definitions();
        //attribute-id,name,target namespace
//		this.definitions.setId(Global.unique(Yaml2XmlConst.DEFINITIONS_ID));
//		this.definitions.setName(Global.unique(Yaml2XmlConst.DEFINITIONS_NAME));
		this.definitions.setTargetNamespace(this.usedNamespace);
		this.definitions.getOtherAttributes().put(new QName("xmlns:" + CommonConst.TOSCA_PREFIX), CommonConst.TOSCA_NS);
		this.definitions.getOtherAttributes().put(new QName("xmlns:" + CommonConst.WINERY_PREFIX), CommonConst.WINERY_NS);
		this.definitions.getOtherAttributes().put(new QName("xmlns:" + CommonConst.WINERY_MODEL_PREFIX), CommonConst.WINERY_MODEL_NS);
        
		//element-documentation
		this.definitions.getDocumentation().add(toDocumentation(this.serviceTemplate.getDescription()));
		if (this.serviceTemplate.getTemplate_author() != null && !this.serviceTemplate.getTemplate_author().isEmpty()) {
			this.definitions.getDocumentation().add(toDocumentation("Template Author: " + this.serviceTemplate.getTemplate_author()));
		}
		if (this.serviceTemplate.getTemplate_version() != null && !this.serviceTemplate.getTemplate_version().isEmpty()) {
			this.definitions.getDocumentation().add(toDocumentation("Template Version: " + this.serviceTemplate.getTemplate_version()));
		}
		
        IYaml2XmlSubSwitch[] subSwitches = { new ImportYaml2XmlSubSwitch(this),
                new ServiceTemplateYaml2XmlSubSwitch(this),
                new NodeTypeYaml2XmlSubSwitch(this),
                new RelationShipTypeYaml2XmlSubSwitch(this)
        };
        
        for (IYaml2XmlSubSwitch subSwitch : subSwitches) {
            subSwitch.process();
        }

        return this.definitions;
    }
    
	/**
	 * Creates a TDocumentation with the given String and adds it to this element
	 *
	 * @param desc the description
	 * @return the TDocumentation reference
	 */
	protected TDocumentation toDocumentation(String desc) {
		final TDocumentation docu = new TDocumentation();
		docu.getContent().add(desc);
		return docu;
	}
}
