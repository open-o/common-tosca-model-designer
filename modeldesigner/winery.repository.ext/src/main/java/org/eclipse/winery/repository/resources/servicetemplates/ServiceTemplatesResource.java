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
