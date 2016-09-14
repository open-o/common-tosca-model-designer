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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.utils;

import javax.xml.namespace.QName;

import org.eclipse.winery.repository.ext.common.CommonConst;


/**
 * Util to maintain namespaces.
 * 
 * @author Sebi
 */
public class NamespaceUtil {

    private String targetNamespace;

	public NamespaceUtil(String targetNamespace) {
		if (targetNamespace == null || targetNamespace.isEmpty()) {
			throw new IllegalArgumentException("target namespace may not be null or empty.");
		}
		this.targetNamespace = targetNamespace;
	}

	/**
	 * Creates a QName element for the given local name with the pre-defined target namespace
	 *
	 * @param localName of the element
	 * @return the QName
	 */
	public QName toTnsQName(String localName) {
        return new QName(this.targetNamespace, localName,
            CommonConst.TARGET_NS_PREFIX);
	}

	/**
	 * Creates a QName element for the given local name with the pre-defined type namespace.
	 *
	 * @param localName local name of the element
	 * @return the QName
	 */
	public QName toTypeNSQName(String localName) {
        return new QName(CommonConst.TYPES_NS, localName, "types");
	}

	/**
	 * Creates a QName element for the given local name with target namespace for base types
	 * {@link org.opentosca.yamlconverter.switchmapper.Yaml2XmlSwitch#BASE_TYPES_NS}
	 *
	 * @param localName local name of the element to reference
	 * @return QName object
	 */
	public QName toBaseTypesNsQName(String localName) {
        return new QName(CommonConst.BASE_TYPES_NS, localName,
            CommonConst.BASE_TYPES_PREFIX);
	}

	/**
	 * Creates a QName element for the given local name with target namespace for specific types
	 * {@link org.opentosca.yamlconverter.switchmapper.Yaml2XmlSwitch#SPECIFIC_TYPES_NS}
	 *
	 * @param localName local name of the element to reference
	 * @return QName object
	 */
	public QName toSpecificTypesNsQName(String localName) {
        return new QName(CommonConst.SPECIFIC_TYPES_NS, localName,
            CommonConst.SPECIFIC_TYPES_PREFIX);
	}
}
