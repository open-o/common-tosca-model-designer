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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeTemplate extends YAMLElement {

	private String type = "";
	private Map<String, Object> properties = new HashMap<String, Object>();
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private List<Map<String, Object>> requirements = new ArrayList<Map<String, Object>>();
    private Map<String, Capability> capabilities = new HashMap<>();
	private Map<String, String> interfaces = new HashMap<String, String>();
	private List<Map<String, Object>> artifacts = new ArrayList<Map<String, Object>>();
	private NodeTemplatePosition position;
	
	public NodeTemplatePosition getPosition() {
        return position;
    }

    public void setPosition(NodeTemplatePosition position) {
        this.position = position;
    }

    public void setType(String type) {
		if (type != null) {
			this.type = type;
		}
	}

	public String getType() {
		return this.type;
	}

	public void setProperties(Map<String, Object> properties) {
		if (properties != null) {
			this.properties = properties;
		}
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setRequirements(List<Map<String, Object>> requirements) {
		if (requirements != null) {
			this.requirements = requirements;
		}
	}

	public List<Map<String, Object>> getRequirements() {
		return this.requirements;
	}

    public void setCapabilities(Map<String, Capability> capabilities) {
		if (capabilities != null) {
			this.capabilities = capabilities;
		}
	}

    public Map<String, Capability> getCapabilities() {
		return this.capabilities;
	}

	public Map<String, String> getInterfaces() {
		return this.interfaces;
	}

	public void setInterfaces(Map<String, String> interfaces) {
		if (interfaces != null) {
			this.interfaces = interfaces;
		}
	}

	public List<Map<String, Object>> getArtifacts() {
		return this.artifacts;
	}

	public void setArtifacts(List<Map<String, Object>> artifacts) {
		if (artifacts != null) {
			this.artifacts = artifacts;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		NodeTemplate that = (NodeTemplate) o;

		if (!artifacts.equals(that.artifacts)) return false;
		if (!capabilities.equals(that.capabilities)) return false;
		if (!interfaces.equals(that.interfaces)) return false;
		if (!properties.equals(that.properties)) return false;
        if (!attributes.equals(that.attributes))
            return false;
		if (!requirements.equals(that.requirements)) return false;
		if (!type.equals(that.type)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + type.hashCode();
		result = 31 * result + properties.hashCode();
        result = 31 * result + attributes.hashCode();
		result = 31 * result + requirements.hashCode();
		result = 31 * result + capabilities.hashCode();
		result = 31 * result + interfaces.hashCode();
		result = 31 * result + artifacts.hashCode();
		return result;
	}
}