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

package org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher;



import org.eclipse.winery.common.PropertyTagUtil;
import org.eclipse.winery.common.propertydefinitionkv.Constraint;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.ext.yamlmodel.AttributeDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.EntrySchema;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.xml.namespace.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Xml2YamlSwitchUtils {
  /**
   * @param map.
   * @return .
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
     * @param name
     * @param entry
     * @return
     */
    public static <T> Entry<String, T> buildEntry(String name, T entry) {
        Map<String, T> map = new HashMap<>();
        map.put(name, entry);

        return map.entrySet().iterator().next();
    }

    /**
     * 
     * @param qname
     * @return
     */
  public static String getNamefromQName(QName qname) {
    if (qname == null || qname.getLocalPart() == null) {
      return null;
    }

    return qname.getLocalPart();
  }

  /**
   * 
   * @param tdoc.
   * @return .
   */
  public static String convert2Description(TDocumentation tdoc) {
    if (tdoc.getContent() != null && !tdoc.getContent().isEmpty()) {
      return tdoc.getContent().get(0).toString();
    }

    return null;
  }

  /**
   * 
   * @param tDocumentationList.
   * @return .
   */
  public static String convert2Description(List<TDocumentation> tdocumentationList) {
    if (tdocumentationList != null && !tdocumentationList.isEmpty()) {
      TDocumentation tdoc = tdocumentationList.get(0);
      return convert2Description(tdoc);

    }

    return null;
  }

  /**
   * 
   * @param tPropertyList.
   * @return .
   */
  public static Map<String, PropertyDefinition> convert2PropertyDefinitions(
      List<Object> tpropertyList) {
    Map<String, PropertyDefinition> yproperties = new HashMap<>();

    if (tpropertyList != null && !tpropertyList.isEmpty()) {
      WinerysPropertiesDefinition wpd = null;
      for (Object o : tpropertyList) {
        if (o instanceof WinerysPropertiesDefinition) {
          wpd = (WinerysPropertiesDefinition) o;
          if ("Properties".equals(wpd.getElementName())) {
            PropertyDefinitionKVList pdkvList = wpd.getPropertyDefinitionKVList();
            if (pdkvList != null && !pdkvList.isEmpty()) {
              for (PropertyDefinitionKV pdKv : pdkvList) {
                if (!isAttribute(pdKv.getTag())) {
                  PropertyDefinition yproperty = buildPropertyDefinition(pdKv);
                  yproperties.put(pdKv.getKey(), yproperty);
                }
              }
            }
          }
        }
      }
    }

    return yproperties;
  }


  /**
   * 
   * @param pdKv.
   * @return .
   */
  public static PropertyDefinition buildPropertyDefinition(PropertyDefinitionKV pdKv) {
    PropertyDefinition yproperty = new PropertyDefinition();
    yproperty.setType(convert2YamlType(pdKv.getType()));
    yproperty.setEntry_schema(convert2YamlEntrySchema(pdKv.getType()));
    yproperty.setDefault(pdKv.getValue());
    yproperty.setRequired(Boolean.valueOf(pdKv.getRequired()));

    Constraint constraint = pdKv.getConstraint();
    if (null != constraint) {
      ArrayList<Map<String, Object>> yconstraints = new ArrayList<>();
      if (null != constraint.getValidValue()) {
        Map<String, Object> validValues = new HashMap<>();
        validValues.put("valid_values", trim(constraint.getValidValue().split(",")));
        yconstraints.add(validValues);
      }
      yproperty.setConstraints(yconstraints);
    }

    return yproperty;
  }
  

  /**
   * @param split
   * @return
   */
  private static String[] trim(String[] split) {

    String[] tmps = new String[split.length];
    for (int i = 0; i < split.length; i++) {
      tmps[i] = split[i].trim();
    }
    return tmps;
  }

  /**
   * 
   * @param tPropertyList.
   * @return .
   */
  public static Map<String, AttributeDefinition> convert2AttributeDefinitions(
      List<Object> tpropertyList) {
    Map<String, AttributeDefinition> yattributes = new HashMap<>();

    if (tpropertyList != null && !tpropertyList.isEmpty()) {
      WinerysPropertiesDefinition wpd = null;
      for (Object o : tpropertyList) {
        if (o instanceof WinerysPropertiesDefinition) {
          wpd = (WinerysPropertiesDefinition) o;
          if ("Properties".equals(wpd.getElementName())) {
            PropertyDefinitionKVList pdkvList = wpd.getPropertyDefinitionKVList();

            if (pdkvList != null && !pdkvList.isEmpty()) {
              for (PropertyDefinitionKV pdKv : pdkvList) {
                if (isAttribute(pdKv.getTag())) {
                  AttributeDefinition yattribute = new AttributeDefinition();
                  yattribute.setType("string");
                  yattributes.put(pdKv.getKey(), yattribute);
                }
              }
            }
          }
        }
      }
    }

    return yattributes;
  }

  private static boolean isAttribute(String value) {
    return PropertyTagUtil.hasType(value, PropertyTagUtil.PropertyTag.METATDATA);
  }

  /**
   * @param type.
   * @return .
   */
  public static String convert2YamlType(String type) {
    if (type == null) {
      return "string";
    }
    if (type.indexOf(":") >= 0) {
      return type.substring(type.indexOf(":") + 1);
    }
    if (type.indexOf("_") <= 0) {
      return type;
    }
    String types[] = type.split("_");
    if ("obj".equalsIgnoreCase(types[0])) {
      return types[1];
    }
    if ("objlist".equalsIgnoreCase(types[0])) {
      return "list";
    }
    if ("objmap".equalsIgnoreCase(types[0])) {
      return "map";
    }
    return types[0];
  }
  
  /**
   * @param type
   * @return
   */
  public static EntrySchema convert2YamlEntrySchema(String type) {
    if (type == null || type.indexOf("_") <= 0) {
      return null;
    }
    
    String types[] = type.split("_");
    if ("obj".equalsIgnoreCase(types[0])) {
      return null;
    }
    
    return new EntrySchema(types[1]);
  }

  /**
   * 
   * @param tProperties.
   * @return .
   */
  public static Map<String, Object> convertTProperties(TEntityTemplate.Properties tproperties) {
    if (tproperties == null) {
      return null;
    }

    return convert2Properties(tproperties.getAny());
  }

  /**
   * @param tProperties.
   * @return .
   */
  public static Map<String, Object> convert2Properties(Object tproperties) {
    if (tproperties.getClass().getName()
        .equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")
        || tproperties.getClass().getName().equals("org.apache.xerces.dom.ElementNSImpl")) {
      Element propRootElement = (Element) tproperties;
      NodeList childList = propRootElement.getChildNodes();
      if (childList == null || childList.getLength() <= 0) {
        return null;
      }

      Map<String, Object> yproperties = new HashMap<>();
      for (int i = 0; i < childList.getLength(); i++) {
        Node child = childList.item(i);
        if (child.getNodeType() == Node.ELEMENT_NODE) {
          String propertyName = child.getLocalName();
          String propertyValue = child.getTextContent();
          if (propertyValue == null || propertyValue.trim().isEmpty()
              || propertyValue.trim().equalsIgnoreCase("null")) {
            continue;
          }

          if (propertyValue.startsWith("[") && propertyValue.endsWith("]")) {
            JsonArray jsonArray = new Gson().fromJson(propertyValue, JsonArray.class);
            List<Object> listValue = parseListValue(jsonArray);
            yproperties.put(propertyName, listValue);
            continue;
          }

          if (propertyValue.startsWith("{") && propertyValue.endsWith("}")) {
            JsonObject jsonObject = new Gson().fromJson(propertyValue, JsonObject.class);
            yproperties.put(propertyName, parseMapValue(jsonObject));
            continue;
          }

          yproperties.put(propertyName, propertyValue);
        }
      }

      return yproperties;
    }

    return null;
  }

  /**
   * @param jsonArray.
   * @return .
   */
  private static List<Object> parseListValue(JsonArray jsonArray) {
    List<Object> list = new ArrayList<>();

    for (int i = 0, size = jsonArray.size(); i < size; i++) {
      if (jsonArray.get(i) instanceof JsonPrimitive) {
        list.add(((JsonPrimitive) jsonArray.get(i)).getAsString());
        continue;
      }

      if (jsonArray.get(i) instanceof JsonObject) {
        list.add(parseMapValue((JsonObject) jsonArray.get(i)));
        continue;
      }
      
      if (jsonArray.get(i) instanceof JsonArray) {
        list.add(parseListValue((JsonArray) jsonArray.get(i)));
        continue;
      }
    }

    return list;
  }

  private static Map<String, Object> parseMapValue(final JsonObject jsonObject) {
    Map<String, Object> map = new HashMap<>();

    Iterator<Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
    while (iterator.hasNext()) {
      Entry<String, JsonElement> next = iterator.next();
      if (next.getValue() instanceof JsonPrimitive) {
        map.put(next.getKey(), next.getValue().getAsString());
        continue;
      }
      
      if (next.getValue() instanceof JsonObject) {
        map.put(next.getKey(), parseMapValue((JsonObject) next.getValue()));
        continue;
      }
      
      if (next.getValue() instanceof JsonArray) {
        map.put(next.getKey(), parseListValue((JsonArray) next.getValue()));
        continue;
      }
    }
    return map;
  }

  /**
   * 
   * @param tPropertyList.
   * @return .
   */
  public static Map<String, PropertyDefinition> convert2PolicyPropertyDefinitions(
      List<Object> tpropertyList) {
    Map<String, PropertyDefinition> yproperties = new HashMap<>();

    if (tpropertyList != null && !tpropertyList.isEmpty()) {
      WinerysPropertiesDefinition wpd = (WinerysPropertiesDefinition) tpropertyList.get(0);
      if ("Properties".equalsIgnoreCase(wpd.getElementName())) {
        PropertyDefinitionKVList pdkvList = wpd.getPropertyDefinitionKVList();

        if (pdkvList != null && !pdkvList.isEmpty()) {
          for (PropertyDefinitionKV pdKv : pdkvList) {
            PropertyDefinition yproperty = buildPropertyDefinition(pdKv);
            yproperties.put(pdKv.getKey(), yproperty);
          }
        }
      }
    }

    return yproperties;
  }

  /**
   * @param value
   * @return
   */
  public static boolean convert2YamlValue(TBoolean value) {
    return value != null && value.equals(TBoolean.YES);
  }
}
