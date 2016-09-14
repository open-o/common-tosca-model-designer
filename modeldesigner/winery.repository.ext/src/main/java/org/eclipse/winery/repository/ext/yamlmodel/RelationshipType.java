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
package org.eclipse.winery.repository.ext.yamlmodel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RelationshipType extends YAMLElement {

	private String derived_from = "";
	private Map<String, PropertyDefinition> properties = new HashMap<String, PropertyDefinition>();
	private Map<String, Map<String, Map<String, String>>> interfaces = new HashMap<String, Map<String, Map<String, String>>>();
	// private String[] valid_targets = new String[0];
    private String[] valid_target_types;

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

	public Map<String, Map<String, Map<String, String>>> getInterfaces() {
		return this.interfaces;
	}

	public void setInterfaces(Map<String, Map<String, Map<String, String>>> interfaces) {
		if (interfaces != null) {
			this.interfaces = interfaces;
		}
	}

	// public String[] getValid_targets() {
	// return valid_targets;
	// }
	//
	// public void setValid_targets(String[] valid_targets) {
	// if (valid_targets != null) {
	// this.valid_targets = valid_targets;
	// }
	// }

	public String[] getValid_target_types() {
		return this.valid_target_types;
	}

	public void setValid_target_types(String[] valid_target_types) {
		this.valid_target_types = valid_target_types;
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

		final RelationshipType that = (RelationshipType) o;

		if (!this.derived_from.equals(that.derived_from)) {
			return false;
		}
		if (!this.interfaces.equals(that.interfaces)) {
			return false;
		}
		if (!this.properties.equals(that.properties)) {
			return false;
		}
		// if (!Arrays.equals(valid_targets, that.valid_targets)) return false;
		if (!Arrays.equals(this.valid_target_types, that.valid_target_types)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.derived_from.hashCode();
		result = 31 * result + this.properties.hashCode();
		result = 31 * result + this.interfaces.hashCode();
		// result = 31 * result + Arrays.hashCode(valid_targets);
		result = 31 * result + Arrays.hashCode(this.valid_target_types);
		return result;
	}
}