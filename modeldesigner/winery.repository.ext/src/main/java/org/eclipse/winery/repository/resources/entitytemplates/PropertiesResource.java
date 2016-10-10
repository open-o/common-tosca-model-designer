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

package org.eclipse.winery.repository.resources.entitytemplates;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;
import org.w3c.dom.Element;

import com.sun.jersey.api.view.Viewable;

public class PropertiesResource {

    private AbstractComponentInstanceResource res;
    private TEntityTemplate template;


    /**
     * @param template the template to store the definitions at
     * @param res the resource to save after modifications
     */
    public PropertiesResource(TEntityTemplate template, AbstractComponentInstanceResource res) {
        this.template = template;
        this.res = res;
    }

    /**
     * Currently, properties can only be updated as a whole XML fragment
     * 
     * Getting/setting a fragment of properties is not possible yet
     */
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response setProperties(TEntityTemplate.Properties properties) {
        this.getTemplate().setProperties(properties);
        return BackendUtils.persist(this.res);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getHTML() {
        return new Viewable("/jsp/entitytemplates/properties.jsp", this);
    }

    /** data for the JSP **/

    public TEntityTemplate getTemplate() {
        return this.template;
    }

    @POST
    @Path("propertiesmap/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onPost(Map<String, String> properties) {
        if (null == properties || properties.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        String templateNS = ((ServiceTemplateResource) this.res).getNamespace().getDecoded();
        String propNS = templateNS + "/propertiesdefinition/winery";
        Element propRootElement = ModelUtilities.buildPropertiesElement(properties, propNS);
        TEntityTemplate.Properties templateProps = new TEntityTemplate.Properties();
        templateProps.setAny(propRootElement);
        this.template.setProperties(templateProps);
        BackendUtils.persist(this.res);
        return Response.status(Status.CREATED).build();
    }

    @DELETE
    @Path("propertiesmap/")
    public Response clearProps() {
        this.template.setProperties(null);
        return BackendUtils.persist(this.res);
    }

    @GET
    @Path("propertiesmap/")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> queryProperties() {
        TEntityTemplate.Properties templateProps = this.template.getProperties();
        if (null == templateProps) {
            return null;
        }

        Object object = templateProps.getAny();
        if (object instanceof Element) {
            return ModelUtilities.resolvePropertiesElement((Element) object);
        }

        return null;
    }
}
