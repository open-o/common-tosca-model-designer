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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;

import com.google.gson.Gson;

/**
 * Util for parsing properties.
 * 
 * @author Sebi
 */
public class PropertiesParserUtil {

	public PropertiesParserUtil(Yaml2XmlSwitch parentSwitch, ServiceTemplate serviceTemplate) {
		if (parentSwitch == null || serviceTemplate == null) {
			throw new IllegalArgumentException("Params may not be null.");
		}
	}

	/**
	 * Creates and sets the PropertiesDefinition for this element and adds the element to the xsd definitions.
	 *
	 * @param properties of this element
	 * @param typename of this element
	 * @return a reference to the PropertiesDefinition
	 */
	public TEntityType.PropertiesDefinition parsePropertiesDefinition(Map<String, PropertyDefinition> properties, String typename) {
		final TEntityType.PropertiesDefinition result = new TEntityType.PropertiesDefinition();
		// setType() works, setElement will throw an error while importing the XML to Winery
        result.setType(new QName(CommonConst.TYPES_NS, typename
                + "Properties", "types"));
		generateTypeXSD(properties, typename + "Properties");
		return result;
	}

	/**
	 * Adds the xsd information for the element to the xsd builder.
	 *
	 * @param properties of the element
	 * @param name of the element
	 */
	private void generateTypeXSD(Map<String, PropertyDefinition> properties, String name) {
        // final String tName = "t" + name;
        // this.parent.getXSDStringBuilder().append("<xs:complexType name=\"").append(tName).append("\">\n");
        // this.parent.getXSDStringBuilder().append("<xs:sequence>\n");
        //
        // for (final Map.Entry<String, PropertyDefinition> entry :
        // properties.entrySet()) {
        // this.parent.getXSDStringBuilder().append("<xs:element name=\"").append(entry.getKey()).append("\" type=\"xs:")
        // .append(entry.getValue().getType()).append("\" />\n");
        // }
        //
        // this.parent.getXSDStringBuilder().append("</xs:sequence>\n").append("</xs:complexType>\n");
        // this.parent.getXSDStringBuilder().append("<xs:element name=\"").append(name).append("\" type=\"").append(tName).append("\" />\n");
	}

	/**
	 * Converts the given map to a jaxb-parseable {@link org.opentosca.yamlconverter.main.utils.AnyMap}
	 *
	 * @param customMap given properties
	 * @param nodeName name of the element
	 * @return the {@link javax.xml.bind.JAXBElement} with the AnyMap
	 */
	public JAXBElement<AnyMap> getAnyMapForProperties(final Map<String, Object> customMap, final QName nodeName) {
        final AnyMap properties = new AnyMap(parseProperties(customMap));
        properties.getOtherAttributes().put(new QName("xmlns"),
                nodeName.getNamespaceURI());
        final QName elementQName = new QName(nodeName.getNamespaceURI(),
                nodeName.getLocalPart() + "Properties", nodeName.getPrefix());
        return new JAXBElement<AnyMap>(elementQName, AnyMap.class, properties);
	}

	/**
	 * Parses a map of properties. Each entry is checked if it contains a getter-method, like get_input or get_property. If yes,
	 * {@link #parseGetter(java.util.Map)} is called. If no, the value is set directly.
	 *
	 * @param properties map of properties
	 * @return a map with replaced get* keywords eventually
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> parseProperties(Map<String, Object> properties) {
		final Map<String, String> result = new HashMap<String, String>();
		for (final Map.Entry<String, Object> entry : properties.entrySet()) {
			String value = "";
			if (isGetter(entry.getValue())) {
				value = parseGetter((Map<String, Object>) entry.getValue());
			} else {
			    if(entry.getValue() instanceof ArrayList){
			        value = parseArrayList(entry);
			    } else if(entry.getValue() instanceof String){
			        value = (String) entry.getValue();
			    }
			}
			result.put(entry.getKey(), value);
		}
		return result;
	}

    private String parseArrayList(final Map.Entry<String, Object> entry) {
        String value;
        ArrayList<?> valuelist = (ArrayList<?>)entry.getValue();
        if (valuelist == null || valuelist.size() == 0) {
            value = "";
        } else {
            value = new Gson().toJson(entry.getValue(), ArrayList.class);
        }
        return value;
    }

	/**
	 * Checks whether the Object is a Getter or just a normal property.
	 *
	 * @param value the object to check for a getter
	 * @return true if getter, false if property
	 */
	private boolean isGetter(Object value) {
		return value instanceof Map<?, ?>;
	}

	/**
	 * Processes the get* keywords, e.g. get_property or get_input.
	 *
	 * @param getterMap combined map of get_inputs and get_properties
	 * @return the String denoting the user input, or {@link Yaml2XmlSwitch#DEFAULT_USER_INPUT} if none exists
	 */
	private String parseGetter(Map<String, Object> getterMap) {
	    if(getterMap == null || getterMap.isEmpty()){
	        return "";
	    }
	    return new Gson().toJson(getterMap, HashMap.class);
	}

}