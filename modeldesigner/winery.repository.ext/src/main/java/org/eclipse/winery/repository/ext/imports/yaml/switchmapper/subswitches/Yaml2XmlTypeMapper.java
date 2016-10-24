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

import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityType.DerivedFrom;


/**
 *
 */
public class Yaml2XmlTypeMapper {
    /**
     * @param yNodeType
     * @return
     */
    public static String mappingNodeType(String yNodeType) {
        if (yNodeType == null || yNodeType.equals("tosca.nodes.Root")) {
            return null;
        }

        return yNodeType;
    }

    /**
     * @param yCapabilityType
     * @return
     */
    public static String mappingCapabilityType(String yCapabilityType) {
        if (yCapabilityType == null || yCapabilityType.equalsIgnoreCase("tosca.capabilities.Root")) {
            return null;
        }

        return yCapabilityType;
    }

    /**
     * @param yRelationshipType
     * @return
     */
    public static String mappingRelationshipType(String yRelationshipType) {
        if (yRelationshipType == null || yRelationshipType.equalsIgnoreCase("tosca.relationships.Root")) {
            return null;
        }

        return yRelationshipType;
    }

    /**
     * @param yGroupType
     * @return
     */
    public static String mappingGroupType(String yGroupType) {
        return yGroupType;
    }

    /**
     * @param namespace
     * @param derivedFrom 
     * @return
     */
    public static DerivedFrom buildDerivedFrom(String namespace, String derivedFrom) {
      if (derivedFrom == null) {
        return null;
      }
      
      TEntityType.DerivedFrom tDerivedFrom = new TEntityType.DerivedFrom();
      tDerivedFrom.setTypeRef(Yaml2XmlDataHelper.newQName(namespace, derivedFrom));
      return tDerivedFrom;
    }

}
