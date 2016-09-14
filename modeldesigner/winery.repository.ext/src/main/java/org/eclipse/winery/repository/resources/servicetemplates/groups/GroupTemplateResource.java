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
package org.eclipse.winery.repository.resources.servicetemplates.groups;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TGroupTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTarget;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.resources.entitytemplates.TEntityTemplateResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

/**
 * @author 10186401
 *
 */
public class GroupTemplateResource extends TEntityTemplateResource<TGroupTemplate> {

  public GroupTemplateResource(IIdDetermination<TGroupTemplate> idDetermination, TGroupTemplate o,
      int idx, List<TGroupTemplate> list, IPersistable res) {
    super(idDetermination, o, idx, list, res);
  }

  @POST
  @Path("target/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response onPost(String target) {
    if (null == target) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    TTarget groupTargets = this.o.getTargets();
    if (null == groupTargets) {
      groupTargets = new TTarget();
      this.o.setTargets(groupTargets);
    }

    if (null != target && !target.isEmpty()) {
      groupTargets.getTarget().add(target);
    }

    BackendUtils.persist(this.res);
    return Response.status(Status.CREATED).build();
  }

  @POST
  @Path("targets/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response onPost(List<String> targets) {
    if (null == targets || targets.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    TTarget groupTargets = new TTarget();
    groupTargets.getTarget().addAll(targets);
    this.o.setTargets(groupTargets);

    BackendUtils.persist(this.res);
    return Response.status(Status.CREATED).build();
  }

  @GET
  @Path("targets/")
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> queryTargets() {
    TTarget groupTargets = this.o.getTargets();
    if (null == groupTargets) {
      return null;
    }
    return groupTargets.getTarget();
  }

  @DELETE
  @Path("target/")
  public Response onDelete(String target) {
    if (null == target) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    TTarget groupTargets = this.o.getTargets();
    if (null == groupTargets) {
      return null;
    }
    List<String> targets = groupTargets.getTarget();
    for (Iterator<String> it = targets.iterator(); it.hasNext();) {
      String string = it.next();
      if (string.equals(target)) {
        it.remove();
      }
    }

    return BackendUtils.persist(this.res);
  }

  @DELETE
  @Path("targets/")
  public Response clearTargets() {
    this.o.setTargets(null);
    return BackendUtils.persist(this.res);
  }

  @DELETE
  @Path("groupandtargets/")
  public Response deleteGroupTemplateAndTargets() {
    deleteTargetsTemplates();
    this.onDelete();
    return BackendUtils.persist(this.res);
  }

  private void deleteTargetsTemplates() {
    List<String> targets = this.queryTargets();
    List<TEntityTemplate> templates = getNodeOrRelation();
    String templateName = null;
    if (null != targets && !targets.isEmpty() && null != templates && !templates.isEmpty()) {
      for (String target : targets) {
        for (Iterator<TEntityTemplate> iterator = templates.iterator(); iterator.hasNext();) {
          TEntityTemplate tEntityTemplate = iterator.next();
          if (tEntityTemplate instanceof TNodeTemplate) {
            templateName = ((TNodeTemplate) tEntityTemplate).getName();
          } else if (tEntityTemplate instanceof TRelationshipTemplate) {
            templateName = ((TRelationshipTemplate) tEntityTemplate).getName();
          }
          if (target.equals(templateName)) {
            iterator.remove();
          }
        }
      }
    }
  }

  private List<TEntityTemplate> getNodeOrRelation() {
    TServiceTemplate serviceTemplate =
        (TServiceTemplate) ((ServiceTemplateResource) res).getElement();
    List<TEntityTemplate> templates =
        serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
    return templates;
  }
}
