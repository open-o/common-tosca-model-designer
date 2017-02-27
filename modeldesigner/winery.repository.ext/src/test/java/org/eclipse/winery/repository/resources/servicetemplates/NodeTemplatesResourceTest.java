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
package org.eclipse.winery.repository.resources.servicetemplates;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.repository.AbstractRepositoryTest;
import org.eclipse.winery.repository.resources.servicetemplates.topologytemplates.NodeTemplateInfo;
import org.eclipse.winery.repository.resources.servicetemplates.topologytemplates.NodeTemplatesResource;
import org.junit.Test;

/**
 * @author 10186401
 *
 */
public class NodeTemplatesResourceTest extends AbstractRepositoryTest {
  private NodeTemplatesResource res = this.getNodeTemplatesResource();

  public NodeTemplatesResource getNodeTemplatesResource() {
    QName type = new QName("http://www.open-o.org/tosca/nfv/vnf", "test");
    ServiceTemplateResource stRes =
        (ServiceTemplateResource) new ServiceTemplatesResource().getComponentInstaceResource(type);
    return stRes.getTopologyTemplateResource().getNodeTemplatesResource();
  }

  @Test
  public void testGetList() {
    @SuppressWarnings("unchecked")
    List<TNodeTemplate> list = (List<TNodeTemplate>) res.getList();
    assertEquals(3, list.size());
  }

  @Test
  public void testOnPost() {
    NodeTemplateInfo info = new NodeTemplateInfo();
    info.setId("id_1");
    info.setName("testNode");
    info.setType("tosca.nodes.nfv.ext.zte.VDU");
    info.setNamespace("http://www.open-o.org/tosca/nfv");
    res.onPost(info);

    @SuppressWarnings("unchecked")
    List<TNodeTemplate> list = (List<TNodeTemplate>) res.getList();
    assertEquals(4, list.size());

    res.getEntityResource("id_1").onDelete();
  }

}
