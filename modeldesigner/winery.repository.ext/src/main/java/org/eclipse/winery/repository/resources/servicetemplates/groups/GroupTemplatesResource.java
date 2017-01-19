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
package org.eclipse.winery.repository.resources.servicetemplates.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityTemplate.Properties;
import org.eclipse.winery.model.tosca.TGroupTemplate;
import org.eclipse.winery.model.tosca.TTarget;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.entitytemplates.TEntityTemplatesResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;
import org.w3c.dom.Element;

import com.sun.jersey.api.view.Viewable;

/**
 * @author 10186401
 *
 */
public class GroupTemplatesResource extends
    TEntityTemplatesResource<GroupTemplateResource, TGroupTemplate> {

  public GroupTemplatesResource(List<TGroupTemplate> l, ServiceTemplateResource res) {
    super(GroupTemplateResource.class, TGroupTemplate.class, l, res);
  }

  @Override
  public String getId(TGroupTemplate entity) {
    return entity.getId();
  }

  @Override
  public Viewable getHTML() {
    throw new IllegalStateException("Not yet implemented.");
  }

  @Override
  public Object getList() {
    List<GroupTemplateInfo> result = new ArrayList<GroupTemplateInfo>();
    for (TGroupTemplate template : this.list) {
      result.add(this.buildGroupInfo(template));
    }
    return result;
  }

  @POST
  @Path("list/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response onPostList(List<GroupTemplateInfo> infos) {
    this.list.clear();
    if (null == infos || infos.isEmpty()) {
      return BackendUtils.persist(this.res);
    }

    List<TGroupTemplate> groups = new ArrayList<TGroupTemplate>();
    for (GroupTemplateInfo info : infos) {
      groups.add(buildGroupTemplate(info));
    }
    super.addNewElementList(groups);
    return Response.status(Status.CREATED).build();
  }

  @POST
  @Path("create/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response onPost(GroupTemplateInfo info) {
    if (null == info || null == info.getName() || null == info.getType()
        || null == info.getNamespace()) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    TGroupTemplate group = buildGroupTemplate(info);
    super.addNewElement(group);
    return Response.status(Status.CREATED).build();
  }

  private TGroupTemplate buildGroupTemplate(GroupTemplateInfo info) {
    TGroupTemplate group = new TGroupTemplate();
    String id = null == info.getId() ? UUID.randomUUID().toString() : info.getId();
    group.setId(id);
    group.setName(info.getName());
    group.setType(new QName(info.getNamespace(), info.getType()));

    List<String> targets = info.getTargets();
    if (null != targets && !targets.isEmpty()) {
      TTarget groupTargets = new TTarget();
      groupTargets.getTarget().addAll(targets);
      group.setTargets(groupTargets);
    }

    Map<String, String> properties = info.getProperties();
    if (null != properties && !properties.isEmpty()) {
      String templateNS = ((ServiceTemplateResource) this.res).getNamespace().getDecoded();
      String propNS = templateNS + "/propertiesdefinition/winery";
      Element propRootElement = ModelUtilities.buildPropertiesElement(properties, propNS);
      TEntityTemplate.Properties templateProps = new TEntityTemplate.Properties();
      templateProps.setAny(propRootElement);
      group.setProperties(templateProps);
    }
    return group;
  }

  private GroupTemplateInfo buildGroupInfo(TGroupTemplate template) {
    if (null == template) {
      return null;
    }

    GroupTemplateInfo info = new GroupTemplateInfo();
    info.setId(template.getId());
    info.setName(template.getName());
    info.setType(template.getType().getLocalPart());
    info.setNamespace(template.getType().getNamespaceURI());
    info.setTargets(getTargets(template.getTargets()));
    info.setProperties(getProperties(template.getProperties()));
    return info;
  }

  private Map<String, String> getProperties(Properties properties) {
    if (null == properties || !(properties.getAny() instanceof Element)) {
      return null;
    }
    return ModelUtilities.resolvePropertiesElement((Element) properties.getAny());
  }

  public List<String> getTargets(TTarget tTarget) {
    if (null == tTarget) {
      return null;
    }
    return tTarget.getTarget();
  }
}
