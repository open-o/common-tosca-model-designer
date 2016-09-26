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

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Yaml2XmlTypeMapper {
    private static Map<String, String> y2tNodeTypeMap = new HashMap<>();
    static {
        y2tNodeTypeMap.put("tosca.nodes.Root", "RootNodeType");
        y2tNodeTypeMap.put("tosca.nodes.Compute", "Server");
        y2tNodeTypeMap.put("tosca.nodes.SoftwareComponent", "OperatingSystem");
        y2tNodeTypeMap.put("tosca.nodes.WebServer", "WebServer");
        y2tNodeTypeMap.put("tosca.nodes.WebApplication", "WebApplication");
        y2tNodeTypeMap.put("tosca.nodes.DBMS", "DBMS");
        y2tNodeTypeMap.put("tosca.nodes.Database", "Database");
        y2tNodeTypeMap.put("tosca.nodes.DBMS.MySQL", "MySQL");
        y2tNodeTypeMap.put("tosca.nodes.Database.MySQL", "MySQLDatabase");
        y2tNodeTypeMap.put("tosca.nodes.WebServer.Apache", "ApacheWebServer");
    }

    /**
     * @param tNodeType
     * @return
     */
    public static String mappingNodeType(String tNodeType) {
        if (tNodeType == null) {
            return "tosca.nodes.Root";
        }

        if (y2tNodeTypeMap.containsKey(tNodeType)) {
            return y2tNodeTypeMap.get(tNodeType);
        }

        return tNodeType;
    }

}
