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
package org.eclipse.winery.repository.resources.entitytypes.grouptypes;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.ids.definitions.GroupTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TGroupType;
import org.eclipse.winery.model.tosca.TTarget;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.EntityTypeResource;

/**
 * @author 10186401
 *
 */
public class GroupTypeResource extends EntityTypeResource {

    public GroupTypeResource(GroupTypeId id) {
        super(id);
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TGroupType();
    }

    public TGroupType getGroupType() {
        return (TGroupType) this.getElement();
    }

    @POST
    @Path("targets/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onPost(List<String> targets) {
        if (null == targets || targets.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        TTarget groupTargets = getGroupType().getTargets();
        if (null == groupTargets) {
            groupTargets = new TTarget();
            getGroupType().setTargets(groupTargets);
        }
        for (String target : targets) {
            if (null != target && !target.isEmpty()) {
                groupTargets.getTarget().add(target);
            }
        }

        BackendUtils.persist(this);
        return Response.status(Status.CREATED).build();
    }

}
