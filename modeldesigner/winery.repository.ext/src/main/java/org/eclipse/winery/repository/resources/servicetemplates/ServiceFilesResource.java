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
package org.eclipse.winery.repository.resources.servicetemplates;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.servicetemplate.FileInfo;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.datatypes.ids.elements.FileDirectoryId;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

public class ServiceFilesResource {

    private final FileDirectoryId fileDir;

    public ServiceFilesResource(GenericId serviceTemplateid) {
        fileDir = new FileDirectoryId(serviceTemplateid);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response onPost(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("file") FormDataBodyPart body,
            @FormDataParam("path") String path, @Context UriInfo uriInfo) {

        String fileName = fileDetail.getFileName();
        if (StringUtils.isEmpty(fileName)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        RepositoryFileReference ref = this.fileName2fileRef(fileName, path, false);

        // TODO: instead of fixing the media type, we could overwrite the browser's mediatype by
        // using some user configuration
        BufferedInputStream bis = new BufferedInputStream(uploadedInputStream);
        MediaType mediaType = Utils.getFixedMimeType(bis, fileName, body.getMediaType());

        Response response = BackendUtils.putContentToFile(ref, bis, mediaType);
        return response;
    }

    public RepositoryFileReference fileName2fileRef(String fileName, String path, boolean encoded) {
        if (encoded) {
            fileName = Util.URLdecode(fileName);
        }
        RepositoryFileReference ref = new RepositoryFileReference(this.fileDir.path(path), fileName);
        return ref;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileInfo> getJSON() {
        SortedSet<FileInfo> allContainedFiles = Repository.INSTANCE.getAllContainedFiles(this.fileDir);
        return new ArrayList<FileInfo>(allContainedFiles);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onDelete(FileInfo fileInfo) {
        RepositoryFileReference ref = this.fileName2fileRef(fileInfo.getFileName(), fileInfo.getPath(), false);
        try {
            Repository.INSTANCE.forceDelete(ref);
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not remove file").build();
        }
        return Response.status(Status.NO_CONTENT).build();
    }

    public List<FileInfo> getRef() {
        SortedSet<FileInfo> allContainedFiles = Repository.INSTANCE.getAllContainedFiles(this.fileDir);
        for (FileInfo fileInfo : allContainedFiles) {
            fileInfo.setRef(this.fileName2fileRef(fileInfo.getFileName(), fileInfo.getPath(), false));
        }
        return new ArrayList<FileInfo>(allContainedFiles);
    }

    public void importFile(String path, String fileName, InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        String mediaType = Utils.getMimeType(bis, fileName.toString());
        RepositoryFileReference ref = this.fileName2fileRef(fileName, path, false);
        Repository.INSTANCE.putContentToFile(ref, bis, MediaType.valueOf(mediaType));
    }
}
