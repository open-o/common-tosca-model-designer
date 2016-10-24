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
package org.eclipse.winery.repository.ext.yamlmodel;


/**
 * @author 10090474
 *
 */
public class AttributeDefinition extends YAMLElement {
  private String type = "";
  private String defaultValue = "";
  private String status = "supported";
  private EntrySchema entry_schema;

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    if (type != null) {
      this.type = type;
    }
  }

  public String getDefault() {
    return this.defaultValue;
  }

  public void setDefault(String defaultValue) {
    if (defaultValue != null) {
      this.defaultValue = defaultValue;
    }
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  
  public EntrySchema getEntry_schema() {
    return entry_schema;
  }
  
  public void setEntry_schema(EntrySchema entry_schema) {
    this.entry_schema = entry_schema;
  }

}
