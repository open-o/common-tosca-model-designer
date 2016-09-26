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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;

public class ServiceTemplatesResource extends AbstractComponentsResource<ServiceTemplateResource> {
    /**
     * add by QinLihan for test
     * 
     * @return
     */
    @GET
    @Path("all/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TServiceTemplate> getAllServiceTemplates() {

        List<TServiceTemplate> stList = new ArrayList<TServiceTemplate>();

        @SuppressWarnings("unchecked")
        Collection<ServiceTemplateResource> res =
                (Collection<ServiceTemplateResource>) (Collection<?>) this.getAll();
        for (Iterator<ServiceTemplateResource> iterator = res.iterator(); iterator.hasNext();) {
            ServiceTemplateResource resource = iterator.next();
            stList.add(resource.getServiceTemplate());
        }

        return stList;
    }
}
