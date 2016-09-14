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
package org.eclipse.winery.repository.resources.entitytypes.relationshiptypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;

public class RelationshipTypesResource extends AbstractComponentsResource<RelationshipTypeResource> {
  
  @GET
  @Path("all/")
  @Produces(MediaType.APPLICATION_JSON)
  public List<TRelationshipType> getAllRelationshipTypes(){

    List<TRelationshipType> stList = new ArrayList<TRelationshipType>();

    @SuppressWarnings("unchecked")
    Collection<RelationshipTypeResource> res =
            (Collection<RelationshipTypeResource>) (Collection<?>) this.getAll();
    for (Iterator<RelationshipTypeResource> iterator = res.iterator(); iterator.hasNext();) {
      RelationshipTypeResource resource = iterator.next();
        stList.add(resource.getRelationshipType());
    }
    return stList;
  }
  
}
