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

import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;
import org.eclipse.winery.repository.ext.yamlmodel.TopologyTemplate;

/**
 * This class provides some general attributes and methods for its subclasses. The methods support
 * the processing of a YAML service template.
 */
public abstract class AbstractXml2YamlSubSwitch implements IXml2YamlSubSwitch {
    private final Xml2YamlSwitch parent;

    /**
     * Initialise the {@link AbstractXml2YamlSubSwitch} with its parent switch.
     * 
     * @param parentSwitch The parent switch
     */
    public AbstractXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
        this.parent = parentSwitch;
    }

    /**
     * Get result from parent switch.
     * 
     * @return
     */
    protected Definitions getDefinitions() {
        return this.parent.getDefinitions();
    }

    /**
     * Get initial ServiceTemplate from parent switch.
     * 
     * @return
     */
    protected ServiceTemplate getServiceTemplate() {
        return this.parent.getServiceTemplate();
    }

    /**
     * @return
     */
    protected TopologyTemplate getTopology_template() {
        if (getServiceTemplate().getTopology_template() == null) {
            getServiceTemplate().setTopology_template(new TopologyTemplate());
        }

        return getServiceTemplate().getTopology_template();
    }

    /**
     * 
     * @return
     */
    protected BoundaryPropertyDefinition getBoundaryPropertyDefinition() {
        TBoundaryDefinitions boundaryDefinitions = getTBoundaryDefinitions();
        if (boundaryDefinitions != null) {
            TBoundaryDefinitions.Properties properties = boundaryDefinitions
                    .getProperties();
            if (properties != null && properties.getAny() != null) {
                return BoundaryPropertyUtil
                        .getBoundaryPropertyDefinition(properties.getAny());
            }
        }

        return null;
    }

    protected TBoundaryDefinitions getTBoundaryDefinitions() {
        TServiceTemplate tServiceTemplate = getTServiceTemplate();
        if (tServiceTemplate != null) {
            return tServiceTemplate.getBoundaryDefinitions();
        }

        return null;
    }

    protected TServiceTemplate getTServiceTemplate() {
        List<?> tNodeList = getDefinitions()
                .getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        if (tNodeList != null && !tNodeList.isEmpty()) {
            for (Object tNode : tNodeList) {
                if (tNode instanceof TServiceTemplate) {
                    return (TServiceTemplate) tNode;
                }
            }
        }

        return null;
    }

    protected TTopologyTemplate getTTopologyTemplate() {
        TServiceTemplate tServiceTemplate = getTServiceTemplate();
        if (tServiceTemplate == null) {
            return null;
        }

        return tServiceTemplate.getTopologyTemplate();
    }

}
