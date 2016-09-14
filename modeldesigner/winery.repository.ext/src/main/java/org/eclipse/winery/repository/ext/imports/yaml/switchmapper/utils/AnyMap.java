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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

/**
 * XMLElement for serialising any Properties-element.
 *
 */
@XmlRootElement(name = "Properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnyMap extends HashMap<String, String> {

	private static final long serialVersionUID = 1L;
	@XmlAnyElement
	public List<JAXBElement<String>> entries = new ArrayList<JAXBElement<String>>();
	@XmlAnyAttribute
	private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

	public AnyMap() { // JAXB required
	}

	public AnyMap(Map<String, String> map) {
		for (final Map.Entry<String, String> entry : map.entrySet()) {
			this.entries.add(new JAXBElement<String>(new QName(entry.getKey()), String.class, entry.getValue()));
		}
	}

	public Map<QName, String> getOtherAttributes() {
		return this.otherAttributes;
	}

}
