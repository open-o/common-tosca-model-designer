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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.winery.repository.ext.yamlmodel.NodeTemplatePosition;

/**
 * @author 10090474
 *
 */
public class PositionNamespaceCache {

  private Map<String, String> node_type_namespaces = new HashMap<>();
  private Map<String, String> capability_type_namespaces = new HashMap<>();
  private Map<String, String> relation_type_namespaces = new HashMap<>();
  private Map<String, String> requiremnet_type_namespaces = new HashMap<>();
  private Map<String, NodeTemplatePosition> positions = new HashMap<>();
  
  public PositionNamespaceCache() {
    super();
  }
  
  public Map<String, String> getNode_type_namespaces() {
    return node_type_namespaces;
  }
  public void setNode_type_namespaces(Map<String, String> node_type_namespaces) {
    this.node_type_namespaces = node_type_namespaces;
  }
  public Map<String, String> getRelation_type_namespaces() {
    return relation_type_namespaces;
  }
  public void setRelation_type_namespaces(Map<String, String> relation_type_namespaces) {
    this.relation_type_namespaces = relation_type_namespaces;
  }
  public Map<String, String> getRequiremnet_type_namespaces() {
    return requiremnet_type_namespaces;
  }
  public void setRequiremnet_type_namespaces(Map<String, String> requiremnet_type_namespaces) {
    this.requiremnet_type_namespaces = requiremnet_type_namespaces;
  }
  public Map<String, String> getCapability_type_namespaces() {
    return capability_type_namespaces;
  }
  public void setCapability_type_namespaces(Map<String, String> capability_type_namespaces) {
    this.capability_type_namespaces = capability_type_namespaces;
  }
  public Map<String, NodeTemplatePosition> getPositions() {
    return positions;
  }
  public void setPositions(Map<String, NodeTemplatePosition> positions) {
    this.positions = positions;
  }

}
