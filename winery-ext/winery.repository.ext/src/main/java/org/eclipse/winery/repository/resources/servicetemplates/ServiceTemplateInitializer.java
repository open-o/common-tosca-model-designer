/**
 * Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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

import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.boundaryproperty.MetaDatas;
import org.eclipse.winery.common.servicetemplate.ServiceTemplateInfo;
import org.eclipse.winery.common.servicetemplate.SubstitutableNodeType;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.ext.serviceInfo.ServiceInitializer;
import org.eclipse.winery.repository.ext.serviceInfo.ServiceTemplateInfoProvider;
import org.reflections.Reflections;

public class ServiceTemplateInitializer extends ServiceInitializer {

    public ServiceTemplateInitializer(ServiceTemplateInfo info, TServiceTemplate res) {
        super(info, res);
    }

    private static List<ServiceTemplateInfoProvider> providers =
            new ArrayList<ServiceTemplateInfoProvider>();

    @Override
    public void initServiceTemplate() throws Exception {
        createMetaDatas();
        createSubstitutableNodeType();
        createDefaultNodeTemplate();
    }

    private void createMetaDatas() {
        if (this.serviceInfo != null) {
            MetaDatas metaDatas = this.serviceInfo.getMetaDatas();
            if (metaDatas != null) {
                TBoundaryDefinitions boundaryDefinitions = new TBoundaryDefinitions();
                Properties properties = new Properties();
                BoundaryPropertyDefinition def =
                        BoundaryPropertyUtil.getBoundaryPropertyDefinition(properties.getAny());
                def.setMetadatas(metaDatas);
                properties.setAny(def);
                boundaryDefinitions.setProperties(properties);
                serviceTemplate.setBoundaryDefinitions(boundaryDefinitions);
            }
        }
    }

    private void createSubstitutableNodeType() {
        if (this.serviceInfo != null) {
            SubstitutableNodeType sNodeType = this.serviceInfo.getSubstitutableNodeType();
            if (sNodeType != null) {
                QName subNode = new QName(sNodeType.getNamespace(), sNodeType.getName());
                serviceTemplate.setSubstitutableNodeType(subNode);
            }
        }
    }

    private void createDefaultNodeTemplate() throws Exception {
        initProviders();
        if(providers.size() > 0){
            TTopologyTemplate topoTemp = new TTopologyTemplate();
            for (ServiceTemplateInfoProvider provider : providers) {
                topoTemp.getNodeTemplateOrRelationshipTemplate()
                        .addAll(provider.getDefaultNodeTemplates(serviceTemplate));
            }
            serviceTemplate.setTopologyTemplate(topoTemp);
        }
    }

    private static List<ServiceTemplateInfoProvider> initProviders() throws Exception {
        if (providers.size() == 0) {
            Reflections reflections = new Reflections("org.eclipse.winery.repository.ext");
            Set<Class<? extends ServiceTemplateInfoProvider>> providersClasses =
                    reflections.getSubTypesOf(
                            org.eclipse.winery.repository.ext.serviceInfo.ServiceTemplateInfoProvider.class);
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

}
