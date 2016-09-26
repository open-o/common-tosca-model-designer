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
package org.eclipse.winery.repository.resources.entitytypes.properties.ext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.EntityTypeResource;

public class ComplexPropertiesDefinitionResource {

    private final EntityTypeResource res;
    private final List<Object> list;

    public ComplexPropertiesDefinitionResource(EntityTypeResource parentRes, List<Object> list) {
        this.res = parentRes;
        this.list = list;
    }

    @GET
    @Path("{elename}")
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyDefinitionKVList getPropertiesByEleName(@PathParam(value = "elename") String eleName) {
        if (null == eleName) {
            return null;
        }

        WinerysPropertiesDefinition wpd = null;
        for (Object o : list) {
            if (o instanceof WinerysPropertiesDefinition) {
                wpd = (WinerysPropertiesDefinition) o;
                if (eleName.equals(wpd.getElementName())) {
                    return wpd.getPropertyDefinitionKVList();
                }
            }
        }
        return null;
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WinerysPropertiesDefinition> getAllProperties() {
        List<WinerysPropertiesDefinition> result = new ArrayList<WinerysPropertiesDefinition>();
        for (Object o : list) {
            if (o instanceof WinerysPropertiesDefinition) {
                result.add((WinerysPropertiesDefinition) o);
            }
        }
        return result;
    }

    @POST
    @Path("all")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addProperties(List<WinerysPropertiesDefinition> properties) {
        list.addAll(properties);
        return BackendUtils.persist(this.res);
    }

    @POST
    @Path("{elename}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addProperty(@PathParam(value = "elename") String eleName, PropertyDefinitionKVList property) {
        if (null == eleName) {
            return Response.status(Status.BAD_REQUEST).entity("empty name").build();
        }
        
        WinerysPropertiesDefinition wpd = buildProperty(eleName, property);
        delete(eleName);
        list.add(wpd);
        
        return BackendUtils.persist(this.res);
    }

    private WinerysPropertiesDefinition buildProperty(String eleName, PropertyDefinitionKVList property) {
        WinerysPropertiesDefinition wpd = new WinerysPropertiesDefinition();
        wpd.setElementName(eleName);
        wpd.setPropertyDefinitionKVList(property);
        if ("Properties".equals(eleName)) {
            String ns = res.getEntityType().getTargetNamespace();
            if (!ns.endsWith("/")) {
                ns += "/";
            }
            ns += "propertiesdefinition/winery";
            wpd.setNamespace(ns);
        }
        return wpd;
    }

    @DELETE
    @Path("all")
    public Response clearProperties() {
        list.clear();
        return BackendUtils.persist(this.res);
    }

    @DELETE
    @Path("{elename}")
    public Response deleteProperty(@PathParam(value = "elename") String eleName) {
        if (null == eleName) {
            return Response.status(Status.BAD_REQUEST).entity("empty name").build();
        }

        delete(eleName);
        return BackendUtils.persist(this.res);
    }

    private void delete(String eleName) {
        for (Iterator<Object> it = list.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof WinerysPropertiesDefinition) {
                if (eleName.equals(((WinerysPropertiesDefinition) o).getElementName())) {
                    it.remove();
                    break;
                }
            }
        }
    }
}
