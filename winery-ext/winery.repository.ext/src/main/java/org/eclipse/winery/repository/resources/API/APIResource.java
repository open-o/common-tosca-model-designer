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
package org.eclipse.winery.repository.resources.API;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.datatypes.select2.Select2DataWithOptGroups;
import org.eclipse.winery.repository.ext.repository.RepositoryResouce;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

public class APIResource {
	
	@GET
	@Path("getallartifacttemplatesofcontaineddeploymentartifacts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllArtifactTemplatesOfContainedDeploymentArtifacts(@QueryParam("servicetemplate") String serviceTemplateQNameString, @QueryParam("nodetemplateid") String nodeTemplateId) {
		if (StringUtils.isEmpty(serviceTemplateQNameString)) {
			return Response.status(Status.BAD_REQUEST).entity("servicetemplate has be given as query parameter").build();
		}
		
		QName serviceTemplateQName = QName.valueOf(serviceTemplateQNameString);
		
		ServiceTemplateId serviceTemplateId = new ServiceTemplateId(serviceTemplateQName);
		if (!Repository.INSTANCE.exists(serviceTemplateId)) {
			return Response.status(Status.BAD_REQUEST).entity("service template does not exist").build();
		}
		ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(serviceTemplateId);
		
		Collection<QName> artifactTemplates = new ArrayList<>();
		List<TNodeTemplate> allNestedNodeTemplates = BackendUtils.getAllNestedNodeTemplates(serviceTemplateResource.getServiceTemplate());
		for (TNodeTemplate nodeTemplate : allNestedNodeTemplates) {
			if (StringUtils.isEmpty(nodeTemplateId) || nodeTemplate.getId().equals(nodeTemplateId)) {
				Collection<QName> ats = BackendUtils.getArtifactTemplatesOfReferencedDeploymentArtifacts(nodeTemplate);
				artifactTemplates.addAll(ats);
			}
		}
		
		// convert QName list to select2 data
		Select2DataWithOptGroups res = new Select2DataWithOptGroups();
		for (QName qName : artifactTemplates) {
			res.add(qName.getNamespaceURI(), qName.toString(), qName.getLocalPart());
		}
		return Response.ok().entity(res.asSortedSet()).build();
	}
	
	/**
	 * Implementation similar to
	 * getAllArtifactTemplatesOfContainedDeploymentArtifacts. Only difference is
	 * "getArtifactTemplatesOfReferencedImplementationArtifacts" instead of
	 * "getArtifactTemplatesOfReferencedDeploymentArtifacts".
	 */
	@GET
	@Path("getallartifacttemplatesofcontainedimplementationartifacts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllArtifactTemplatesOfContainedImplementationArtifacts(@QueryParam("servicetemplate") String serviceTemplateQNameString, @QueryParam("nodetemplateid") String nodeTemplateId) {
		if (StringUtils.isEmpty(serviceTemplateQNameString)) {
			return Response.status(Status.BAD_REQUEST).entity("servicetemplate has be given as query parameter").build();
		}
		QName serviceTemplateQName = QName.valueOf(serviceTemplateQNameString);
		
		ServiceTemplateId serviceTemplateId = new ServiceTemplateId(serviceTemplateQName);
		if (!Repository.INSTANCE.exists(serviceTemplateId)) {
			return Response.status(Status.BAD_REQUEST).entity("service template does not exist").build();
		}
		ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(serviceTemplateId);
		
		Collection<QName> artifactTemplates = new ArrayList<>();
		List<TNodeTemplate> allNestedNodeTemplates = BackendUtils.getAllNestedNodeTemplates(serviceTemplateResource.getServiceTemplate());
		for (TNodeTemplate nodeTemplate : allNestedNodeTemplates) {
			if (StringUtils.isEmpty(nodeTemplateId) || nodeTemplate.getId().equals(nodeTemplateId)) {
				Collection<QName> ats = BackendUtils.getArtifactTemplatesOfReferencedImplementationArtifacts(nodeTemplate);
				artifactTemplates.addAll(ats);
			}
		}
		
		// convert QName list to select2 data
		Select2DataWithOptGroups res = new Select2DataWithOptGroups();
		for (QName qName : artifactTemplates) {
			res.add(qName.getNamespaceURI(), qName.toString(), qName.getLocalPart());
		}
		return Response.ok().entity(res.asSortedSet()).build();
	}
	
    @Path("templates/")
	public RepositoryResouce getRepositoryResource(){
	    return new RepositoryResouce();
	}
	
}
