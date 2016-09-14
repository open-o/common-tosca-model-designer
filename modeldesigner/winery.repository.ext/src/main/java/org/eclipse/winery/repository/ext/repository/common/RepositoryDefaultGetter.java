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
package org.eclipse.winery.repository.ext.repository.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.boundaryproperty.Input;
import org.eclipse.winery.common.boundaryproperty.Inputs;
import org.eclipse.winery.common.boundaryproperty.MetaData;
import org.eclipse.winery.common.boundaryproperty.MetaDatas;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityRef;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityTemplate.Properties;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities;
import org.eclipse.winery.model.tosca.TNodeTemplate.Requirements;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.ext.repository.RepositoryService;
import org.eclipse.winery.repository.ext.repository.entity.NodeTemplateDetail;
import org.eclipse.winery.repository.ext.repository.entity.Summary;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplatesResource;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.NotFoundException;

public class RepositoryDefaultGetter extends RepositoryService {

    public static final String SCOPE = "nfv";

    private static final XLogger logger = XLoggerFactory.getXLogger(RepositoryDefaultGetter.class);

    public static final String SEPARATE_COMMA = ",";


    @Override
    public List<TEntityTemplate> getNodeDetails(String[] ids, String type) {
        List<TEntityTemplate> list = new ArrayList<TEntityTemplate>();
        try {
            for (String id : ids) {
                Definitions entryDefinitions =
                        (Definitions) Utils.getDefiniton(type, id, ServiceTemplatesResource.class);
                List<TExtensibleElements> serviceTemplates =
                        entryDefinitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
                TServiceTemplate st = (TServiceTemplate) serviceTemplates.get(0);
                TNodeTemplate template = convert2NodeTemplate(st);
                template.setType(st.getSubstitutableNodeType());
                template.setName(st.getName());
                list.add(template);
            }
        } catch (NotFoundException e) {
            logger.info("can not found node:" + ids.toString());
        }

        return list;
    }

    @Override
    public List<Summary> getSummarys(String type, String filter) {
        return getServiceTemplateSummaries(type, filter);
    }

    private List<Summary> getServiceTemplateSummaries(String type, String filter) {
        List<Summary> list = new ArrayList<Summary>();
        ServiceTemplatesResource serviceTemplatesResource = new ServiceTemplatesResource();
        Class<? extends TOSCAComponentId> cIdClass =
                Utils.getComponentIdClassForComponentContainer(serviceTemplatesResource.getClass());
        SortedSet<? extends TOSCAComponentId> componentInstanceIds =
                Repository.INSTANCE.getAllTOSCAComponentIds(cIdClass);
        String id = null;
        String namespace = null;
        if (filter != null) {
            id = filter.split(SEPARATE_COMMA)[1];
            namespace = filter.split(SEPARATE_COMMA)[0];
        }
        for (TOSCAComponentId componentId : componentInstanceIds) {
            TServiceTemplate st = getServiceTemplate(componentId);
            if (!isCurrentServiceTepmlate(id, namespace, componentId)
                    && isSubstitutableNodeTypeExsist(st)) {
                Summary sumary = new Summary();
                sumary.setId(componentId.getQName().getLocalPart());
                sumary.setName(componentId.getQName().getLocalPart());
                sumary.setType(componentId.getQName().getNamespaceURI());
                sumary.setDescription(this.getDescription(componentId));
                list.add(sumary);
            }
        }
        return list;
    }

    private boolean isCurrentServiceTepmlate(String currentId, String currentNamespace,
            TOSCAComponentId componentId) {
        return componentId.getQName().getLocalPart().equals(currentId)
                && componentId.getQName().getNamespaceURI().equals(currentNamespace);
    }

    private TServiceTemplate getServiceTemplate(TOSCAComponentId componentId) {
        AbstractComponentInstanceResource res =
                AbstractComponentsResource.getComponentInstaceResource(componentId);
        Definitions entryDefinitions = res.getDefinitions();
        List<TExtensibleElements> serviceTemplates =
                entryDefinitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        return (TServiceTemplate) serviceTemplates.get(0);
    }

    private boolean isSubstitutableNodeTypeExsist(TServiceTemplate st) {
        QName qname = st.getSubstitutableNodeType();
        if (qname != null) {
            return getNodeType(qname.getNamespaceURI(), qname.getLocalPart()) != null;
        }
        return false;
    }

    private TEntityType getNodeType(String namespace, String type) {
        try {
            NodeTypesResource res = new NodeTypesResource();
            NodeTypeResource nodetypeRes =
                    res.getComponentInstaceResource(Util.URLencode(namespace), type);
            return nodetypeRes.getEntityType();
        } catch (NotFoundException e) {
            logger.error("can not found node with namespace: " + namespace + " and type: " + type);
        }
        return null;
    }

    private TNodeTemplate convert2NodeTemplate(TServiceTemplate st) {
        NodeTemplateDetail nodeTemplate = new NodeTemplateDetail();
        TBoundaryDefinitions bd = st.getBoundaryDefinitions();
        if (bd != null) {
            convertProperties(nodeTemplate, bd);
            convertRequirements(nodeTemplate, bd);
            convertCapabilities(nodeTemplate, bd);
        }
        List<TDocumentation> docs = st.getDocumentation();
        if (docs != null) nodeTemplate.getDocumentation().addAll(docs);
        return nodeTemplate;
    }

    private void convertProperties(TNodeTemplate nodeTemplate, TBoundaryDefinitions bd) {
        BoundaryPropertyDefinition boundaryPropertyDefinition =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(bd.getProperties().getAny());
        TEntityTemplate.Properties properties = new Properties();
        Inputs inputs = boundaryPropertyDefinition.getInputs();
        List<Input> inputList = new ArrayList<Input>();
        if (inputs != null) inputList = (List<Input>) inputs.getInputs();

        MetaDatas metaDatas = boundaryPropertyDefinition.getMetadatas();
        List<MetaData> metadataList = new ArrayList<MetaData>();
        if (metaDatas != null) metadataList = metaDatas.getMetadatas();

        HashMap<String, String> map = new HashMap<String, String>();

        if (inputList != null) {
            for (Input input : inputList) {
                map.put(input.getName(), input.getValue());
            }
        }

        if (metadataList != null) {
            for (MetaData data : metadataList) {
                map.put(data.getKey(), data.getValue());
            }
        }

        properties.setAny(map);
        nodeTemplate.setProperties(properties);
    }

    private void convertRequirements(TNodeTemplate nodeTemplate, TBoundaryDefinitions bd) {
        TBoundaryDefinitions.Requirements bdRequirements = bd.getRequirements();
        if (bdRequirements != null) {
            TNodeTemplate.Requirements requirements = new Requirements();
            for (TRequirementRef ref : bdRequirements.getRequirement()) {
                TRequirement nodeTemplateReq = new TRequirement();
                TRequirement serviceTemplateReq = (TRequirement) ref.getRef();

                nodeTemplateReq.setId(UUID.randomUUID().toString());
                nodeTemplateReq.setName(ref.getName());
                nodeTemplateReq.setType(serviceTemplateReq.getType());
                Properties reqProps = serviceTemplateReq.getProperties();
                Properties properties = getProperties(reqProps);
                nodeTemplateReq.setProperties(properties);
                requirements.getRequirement().add(nodeTemplateReq);
            }
            nodeTemplate.setRequirements(requirements);
        }
    }



    private void convertCapabilities(TNodeTemplate nodeTemplate, TBoundaryDefinitions bd) {
        TBoundaryDefinitions.Capabilities bdCapabilities = bd.getCapabilities();
        TNodeTemplate.Capabilities capabilities = new Capabilities();
        if (bdCapabilities != null) {
            for (TCapabilityRef ref : bdCapabilities.getCapability()) {
                TCapability nodeTemplateCap = new TCapability();
                TCapability serviceTemplateCap = (TCapability) ref.getRef();

                nodeTemplateCap.setId(UUID.randomUUID().toString());
                nodeTemplateCap.setName(ref.getName());
                nodeTemplateCap.setType(serviceTemplateCap.getType());
                Properties capProps = serviceTemplateCap.getProperties();
                Properties properties = getProperties(capProps);
                nodeTemplateCap.setProperties(properties);

                capabilities.getCapability().add(nodeTemplateCap);
            }
        }
        nodeTemplate.setCapabilities(capabilities);
    }

    private Properties getProperties(Properties props) {
        if (null == props) {
            return null;
        }
        Map<String, String> map = getNodeProperties(props.getAny());
        if (map.isEmpty()) {
            return null;
        }
        Properties properties = new Properties();
        properties.setAny(map);
        return properties;
    }

    private Map<String, String> getNodeProperties(Object properties) {
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
    
    private String getDescription(TOSCAComponentId componentId){
        AbstractComponentInstanceResource res =
                AbstractComponentsResource.getComponentInstaceResource(componentId);
        Definitions entryDefinitions = res.getDefinitions();
        List<TExtensibleElements> serviceTemplates =
                entryDefinitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        TServiceTemplate st = (TServiceTemplate) serviceTemplates.get(0);
        List<TDocumentation> docs= st.getDocumentation();
        if(docs.size() > 0){
            TDocumentation doc = docs.get(0);
            List<Object> contents = doc.getContent();
            if(contents.size() > 0)
               return (String) doc.getContent().get(0);
        }
        return null;
    }

    public String getScope() {
        return SCOPE;
    }

}
