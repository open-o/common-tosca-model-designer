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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.typemapper;

/**
 * This exception can be thrown if no mapping is defined for a YAML base or specific type to a XML base or specific type.
 *
 * @author Sebi
 */
public class NoTypeMappingException extends Exception {

	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = 1142713228334922990L;

	public NoTypeMappingException() {
		super("No mapping defined for your desired request.");
	}

	public NoTypeMappingException(final String expectedYamlBaseType) {
		super("No mapping defined for '" + expectedYamlBaseType + "'");
	}
}
