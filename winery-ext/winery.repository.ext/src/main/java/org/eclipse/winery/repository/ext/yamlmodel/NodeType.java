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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeType extends YAMLElement {

	private String derived_from = "";
	private Map<String, PropertyDefinition> properties = new HashMap<String, PropertyDefinition>();
	private Map<String, AttributeDefinition> attributes = new HashMap<String, AttributeDefinition>();
	private List<Map<String, Object>> requirements = new ArrayList<Map<String, Object>>();
	private Map<String, Object> capabilities = new HashMap<String, Object>();
	private Map<String, Map<String, Map<String, String>>> interfaces = new HashMap<String, Map<String, Map<String, String>>>();
	private List<Map<String, Object>> artifacts = new ArrayList<Map<String, Object>>();
    
	public void setArtifacts(List<Map<String, Object>> artifacts) {
		if (artifacts != null) {
			this.artifacts = artifacts;
		}
	}

	public List<Map<String, Object>> getArtifacts() {
		return this.artifacts;
	}

	public String getDerived_from() {
		return this.derived_from;
	}

	public void setDerived_from(String derived_from) {
		if (derived_from != null) {
			this.derived_from = derived_from;
		}
	}

	public Map<String, PropertyDefinition> getProperties() {
		return this.properties;
	}

	public void setProperties(Map<String, PropertyDefinition> properties) {
		if (properties != null) {
			this.properties = properties;
		}
	}

	public Map<String, AttributeDefinition> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, AttributeDefinition> attributes) {
        this.attributes = attributes;
    }

    public List<Map<String, Object>> getRequirements() {
		return this.requirements;
	}

	public void setRequirements(List<Map<String, Object>> requirements) {
		if (requirements != null) {
			this.requirements = requirements;
		}
	}

	public Map<String, Object> getCapabilities() {
		return this.capabilities;
	}

	public void setCapabilities(Map<String, Object> capabilities) {
		if (capabilities != null) {
			this.capabilities = capabilities;
		}
	}

	public Map<String, Map<String, Map<String, String>>> getInterfaces() {
		return this.interfaces;
	}

	public void setInterfaces(Map<String, Map<String, Map<String, String>>> interfaces) {
		if (interfaces != null) {
			this.interfaces = interfaces;
		}
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

		final NodeType nodeType = (NodeType) o;

		if (!this.artifacts.equals(nodeType.artifacts)) {
			return false;
		}
		if (!this.capabilities.equals(nodeType.capabilities)) {
			return false;
		}
		if (!this.derived_from.equals(nodeType.derived_from)) {
			return false;
		}
		if (!this.interfaces.equals(nodeType.interfaces)) {
			return false;
		}
		if (!this.properties.equals(nodeType.properties)) {
			return false;
		}
		if (!this.attributes.equals(nodeType.attributes)) {
            return false;
        }
		if (!this.requirements.equals(nodeType.requirements)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.derived_from.hashCode();
		result = 31 * result + this.properties.hashCode();
		result = 31 * result + this.attributes.hashCode();
		result = 31 * result + this.requirements.hashCode();
		result = 31 * result + this.capabilities.hashCode();
		result = 31 * result + this.interfaces.hashCode();
		result = 31 * result + this.artifacts.hashCode();
		return result;
	}
}