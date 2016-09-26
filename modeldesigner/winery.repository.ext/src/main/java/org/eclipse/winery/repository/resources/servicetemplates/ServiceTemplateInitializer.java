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
package org.eclipse.winery.repository.resources.servicetemplates;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.boundaryproperty.MetaDatas;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.servicetemplate.SubstitutableNodeType;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityType.DerivedFrom;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.ext.overview.ServiceTemplateOverviewInfo;
import org.eclipse.winery.repository.ext.overview.ServiceTemplatesOverviewResource;
import org.eclipse.winery.repository.ext.repository.RepositoryUtil;
import org.eclipse.winery.repository.ext.serviceInfo.ServiceInitializer;
import org.eclipse.winery.repository.ext.serviceInfo.ServiceTemplateInfo;
import org.eclipse.winery.repository.ext.serviceInfo.ServiceTemplateInfoProvider;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTemplateInitializer extends ServiceInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTemplateInitializer.class);


    public ServiceTemplateInitializer(ServiceTemplateInfo info, TServiceTemplate res) {
        super(info, res);
    }

    private static List<ServiceTemplateInfoProvider> providers =
            new ArrayList<ServiceTemplateInfoProvider>();

    @Override
    public void initServiceTemplate() throws Exception {
        copyService();
        createMetaDatas();
        createSubstitutableNodeType();
        createDefaultNodeTemplate();
        addSource();
        addOverview();
    }

    private void addSource() {
        String source = null;
        if (serviceInfo.getCopyId() != null && serviceInfo.getCopyNameSpace() != null) {
            source = Constants.TEMPLATE_SOURCE_REPLICA;
        } else {
            source = Constants.TEMPLATE_SOURCE_DERIVED;
        }

        serviceTemplate.getOtherAttributes().put(new QName(Constants.TEMPLATE_SOURCE), source);
    }

    private void addOverview() {
        new ServiceTemplatesOverviewResource()
                .add(new ServiceTemplateOverviewInfo(serviceTemplate));
    }

    private void createMetaDatas() {
        if (this.serviceInfo != null) {
            MetaDatas metaDatas = this.serviceInfo.getMetaDatas();
            if (metaDatas != null) {
                logger.info("begin to create meta data.");
                TBoundaryDefinitions boundaryDefinitions = new TBoundaryDefinitions();
                Properties properties = new Properties();
                BoundaryPropertyDefinition def =
                        BoundaryPropertyUtil.getBoundaryPropertyDefinition(properties.getAny());
                def.setMetadatas(metaDatas);
                properties.setAny(def);
                boundaryDefinitions.setProperties(properties);
                serviceTemplate.setBoundaryDefinitions(boundaryDefinitions);
                logger.info("finish to create meta data.");
            }
        }
    }

    private void createSubstitutableNodeType() {
        if (this.serviceInfo != null) {
            logger.info("begin to create substitutable node type.");
            SubstitutableNodeType sNodeType = this.serviceInfo.getSubstitutableNodeType();
            if (sNodeType != null) {
                QName subNode = new QName(sNodeType.getNamespace(), sNodeType.getName());
                serviceTemplate.setSubstitutableNodeType(subNode);
            }
            logger.info("finish to create substitutable node type.");
        }
    }

    private void createDefaultNodeTemplate() throws Exception {
        initProviders();
        if (providers.size() > 0) {
            logger.info("begin to create default node.");
            TTopologyTemplate topoTemp = serviceTemplate.getTopologyTemplate();
            if (topoTemp == null)
                topoTemp = new TTopologyTemplate();
            for (ServiceTemplateInfoProvider provider : providers) {
                topoTemp.getNodeTemplateOrRelationshipTemplate().addAll(
                        provider.getDefaultNodeTemplates(serviceTemplate));
            }
            serviceTemplate.setTopologyTemplate(topoTemp);
            logger.info("finish to create default node.");
        }
    }

    private static List<ServiceTemplateInfoProvider> initProviders() throws Exception {
        if (providers.size() == 0) {
            Reflections reflections = new Reflections("org.eclipse.winery.repository.ext");
            Set<Class<? extends ServiceTemplateInfoProvider>> providersClasses =
                    reflections
                            .getSubTypesOf(org.eclipse.winery.repository.ext.serviceInfo.ServiceTemplateInfoProvider.class);
            if (providersClasses != null) {
                Iterator<Class<? extends ServiceTemplateInfoProvider>> it =
                        providersClasses.iterator();
                while (it.hasNext()) {
                    Class<? extends ServiceTemplateInfoProvider> implClass =
                            (Class<? extends ServiceTemplateInfoProvider>) it.next();
                    if (!Modifier.isAbstract(implClass.getModifiers())) {
                        providers.add(implClass.newInstance());
                    }
                }
            }
        }
        return providers;
    }

    private void copyService() {
        if (serviceInfo.getCopyId() != null && serviceInfo.getCopyNameSpace() != null) {
            logger.info("begin to copy service,source id is: " + serviceInfo.getCopyId());
            TDefinitions definition =
                    RepositoryUtil.getServiceTemplateDefinition(serviceInfo.getCopyNameSpace(),
                            serviceInfo.getCopyId());
            List<TExtensibleElements> serviceList =
                    definition.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
            TServiceTemplate sourceTemplate = null;
            if (!serviceList.isEmpty()) {
                sourceTemplate = (TServiceTemplate) serviceList.get(0);
                serviceTemplate.setTopologyTemplate(sourceTemplate.getTopologyTemplate());
                serviceTemplate.setBoundaryDefinitions(sourceTemplate.getBoundaryDefinitions());
                replaceSubstitutableNodeType(sourceTemplate.getSubstitutableNodeType());
                serviceTemplate.setPlans(sourceTemplate.getPlans());
                serviceTemplate.setGroupTemplates(sourceTemplate.getGroupTemplates());
            }
            logger.info("copy finished");
        }
    }

    private void replaceSubstitutableNodeType(QName oldNodeTypeName) {
        serviceTemplate.setSubstitutableNodeType(transformSubstitutableNodeType(oldNodeTypeName));
    }

    private QName transformSubstitutableNodeType(QName oldNode) {
        if (oldNode == null)
            return null;
        NodeTypesResource res = new NodeTypesResource();
        NodeTypeResource nodetypeRes =
                res.getComponentInstaceResource(Util.URLencode(oldNode.getNamespaceURI()),
                        oldNode.getLocalPart());
        TNodeType oldNodeType = (TNodeType) nodetypeRes.getEntityType();
        DerivedFrom derivedFrom = oldNodeType.getDerivedFrom();
        QName derivedFromName = derivedFrom.getTypeRef();

        String newNodeTypeLocalPart =
                derivedFromName.getLocalPart() + "." + serviceTemplate.getId();
        NodeTypeId newNodeTypeId =
                new NodeTypeId(derivedFromName.getNamespaceURI(), newNodeTypeLocalPart, false);
        NodeTypeResource newRes = new NodeTypeResource(newNodeTypeId);
        newRes.putDerivedFrom(derivedFromName.toString());
        return newRes.getQName();
    }

}
