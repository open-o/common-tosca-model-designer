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
package org.eclipse.winery.repository.ext.export.yaml.switcher;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.winery.repository.ext.yamlmodel.NodeTemplatePosition;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlWriter;

/**
 * @author 10090474
 *
 */
public class PositionNamespaceHelper {
  private static final Logger logger = LoggerFactory.getLogger(PositionNamespaceHelper.class);
  private static PositionNamespaceHelper instance = null;
  private PositionNamespaceCache cache = new PositionNamespaceCache();
  
  public static PositionNamespaceHelper getInstance() {
    if (instance == null) {
      instance = new PositionNamespaceHelper();
    }
    return instance;
  }
  
  private PositionNamespaceHelper(){
  }
  
  public void clear() {
    cache.getNode_type_namespaces().clear();
    cache.getCapability_type_namespaces().clear();
    cache.getRelation_type_namespaces().clear();
    cache.getRequiremnet_type_namespaces().clear();
    cache.getPositions().clear();
  }
  
  public void addNodeTypeNamespace(String id, String namespace) {
    cache.getNode_type_namespaces().put(id, namespace);
  }
  
  public void addCapabilityTypeNamespace(String id, String namespace) {
    cache.getCapability_type_namespaces().put(id, namespace);
  }
  
  public void addRelationTypeNamespace(String id, String namespace) {
    cache.getRelation_type_namespaces().put(id, namespace);
  }
  public void addRequiremnetTypeNamespace(String id, String namespace) {
    cache.getRequiremnet_type_namespaces().put(id, namespace);
  }
  public void addPosition(String id, NodeTemplatePosition position) {
    cache.getPositions().put(id, position);
  }
  
  /**
   * @param st
   * @param namespace
   */
  public void addNamespace(ServiceTemplate st, String namespace) {
    for (String nodeTypeId : st.getNode_types().keySet()) {
      this.addNodeTypeNamespace(nodeTypeId, namespace);
    }
    for (String capabilityTypeId : st.getCapability_types().keySet()) {
      this.addCapabilityTypeNamespace(capabilityTypeId, namespace);
    }   
    for (String relationshipTypeId : st.getRelationship_types().keySet()) {
      this.addRelationTypeNamespace(relationshipTypeId, namespace);
    }
  }
  
  
  /**
   * @param out
   * @param filePath
   */
  public void save2File(OutputStream out, String filePath) {
    YamlWriter writer = new YamlWriter(new OutputStreamWriter(out));
    writer.getConfig().writeConfig.setWriteRootTags(false);
    writer.getConfig().writeConfig.setWriteRootElementTags(false);
    writer.getConfig().writeConfig.setAutoAnchor(false);
    writer.getConfig().writeConfig.setIndentSize(2);
    writer.getConfig().setPropertyElementType(PositionNamespaceCache.class, "positions", NodeTemplatePosition.class);
    try {
      writer.write(this.cache);
    } catch (YamlException e) {
      logger.warn("Save position namespace to file failed.", e);
    }
  }

}
