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
/**
 * 
 */
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.yamlmodel.AttributeDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;

/**
 *
 */
public class Yaml2XmlSwitchUtils {
    public static List<WinerysPropertiesDefinition> convert2PropertyDefinitions(
            Map<String, PropertyDefinition> yPropertyMap) {
        PropertyDefinitionKVList tpdkvList = new PropertyDefinitionKVList();
        for (Map.Entry<String, PropertyDefinition> yProperty : yPropertyMap
                .entrySet()) {
            String type = convertType(yProperty.getValue().getType());
            PropertyDefinitionKV tpdkv = new PropertyDefinitionKV(
                    yProperty.getKey(), type);
            tpdkvList.add(tpdkv);
        }

        List<WinerysPropertiesDefinition> tPropertyList = new ArrayList<>();
        WinerysPropertiesDefinition twpd = new WinerysPropertiesDefinition();
        twpd.setElementName("Properties");
        twpd.setPropertyDefinitionKVList(tpdkvList);
        tPropertyList.add(twpd);

        return tPropertyList;

    }

    public static List<WinerysPropertiesDefinition> convert2AttritueDefinitions(
            Map<String, AttributeDefinition> yPropertyMap) {
        PropertyDefinitionKVList tpdkvList = new PropertyDefinitionKVList();
        for (Map.Entry<String, AttributeDefinition> yAttribute : yPropertyMap.entrySet()) {
            PropertyDefinitionKV tpdkv =
                    new PropertyDefinitionKV(yAttribute.getKey(), "attribute");
            tpdkvList.add(tpdkv);
        }

        List<WinerysPropertiesDefinition> tPropertyList = new ArrayList<>();
        WinerysPropertiesDefinition twpd = new WinerysPropertiesDefinition();
        twpd.setElementName("Properties");
        twpd.setPropertyDefinitionKVList(tpdkvList);
        tPropertyList.add(twpd);

        return tPropertyList;

    }
    
    /**
     * @param yProperty
     * @return
     */
    private static String convertType(String type) {
        if (type == null) {
            return null;
        }

        if (type.equals("string") || type.equals("int") || type.equals("float")) {
            return "xsd:" + type;

        }

        return type;
    }

    public static ElementNSImpl convert2Properties(
            Map<String, Object> yPropertyMap) {
        ElementNSImpl tProperties = (ElementNSImpl) (new CoreDocumentImpl())
                .createElementNS(CommonConst.NS, "Properties");

        return tProperties;
    }

    // /**
    // * @param tProperties
    // * @return
    // */
    // public static Map<String, Object> convert2Properties(Object tProperties)
    // {
    // if (tProperties.getClass().getName()
    // .equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")
    // || tProperties.getClass().getName()
    // .equals("org.apache.xerces.dom.ElementNSImpl")) {
    // Element propRootElement = (Element) tProperties;
    // NodeList childList = propRootElement.getChildNodes();
    // if (childList == null || childList.getLength() <= 0) {
    // return null;
    // }
    //
    // Map<String, Object> yProperties = new HashMap<>();
    // for (int i = 0; i < childList.getLength(); i++) {
    // Node child = childList.item(i);
    // if (child.getNodeType() == Node.ELEMENT_NODE) {
    // String propertyName = child.getLocalName();
    // String propertyValue = child.getTextContent();
    //
    // if (propertyValue.startsWith("[")
    // && propertyValue.endsWith("]")) {
    // JsonArray jsonArray = new Gson().fromJson(
    // propertyValue, JsonArray.class);
    // List<Object> listValue = parseListValue(jsonArray);
    // yProperties.put(propertyName, listValue);
    // continue;
    // }
    //
    // if (propertyValue.startsWith("{")
    // && propertyValue.endsWith("}")) {
    // JsonObject jsonObject = new Gson().fromJson(
    // propertyValue, JsonObject.class);
    // yProperties
    // .put(propertyName, parseMapValue(jsonObject));
    // continue;
    // }
    //
    // yProperties.put(propertyName, propertyValue);
    // }
    // }
    //
    // return yProperties;
    // }
    //
    // return null;
    // }
    //
    // /**
    // * @param jsonArray
    // * @return
    // */
    // private static List<Object> parseListValue(JsonArray jsonArray) {
    // List<Object> list = new ArrayList<>();
    //
    // for (int i = 0, size = jsonArray.size(); i < size; i++) {
    // if (jsonArray.get(i) instanceof JsonPrimitive) {
    // list.add(((JsonPrimitive) jsonArray.get(i)).getAsString());
    // continue;
    // }
    //
    // if (jsonArray.get(i) instanceof JsonObject) {
    // Map<String, Object> map = parseMapValue((JsonObject) jsonArray
    // .get(i));
    // list.add(map);
    // continue;
    // }
    // }
    //
    // return list;
    // }
    //
    // private static Map<String, Object> parseMapValue(final JsonObject
    // jsonObject) {
    // Map<String, Object> map = new HashMap<>();
    //
    // Iterator<Entry<String, JsonElement>> iterator = jsonObject.entrySet()
    // .iterator();
    // while (iterator.hasNext()) {
    // Entry<String, JsonElement> next = iterator.next();
    // if (next.getValue() instanceof JsonPrimitive) {
    // map.put(next.getKey(), next.getValue().getAsString());
    // continue;
    // }
    //
    // if (next.getValue() instanceof JsonObject) {
    // map.put(next.getKey(),
    // parseMapValue((JsonObject) next.getValue()));
    // continue;
    // }
    // }
    // return map;
    // }

}
