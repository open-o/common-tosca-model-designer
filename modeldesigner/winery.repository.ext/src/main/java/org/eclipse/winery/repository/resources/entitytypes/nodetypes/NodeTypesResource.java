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

package org.eclipse.winery.repository.resources.entitytypes.nodetypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;

/**
 * Manages all nodetypes in all available namespaces <br />
 * The actual implementation is done in the AbstractComponentsResource
 */
public class NodeTypesResource extends AbstractComponentsResource<NodeTypeResource> {
	
  @GET
  @Path("all/")
  @Produces(MediaType.APPLICATION_JSON)
  public List<TNodeType> getAllNodeTypes(){

    List<TNodeType> stList = new ArrayList<TNodeType>();

    @SuppressWarnings("unchecked")
    Collection<NodeTypeResource> res =
            (Collection<NodeTypeResource>) (Collection<?>) this.getAll();
    for (Iterator<NodeTypeResource> iterator = res.iterator(); iterator.hasNext();) {
      NodeTypeResource resource = iterator.next();
        stList.add(resource.getNodeType());
    }

    return stList;
  }
}
