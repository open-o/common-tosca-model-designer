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

import java.util.HashMap;
import java.util.Map;

public class GroupType extends YAMLElement {

	private String derived_from = "";
	private Map<String, PropertyDefinition> properties = new HashMap<String, PropertyDefinition>();
    private String[] members;
	private Map<String, Map<String, Map<String, String>>> interfaces = new HashMap<String, Map<String, Map<String, String>>>();

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

    public String[] getMembers() {
      return members;
    }

    public void setMembers(String[] members) {
      this.members = members;
    }

    public Map<String, Map<String, Map<String, String>>> getInterfaces() {
		return this.interfaces;
	}

	public void setInterfaces(Map<String, Map<String, Map<String, String>>> interfaces) {
		if (interfaces != null) {
			this.interfaces = interfaces;
		}
	}

}