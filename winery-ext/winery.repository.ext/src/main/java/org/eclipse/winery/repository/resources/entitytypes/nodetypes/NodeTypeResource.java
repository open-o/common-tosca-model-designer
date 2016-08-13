/**
 * Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.winery.repository.resources.entitytypes.nodetypes;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.elements.TOSCAElementId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeType.CapabilityDefinitions;
import org.eclipse.winery.model.tosca.TNodeType.Interfaces;
import org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.resources.entitytypes.InstanceStatesResource;
import org.eclipse.winery.repository.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.reqandcapdefs.CapabilityDefinitionsResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.reqandcapdefs.RequirementDefinitionsResource;
import org.eclipse.winery.repository.resources.interfaces.InterfacesResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.multipart.FormDataBodyPart;

public class NodeTypeResource extends TopologyGraphElementEntityTypeResource {

    private static final Logger logger = LoggerFactory.getLogger(NodeTypeResource.class);


    public NodeTypeResource(NodeTypeId id) {
        super(id);
    }

    /**
     * Convenience method to avoid casting at the caller's side.
     */
    public TNodeType getNodeType() {
        return (TNodeType) this.getElement();
    }

    /** sub-resources **/

    @Path("implementations/")
    public ImplementationsOfOneNodeTypeResource getImplementations() {
        return new ImplementationsOfOneNodeTypeResource((NodeTypeId) this.id);
    }

    @Path("instancestates/")
    public InstanceStatesResource getInstanceStatesResource() {
        TTopologyElementInstanceStates instanceStates = this.getNodeType().getInstanceStates();
        if (instanceStates == null) {
            // if an explicit (empty) list does not exist, create it
            instanceStates = new TTopologyElementInstanceStates();
            this.getNodeType().setInstanceStates(instanceStates);
        }
        return new InstanceStatesResource(instanceStates, this);
    }

    @Path("interfaces/")
    public InterfacesResource getInterfaces() {
        Interfaces interfaces = this.getNodeType().getInterfaces();
        if (interfaces == null) {
            interfaces = new Interfaces();
            this.getNodeType().setInterfaces(interfaces);
        }
        return new InterfacesResource(null, interfaces.getInterface(), this);
    }

    @Path("requirementdefinitions/")
    public RequirementDefinitionsResource getRequirementDefinitions() {
        RequirementDefinitions definitions = this.getNodeType().getRequirementDefinitions();
        if (definitions == null) {
            definitions = new RequirementDefinitions();
            this.getNodeType().setRequirementDefinitions(definitions);
        }
        return new RequirementDefinitionsResource(this, definitions.getRequirementDefinition());
    }

    @Path("capabilitydefinitions/")
    public CapabilityDefinitionsResource getCapabilityDefinitions() {
        CapabilityDefinitions definitions = this.getNodeType().getCapabilityDefinitions();
        if (definitions == null) {
            definitions = new CapabilityDefinitions();
            this.getNodeType().setCapabilityDefinitions(definitions);
        }
        return new CapabilityDefinitionsResource(this, definitions.getCapabilityDefinition());
    }

    @Path("visualappearance/")
    public VisualAppearanceResource getVisualAppearanceResource() {
        return new VisualAppearanceResource(this, this.getElement().getOtherAttributes(), (NodeTypeId) this.id);
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TNodeType();
    }

    @Override
    protected void derivedPostProcess(QName qname) {
        NodeTypeResource parentNodeTypeRes =
                new NodeTypesResource().getComponentInstaceResource(qname.getNamespaceURI(), qname.getLocalPart(),
                        false);
        TOSCAElementId toscaEleId = parentNodeTypeRes.getVisualAppearanceResource().getId();
        RepositoryFileReference ref = new RepositoryFileReference(toscaEleId, Filename.FILENAME_BIG_ICON);
        if (!Repository.INSTANCE.exists(ref)) {
            return;
        }
        InputStream is = null;
        try {
            is = Repository.INSTANCE.newInputStream(ref);
            String mimeType = Repository.INSTANCE.getMimeType(ref);
            String[] split = mimeType.split("/");
            FormDataBodyPart body = new FormDataBodyPart();
            body.setMediaType(new MediaType(split[0], split[1]));
            this.getVisualAppearanceResource().post50x50Image(is, body);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
