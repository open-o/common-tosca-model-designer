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
package org.eclipse.winery.repository.ext.overview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.repository.entity.Summary;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplatesResource;


/**
 * @author 10186401
 *
 */
public class ServiceTemplatesOverviewResource {

  @GET
  @Path("reload/")
  public void relaod() {
    OverwiewConfHandler.reload();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<ServiceTemplateOverviewInfo> queryAll() {
    if (null != OverwiewConfHandler.INSTANCE) {
      return OverwiewConfHandler.INSTANCE.getTemplates();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @GET
  @Path("paas/")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Summary> queryPaasTemplates(String project, String type) {
    List<Summary> list = new ArrayList<Summary>();
    List<TServiceTemplate> allServiceTemplates =
        new ServiceTemplatesResource().getAllServiceTemplates();
    String namespacePrefix = "http://www.zte.com.cn/paas/r3" + "/" + project + "/" + type;
    for (TServiceTemplate serviceTemplate : allServiceTemplates) {
      if (serviceTemplate.getTargetNamespace().contains(namespacePrefix)) {
        Summary summary = new Summary();
        summary.setName(serviceTemplate.getId());
        Map<String, String> properties =
            (Map<String, String>) serviceTemplate.getBoundaryDefinitions().getProperties().getAny();
        summary.setVersion(properties.get("version"));
        summary.setType(properties.get("kind"));
        summary.setCreateTime(properties.get("createTime"));
        list.add(summary);
      }
    }
    return list;
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public void add(ServiceTemplateOverviewInfo info) {
    if (null != OverwiewConfHandler.INSTANCE) {
      OverwiewConfHandler.INSTANCE.addInfo(info);
      OverwiewConfHandler.save();
    }
  }

  @DELETE
  @Path("{namespace}/{id}/")
  @Produces(MediaType.APPLICATION_JSON)
  public void delete(@PathParam("namespace") String namespace, @PathParam("id") String id) {
    ServiceTemplateOverviewInfo info = new ServiceTemplateOverviewInfo();
    info.setId(id);
    info.setNamespace(namespace);
    if (null != OverwiewConfHandler.INSTANCE) {
      OverwiewConfHandler.INSTANCE.deleteInfo(info);
      OverwiewConfHandler.save();
    }
  }
}
