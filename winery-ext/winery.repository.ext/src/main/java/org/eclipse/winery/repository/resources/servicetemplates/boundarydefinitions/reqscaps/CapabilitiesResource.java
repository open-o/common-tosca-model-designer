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
package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.reqscaps;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityRef;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.CollectionsHelper;
import org.eclipse.winery.repository.resources._support.collections.withoutid.EntityWithoutIdCollectionResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import com.sun.jersey.api.view.Viewable;

/**
 * This class is an adaption from
 * {@link org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.reqscaps.RequirementsResource}
 */
public class CapabilitiesResource
        extends EntityWithoutIdCollectionResource<CapabilityResource, TCapabilityRef> {

    public CapabilitiesResource(IPersistable res, List<TCapabilityRef> refs) {
        super(CapabilityResource.class, TCapabilityRef.class, refs, res);
    }

    @Override
    public Viewable getHTML() {
        throw new IllegalStateException(
                "Not yet required: boundarydefinitions.jsp renders all tab content.");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addNewElement(@FormParam("name") String name, @FormParam("ref") String reference) {
        // Implementation adapted from super addNewElement

        if (reference == null) {
            return Response.status(Status.BAD_REQUEST).entity("A reference has to be provided")
                    .build();
        }

        TCapabilityRef ref = new TCapabilityRef();
        ref.setName(name); // may also be null

        // The XML model fordces us to put a reference to the object and not just the string
        ServiceTemplateResource rs = (ServiceTemplateResource) this.res;
        TCapability resolved = ModelUtilities.resolveCapability(rs.getServiceTemplate(), reference);
        // In case nothing was found: report back to the user
        if (resolved == null) {
            return Response.status(Status.BAD_REQUEST).entity("Reference could not be resolved")
                    .build();
        }

        ref.setRef(resolved);

        // "this.alreadyContains(ref)" cannot be called as this leads to a mappable exception: The
        // data does not contain an id where the given ref attribute may point to

        this.list.add(ref);
        return CollectionsHelper.persist(this.res, this, ref);
    }

    @POST
    @Path("list/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addElements(ReqsCapsRequest request) {

        if (null == request || null == request.getReqcapList()) {
            return Response.status(Status.BAD_REQUEST).entity("request is empty!").build();
        }

        this.list.clear();
        for (ReqCapInfo info : request.getReqcapList()) {
            if (null == info.getName() || null == info.getRef()) {
                continue;
            }

            ServiceTemplateResource rs = (ServiceTemplateResource) this.res;
            TCapability resolved =
                    ModelUtilities.resolveCapability(rs.getServiceTemplate(), info.getRef());
            if (resolved == null) {
                continue;
            }

            TCapabilityRef ref = new TCapabilityRef();
            ref.setName(info.getName());
            ref.setRef(resolved);

            this.list.add(ref);
        }

        return BackendUtils.persist(this.res);
    }
}
