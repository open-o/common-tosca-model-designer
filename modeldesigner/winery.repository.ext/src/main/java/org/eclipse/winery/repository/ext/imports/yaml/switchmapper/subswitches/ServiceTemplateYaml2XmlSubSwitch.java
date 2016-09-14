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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.utils.Global;

public class ServiceTemplateYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

    public ServiceTemplateYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
        super(parentSwitch);
    }
    
    
	@Override
    public void process() {
        if (getServiceTemplate().getTopology_template() == null) {
            return;
        }

        TServiceTemplate tServiceTemplate = new TServiceTemplate();

        // attributes
        if (getServiceTemplate().getTemplate_name() != null
                && !getServiceTemplate().getTemplate_name().isEmpty()) {
            tServiceTemplate.setId(getServiceTemplate().getTemplate_name());
            tServiceTemplate.setName(getServiceTemplate().getTemplate_name());
        } else {
            tServiceTemplate.setId(Global.unique(CommonConst.SERVICE_TEMPLATE_ID));
            tServiceTemplate.setName(CommonConst.SERVICE_TEMPLATE_NAME);
        }

        tServiceTemplate.setTargetNamespace(getParent().getUsedNamespace());

        // elements-Tags
        // none

        // elements-BoundaryDefinitions
        new BoundaryDefinitionsYaml2XmlSubSwitch(getParent(), tServiceTemplate).process();

        // elements-topologyTemplate
        new TopologyTemplateYaml2XmlSubSwitch(getParent(), tServiceTemplate).process();

        // elements-Plans-none

        getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(
                tServiceTemplate);
    }

}
