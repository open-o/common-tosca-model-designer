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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.boundaryproperty.Input;
import org.eclipse.winery.common.boundaryproperty.Inputs;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;

/**
 * This class supports processing of node types from a YAML service template.
 */
public class InputsYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

    public InputsYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
        super(parentSwitch);
    }

    /**
     * Processes every in-memory node type and creates a corresponding YAML node type.
     */
    @Override
    public void process() {
        Map<String, org.eclipse.winery.repository.ext.yamlmodel.Input> yInputs = getServiceTemplate()
                .getTopology_template().getInputs();
        if (yInputs != null && yInputs.size() > 0) {
            List<Input> tInputList = new ArrayList<>();
            for (Map.Entry<String, org.eclipse.winery.repository.ext.yamlmodel.Input> yInput : yInputs
                    .entrySet()) {
                tInputList.add(buildInput(yInput));
            }

            Inputs tInputs = new Inputs();
            tInputs.setInputs(tInputList);

            BoundaryPropertyDefinition tbpd = getBoundaryPropertyDefinition();
            tbpd.setInputs(tInputs);

            TBoundaryDefinitions.Properties tProperties = new TBoundaryDefinitions.Properties();
            tProperties.setAny(BoundaryPropertyUtil.getPropertiesAny(tbpd));

            TBoundaryDefinitions tBoundaryDefinitions = new TBoundaryDefinitions();
            tBoundaryDefinitions.setProperties(tProperties);

            TServiceTemplate tServiceTemplate = getTServiceTemplate();
            if (tServiceTemplate != null) {
                tServiceTemplate.setBoundaryDefinitions(tBoundaryDefinitions);
            }
        }
    }

    private Input buildInput(
            Map.Entry<String, org.eclipse.winery.repository.ext.yamlmodel.Input> yInput) {
        Input tInput = new Input();
        tInput.setName(Yaml2XmlTypeMapper.mappingNodeType(yInput
                .getKey()));

        tInput.setType(yInput.getValue().getType());
        tInput.setDesc(yInput.getValue().getDescription());
        tInput.setValue(yInput.getValue().getDefault());
        return tInput;
    }
}
