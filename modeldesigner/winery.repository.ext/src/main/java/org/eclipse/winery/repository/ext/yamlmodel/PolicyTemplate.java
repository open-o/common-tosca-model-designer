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
package org.eclipse.winery.repository.ext.yamlmodel;

import java.util.HashMap;
import java.util.Map;

public class PolicyTemplate extends YAMLElement {

    private String derived_from = "";
    private String type = "";
    private String description = "";
    private Map<String, Object> properties = new HashMap<String, Object>();
    private String[] targets = new String[0];
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDerived_from() {
        return derived_from;
    }
    public void setDerived_from(String derived_from) {
        this.derived_from = derived_from;
    }
    public Map<String, Object> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    public String[] getTargets() {
        return targets;
    }
    public void setTargets(String[] targets) {
        this.targets = targets;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final PolicyTemplate policyType = (PolicyTemplate) o;

        if (!this.derived_from.equals(policyType.derived_from)) {
            return false;
        }
        if (!this.type.equals(policyType.type)) {
            return false;
        }
        if (!this.description.equals(policyType.description)) {
            return false;
        }
        if (!this.properties.equals(policyType.properties)) {
            return false;
        }
        if (!this.targets.equals(policyType.targets)) {
            return false;
        }

        return true;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.derived_from.hashCode();
        result = 31 * result + this.type.hashCode();
        result = 31 * result + this.description.hashCode();
        result = 31 * result + this.properties.hashCode();
        result = 31 * result + this.targets.hashCode();
        return result;
    }
}
