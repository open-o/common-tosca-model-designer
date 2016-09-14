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
package org.eclipse.winery.repository.resources.servicetemplates.plans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;  
import java.io.FileInputStream;  
import java.util.zip.ZipEntry;  
import java.util.zip.ZipFile;  
import java.util.zip.ZipInputStream; 

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;
import org.restdoc.annotations.RestDoc;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import org.w3c.dom.*;

public class PlanFileResource {
	
	private final PlanId planId;
	private TPlan plan;
	private ServiceTemplateResource res;
	
	
	public PlanFileResource(ServiceTemplateResource res, PlanId planId, TPlan plan) {
		this.res = res;
		this.planId = planId;
		this.plan = plan;
	}
	
	/**
	 * Extracts the file reference from plan's planModelReference
	 */
	private RepositoryFileReference getFileRef() {
		String reference = this.plan.getPlanModelReference().getReference();
		File f = new File(reference);
		return new RepositoryFileReference(this.planId, f.getName());
	}
	
	@PUT
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	@RestDoc(methodDescription = "Resource currently works for BPMN4TOSCA plans only")
	// @formatter:off
	public Response onPutFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("file") FormDataBodyPart body
	) {
	// @formatter:on
		
		String fileName = fileDetail.getFileName();
		RepositoryFileReference ref = new RepositoryFileReference(this.planId, fileName);
		RepositoryFileReference oldRef = this.getFileRef();
		boolean persistanceNecessary;
		if (ref.equals(oldRef)) {
			// nothing todo, file will be replaced
			persistanceNecessary = false;
		} else {
			// new filename sent
			BackendUtils.delete(oldRef);
			PlansResource.setPlanModelReference(this.plan, this.planId, fileName);
			persistanceNecessary = true;
		}
		
		// Really store it
		try {
			Repository.INSTANCE.putContentToFile(ref, uploadedInputStream, body.getMediaType());
		} catch (IOException e1) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not store plan. " + e1.getMessage()).build();
		}
		
		if (persistanceNecessary) {
			return BackendUtils.persist(this.res);
		} else {
			return Response.noContent().build();
		}
	}
	
	@PUT
	@Consumes({MediaType.APPLICATION_JSON})
	// @formatter:off
	public Response onPutJSON(InputStream is) {
		RepositoryFileReference ref = this.getFileRef();
		return BackendUtils.putContentToFile(ref, is, MediaType.APPLICATION_JSON_TYPE);
	}

	/**
	 * Returns the stored file.
	 */
	@GET
	public Response getFile(@HeaderParam("If-Modified-Since") String modified) {
		RepositoryFileReference ref = this.getFileRef();
		return BackendUtils.returnRepoPath(ref, modified);
	}
	
	
	 /**
     * Returns the stored wsdl json.
     */
    public Response getWsdl() {
        try {
            StringBuilder jsonRes = new StringBuilder("[");
            RepositoryFileReference ref = this.getFileRef();
            String planPath = Repository.INSTANCE.getRelativePath(ref);
            
            ZipFile zf = new ZipFile(new File(planPath)); 
            InputStream in = new BufferedInputStream(new FileInputStream(new File(planPath)));  
            ZipInputStream zin = new ZipInputStream(in);  
            ZipEntry ze;  
            
            while ((ze = zin.getNextEntry()) != null) {  
                if (ze.isDirectory()) {
                } 
                else {  
                    if(-1 < ze.getName().indexOf(".wsdl") && 0 > ze.getName().indexOf("/"))
                    {
                        InputStream is = zf.getInputStream(ze);
                        
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        
                        Document document = db.parse(is);
                        NodeList list = document.getElementsByTagName("schema");
                        
                        Element ent1 = (Element)list.item(0);
                        jsonRes.append("{\"namespace\":\"" + ent1.getAttribute("targetNamespace") +"\"},");
                        
                        Element ent2 = (Element)ent1.getElementsByTagName("element").item(0);
                        jsonRes.append("{\"request\":\"" + ent2.getAttribute("name") +"\"},");
                        jsonRes.append("{\"paraname\":{");
                        NodeList list1 = ent2.getElementsByTagName("element");
                        
                        int len = list1.getLength();
                        for (int j = 0; j < len; j++)
                        {
                            Element ent3 = (Element)list1.item(j);
                            jsonRes.append("\"" + ent3.getAttribute("name") + "\":{\"type\":\"" 
                                + ent3.getAttribute("type") +"\",\"value\":\"\"}");
                            if((len - 1) != j)
                            {
                                jsonRes.append(",");
                            }
                        }
                        jsonRes.append("}}]");
                    }
                }  
            }  
            zin.closeEntry();  
            
            return Response.ok().entity(jsonRes.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("getWsdl Could not read plan. " + e.getMessage()).build();
        } 
        
    }
}
