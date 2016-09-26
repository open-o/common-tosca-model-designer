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
package org.eclipse.winery.repository.resources.servicetemplates.topologytemplates;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources.entitytemplates.TEntityTemplatesResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import com.sun.jersey.api.view.Viewable;

public class NodeTemplatesResource extends
        TEntityTemplatesResource<NodeTemplateResource, TNodeTemplate> {

    public NodeTemplatesResource(List<TNodeTemplate> list, IPersistable res) {
        super(NodeTemplateResource.class, TNodeTemplate.class, list, res);
    }

    @Override
    public String getId(TNodeTemplate entity) {
        return entity.getId();
    }

    @Override
    public Viewable getHTML() {
        // TODO Auto-generated method stub
        throw new IllegalStateException("Not yet implemented.");
    }

    @Override
    protected void addEntity(TNodeTemplate entity) {
        super.addEntity(entity);
        ServiceTemplateResource stRes = (ServiceTemplateResource) res;
        TTopologyTemplate topologyTemplate = stRes.getServiceTemplate().getTopologyTemplate();
        if (null != topologyTemplate) {
            List<TEntityTemplate> nodeTemplateOrRelationshipTemplate =
                    topologyTemplate.getNodeTemplateOrRelationshipTemplate();
            if (null != nodeTemplateOrRelationshipTemplate) {
                nodeTemplateOrRelationshipTemplate.add(entity);
            }
        }
    }

    @POST
    @Path("create/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onPost(NodeTemplateInfo info) {
        if (null == info || null == info.getName() || null == info.getType()
                || null == info.getNamespace()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        TNodeTemplate node = buildTNodeTemplate(info);
        super.addNewElement(node);
        return Response.status(Status.CREATED).build();
    }

    private TNodeTemplate buildTNodeTemplate(NodeTemplateInfo info) {
        TNodeTemplate node = new TNodeTemplate();
        String id = null == info.getId() ? UUID.randomUUID().toString() : info.getId();
        node.setId(id);
        node.setName(info.getName());
        node.setType(new QName(info.getNamespace(), info.getType()));
        return node;
    }
    
    @Override
    public Object getList() {
        return list;
    }
}
