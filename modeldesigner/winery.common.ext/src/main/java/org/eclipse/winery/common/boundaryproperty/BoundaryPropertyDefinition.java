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
package org.eclipse.winery.common.boundaryproperty;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;

@XmlRootElement(name = "BoundaryPropertyDefinition")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"inputs", "metadatas", "flavors", "outputs"})
public class BoundaryPropertyDefinition {

    @XmlElement(name = "Inputs")
    private Inputs inputs;

    @XmlElement(name = "Outputs")
    private Outputs outputs;

    @XmlElement(name = "MetaDatas")
    private MetaDatas metadatas;

    @XmlElement(name = "Flavors")
    private Flavors flavors;

    public Outputs getOutputs() {
        return outputs;
    }

    public void setOutputs(Outputs outputs) {
        this.outputs = outputs;
    }

    public Flavors getFlavors() {
        return flavors;
    }

    public void setFlavors(Flavors flavors) {
        this.flavors = flavors;
    }

    public Inputs getInputs() {
        return inputs;
    }

    public void setInputs(Inputs inputs) {
        this.inputs = inputs;
    }

    public MetaDatas getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(MetaDatas metadatas) {
        this.metadatas = metadatas;
    }

    public void addInput(Input input) {
        if (this.inputs == null) {
            this.inputs = new Inputs();
        }
        if (null == this.inputs.getInputs()) {
            this.inputs.setInputs(new ArrayList<Input>());
        }
        for (Input ele : inputs.getInputs()) {
            if (ele.getName().equals(input.getName())) {
                ele.setValue(input.getValue());
                ele.setDesc(input.getDesc());
                ele.setType(input.getType());
                return;
            }
        }
        this.inputs.getInputs().add(input);
    }

    public void delInput(String inputName) {
        if ((inputName == null) || (this.inputs == null) || (null == this.inputs.getInputs())) {
            return;
        }
        for (Iterator<Input> it = this.inputs.getInputs().iterator(); it.hasNext();) {
            Input input = (Input) it.next();
            if (inputName.equals(input.getName())) {
                it.remove();
            }
        }
        if (this.inputs.getInputs().isEmpty()) {
            this.inputs = null;
        }
    }

    public void addMetaData(MetaData data) {
        if (data == null || data.getKey() == null) {
            return;
        }
        if (this.metadatas == null) {
            this.metadatas = new MetaDatas();
        }

        if (null == this.metadatas.getMetadatas()) {
            this.metadatas.setMetadatas(new ArrayList<MetaData>());
        }

        for (MetaData ele : metadatas.getMetadatas()) {
            if (ele.getKey().equals(data.getKey())) {
                ele.setValue(data.getValue());
                return;
            }
        }
        this.metadatas.getMetadatas().add(data);
    }

    public void delMetaData(String key) {
        if ((key == null) || (this.metadatas == null) || (this.metadatas.getMetadatas() == null)) {
            return;
        }
        for (Iterator<MetaData> it = this.metadatas.getMetadatas().iterator(); it.hasNext();) {
            MetaData ele = (MetaData) it.next();
            if (key.equals(ele.getKey())) {
                it.remove();
            }
        }
        if (this.metadatas.getMetadatas().isEmpty()) {
            this.metadatas = null;
        }
    }

    public BoundaryPropertyDefinition rmEmpty() {
        return (this.inputs == null) && (this.metadatas == null) && (this.outputs == null)
                && (this.flavors == null) ? null : this;
    }

    public Input getInput(String inputName) {
        if ((inputName == null) || (this.inputs == null) || (null == this.inputs.getInputs())) {
            return null;
        }
        for (Input input : this.inputs.getInputs()) {
            if (inputName.equals(input.getName())) {
                return input;
            }
        }

        return null;
    }

    public String getMetaData(String key) {
        if ((key == null) || (this.metadatas == null || null == this.metadatas.getMetadatas())) {
            return null;
        }
        for (MetaData metadata : this.metadatas.getMetadatas()) {
            if (key.equals(metadata.getKey())) {
                return metadata.getValue();
            }
        }
        return null;
    }

    public void addOutput(PropertyDefinitionKV output) {
        if (this.outputs == null) {
            this.outputs = new Outputs();
        }
        if (null == this.outputs.getOutputs()) {
            this.outputs.setOutputs(new ArrayList<PropertyDefinitionKV>());
        }
        for (PropertyDefinitionKV ele : outputs.getOutputs()) {
            if (ele.getKey().equals(output.getKey())) {
                ele.setValue(output.getValue());
                ele.setDesc(output.getDesc());
                ele.setType(output.getType());
                return;
            }
        }
        this.outputs.getOutputs().add(output);
    }

    public void delOutput(String outputKey) {
        if ((outputKey == null) || (this.outputs == null) || (null == this.outputs.getOutputs())) {
            return;
        }
        for (Iterator<PropertyDefinitionKV> it = this.outputs.getOutputs().iterator(); it.hasNext();) {
            PropertyDefinitionKV output = it.next();
            if (outputKey.equals(output.getKey())) {
                it.remove();
            }
        }
        if (this.outputs.getOutputs().isEmpty()) {
            this.inputs = null;
        }
    }

    public PropertyDefinitionKV getOutput(String outputKey) {
        if ((outputKey == null) || (this.outputs == null) || (null == this.outputs.getOutputs())) {
            return null;
        }
        for (PropertyDefinitionKV output : this.outputs.getOutputs()) {
            if (outputKey.equals(output.getKey())) {
                return output;
            }
        }

        return null;
    }
}
