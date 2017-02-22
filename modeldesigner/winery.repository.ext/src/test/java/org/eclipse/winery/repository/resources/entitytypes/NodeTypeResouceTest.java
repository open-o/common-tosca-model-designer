/**
 * Copyright 2017 ZTE Corporation.
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
package org.eclipse.winery.repository.resources.entitytypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.repository.AbstractRepositoryTest;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.junit.Test;

import com.google.gson.Gson;

/**
 * @author 10186401
 *
 */
public class NodeTypeResouceTest extends AbstractRepositoryTest {

  private NodeTypesResource res = new NodeTypesResource();

  @Test
  public void testOnPost() {
    QName type = new QName("http://www.open-o.org/tosca/nfv", "tosca.nodes.nfv.Test");

    List<TNodeType> beforeList = res.getAllNodeTypes();
    assertEquals(14, beforeList.size());

    res.onPost("http://www.open-o.org/tosca/nfv", "tosca.nodes.nfv.Test", null);

    List<TNodeType> afterList = res.getAllNodeTypes();
    assertEquals(15, afterList.size());

    addClearUpIds(new NodeTypeId(type));
  }

  @Test
  public void testGetResource() {
    NodeTypeResource nodetypeRes =
        getNodeTypeResource("http://www.open-o.org/tosca/nfv", "tosca.nodes.nfv.ext.ImageFile");
    assertNotNull(nodetypeRes);
    assertEquals("tosca.nodes.nfv.ext.ImageFile", nodetypeRes.getNodeType().getName());
  }

  @Test
  public void testGetAllNodeTypes() {
    List<TNodeType> beforeList = res.getAllNodeTypes();
    assertEquals(14, beforeList.size());
  }

  @Test
  public void testGetListOfAllIds() {
    String idsStr = res.getListOfAllIds();
    Object[] listOfAllIds = new Gson().fromJson(idsStr, Object[].class);
    assertEquals(14, listOfAllIds.length);
  }

  public NodeTypeResource getNodeTypeResource(String ns, String name) {
    QName type = new QName(ns, name);
    NodeTypeResource nodetypeRes = (NodeTypeResource) res.getComponentInstaceResource(type);
    return nodetypeRes;
  }
}
