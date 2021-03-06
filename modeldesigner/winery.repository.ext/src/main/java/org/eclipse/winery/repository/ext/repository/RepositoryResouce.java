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
package org.eclipse.winery.repository.ext.repository;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate.Requirements;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.ext.repository.common.PropertyUtil;
import org.eclipse.winery.repository.ext.repository.entity.RepositoryNodeDetail;
import org.eclipse.winery.repository.ext.repository.entity.Summary;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.relationshiptypes.RelationshipTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.relationshiptypes.RelationshipTypesResource;
import org.reflections.Reflections;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.sun.jersey.api.NotFoundException;

public class RepositoryResouce {

  private static final XLogger logger = XLoggerFactory.getXLogger(RepositoryResouce.class);

  private static List<RepositoryService> impls = new ArrayList<RepositoryService>();

  public RepositoryResouce() {
    init();
  }

  private void init() {
    if (impls.size() == 0) {
      Reflections reflections = new Reflections("org.eclipse.winery.repository.ext");
      Set<Class<? extends RepositoryService>> implenmetions = reflections
          .getSubTypesOf(org.eclipse.winery.repository.ext.repository.RepositoryService.class);
      Iterator<Class<? extends RepositoryService>> it = implenmetions.iterator();
      while (it.hasNext()) {
        try {
          Class<? extends RepositoryService> implClass =
              (Class<? extends RepositoryService>) it.next();
          if (!Modifier.isAbstract(implClass.getModifiers()))
            impls.add(implClass.newInstance());
        } catch (InstantiationException e) {
          logger.error("InstantiationException", e);
        } catch (IllegalAccessException e) {
          logger.error("IllegalAccessException", e);
        }
      }
    }
  }


  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTemplateFromRepositoryById(@PathParam("id") String id,
      @QueryParam("type") String type, @QueryParam("namespace") String namespace) {
    List<RepositoryNodeDetail> nodes = new ArrayList<RepositoryNodeDetail>();
    for (RepositoryService impl : impls) {
      List<TEntityTemplate> results = impl.getNodeDetails(id.split(","), type);
      for (TEntityTemplate template : results) {
        RepositoryNodeDetail node = new RepositoryNodeDetail();
        node.setTemplate(template);
        TEntityType nodeType = null;
        if (template instanceof TRelationshipTemplate) {
          nodeType = getRelationType(template.getType().getNamespaceURI(),
              template.getType().getLocalPart());
        } else {
          nodeType =
              getNodeType(template.getType().getNamespaceURI(), template.getType().getLocalPart());
          processForNfv(nodeType,(TNodeTemplate)template);
        }
        node.setType(nodeType);
        nodes.add(node);
      }
    }
    return Response.ok().entity(nodes).build();
  }

  @GET
  @Path("summaries")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTemplateList(@QueryParam("type") String type,
      @QueryParam("filter") String filter) {
    Map<String, List<Summary>> rtnMap = new HashMap<String, List<Summary>>();
    List<Summary> results = new ArrayList<Summary>();
    String namespace = getNamespace(filter);
    for (RepositoryService plugin : impls) {
      if (acceptByPlugin(plugin, namespace)) {
        results = plugin.getSummarys(getType(namespace), filter);
        // nfv需要按类型进行分类
        if (RepositoryService.DEFAULT.equals(plugin.getScope())) {
          fillMap(rtnMap, results);
        } else {
          if (!results.isEmpty())
            rtnMap.put(getType(namespace), results);
        }
      }
    }
    return Response.ok().entity(rtnMap).build();
  }

  private TEntityType getNodeType(String namespace, String type) {
    try {
      NodeTypesResource res = new NodeTypesResource();
      NodeTypeResource nodetypeRes =
          res.getComponentInstaceResource(Util.URLencode(namespace), type);
      return nodetypeRes.getEntityType();
    } catch (NotFoundException e) {
      logger.error("can not found node with namespace: " + namespace + " and type: " + type, e);
    }
    return null;
  }

  private TEntityType getRelationType(String namespace, String type) {
    try {
      RelationshipTypesResource res = new RelationshipTypesResource();
      RelationshipTypeResource relationshipRes =
          res.getComponentInstaceResource(Util.URLencode(namespace), type);
      return relationshipRes.getEntityType();
    } catch (NotFoundException e) {
      logger.error("can not found relation with namespace: " + namespace + " and type: " + type, e);
    }
    return null;

  }

  private String getNamespace(String filter) {
    if (filter != null)
      return filter.split(",")[0];
    return null;
  }

  private void fillMap(Map<String, List<Summary>> rtnMap, List<Summary> results) {
    for (Summary summary : results) {
      List<Summary> tempList = null;
      tempList = rtnMap.get(summary.getType());
      if (tempList == null) {
        tempList = new ArrayList<Summary>();
        tempList.add(summary);
        rtnMap.put(summary.getType(), tempList);
      } else
        rtnMap.get(summary.getType()).add(summary);
    }
  }

  private String getProcessModule(String namespace) {
    if (namespace == null)
      return null;
    String confPath = getWebsiteURL() + Constants.RES_PLUGIN_MAPPING_FILE;
    Properties property = PropertyUtil.getParamterProperties(confPath);
    return (String) property.get(namespace);
  }

  private String getType(String namespace) {
    if (namespace == null)
      return null;
    String confPath = getWebsiteURL() + Constants.RES_TYPE_MAPPING_FILE;
    Properties property = PropertyUtil.getParamterProperties(confPath);
    return (String) property.get(namespace);
  }

  private String getWebsiteURL() {
    String path = RepositoryResouce.class.getResource("/").getPath();
    return path.replace("/build/classes", "").replace("%20", " ").replace("classes/", "");
  }



  private boolean acceptByPlugin(RepositoryService plugin, String namespace) {
    String modules = getProcessModule(namespace);
    logger.info("namespace is: " + namespace + " and the process module is: " + modules);
    if (modules != null)
      return modules.contains(plugin.getScope());
    else
      return false;
  }

  private void processForNfv(TEntityType nodeType, TNodeTemplate nodeTemplate) {
    List<TRequirement> reqs = ModelUtilities.instantiateRequirements((TNodeType) nodeType);
    if (reqs != null) {
      for (TRequirement req : reqs) {
        TNodeTemplate.Requirements requirements = nodeTemplate.getRequirements();
        if (requirements == null)
          requirements = new Requirements();
        requirements.getRequirement().add(req);
        nodeTemplate.setRequirements(requirements);
      }
    }
  }

}
