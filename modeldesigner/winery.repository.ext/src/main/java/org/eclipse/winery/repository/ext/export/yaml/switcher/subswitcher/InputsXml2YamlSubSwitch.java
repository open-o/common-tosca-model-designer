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
import java.util.Map.Entry;

import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.Input;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;

/**
 * This class supports processing of node types from a YAML service template.
 */
public class InputsXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

    public InputsXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
        super(parentSwitch);
    }

    /**
     * Processes every in-memory node type and creates a corresponding YAML node type.
     */
    @Override
    public void process() {
        List<Input> tInputList = getTInputList();

        if (tInputList != null && !tInputList.isEmpty()) {
            for (Input tInput : tInputList) {
                Entry<String, org.eclipse.winery.repository.ext.yamlmodel.Input> yInput = createInput(tInput);
                getTopology_template().getInputs().put(yInput.getKey(),
                        yInput.getValue());
            }

        }
    

    }

    public List<Input> getTInputList() {
        BoundaryPropertyDefinition tbpd = getBoundaryPropertyDefinition();
        if (tbpd != null && tbpd.getInputs() != null) {
            return tbpd.getInputs().getInputs();
        }

        return null;
    }


    /**
     * @param tInput
     * @return
     */
    private Entry<String, org.eclipse.winery.repository.ext.yamlmodel.Input> createInput(
            Input tInput) {
        org.eclipse.winery.repository.ext.yamlmodel.Input yInput = new org.eclipse.winery.repository.ext.yamlmodel.Input();

        yInput.setType(tInput.getType());
        yInput.setDescription(tInput.getDesc());
        yInput.setDefault(tInput.getValue());

        return Xml2YamlSwitchUtils.buildEntry(Xml2YamlTypeMapper.mappingNodeType(tInput.getName()),
                yInput);
    }

}
