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
