/*******************************************************************************
 * Copyright (c) 2014-2015 University of Stuttgart.
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

package org.eclipse.winery.repository.resources.servicetemplates.plans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;
import org.restdoc.annotations.RestDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.io.Files;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import de.ustutt.iaas.bpmn2bpel.BPMN4Tosca2BPEL;

public class PlanFileResource {
  private static final Logger logger = LoggerFactory.getLogger(PlanFileResource.class);
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
  public Response onPutFile(@FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail,
      @FormDataParam("file") FormDataBodyPart body) {
    // @formatter:on

    String fileName = fileDetail.getFileName();
    RepositoryFileReference ref = new RepositoryFileReference(this.planId, fileName);
    RepositoryFileReference oldRef = this.getFileRef();
    if (!ref.equals(oldRef)) {
      // new filename sent
      BackendUtils.delete(oldRef);
      PlansResource.setPlanModelReference(this.plan, this.planId, fileName);
    }

    // Really store it
    try {
      Repository.INSTANCE.putContentToFile(ref, uploadedInputStream, body.getMediaType());
      this.convert("bpel");// temporary treatment, for now only support bpel, bpmn4tosca2bpel
    } catch (IOException e1) {
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity("Could not store plan. " + e1.getMessage()).build();
    }

    return BackendUtils.persist(this.res);
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
  @SuppressWarnings("resource")
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
        } else {
          if (-1 < ze.getName().indexOf(".wsdl") && 0 > ze.getName().indexOf("/")) {
            InputStream is = zf.getInputStream(ze);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document document = db.parse(is);
            NodeList list = document.getElementsByTagName("schema");

            Element ent1 = (Element) list.item(0);
            jsonRes.append("{\"namespace\":\"" + ent1.getAttribute("targetNamespace") + "\"},");

            Element ent2 = (Element) ent1.getElementsByTagName("element").item(0);
            jsonRes.append("{\"request\":\"" + ent2.getAttribute("name") + "\"},");
            jsonRes.append("{\"paraname\":{");
            NodeList list1 = ent2.getElementsByTagName("element");

            int len = list1.getLength();
            for (int j = 0; j < len; j++) {
              Element ent3 = (Element) list1.item(j);
              jsonRes.append("\"" + ent3.getAttribute("name") + "\":{\"type\":\""
                  + ent3.getAttribute("type") + "\",\"value\":\"\"}");
              if ((len - 1) != j) {
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
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity("getWsdl Could not read plan. " + e.getMessage()).build();
    }

  }

  public void convert(String type) {
    if ("bpmn".equals(type)) {
      // TODO
    } else {
      // default convert to bpel
      bpmn4tosca2bpel();
    }
  }

  @POST
  @Path("bpmn4tosca2bpel")
  @Produces(MediaType.APPLICATION_JSON)
  public void bpmn4tosca2bpel() {
    RepositoryFileReference ref = this.getFileRef();
    String srcFileArchive = BackendUtils.getPathInsideRepo(ref);
    if (!srcFileArchive.endsWith(".bpmn4tosca") && !srcFileArchive.endsWith("file.json")) {
      logger.info("{} is not a bpmn4tosca file!", srcFileArchive);
      return;
    }

    String repositoryRoot = Repository.INSTANCE.getRepositoryRootPath() + "/";
    String srcFileName = repositoryRoot + srcFileArchive;
    File srcFile = new File(srcFileName);
    if (!srcFile.exists()) {
      logger.info("{} does not exsit!", srcFileArchive);
      return;
    }

    String nameSpace = res.getNamespace().getDecoded();
    String csarName = res.getName();
    String planName = planId.getXmlId().getDecoded();
    String bpelFileName = buildBpelFileName(planName, csarName);

    String tempPath = repositoryRoot + "temp/";
    File tempDir = new File(tempPath);
    if (!tempDir.exists()) {
      tempDir.mkdirs();
    }

    String tempFileName = tempPath + bpelFileName;
    File tempFile = new File(tempFileName);
    if (tempFile.exists()) {
      logger.info("{} has exsited! delete it before converting plan", tempFileName);
      tempFile.delete();
    }

    String targeFileName = getTargetFileName(bpelFileName, repositoryRoot);
    File targetFile = new File(targeFileName);

    try {
      logger.info("begin to convert file {} to bpel!", srcFileArchive);
      new BPMN4Tosca2BPEL().transform(srcFile.toURI(), tempFile.toURI(), bpelFileName, nameSpace,
          csarName + ".csar", nameSpace, csarName);

      if (tempFile.exists()) {
        Files.createParentDirs(targetFile);
        Files.move(tempFile, targetFile);
        this.plan.getOtherAttributes().put(new QName(Constants.PLAN_NAME), bpelFileName);
      }
      logger.info("convert file {} to BPEL successful!", srcFileArchive);
    } catch (Exception e) {
      logger.error("Could not convert file to BPEL", e);
    }
  }

  private String buildBpelFileName(String planName, String csarName) {
    String provider = "csarProvider";
    String version = "csarVersion";
    String type = "csarType";
    TBoundaryDefinitions boundaryDefinitions = res.getServiceTemplate().getBoundaryDefinitions();
    BoundaryPropertyDefinition def =
        BoundaryPropertyUtil.getBoundaryPropertyDefinition(boundaryDefinitions.getProperties()
            .getAny());
    if (null != def) {
      provider = getMetaDataValue(def, provider);
      version = getMetaDataValue(def, version);
      type = getMetaDataValue(def, type);
    }

    return new StringBuffer(planName).append("_").append(type).append("_").append(provider)
        .append("_").append(csarName).append("_").append(version).append(".zip").toString();
  }

  private String getMetaDataValue(BoundaryPropertyDefinition def, String key) {
    String value = def.getMetaData(key);
    return null == value ? key : value;
  }

  private String getTargetFileName(String fileName, String rootPath) {
    RepositoryFileReference fileRef =
        res.getServiceFilesResource().fileName2fileRef(fileName, "/plan", false);
    String targetFileArchive = BackendUtils.getPathInsideRepo(fileRef);
    return rootPath + targetFileArchive;
  }
}
