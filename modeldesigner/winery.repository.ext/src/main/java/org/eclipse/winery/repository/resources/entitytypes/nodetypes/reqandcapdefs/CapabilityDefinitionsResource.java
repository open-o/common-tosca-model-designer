/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
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
package org.eclipse.winery.repository.resources.entitytypes.nodetypes.reqandcapdefs;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.Viewable;

public class CapabilityDefinitionsResource extends
    RequirementOrCapabilityDefinitionsResource<CapabilityDefinitionResource, TCapabilityDefinition> {

  private static final Logger logger = LoggerFactory.getLogger(CapabilityDefinitionsResource.class);


  public CapabilityDefinitionsResource(NodeTypeResource res, List<TCapabilityDefinition> defs) {
    super(CapabilityDefinitionResource.class, TCapabilityDefinition.class, defs, res);
  }

  @Override
  public Viewable getHTML() {
    return new Viewable("/jsp/entitytypes/nodetypes/reqandcapdefs/capdefs.jsp", this);
  }

  @Override
  public Collection<QName> getAllTypes() {
    SortedSet<CapabilityTypeId> allTOSCAComponentIds =
        Repository.INSTANCE.getAllTOSCAComponentIds(CapabilityTypeId.class);
    return BackendUtils.convertTOSCAComponentIdCollectionToQNameCollection(allTOSCAComponentIds);
  }

  @Override
  public Object getList() {
    return list;
  }

  @POST
  @Path("all")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addProperties(List<ReqCapDefinitionInfo> infos) {
    if (null == infos) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    list.clear();
    for (ReqCapDefinitionInfo info : infos) {
      TCapabilityDefinition tReqDef = buildDefinition(info);
      if (null != tReqDef) {
        list.add(tReqDef);
      }
    }
    return BackendUtils.persist(this.res);
  }

  private TCapabilityDefinition buildDefinition(ReqCapDefinitionInfo info) {
    if (StringUtils.isEmpty(info.getName()) || StringUtils.isEmpty(info.getType())) {
      logger.debug("Name or Type has to be provided");
      return null;
    }

    int lbound = 1;
    if (!StringUtils.isEmpty(info.getLowerBound())) {
      try {
        lbound = Integer.parseInt(info.getLowerBound());
      } catch (NumberFormatException e) {
        logger.debug("Bad format of lowerbound: " + e.getMessage());
        return null;
      }
    }

    String ubound = "1";
    if (!StringUtils.isEmpty(info.getUpperBound())) {
      ubound = info.getUpperBound();
    }

    TCapabilityDefinition def = new TCapabilityDefinition();
    def.setLowerBound(lbound);
    def.setUpperBound(ubound);
    def.setName(info.getName());
    def.setCapabilityType(QName.valueOf(info.getType()));
    return def;
  }
}
