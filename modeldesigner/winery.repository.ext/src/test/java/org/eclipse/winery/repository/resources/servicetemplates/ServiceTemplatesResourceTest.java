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
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.boundaryproperty.Input;
import org.eclipse.winery.common.boundaryproperty.Inputs;
import org.eclipse.winery.common.boundaryproperty.MetaData;
import org.eclipse.winery.common.boundaryproperty.MetaDatas;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.servicetemplate.SubstitutableNodeType;
import org.eclipse.winery.common.servicetemplate.boundarydefinitions.reqscaps.ReqCapInfo;
import org.eclipse.winery.common.servicetemplate.boundarydefinitions.reqscaps.ReqsCapsRequest;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities;
import org.eclipse.winery.model.tosca.TNodeTemplate.Requirements;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.AbstractRepositoryTest;
import org.eclipse.winery.repository.ext.repository.entity.NodeTemplateDetail;
import org.eclipse.winery.repository.ext.serviceInfo.ServiceTemplateInfo;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.BoundaryDefinitionsResource;
import org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.BoundaryPropertyDefinitionsResource;
import org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.reqscaps.CapabilitiesResource;
import org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.reqscaps.RequirementsResource;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

/**
 * @author 10186401
 *
 */
public class ServiceTemplatesResourceTest extends AbstractRepositoryTest {

  private ServiceTemplatesResource res = new ServiceTemplatesResource();
  private ServiceTemplateResource stRes = this.getServiceTemplateResource(
      "http://www.open-o.org/tosca/nfv/vnf", "test");

   @Test
   public void testGetAll() {
   List<TServiceTemplate> allServiceTemplates = res.getAllServiceTemplates();
   assertEquals(1, allServiceTemplates.size());
  
   TServiceTemplate st = allServiceTemplates.get(0);
   assertEquals("test", st.getName());
   }
  
   @Test
   public void testOnPost() {
   Collection<AbstractComponentInstanceResource> before = res.getAll();
   assertEquals(1, before.size());
  
   QName type = new QName("http://www.open-o.org/tosca/nfv/vnf", "test2");
   res.onPost("http://www.open-o.org/tosca/nfv/vnf", "test2", null);
  
   Collection<AbstractComponentInstanceResource> after = res.getAll();
   assertEquals(2, after.size());
  
   addClearUpIds(new ServiceTemplateId(type));
   }
  
   @Test
   public void testGetListOfAllIds() {
   String idsStr = res.getListOfAllIds();
   Object[] listOfAllIds = new Gson().fromJson(idsStr, Object[].class);
   assertEquals(1, listOfAllIds.length);
   }

  public ServiceTemplateResource getServiceTemplateResource(String ns, String name) {
    QName type = new QName(ns, name);
    ServiceTemplateResource stRes = (ServiceTemplateResource) res.getComponentInstaceResource(type);
    return stRes;
  }

  @Test
  public void testExport() {
    stRes.getCSARDeploy("deploy", null, null);
  }

  @Test
  public void testGetName() {
    assertEquals("test", stRes.getName());
  }

  @Test
  public void testGetSubstitutableNodeType() {
    QName substitutableNodeType = stRes.getSubstitutableNodeType();
    assertEquals("tosca.nodes.nfv.ext.zte.VNF.test", substitutableNodeType.getLocalPart());
  }

  @Test
  public void testDelSubstitutableNodeType() {
    stRes.deleteSubstitutableNodeType();
    QName substitutableNodeType = stRes.getSubstitutableNodeType();
    Assert.assertNull(substitutableNodeType);

    setSubstitutableNodeType();
  }

  @Test
  public void setSubstitutableNodeType() {
    SubstitutableNodeType type = new SubstitutableNodeType();
    type.setName("tosca.nodes.nfv.ext.zte.VNF.test");
    type.setNamespace("http://www.open-o.org/tosca/nfv/vnf");
    stRes.setSubstitutableNodeType(type);

    QName substitutableNodeType = stRes.getSubstitutableNodeType();
    assertEquals("tosca.nodes.nfv.ext.zte.VNF.test", substitutableNodeType.getLocalPart());
  }

  @Test
  public void testCopyServiceTemplate() {
    ServiceTemplateResource testRes = createNewByCopy();
    TServiceTemplate serviceTemplate = testRes.getServiceTemplate();
    BoundaryPropertyDefinition def =
        BoundaryPropertyUtil.getBoundaryPropertyDefinition(serviceTemplate.getBoundaryDefinitions()
            .getProperties().getAny());
    assertEquals("1", def.getMetaData("name"));
    assertEquals("2", def.getMetaData("vnfd_version"));

    List<TEntityTemplate> nodeOrRelationshipTemplate =
        serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
    assertEquals(5, nodeOrRelationshipTemplate.size());

    addClearUpIds(testRes.getId());
    addClearUpIds(new NodeTypeId(new QName("http://www.open-o.org/tosca/nfv/vnf",
        "tosca.nodes.nfv.ext.zte.VNF.test2")));
  }

  @Test
  public void testInitServiceTemplate() {
    ServiceTemplateInfo info = buildServiceTemplateInfo();
    ServiceTemplateResource testRes = initNew(info);

    BoundaryDefinitionsResource boundaryDefinitionsResource =
        testRes.getBoundaryDefinitionsResource();
    BoundaryPropertyDefinitionsResource boundaryPropertiesResource =
        boundaryDefinitionsResource.getPropertiessResource();
    testInputs(boundaryPropertiesResource);
    testMetadatas(boundaryPropertiesResource);

    addClearUpIds(testRes.getId());
  }

  private ServiceTemplateResource createNewByCopy() {
    ServiceTemplateInfo info = new ServiceTemplateInfo();
    info.setCopyId("test");
    info.setCopyNameSpace("http://www.open-o.org/tosca/nfv/vnf");
    return initNew(info);
  }

  private ServiceTemplateResource initNew(ServiceTemplateInfo info) {
    res.onPost("http://www.open-o.org/tosca/nfv/vnf", "test2", null);
    ServiceTemplateResource testRes =
        getServiceTemplateResource("http://www.open-o.org/tosca/nfv/vnf", "test2");
    testRes.initServiceTemplate(info);
    return testRes;
  }

  @Test
  public void testExtendProperties() {
    ServiceTemplateResource testRes = createNewByCopy();

    ServiceTemplateInfo info = buildServiceTemplateInfo();
    testRes.setServiceTemplateProperties(info);
    testRes.getExtendProperties();

    testDoc(testRes);
    BoundaryDefinitionsResource boundaryDefinitionsResource =
        testRes.getBoundaryDefinitionsResource();
    BoundaryPropertyDefinitionsResource boundaryPropertiesResource =
        boundaryDefinitionsResource.getPropertiessResource();
    testInputs(boundaryPropertiesResource);
    testMetadatas(boundaryPropertiesResource);
    testBoundaryCapabilites(boundaryDefinitionsResource.getCapabilitiesResource());
    testBoundaryRequirements(boundaryDefinitionsResource.getRequiremensResource());

    addClearUpIds(testRes.getId());
    addClearUpIds(new NodeTypeId(new QName("http://www.open-o.org/tosca/nfv/vnf",
        "tosca.nodes.nfv.ext.zte.VNF.test2")));
  }

  private void testBoundaryRequirements(RequirementsResource requiremensResource) {
    // TODO Auto-generated method stub
  }

  private void testBoundaryCapabilites(CapabilitiesResource capabilitiesResource) {
    // TODO Auto-generated method stub

  }

  private void testMetadatas(BoundaryPropertyDefinitionsResource boundaryPropertiesResource) {
    List<MetaData> metaDatas = boundaryPropertiesResource.getMetaDatas();
    assertEquals(1, metaDatas.size());
    MetaData metaData = metaDatas.get(0);
    assertEquals("name", metaData.getKey());
    assertEquals("metadata", metaData.getTag());
    assertEquals("required", metaData.getRequired());
    assertEquals("test2", metaData.getValue());
  }

  private void testInputs(BoundaryPropertyDefinitionsResource boundaryPropertiesResource) {
    List<Input> inputs = boundaryPropertiesResource.getInputs();
    assertEquals(1, inputs.size());
    Input input = inputs.get(0);
    assertEquals("desc", input.getDesc());
    assertEquals("input", input.getName());
    assertEquals("tag", input.getTag());
    assertEquals("string", input.getType());
    assertEquals("value", input.getValue());
  }

  private void testDoc(ServiceTemplateResource testRes) {
    List<TDocumentation> documentation = testRes.getServiceTemplate().getDocumentation();
    assertEquals(1, documentation.size());
    Object desc = documentation.get(0).getContent().get(0);
    assertEquals("description", desc);
  }

  private ServiceTemplateInfo buildServiceTemplateInfo() {

    TRequirement nodeReq = new TRequirement();
    nodeReq.setId("nodeReqId");
    nodeReq.setName("nodeReqName");
    nodeReq.setType(new QName("http://www.open-o.org/tosca/nfv",
        "tosca.requirements.nfv.VirtualLink"));
    Requirements nodeReqs = new Requirements();
    nodeReqs.getRequirement().add(nodeReq);

    TCapability nodeCap = new TCapability();
    nodeCap.setId("nodeCapId");
    nodeCap.setName("nodeCapName");
    nodeCap.setType(new QName("http://www.open-o.org/tosca/nfv",
        "tosca.capabilities.nfv.VirtualLinkable"));
    Capabilities nodeCaps = new Capabilities();
    nodeCaps.getCapability().add(nodeCap);

    NodeTemplateDetail node = new NodeTemplateDetail();
    node.setId("nodeId");
    node.setName("node");
    node.setType(new QName("http://www.open-o.org/tosca/nfv", "tosca.nodes.nfv.ext.zte.VDU"));
    node.setRequirements(nodeReqs);
    node.setCapabilities(nodeCaps);
    List<NodeTemplateDetail> nodeTemplates = new ArrayList<NodeTemplateDetail>();
    nodeTemplates.add(node);

    TRelationshipTemplate relation = new TRelationshipTemplate();
    relation.setId("relationId");
    relation.setName("relation");
    relation.setType(new QName("http://www.open-o.org/tosca/nfv",
        "tosca.relationships.nfv.VirtualLinksTo"));
    List<TRelationshipTemplate> relationTemplates = new ArrayList<TRelationshipTemplate>();
    relationTemplates.add(relation);

    MetaData metadata = new MetaData();
    metadata.setKey("name");
    metadata.setRequired("required");
    metadata.setTag("metadata");
    metadata.setValue("test2");
    List<MetaData> metadataList = new ArrayList<MetaData>();
    metadataList.add(metadata);
    MetaDatas metaDatas = new MetaDatas();
    metaDatas.setMetadatas(metadataList);

    Input input = new Input();
    input.setDesc("desc");
    input.setName("input");
    input.setTag("tag");
    input.setType("string");
    input.setValue("value");
    List<Input> inputList = new ArrayList<Input>();
    inputList.add(input);
    Inputs inputs = new Inputs();
    inputs.setInputs(inputList);

    // Flavor flavor = new Flavor();
    // flavor.setDesc("desc");
    // flavor.setName("flavor");
    // List<Flavor> flavorList = new ArrayList<Flavor>();
    // flavorList.add(flavor);
    // Flavors flavors = new Flavors();
    // flavors.setFlavors(flavorList);

    ReqCapInfo reqInfo = new ReqCapInfo();
    reqInfo.setName("stReq");
    reqInfo.setNodeName("node");
    reqInfo.setRef("nodeReq");
    reqInfo.setRefName("nodeReqName");
    List<ReqCapInfo> reqList = new ArrayList<ReqCapInfo>();
    reqList.add(reqInfo);
    ReqsCapsRequest requirments = new ReqsCapsRequest();
    requirments.setReqcapList(reqList);

    ReqCapInfo capInfo = new ReqCapInfo();
    capInfo.setName("stCap");
    capInfo.setNodeName("node");
    capInfo.setRef("nodeCap");
    capInfo.setRefName("nodeCapName");
    List<ReqCapInfo> capList = new ArrayList<ReqCapInfo>();
    capList.add(capInfo);
    ReqsCapsRequest capabilities = new ReqsCapsRequest();
    capabilities.setReqcapList(capList);

    ServiceTemplateInfo info = new ServiceTemplateInfo();
    info.setDocumentation("description");
    info.setMetaDatas(metaDatas);
    info.setInputs(inputs);
    // info.setFlavors(flavors);
    info.setRequirments(requirments);
    info.setCapabilities(capabilities);
    info.setNodeTemplates(nodeTemplates);
    info.setRelationTemplates(relationTemplates);

    return info;
  }

}
