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
package org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.ext.yamlmodel.AttributeDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 *
 */
public class Xml2YamlSwitchUtils {
    /**
     * @param map
     * @return
     */
    public static <K, T> List<Map<K, T>> convertMap2ListMap(Map<K, T> map) {
        if (map == null || map.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<K, T>> list = new ArrayList<>();

        for (Entry<K, T> entry : map.entrySet()) {
            Map<K, T> tmpMap = new HashMap<>();
            tmpMap.put(entry.getKey(), entry.getValue());
            list.add(tmpMap);
        }

        return list;
    }

    /**
     * 
     * @param qName
     * @return
     */
    public static String getNamefromQName(QName qName) {
        if (qName == null || qName.getLocalPart() == null) {
            return null;
        }

        return qName.getLocalPart();
    }

    /**
     * 
     * @param tdoc
     * @return
     */
    public static String convert2Description(TDocumentation tdoc) {
        if (tdoc.getContent() != null && !tdoc.getContent().isEmpty()) {
            return tdoc.getContent().get(0).toString();
        }

        return null;
    }

    /**
     * 
     * @param tDocumentationList
     * @return
     */
    public static String convert2Description(
            List<TDocumentation> tDocumentationList) {
        if (tDocumentationList != null && !tDocumentationList.isEmpty()) {
            TDocumentation tdoc = tDocumentationList.get(0);
            return convert2Description(tdoc);

        }

        return null;
    }

    /**
     * 
     * @param tPropertyList
     * @return
     */
    public static Map<String, PropertyDefinition> convert2PropertyDefinitions(
            List<Object> tPropertyList) {
        Map<String, PropertyDefinition> yProperties = new HashMap<>();

        if (tPropertyList != null && !tPropertyList.isEmpty()) {
            WinerysPropertiesDefinition wpd = (WinerysPropertiesDefinition) tPropertyList
                    .get(0);
            if ("Properties".equalsIgnoreCase(wpd.getElementName())) {
                PropertyDefinitionKVList pdkvList = wpd
                        .getPropertyDefinitionKVList();

                if (pdkvList != null && !pdkvList.isEmpty()) {
                    for (PropertyDefinitionKV pdKV : pdkvList) {
                        if (!isAttribute(convert2YamlPropertyType(pdKV.getType()))) {
                            PropertyDefinition yProperty = new PropertyDefinition();
                            yProperty.setType(convert2YamlPropertyType(pdKV.getType()));
                            yProperties.put(pdKV.getKey(), yProperty);
                        }
                    }
                }
            }
        }

        return yProperties;
    }

    /**
     * 
     * @param tPropertyList
     * @return
     */
    public static Map<String, AttributeDefinition> convert2AttributeDefinitions(
            List<Object> tPropertyList) {
        Map<String, AttributeDefinition> yAttributes = new HashMap<>();

        if (tPropertyList != null && !tPropertyList.isEmpty()) {
            WinerysPropertiesDefinition wpd = (WinerysPropertiesDefinition) tPropertyList
                    .get(0);
            if ("Properties".equalsIgnoreCase(wpd.getElementName())) {
                PropertyDefinitionKVList pdkvList = wpd
                        .getPropertyDefinitionKVList();

                if (pdkvList != null && !pdkvList.isEmpty()) {
                    for (PropertyDefinitionKV pdKV : pdkvList) {
                        if (isAttribute(convert2YamlPropertyType(pdKV.getType()))) {
                            AttributeDefinition yAttribute = new AttributeDefinition();
                            yAttribute.setType("string");
                            yAttributes.put(pdKV.getKey(), yAttribute);
                        }
                    }
                }
            }
        }

        return yAttributes;
    }
    
    private static boolean isAttribute(String value){
        return value.equalsIgnoreCase("attribute");
    }
    
    /**
     * @param type
     * @return
     */
    private static String convert2YamlPropertyType(String type) {
        if (type != null && type.indexOf(":") >= 0) {
            return type.substring(type.indexOf(":") + 1);
        }

        return type;
    }

    public static Map<String, Object> convertTProperties(
            TEntityTemplate.Properties tProperties) {
        if (tProperties == null) {
            return null;
        }

        return convert2Properties(tProperties.getAny());
    }

    /**
     * @param tProperties
     * @return
     */
    public static Map<String, Object> convert2Properties(Object tProperties) {
        if (tProperties.getClass().getName()
                .equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")
                || tProperties.getClass().getName()
                        .equals("org.apache.xerces.dom.ElementNSImpl")) {
            Element propRootElement = (Element) tProperties;
            NodeList childList = propRootElement.getChildNodes();
            if (childList == null || childList.getLength() <= 0) {
                return null;
            }

            Map<String, Object> yProperties = new HashMap<>();
            for (int i = 0; i < childList.getLength(); i++) {
                Node child = childList.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String propertyName = child.getLocalName();
                    String propertyValue = child.getTextContent();
                    if (propertyValue == null || propertyValue.trim().isEmpty()
                            || propertyValue.trim().equalsIgnoreCase("null")) {
                        continue; // 绌哄�銆佹棤鏁堝� Property 涓嶅鍑恒�
                    }

                    if (propertyValue.startsWith("[")
                            && propertyValue.endsWith("]")) {
                        JsonArray jsonArray = new Gson().fromJson(
                                propertyValue, JsonArray.class);
                        List<Object> listValue = parseListValue(jsonArray);
                        yProperties.put(propertyName, listValue);
                        continue;
                    }

                    if (propertyValue.startsWith("{")
                            && propertyValue.endsWith("}")) {
                        JsonObject jsonObject = new Gson().fromJson(
                                propertyValue, JsonObject.class);
                        yProperties.put(propertyName, parseMapValue(jsonObject));
                        continue;
                    }

                    yProperties.put(propertyName, propertyValue);
                }
            }

            return yProperties;
        }

        return null;
    }

    /**
     * @param jsonArray
     * @return
     */
    private static List<Object> parseListValue(JsonArray jsonArray) {
        List<Object> list = new ArrayList<>();

        for (int i = 0, size = jsonArray.size(); i < size; i++) {
            if (jsonArray.get(i) instanceof JsonPrimitive) {
                list.add(((JsonPrimitive) jsonArray.get(i)).getAsString());
                continue;
            }

            if (jsonArray.get(i) instanceof JsonObject) {
                Map<String, Object> map = parseMapValue((JsonObject) jsonArray
                        .get(i));
                list.add(map);
                continue;
            }
        }

        return list;
    }

    private static Map<String, Object> parseMapValue(final JsonObject jsonObject) {
        Map<String, Object> map = new HashMap<>();

        Iterator<Entry<String, JsonElement>> iterator = jsonObject.entrySet()
                .iterator();
        while (iterator.hasNext()) {
            Entry<String, JsonElement> next = iterator.next();
            if (next.getValue() instanceof JsonPrimitive) {
                map.put(next.getKey(), next.getValue().getAsString());
                continue;
            }

            if (next.getValue() instanceof JsonObject) {
                map.put(next.getKey(),
                        parseMapValue((JsonObject) next.getValue()));
                continue;
            }
        }
        return map;
    }

}
