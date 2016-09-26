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
package org.eclipse.winery.repository.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.importing.CSARImporter;
import org.eclipse.winery.repository.resources.API.APIResource;
import org.eclipse.winery.repository.resources.admin.AdminTopResource;
import org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates.ArtifactTemplatesResource;
import org.eclipse.winery.repository.resources.entitytemplates.policytemplates.PolicyTemplatesResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationsResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationsResource;
import org.eclipse.winery.repository.resources.entitytypes.artifacttypes.ArtifactTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.capabilitytypes.CapabilityTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.grouptypes.GroupTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.policytypes.PolicyTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.relationshiptypes.RelationshipTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.requirementtypes.RequirementTypesResource;
import org.eclipse.winery.repository.resources.imports.ImportsResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplatesResource;
import org.restdoc.annotations.RestDoc;
import org.restdoc.annotations.RestDocParam;
import org.restdoc.annotations.RestDocReturnCode;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * All paths listed here have to be listed in Jersey's filter configuration
 */
@Path("/")
public class MainResource {

    @Path("API/")
    public APIResource api() {
        return new APIResource();
    }

    @Path("artifacttemplates/")
    public ArtifactTemplatesResource artifacttemplates() {
        return new ArtifactTemplatesResource();
    }

    @Path("artifacttypes/")
    public ArtifactTypesResource artifactypes() {
        return new ArtifactTypesResource();
    }

    @Path("admin/")
    public AdminTopResource admin() {
        return new AdminTopResource();
    }

    @Path("capabilitytypes/")
    public CapabilityTypesResource capabilitytypes() {
        return new CapabilityTypesResource();
    }

    @Path("imports/")
    public ImportsResource imports() {
        return new ImportsResource();
    }

    @Path("nodetypes/")
    public NodeTypesResource nodetypes() {
        return new NodeTypesResource();
    }

    @Path("nodetypeimplementations/")
    public NodeTypeImplementationsResource nodetypeimplementations() {
        return new NodeTypeImplementationsResource();
    }

    @Path("other/")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getOtherElements() {
        return new Viewable("/jsp/otherElements.jsp");
    }

    @Path("policytemplates/")
    public PolicyTemplatesResource policytemplates() {
        return new PolicyTemplatesResource();
    }

    @Path("policytypes/")
    public PolicyTypesResource policytypes() {
        return new PolicyTypesResource();
    }

    @Path("relationshiptypes/")
    public RelationshipTypesResource relationshiptypes() {
        return new RelationshipTypesResource();
    }

    @Path("requirementtypes/")
    public RequirementTypesResource requirementtypes() {
        return new RequirementTypesResource();
    }

    @Path("relationshiptypeimplementations/")
    public RelationshipTypeImplementationsResource relationshiptypeimplementations() {
        return new RelationshipTypeImplementationsResource();
    }

    @Path("servicetemplates/")
    public ServiceTemplatesResource servicetemplates() {
        ServiceTemplatesResource resource = new ServiceTemplatesResource();
        return resource;
    }

    @GET
    @Path("BPMN4TOSCAModeler/")
    @Produces(MediaType.TEXT_HTML)
    public Response getHTML() {
        System.out.println("to page BPMN4TOSCAModeler");
        return Response.ok().entity(new Viewable("/BPMN4TOSCAModeler/index.html")).build();
    }

    /**
     * Returns the main page of winery.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response onGet() {
        return Response.temporaryRedirect(Utils.createURI("servicetemplates/")).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RestDoc(methodDescription = "Imports the given CSAR (sent by simplesinglefileupload.jsp)")
    @RestDocReturnCode(
            code = "200",
            description = "If the CSAR could be partially imported, the points where it failed are returned in the body")
    // @formatter:off
    public Response importCSAR(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("overwrite") @RestDocParam(
                    description = "true: content of CSAR overwrites existing content. false (default): existing content is kept") Boolean overwrite) {
        // @formatter:on
        CSARImporter importer = new CSARImporter();
        List<String> errors = new ArrayList<String>();
        boolean ow;
        ow = (overwrite != null) && overwrite;
        try {
            importer.readCSAR(uploadedInputStream, errors, ow, true);
        } catch (Exception e) {
            return Response.serverError().entity("Could not import CSAR").entity(e.getMessage())
                    .build();
        }
        if (errors.isEmpty()) {
            return Response.noContent().build();
        } else {
            // In case there are errors, we send them as "bad request"
            return Response.status(Status.BAD_REQUEST).entity(errors).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importDefinitions(InputStream is) throws IOException {
        File toscaFile;
        toscaFile = File.createTempFile("TOSCA", ".tosca");
        FileUtils.copyInputStreamToFile(is, toscaFile);
        CSARImporter importer = new CSARImporter();
        List<String> errors = new ArrayList<>();
        importer.importDefinitions(null, toscaFile.toPath(), errors, false, true);
        if (errors.isEmpty()) {
            return Response.noContent().build();
        } else {
            return Response.status(Status.BAD_REQUEST).entity(errors).build();
        }
    }

    @Path("grouptypes/")
    public GroupTypesResource grouptypes() {
        return new GroupTypesResource();
    }
}
