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
package org.openo.commontosca.catalog.mdserver.backend.ids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenericId {
  private String id;
  private List<String> path = new ArrayList<String>();

  public GenericId() {
    super();
  }

  public GenericId(String id) {
    this.id = id;
  }

  public GenericId(String id, List<String> path) {
    super();
    this.id = id;
    this.path = path;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getPath() {
    return path;
  }

  public void setPath(List<String> path) {
    if (null != path)
      this.path = path;
  }

  @Override
  public String toString() {
    return "GenericId [id=" + id + ", path=" + Arrays.toString(path.toArray()) + "]";
  }

  public List<String> getPaths() {
    return path;
  }

  public void addPath(String s) {
    path.add(s);
  }
}
