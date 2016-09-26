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
package org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Xml2YamlTypeMapper {
    private static Map<String, String> t2yNodeTypeMap = new HashMap<>();
    static {
        t2yNodeTypeMap.put("RootNodeType", "tosca.nodes.Root");
        t2yNodeTypeMap.put("Server", "tosca.nodes.Compute");
        t2yNodeTypeMap.put("OperatingSystem", "tosca.nodes.SoftwareComponent");
        t2yNodeTypeMap.put("WebServer", "tosca.nodes.WebServer");
        t2yNodeTypeMap.put("WebApplication", "tosca.nodes.WebApplication");
        t2yNodeTypeMap.put("DBMS", "tosca.nodes.DBMS");
        t2yNodeTypeMap.put("Database", "tosca.nodes.Database");
        t2yNodeTypeMap.put("MySQL", "tosca.nodes.DBMS.MySQL");
        t2yNodeTypeMap.put("MySQLDatabase", "tosca.nodes.Database.MySQL");
        t2yNodeTypeMap.put("ApacheWebServer", "tosca.nodes.WebServer.Apache");

    }

    /**
     * @param tNodeType
     * @return
     */
    public static String mappingNodeType(String tNodeType) {
        if (tNodeType == null) {
            return "tosca.nodes.Root";
        }

        if (t2yNodeTypeMap.containsKey(tNodeType)) {
            return t2yNodeTypeMap.get(tNodeType);
        }

        return tNodeType;
    }

    private static Map<String, String> t2yCapabilityTypeMap = new HashMap<>();
    static {
        t2yCapabilityTypeMap.put("RootCapabilityType", "tosca.capabilities.Root");
        t2yCapabilityTypeMap.put("FeatureCapability", "tosca.capabilities.Feature");
        t2yCapabilityTypeMap.put("ContainerCapability", "tosca.capabilities.Container");
        t2yCapabilityTypeMap.put("EndpointCapability", "tosca.capabilities.Endpoint");
        t2yCapabilityTypeMap.put("DatabaseEndpointCapability",
                "tosca.capabilities.DatabaseEndpoint");
        t2yCapabilityTypeMap.put("MySQLDatabaseEndpointCapability",
                "tosca.capabilities.DatabaseEndpoint.MySQL");

        t2yCapabilityTypeMap.put("OSContainerCapability",
                "tosca.capabilities.OperatingSystem");
        t2yCapabilityTypeMap.put("VirtualBindable",
                "tosca.capabilites.nfv.VirtualBindable");
        t2yCapabilityTypeMap.put("Metric", "tosca.capabilities.nfv.Metric");
    }

    /**
     * @param tCapabilityType
     * @return
     */
    public static String mappingCapabilityType(String tCapabilityType) {
        if (tCapabilityType == null) {
            return "tosca.capabilities.Root";
        }

        if (t2yCapabilityTypeMap.containsKey(tCapabilityType)) {
            return t2yCapabilityTypeMap.get(tCapabilityType);
        }

        return tCapabilityType;
    }


    private static Map<String, String> tRequirement2yCapabilityTypeMap = new HashMap<>();
    static {
        tRequirement2yCapabilityTypeMap.put("OSContainerRequirement",
                "tosca.capabilities.OperatingSystem");
        tRequirement2yCapabilityTypeMap.put("VirtualBinding",
                "tosca.capabilites.nfv.VirtualBindable");
    }

    /**
     * @param tRequirementType
     * @return
     */
    public static String mappingTRequirement2yCapabilityType(
            String tRequirementType) {
        if (tRequirement2yCapabilityTypeMap.containsKey(tRequirementType)) {
            return tRequirement2yCapabilityTypeMap.get(tRequirementType);
        }

        return tRequirementType;
    }

    private static Map<String, String> t2yRelationshipTypeMap = new HashMap<>();
    static {
        t2yRelationshipTypeMap.put("RootRelationshipType",
                "tosca.relationships.Root");
        t2yRelationshipTypeMap
                .put("DependsOn", "tosca.relationships.DependsOn");
        t2yRelationshipTypeMap.put("DeployedOn",
                "tosca.relationships.DeployedOn");
        t2yRelationshipTypeMap.put("HostedOn", "tosca.relationships.HostedOn");
        t2yRelationshipTypeMap.put("ConnectsTo",
                "tosca.relationships.ConnectsTo");
    }

    /**
     * @param tRelationshipType
     * @return
     */
    public static String mappingRelationshipType(String tRelationshipType) {
        if (tRelationshipType == null) {
            return "tosca.relationships.Root";
        }

        if (t2yRelationshipTypeMap.containsKey(tRelationshipType)) {
            return t2yRelationshipTypeMap.get(tRelationshipType);
        }

        return tRelationshipType;
    }

    /**
     * @param tGroupType
     * @return
     */
    public static String mappingGroupType(String tGroupType) {
        if (tGroupType == null || tGroupType.isEmpty()) {
            return "tosca.groups.Root";
        }

        return tGroupType;
    }

}
