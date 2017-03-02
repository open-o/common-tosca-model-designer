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
package org.eclipse.winery.repository.resources.servicetemplates.policy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.servicetemplate.FileInfo;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceFilesResource;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class PolicyResource {

  private static final XLogger logger = XLoggerFactory.getXLogger(PolicyResource.class);

  public static final String path = "/Policies";

  private ServiceFilesResource fileResource = null;

  private static final String file_suffix = ".drl";

  public PolicyResource(GenericId serviceTemplateid) {
    fileResource = new ServiceFilesResource(serviceTemplateid);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response onPost(PolicyInfo info) {
    if (StringUtils.isEmpty(info.getName())) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    RepositoryFileReference ref =
        fileResource.fileName2fileRef(info.getName() + file_suffix, path, false);
    BufferedInputStream bis = new BufferedInputStream(IOUtils.toInputStream(info.getContent()));
    MediaType mediaType =
        Utils.getFixedMimeType(bis, info.getName(), MediaType.MULTIPART_FORM_DATA_TYPE);

    BackendUtils.putContentToFile(ref, bis, mediaType);
    String filePath = path + "/" + info.getName() + file_suffix;
    return Response.ok().entity(filePath).build();
  }

  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  public Response onDelete(@QueryParam("fileName") String fileName) {
    RepositoryFileReference ref =
        fileResource.fileName2fileRef(fileName + file_suffix, path, false);
    if (ref == null)
      return Response.status(Status.NOT_FOUND).build();
    try {
      Repository.INSTANCE.forceDelete(ref);
    } catch (IOException e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not remove file").build();
    }
    return Response.status(Status.NO_CONTENT).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getJson(@QueryParam("fileName") String fileName) {
    try {
      RepositoryFileReference ref =
          fileResource.fileName2fileRef(fileName + file_suffix, path, false);
      if (ref == null)
        return Response.status(Status.NOT_FOUND).build();
      InputStream stream = Repository.INSTANCE.newInputStream(ref);
      String fileContent = IOUtils.toString(stream, "UTF-8");
      PolicyInfo result = new PolicyInfo();
      result.setContent(fileContent);
      result.setName(fileName);
      return Response.ok().entity(result).build();
    } catch (IOException e) {
      logger.error("IO exception", e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not get file").build();
    }
  }
  
  @GET
  @Path("list")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getList(){
    List<FileInfo> result = new ArrayList<FileInfo>();
    List<FileInfo> fileInfos =  fileResource.getJSON();
    for(FileInfo info : fileInfos){
      if(info.getPath().contains(path))
        result.add(info);
    }
    return Response.ok().entity(result).build();
  }

}
