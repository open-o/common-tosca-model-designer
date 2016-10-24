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
/**
 * 
 */
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.eclipse.winery.common.PropertyTagUtil;
import org.eclipse.winery.common.propertydefinitionkv.Constraint;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.yamlmodel.AttributeDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.EntrySchema;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;

/**
 *
 */
public class Yaml2XmlSwitchUtils {
  public static WinerysPropertiesDefinition buildWinerysPropertiesDefinition(
      Map<String, PropertyDefinition> propDefs, String namespace) {
    WinerysPropertiesDefinition wpd = new WinerysPropertiesDefinition();
    wpd.setElementName(CommonConst.PROPERTIES_LOCALPART);
    wpd.setNamespace(namespace);
    
    PropertyDefinitionKVList pdVList = buildPropertyDefinitionKVList(propDefs);
    wpd.setPropertyDefinitionKVList(pdVList);
    return wpd;
  }
  
  public static PropertyDefinitionKVList buildAttributeDefinitionKVList(
      Map<String, AttributeDefinition> attrDefs) {
    PropertyDefinitionKVList pdKVList = new PropertyDefinitionKVList();
    for (Map.Entry<String, AttributeDefinition> entry : attrDefs.entrySet()) {
        pdKVList.add(buidAttributeDefinitionKV(entry));
    }
    return pdKVList;
  }
  
  /**
   * @param entry
   * @return
   */
  private static PropertyDefinitionKV buidAttributeDefinitionKV(
      Entry<String, AttributeDefinition> entry) {
    
    PropertyDefinitionKV pdKv = new PropertyDefinitionKV();
    pdKv.setKey(entry.getKey());
    pdKv.setType(convertType(entry.getValue().getType(), entry.getValue().getEntry_schema()));
    pdKv.setValue(entry.getValue().getDefault());
    pdKv.setTag(String.valueOf(PropertyTagUtil.PropertyTag.METATDATA));
    
    return pdKv;
  }

  private static PropertyDefinitionKVList buildPropertyDefinitionKVList(
      Map<String, PropertyDefinition> propDefs) {
    PropertyDefinitionKVList pdKVList = new PropertyDefinitionKVList();
    for (Map.Entry<String, PropertyDefinition> entry : propDefs.entrySet()) {
        pdKVList.add(buidPropertyDefinitionKV(entry));
    }
    return pdKVList;
  }
  
  private static PropertyDefinitionKV buidPropertyDefinitionKV(Map.Entry<String, PropertyDefinition> entry) {
    PropertyDefinitionKV pdKv = new PropertyDefinitionKV();
    pdKv.setKey(entry.getKey());
    pdKv.setType(convertType(entry.getValue().getType(), entry.getValue().getEntry_schema()));
    pdKv.setValue(entry.getValue().getDefault());
    pdKv.setRequired(String.valueOf(entry.getValue().isRequired()));
    
    List<Map<String, Object>> yConstraint = entry.getValue().getConstraints();
    pdKv.setConstraint(buildConstraint(yConstraint));
    
    return pdKv;
  }

  private static Constraint buildConstraint(List<Map<String,Object>> yConstraint) {
    Constraint tConstraint = new Constraint();
    for (Map<String, Object> constraintPart : yConstraint) {
      if (constraintPart.containsKey("valid_values")) {
        tConstraint.setValidValue(buildValidValue(constraintPart));
        continue;
      }
    }
    
    return tConstraint;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static String buildValidValue(Map<String, Object> constraint) {
    if (constraint.get("valid_values") instanceof Object[]) {
      return concat((Object[]) constraint.get("valid_values"));
    }
    if (constraint.get("valid_values") instanceof List) {
      List validValues = ((List) constraint.get("valid_values"));
      return concat(validValues.toArray(new Object[0]));
    }

    return "";
  }
  
    /**
   * @param values
   * @return
   */
  private static String concat(Object[] values) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      sb.append(values[i]).append(", ");
    }
    
    if (sb.length() > 2) {
      sb.setLength(sb.length() - 2);
    }

    return sb.toString();
  }

    public static List<WinerysPropertiesDefinition> buildWinerysPropertiesDefinitionList(
            Map<String, PropertyDefinition> propDefs, String namespace) {
        List<WinerysPropertiesDefinition> tPropertyList = new ArrayList<>();
        tPropertyList.add(buildWinerysPropertiesDefinition(propDefs, namespace));

        return tPropertyList;
    }
    
    private static final Set<String> BASE_DATA_TYPE_SET = new HashSet<>();
    static {
      BASE_DATA_TYPE_SET.add("string");
      BASE_DATA_TYPE_SET.add("integer");
      BASE_DATA_TYPE_SET.add("float");
      BASE_DATA_TYPE_SET.add("boolean");
      BASE_DATA_TYPE_SET.add("timestamp");
      BASE_DATA_TYPE_SET.add("null");
    }
    
    private static String convertType(String type, EntrySchema entrySchema) {
      if (entrySchema == null) {
        if (BASE_DATA_TYPE_SET.contains(type)) {
          return "xsd:" + type;
        } else {
          return "obj_" + type;
        }
      } else {
        if ("list".equalsIgnoreCase(type)) {
          return "objlist_" + entrySchema.getType();
        }
        
        if ("map".equalsIgnoreCase(type)) {
          return "objmap_" + entrySchema.getType();
        }
      }
      
      return type;
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
