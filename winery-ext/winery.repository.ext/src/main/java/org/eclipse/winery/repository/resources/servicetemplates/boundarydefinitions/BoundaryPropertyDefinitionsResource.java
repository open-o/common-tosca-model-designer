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
package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.boundaryproperty.Flavors;
import org.eclipse.winery.common.boundaryproperty.Input;
import org.eclipse.winery.common.boundaryproperty.Inputs;
import org.eclipse.winery.common.boundaryproperty.MetaData;
import org.eclipse.winery.common.boundaryproperty.MetaDatas;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;
import org.restdoc.annotations.RestDoc;
import org.restdoc.annotations.RestDocParam;
import org.w3c.dom.Document;

public class BoundaryPropertyDefinitionsResource {
    private final TBoundaryDefinitions.Properties property;
    private final ServiceTemplateResource res;

    public BoundaryPropertyDefinitionsResource(TBoundaryDefinitions.Properties property,
            ServiceTemplateResource res) {
        super();
        this.property = property;
        this.res = res;
    }

    @PUT
    @Consumes(MediaType.TEXT_XML)
    @RestDoc(resourceDescription = "Models the user-defined properties. The property mappings go into a separate resource propertymappings.")
    public Response putProperties(
            @RestDocParam(description = "Stored properties. The XSD allows a single element only. Therefore, we go for the contained element") Document doc) {
        property.setAny(doc.getDocumentElement());
        return BackendUtils.persist(this.res);
    }

    @POST
    @Path("inputs")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addInput(Input input) {
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def) {
            return Response.notModified().build();
        }

        def.addInput(input);
        this.property.setAny(def);
        return BackendUtils.persist(this.res);
    }

    @DELETE
    @Path("inputs")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delInput(@QueryParam(value = "name") String inputName) {
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def) {
            return Response.status(Status.NOT_FOUND).build();
        }

        def.delInput(inputName);
        this.property.setAny(def.rmEmpty());
        return BackendUtils.persist(this.res);
    }

    @GET
    @Path("inputs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Input> getInputs() {
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def || null == def.getInputs()) {
            return null;
        }

        return def.getInputs().getInputs();
    }

    @POST
    @Path("metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMetaData(MetaData metadata) {
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def) {
            return Response.notModified().build();
        }

        def.addMetaData(metadata);
        this.property.setAny(def);
        return BackendUtils.persist(this.res);
    }

    @DELETE
    @Path("metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delMetaData(@QueryParam(value = "key") String key) {
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def) {
            return Response.status(Status.NOT_FOUND).build();
        }

        def.delMetaData(key);
        this.property.setAny(def.rmEmpty());
        return BackendUtils.persist(this.res);
    }

    @GET
    @Path("metadata")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MetaData> getMetaDatas() {
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def || null == def.getMetadatas()) {
            return null;
        }

        return def.getMetadatas().getMetadatas();
    }

    @POST
    @Path("inputs/list")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addInputList(Inputs inputs) {
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def) {
            return Response.notModified().build();
        }

        def.setInputs(inputs);
        this.property.setAny(def);
        return BackendUtils.persist(this.res);
    }
    
    @POST
    @Path("metadata/list")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMetaDataList(MetaDatas metadatas) {
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def) {
            return Response.notModified().build();
        }

        def.setMetadatas(metadatas);
        this.property.setAny(def);
        return BackendUtils.persist(this.res);
    }
    
    @POST
    @Path("flavor/list")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFlavors(Flavors flavors){
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def) {
            return Response.notModified().build();
        }
        
        def.setFlavors(flavors);
        this.property.setAny(def);
        return BackendUtils.persist(this.res);
    }
    
    @GET
    @Path("flavors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlavors(){
        BoundaryPropertyDefinition def =
                BoundaryPropertyUtil.getBoundaryPropertyDefinition(this.property.getAny());
        if (null == def || null == def.getFlavors()|| null == def.getFlavors().getFlavors()) {
            return Response.noContent().build();
        }
        return Response.ok().entity(def.getFlavors().getFlavors()).build();
    
    }
}
