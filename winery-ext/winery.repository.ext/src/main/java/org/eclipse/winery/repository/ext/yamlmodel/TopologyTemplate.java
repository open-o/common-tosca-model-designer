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
package org.eclipse.winery.repository.ext.yamlmodel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 10090474
 *
 */
public class TopologyTemplate extends YAMLElement {
    private Map<String, Input> inputs = new HashMap<String, Input>();
    private Map<String, NodeTemplate> node_templates = new HashMap<String, NodeTemplate>();
    private Map<String, Object> relationship_templates = new HashMap<>();
    private Map<String, Group> groups = new HashMap<String, Group>();
    private Map<String, Object> policies = new HashMap<>();
    private Map<String, Output> outputs = new HashMap<String, Output>();
    private Map<String, Object> substitution_mappings = new HashMap<>();


    public void setInputs(Map<String, Input> inputs) {
        if (inputs != null) {
            this.inputs = inputs;
        }
    }

    public Map<String, Input> getInputs() {
        return this.inputs;
    }

    public void setNode_templates(Map<String, NodeTemplate> node_templates) {
        if (node_templates != null) {
            this.node_templates = node_templates;
        }
    }

    public Map<String, NodeTemplate> getNode_templates() {
        return this.node_templates;
    }

    public Map<String, Object> getRelationship_templates() {
        return relationship_templates;
    }

    public void setRelationship_templates(
            Map<String, Object> relationship_templates) {
        this.relationship_templates = relationship_templates;
    }

    public void setGroups(Map<String, Group> groups) {
        if (groups != null) {
            this.groups = groups;
        }
    }

    public Map<String, Group> getGroups() {
        return this.groups;
    }

    public Map<String, Object> getPolicies() {
        return policies;
    }

    public void setPolicies(Map<String, Object> policies) {
        this.policies = policies;
    }

    public void setOutputs(Map<String, Output> outputs) {
        if (outputs != null) {
            this.outputs = outputs;
        }
    }

    public Map<String, Output> getOutputs() {
        return this.outputs;
    }

    public Map<String, Object> getSubstitution_mappings() {
        return substitution_mappings;
    }

    public void setSubstitution_mappings(
            Map<String, Object> substitution_mappings) {
        this.substitution_mappings = substitution_mappings;
    }

}
