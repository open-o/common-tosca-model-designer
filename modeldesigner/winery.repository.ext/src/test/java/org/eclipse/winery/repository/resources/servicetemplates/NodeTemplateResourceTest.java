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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.repository.AbstractRepositoryTest;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.resources.servicetemplates.topologytemplates.NodeTemplateInfo;
import org.eclipse.winery.repository.resources.servicetemplates.topologytemplates.NodeTemplateResource;
import org.eclipse.winery.repository.resources.servicetemplates.topologytemplates.NodeTemplatesResource;
import org.eclipse.winery.repository.resources.servicetemplates.topologytemplates.RequirementInfo;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author 10186401
 *
 */
public class NodeTemplateResourceTest extends AbstractRepositoryTest {
  private static NodeTemplateResource res = getNodeTemplateResource();

  public static NodeTemplateResource getNodeTemplateResource() {
    QName type = new QName("http://www.open-o.org/tosca/nfv/vnf", "test");
    ServiceTemplateResource stRes =
        (ServiceTemplateResource) new ServiceTemplatesResource().getComponentInstaceResource(type);
    NodeTemplatesResource nodeTemplatesResource =
        stRes.getTopologyTemplateResource().getNodeTemplatesResource();
    NodeTemplateInfo info = new NodeTemplateInfo();
    info.setId("id_1");
    info.setName("testNode");
    info.setType("tosca.nodes.nfv.ext.zte.VDU");
    info.setNamespace("http://www.open-o.org/tosca/nfv");
    nodeTemplatesResource.onPost(info);
    return nodeTemplatesResource.getEntityResource("id_1");
  }

  @AfterClass
  public static void clearUp4Class() {
    res.onDelete();
  }

  @Test
  public void testReqs() {
    RequirementInfo info = new RequirementInfo();
    info.setId("req_id");
    info.setName("req_name");
    info.setNode("testNode");
    info.setType("tosca.requirements.nfv.VirtualBinding");
    info.setNamespace("http://www.open-o.org/tosca/nfv");
    info.setCapability("tosca.capabilities.nfv.VirtualBindable");
    List<RequirementInfo> infos = new ArrayList<RequirementInfo>();
    infos.add(info);
    res.onPost(infos);

    Object entity = res.getJSON().getEntity();
    Assert.assertNotNull(entity);
    TNodeTemplate node = (TNodeTemplate) entity;
    List<TRequirement> requirements = node.getRequirements().getRequirement();
    assertEquals(1, requirements.size());
    TRequirement req = requirements.get(0);
    assertEquals("req_id", req.getId());
    assertEquals("req_name", req.getName());
    assertEquals("tosca.requirements.nfv.VirtualBinding", req.getType().getLocalPart());
    assertEquals("http://www.open-o.org/tosca/nfv", req.getType().getNamespaceURI());
    Map<QName, String> otherAttributes = req.getOtherAttributes();
    assertEquals("testNode", otherAttributes.get(new QName(Constants.REQUIREMENT_EXT_NODE)));
    assertEquals("tosca.capabilities.nfv.VirtualBindable",
        otherAttributes.get(new QName(Constants.REQUIREMENT_EXT_CAPABILITY)));

    res.clearRequirements();
    Assert.assertNull(node.getRequirements());

  }
}
