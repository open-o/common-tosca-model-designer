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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.tosca.TConstraint;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementDefinition.Constraints;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.sun.jersey.api.view.Viewable;

public class RequirementDefinitionsResource
    extends
    RequirementOrCapabilityDefinitionsResource<RequirementDefinitionResource, TRequirementDefinition> {

  private static final Logger logger = LoggerFactory.getLogger(CapabilityDefinitionsResource.class);

  public RequirementDefinitionsResource(NodeTypeResource res, List<TRequirementDefinition> defs) {
    super(RequirementDefinitionResource.class, TRequirementDefinition.class, defs, res);
  }

  @Override
  public Viewable getHTML() {
    return new Viewable("/jsp/entitytypes/nodetypes/reqandcapdefs/reqdefs.jsp", this);
  }

  @Override
  public Collection<QName> getAllTypes() {
    SortedSet<RequirementTypeId> allTOSCAComponentIds =
        Repository.INSTANCE.getAllTOSCAComponentIds(RequirementTypeId.class);
    return BackendUtils.convertTOSCAComponentIdCollectionToQNameCollection(allTOSCAComponentIds);
  }

  @POST
  @Path("{name}/")
  public Response onPost(@PathParam("name") String name, @QueryParam("ConstraintType") String type,
      @QueryParam("ConstraintValue") String value) {
    if (null == name || null == type) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    for (TRequirementDefinition tReqDef : list) {
      if (name.equals(tReqDef.getName())) {
        addConstraint(tReqDef, type, value);
        return BackendUtils.persist(this.res);
      }
    }

    return Response.status(Status.NOT_FOUND).build();
  }

  private void addConstraint(TRequirementDefinition tReqDef, String type, String value) {
    Constraints constraints = tReqDef.getConstraints();
    if (null == constraints) {
      constraints = new Constraints();
      tReqDef.setConstraints(constraints);
    }

    Element propRootElement = buildElement(type, value);
    boolean updateFlag = false;
    for (TConstraint tConstraint : constraints.getConstraint()) {
      if (type.equals(tConstraint.getConstraintType())) {
        tConstraint.setAny(propRootElement);
        updateFlag = true;
      }
    }

    if (!updateFlag) {
      TConstraint newConstraint = new TConstraint();
      newConstraint.setConstraintType(type);
      newConstraint.setAny(propRootElement);
      constraints.getConstraint().add(newConstraint);
    }
  }

  private Element buildElement(String type, String value) {
    String templateNS = this.res.getNamespace().getDecoded();
    String propNS = templateNS + "/propertiesdefinition/winery";
    Map<String, String> properties = new HashMap<String, String>();
    properties.put(type, value);
    return ModelUtilities.buildPropertiesElement(properties, propNS);
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
      TRequirementDefinition tReqDef = buildDefinition(info);
      if (null != tReqDef) {
        list.add(tReqDef);
      }
    }
    return BackendUtils.persist(this.res);
  }

  private TRequirementDefinition buildDefinition(ReqCapDefinitionInfo info) {
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

    TRequirementDefinition def = new TRequirementDefinition();
    def.setLowerBound(lbound);
    def.setUpperBound(ubound);
    def.setName(info.getName());
    def.setRequirementType(QName.valueOf(info.getType()));
    return def;
  }
}
