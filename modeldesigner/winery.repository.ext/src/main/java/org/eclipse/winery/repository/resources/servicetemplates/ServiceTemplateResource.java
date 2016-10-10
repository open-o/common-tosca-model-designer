/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
/*
 * Modifications Copyright 2016 ZTE Corporation.
 */

package org.eclipse.winery.repository.resources.servicetemplates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.boundaryproperty.Flavors;
import org.eclipse.winery.common.boundaryproperty.Input;
import org.eclipse.winery.common.boundaryproperty.Inputs;
import org.eclipse.winery.common.boundaryproperty.MetaData;
import org.eclipse.winery.common.boundaryproperty.MetaDatas;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.common.ids.elements.PlansId;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.common.servicetemplate.SubstitutableNodeType;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TGroupTemplate;
import org.eclipse.winery.model.tosca.TGroupTemplates;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlan.PlanModelReference;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.ext.overview.ServiceTemplatesOverviewResource;
import org.eclipse.winery.repository.ext.repository.entity.NodeTemplateDetail;
import org.eclipse.winery.repository.ext.serviceInfo.ServiceTemplateInfo;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceWithReferencesResource;
import org.eclipse.winery.repository.resources.IHasName;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.BoundaryDefinitionsResource;
import org.eclipse.winery.repository.resources.servicetemplates.groups.GroupTemplatesResource;
import org.eclipse.winery.repository.resources.servicetemplates.plans.PlansResource;
import org.eclipse.winery.repository.resources.servicetemplates.plans.PlansResourceData;
import org.eclipse.winery.repository.resources.servicetemplates.selfserviceportal.SelfServicePortalResource;
import org.eclipse.winery.repository.resources.servicetemplates.topologytemplates.TopologyTemplateResource;
import org.restdoc.annotations.RestDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sun.jersey.api.NotFoundException;

public class ServiceTemplateResource extends AbstractComponentInstanceWithReferencesResource
        implements IHasName {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTemplateResource.class);


    public ServiceTemplateResource(ServiceTemplateId id) {
        super(id);
    }

    /** sub-resources **/

    @Path("topologytemplate/")
    public TopologyTemplateResource getTopologyTemplateResource() {
        if (this.getServiceTemplate().getTopologyTemplate() == null) {
            // the main service template resource exists
            // default topology template: empty template
            // This eases the JSPs etc. and is valid as a non-existant topology template is equal to
            // an empty one
            this.getServiceTemplate().setTopologyTemplate(new TTopologyTemplate());
        }
        return new TopologyTemplateResource(this);
    }

    @Path("plans/")
    public PlansResource getPlansResource() {
        TPlans plans = this.getServiceTemplate().getPlans();
        if (plans == null) {
            plans = new TPlans();
            this.getServiceTemplate().setPlans(plans);
        }
        return new PlansResource(plans.getPlan(), this);
    }

    @GET
    @Path("planslist/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlanlist() {
        TPlans plans = this.getServiceTemplate().getPlans();
        if (plans == null) {
            plans = new TPlans();
            this.getServiceTemplate().setPlans(plans);
        }
        PlansResourceData planlist = new PlansResourceData(plans.getPlan());
        return Response.ok(planlist.getEmbeddedPlansTableData()).build();
    }


    @Path("selfserviceportal/")
    public SelfServicePortalResource getSelfServicePortalResource() {
        return new SelfServicePortalResource(this);
    }

    @Path("boundarydefinitions/")
    public BoundaryDefinitionsResource getBoundaryDefinitionsResource() {
        TBoundaryDefinitions boundaryDefinitions =
                this.getServiceTemplate().getBoundaryDefinitions();
        if (boundaryDefinitions == null) {
            boundaryDefinitions = new TBoundaryDefinitions();
            this.getServiceTemplate().setBoundaryDefinitions(boundaryDefinitions);
        }
        return new BoundaryDefinitionsResource(this, boundaryDefinitions);
    }

    @Override
    public String getName() {
        String name = this.getServiceTemplate().getName();
        if (name == null) {
            // place default
            name = this.getId().getXmlId().getDecoded();
        }
        return name;
    }

    @Override
    public Response setName(String name) {
        this.getServiceTemplate().setName(name);
        return BackendUtils.persist(this);
    }

    // @formatter:off
    @GET
    @RestDoc(
            methodDescription = "Returns the associated node type, which can be substituted by this service template.<br />"
                    + "@return a QName of the form {namespace}localName is returned.")
    @Path("substitutableNodeType")
    @Produces(MediaType.TEXT_PLAIN)
    // @formatter:on
    public Response getSubstitutableNodeTypeAsResponse() {
        QName qname = this.getServiceTemplate().getSubstitutableNodeType();
        if (qname == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(qname.toString()).build();
        }
    }

    /**
     * 
     * @return null if there is no substitutable node type
     */
    public QName getSubstitutableNodeType() {
        return this.getServiceTemplate().getSubstitutableNodeType();
    }

    @DELETE
    @RestDoc(methodDescription = "Removes the association to substitutable node type")
    @Path("substitutableNodeType")
    public Response deleteSubstitutableNodeType() {
        this.getServiceTemplate().setSubstitutableNodeType(null);
        BackendUtils.persist(this);
        return Response.noContent().build();
    }

    public TServiceTemplate getServiceTemplate() {
        return (TServiceTemplate) this.getElement();
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TServiceTemplate();
    }

    @Override
    protected void copyIdToFields() {
        this.getServiceTemplate().setId(this.getId().getXmlId().getDecoded());
        this.getServiceTemplate().setTargetNamespace(this.getId().getNamespace().getDecoded());
    }

    /**
     * Synchronizes the known plans with the data in the XML. When there is a stored file, but no
     * known entry in the XML, we guess "BPEL" as language and "build plan" as type.
     * 
     * @throws IOException
     */
    @Override
    public void synchronizeReferences() {
        // locally stored plans
        TPlans plans = this.getServiceTemplate().getPlans();

        // plans stored in the repository
        PlansId plansContainerId = new PlansId((ServiceTemplateId) this.getId());
        SortedSet<PlanId> nestedPlans =
                Repository.INSTANCE.getNestedIds(plansContainerId, PlanId.class);

        Set<PlanId> plansToAdd = new HashSet<PlanId>();
        plansToAdd.addAll(nestedPlans);

        if (nestedPlans.isEmpty()) {
            if (plans == null) {
                // data on the file system equals the data -> no plans
                return;
            } else {
                // we have to check for equality later
            }
        }

        if (plans == null) {
            plans = new TPlans();
            this.getServiceTemplate().setPlans(plans);
        }

        for (Iterator<TPlan> iterator = plans.getPlan().iterator(); iterator.hasNext();) {
            TPlan plan = iterator.next();
            if (plan.getPlanModel() != null) {
                // in case, a plan is directly contained in a Model element, we do not need to do
                // anything
                continue;
            }
            PlanModelReference planModelReference = plan.getPlanModelReference();
            if ((planModelReference = plan.getPlanModelReference()) != null) {
                String ref = planModelReference.getReference();
                if ((ref == null) || ref.startsWith("../")) {
                    // references to local plans start with "../"
                    // special case (due to errors in the importer): empty PlanModelReference field
                    if (plan.getId() == null) {
                        // invalid plan entry: no id.
                        // we remove the entry
                        iterator.remove();
                        continue;
                    }
                    PlanId planId = new PlanId(plansContainerId, new XMLId(plan.getId(), false));
                    if (nestedPlans.contains(planId)) {
                        // everything allright
                        // we do NOT need to add the plan on the HDD to the XML
                        plansToAdd.remove(planId);
                    } else {
                        // no local storage for the plan, we remove it from the XML
                        iterator.remove();
                    }
                }
            }
        }

        // add all plans locally stored, but not contained in the XML, as plan element to the plans
        // of the service template.
        List<TPlan> thePlans = plans.getPlan();
        for (PlanId planId : plansToAdd) {
            SortedSet<RepositoryFileReference> files =
                    Repository.INSTANCE.getContainedFiles(planId);
            if (files.size() != 1) {
                throw new IllegalStateException("Currently, only one file per plan is supported.");
            }
            RepositoryFileReference ref = files.iterator().next();

            TPlan plan = new TPlan();
            plan.setId(planId.getXmlId().getDecoded());
            plan.setName(planId.getXmlId().getDecoded());
            plan.setPlanType(org.eclipse.winery.repository.Constants.TOSCA_PLANTYPE_BUILD_PLAN);
            plan.setPlanLanguage(org.eclipse.winery.common.constants.Namespaces.URI_BPEL20_EXECUTABLE);

            // create a PlanModelReferenceElement pointing to that file
            String path = Utils.getURLforPathInsideRepo(BackendUtils.getPathInsideRepo(ref));
            // path is relative from the definitions element
            path = "../" + path;
            PlanModelReference pref = new PlanModelReference();
            pref.setReference(path);

            plan.setPlanModelReference(pref);
            thePlans.add(plan);
        }

        try {
            this.persist();
        } catch (IOException e) {
            throw new IllegalStateException("Could not persist resource", e);
        }
        return;
    }

    /**
     * add by qinlihan create substitution_mappings
     * 
     * @param namespace
     * @param name
     * @return
     */
    @POST
    @Path("substitutableNodeType")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setSubstitutableNodeType(SubstitutableNodeType request) {
        QName subNode = new QName(request.getNamespace(), request.getName());
        this.getServiceTemplate().setSubstitutableNodeType(subNode);
        return BackendUtils.persist(this);
    }

    @POST
    @Path("initialInformations")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response initServiceTemplate(ServiceTemplateInfo request) {
        ServiceTemplateInitializer sti =
                new ServiceTemplateInitializer(request, this.getServiceTemplate());
        try {
            sti.initServiceTemplate();
            return BackendUtils.persist(this);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                BackendUtils.persist(this);
                return ((NotFoundException) e).getResponse();
            }
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("extendProperties")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setServiceTemplateProperties(ServiceTemplateInfo serviceInfo) {
        updateBoundaryDefinitions(serviceInfo);
        updateDocumentations(serviceInfo);
        updateSubstitutableNodeType();
        return BackendUtils.persist(this);
    }

    @GET
    @Path("extendProperties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExtendProperties() {
        ServiceTemplateInfo exproperties = new ServiceTemplateInfo();
        List<TDocumentation> docs = this.getServiceTemplate().getDocumentation();
        if (docs.size() > 0) {
            TDocumentation doc = docs.get(0);
            List<Object> contents = doc.getContent();
            if (contents.size() > 0)
                exproperties.setDocumentation((String) doc.getContent().get(0));
        }
        return Response.ok().entity(exproperties).build();
    }

    private void updateSubstitutableNodeType() {
        QName qname = this.getSubstitutableNodeType();
        if (qname != null) {
            NodeTypesResource res = new NodeTypesResource();
            NodeTypeResource nodetypeRes =
                    res.getComponentInstaceResource(Util.URLencode(qname.getNamespaceURI()),
                            qname.getLocalPart());
            TNodeType nodeType = (TNodeType) nodetypeRes.getEntityType();

            QName parentQname = nodeType.getDerivedFrom().getTypeRef();
            NodeTypeResource parentNodetypeRes =
                    res.getComponentInstaceResource(Util.URLencode(parentQname.getNamespaceURI()),
                            parentQname.getLocalPart());
            TNodeType parentNodeType = (TNodeType) parentNodetypeRes.getEntityType();
            nodeType.setRequirementDefinitions(parentNodeType.getRequirementDefinitions());
            nodeType.setCapabilityDefinitions(parentNodeType.getCapabilityDefinitions());

            WinerysPropertiesDefinition wpd = new WinerysPropertiesDefinition();
            wpd.setElementName("Properties");
            wpd.setNamespace(this.getServiceTemplate().getTargetNamespace()
                    + "/propertiesdefinition/winery");
            PropertyDefinitionKVList list = new PropertyDefinitionKVList();

            TBoundaryDefinitions bd = this.getServiceTemplate().getBoundaryDefinitions();
            BoundaryPropertyDefinition boundaryPropertyDefinition =
                    BoundaryPropertyUtil.getBoundaryPropertyDefinition(bd.getProperties().getAny());

            Inputs inputs = boundaryPropertyDefinition.getInputs();
            List<Input> inputList = new ArrayList<Input>();
            if (inputs != null)
                inputList = (List<Input>) inputs.getInputs();

            MetaDatas metaDatas = boundaryPropertyDefinition.getMetadatas();
            List<MetaData> metadataList = new ArrayList<MetaData>();
            if (metaDatas != null)
                metadataList = metaDatas.getMetadatas();

            if (inputList != null) {
                for (Input input : inputList) {
                    PropertyDefinitionKV kv = new PropertyDefinitionKV();
                    kv.setKey(input.getName());
                    kv.setType("xsd:string");
                    kv.setValue(input.getValue());
                    kv.setTag(input.getTag());
                    list.add(kv);
                }
            }

            if (metadataList != null) {
                for (MetaData data : metadataList) {
                    PropertyDefinitionKV kv = new PropertyDefinitionKV();
                    kv.setKey(data.getKey());
                    kv.setType("xsd:string");
                    kv.setTag(data.getTag());
                    kv.setValue(data.getValue());
                    kv.setRequired(data.getRequired());
                    list.add(kv);
                }
            }

            wpd.setPropertyDefinitionKVList(list);
            ModelUtilities.replaceWinerysPropertiesDefinition(nodeType, wpd);

            BackendUtils.persist(nodetypeRes);
        }
    }


    @Path("servicefiles/")
    public ServiceFilesResource getServiceFilesResource() {
        return new ServiceFilesResource(this.id);
    }

    @Override
    protected void deletePreProcess(boolean delSubstitutableNodeType) {
        // if delete service template , delete the SubstitutableNodeType
        TServiceTemplate serviceTemplate = this.getServiceTemplate();
        try {
            if (element instanceof TServiceTemplate) {
                QName subNode = serviceTemplate.getSubstitutableNodeType();
                if (subNode != null && delSubstitutableNodeType) {
                    NodeTypesResource nres = new NodeTypesResource();
                    NodeTypeResource nodetypeRes =
                            nres.getComponentInstaceResource(
                                    Util.URLencode(subNode.getNamespaceURI()),
                                    subNode.getLocalPart());
                    nodetypeRes.onDelete(false);
                }
            }
        } catch (NotFoundException e) {
            logger.info("the node type has already moved.");
        }
    }

    @Override
    protected void deletePostProcess() {
        String namespace = this.id.getNamespace().getDecoded();
        String id = this.id.getXmlId().getDecoded();
        new ServiceTemplatesOverviewResource().delete(namespace, id);
    }

    @Path("grouptemplates/")
    public GroupTemplatesResource getGroupTemplatesResource() {
        TGroupTemplates tGroupTemplates = this.getServiceTemplate().getGroupTemplates();
        if (null == tGroupTemplates) {
            tGroupTemplates = new TGroupTemplates();
            this.getServiceTemplate().setGroupTemplates(tGroupTemplates);
        }

        List<TGroupTemplate> groups = tGroupTemplates.getGroupTemplates();
        return new GroupTemplatesResource(groups, this);
    }
    
  public void updateBoundaryDefinitions(ServiceTemplateInfo serviceInfo){
      if (serviceInfo != null) {
          MetaDatas metaDatas = serviceInfo.getMetaDatas();
          Inputs inputs  = serviceInfo.getInputs();
          Flavors flavors = serviceInfo.getFlavors();
          logger.info("begin to update meta data.");
          TBoundaryDefinitions boundaryDefinitions = this.getServiceTemplate().getBoundaryDefinitions();
          Properties properties = boundaryDefinitions.getProperties();
          BoundaryPropertyDefinition def =
                  BoundaryPropertyUtil.getBoundaryPropertyDefinition(properties.getAny());
          if(metaDatas != null)
             def.setMetadatas(metaDatas);
          if(inputs != null)
            def.setInputs(inputs);
          if(flavors != null)
            def.setFlavors(flavors);
          properties.setAny(def);
          boundaryDefinitions.setProperties(properties);
          this.getServiceTemplate().setBoundaryDefinitions(boundaryDefinitions);
          BackendUtils.persist(this);
      }
    }

    public void updateDocumentations(ServiceTemplateInfo serviceInfo){
      String doc = serviceInfo.getDocumentation();
      if (doc != null) {
          TDocumentation documentation = new TDocumentation();
          documentation.getContent().add(doc);
          this.getServiceTemplate().getDocumentation().clear();
          this.getServiceTemplate().getDocumentation().add(documentation);
      }
      BackendUtils.persist(this);
    }
    
  /**
   * 
   * @param serviceInfo.
   * @return .
   */
  @POST
  @Path("nodeAndRelationTemplates")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateTemplate(ServiceTemplateInfo serviceInfo) {
    TTopologyTemplate topoTemplate = this.getServiceTemplate().getTopologyTemplate();
    if (topoTemplate == null) {
      topoTemplate = new TTopologyTemplate();
    }
    topoTemplate.getNodeTemplateOrRelationshipTemplate().clear();
    List<NodeTemplateDetail> nodetemplates = serviceInfo.getNodeTemplates();
    if (!nodetemplates.isEmpty()) {
      for(TNodeTemplate nodeTemplate : nodetemplates){
        if(nodeTemplate.getProperties() != null){
          Object any = nodeTemplate.getProperties().getAny();
          if(any != null){
            Element element = parseToElement((Map<String,String>)any);
            nodeTemplate.getProperties().setAny(element);
          }
        }      
      }
      topoTemplate.getNodeTemplateOrRelationshipTemplate().addAll(nodetemplates);
    }
    List<TRelationshipTemplate> relationTemplates = serviceInfo.getRelationTemplates();
    if (!relationTemplates.isEmpty()) {
      topoTemplate.getNodeTemplateOrRelationshipTemplate().addAll(relationTemplates);
    }
    this.getServiceTemplate().setTopologyTemplate(topoTemplate);
    return BackendUtils.persist(this);
  }
  
  @GET
  @Path("instances")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSelf(){
    TServiceTemplate service = this.getServiceTemplate();
    List<TEntityTemplate> templates = service.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
    for(TEntityTemplate template : templates){
      if(template instanceof TNodeTemplate){
        TNodeTemplate nodeTemplate = (TNodeTemplate)template;
        if(nodeTemplate.getProperties() != null){
          Object any = nodeTemplate.getProperties().getAny();
          if(any != null){
            Map<String,String> properties = parseElementToMap((Element)any);
            nodeTemplate.getProperties().setAny(properties);
          }
        }
      }
    }
    return Response.ok().entity(service).build();
  }
  
  public Map<String,String> parseElementToMap(Element properties){
    Map<String, String> map = new HashMap<String, String>();
    Element propRootElement = (Element) properties;
    NodeList childList = propRootElement.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
        Node child = childList.item(i);
        if (child.getNodeType() == Node.ELEMENT_NODE) {
            String propertyName = child.getLocalName();
            String propertyValue = child.getTextContent();
            map.put(propertyName, propertyValue);
        }
    }
    return map;
  }
  
  public Element parseToElement(Map<String,String> map){
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = null;
    try {
        db = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
    }
    Document doc = db.newDocument();
    Element root = doc.createElementNS("http://www.zte.com.cn/paas", "paasdefinition");
    doc.appendChild(root);
    for(String key : map.keySet()){
      addRootChildElement(doc, root, key, map.get(key));
    }
    return doc.getDocumentElement();
  }
  
  private void addRootChildElement(Document doc,Element root,String elementKey,String elementValue){
    Element element = doc.createElement(elementKey);
    root.appendChild(element);
    if(null != elementValue){
        Text text = doc.createTextNode(elementValue);
        element.appendChild(text); 
    }
}
}
