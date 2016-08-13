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
package org.eclipse.winery.repository.ext.repository;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplatesResource;

public class RepositoryUtil {
    
    @SuppressWarnings("rawtypes")
    private static TDefinitions getDefiniton(String namespace,String id,Class<? extends AbstractComponentsResource> containerClass){
        Class<? extends TOSCAComponentId> idClass = Utils.getComponentIdClassForComponentContainer(containerClass);
        TOSCAComponentId componetId = BackendUtils.getTOSCAcomponentId(idClass, namespace, id, false);
        AbstractComponentInstanceResource res = AbstractComponentsResource.getComponentInstaceResource(componetId);
        Definitions entryDefinitions = res.getDefinitions();
        return entryDefinitions;
    }
    
    public static TDefinitions getServiceTemplateDefinition(String namespace,String id){
        return getDefiniton(namespace, id, ServiceTemplatesResource.class);
    }
    
}
