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
package org.eclipse.winery.repository.ext.serviceInfo;

import java.util.List;

import org.eclipse.winery.common.boundaryproperty.Flavors;
import org.eclipse.winery.common.boundaryproperty.Inputs;
import org.eclipse.winery.common.boundaryproperty.MetaDatas;
import org.eclipse.winery.common.servicetemplate.SubstitutableNodeType;
import org.eclipse.winery.common.servicetemplate.boundarydefinitions.reqscaps.ReqsCapsRequest;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.repository.ext.repository.entity.NodeTemplateDetail;

public class ServiceTemplateInfo {
    
    private String documentation;
    
    private SubstitutableNodeType substitutableNodeType;
    
    private MetaDatas metaDatas;
    
    private Inputs inputs;
    
    private String copyNameSpace;
    
    private String copyId;
    
    private Flavors flavors;
    
    private ReqsCapsRequest requirments;
    
    private ReqsCapsRequest capabilities;
    
    private List<NodeTemplateDetail> nodeTemplates;
    
    private List<TRelationshipTemplate> relationTemplates;

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public SubstitutableNodeType getSubstitutableNodeType() {
        return substitutableNodeType;
    }

    public void setSubstitutableNodeType(SubstitutableNodeType substitutableNodeType) {
        this.substitutableNodeType = substitutableNodeType;
    }

    public MetaDatas getMetaDatas() {
        return metaDatas;
    }

    public void setMetaDatas(MetaDatas metaDatas) {
        this.metaDatas = metaDatas;
    }

    public String getCopyNameSpace() {
        return copyNameSpace;
    }

    public void setCopyNameSpace(String copyNameSpace) {
        this.copyNameSpace = copyNameSpace;
    }

    public String getCopyId() {
        return copyId;
    }

    public void setCopyId(String copyId) {
        this.copyId = copyId;
    }

    public Inputs getInputs() {
      return inputs;
    }

    public void setInputs(Inputs inputs) {
      this.inputs = inputs;
    }

    public Flavors getFlavors() {
      return flavors;
    }

    public void setFlavors(Flavors flavors) {
      this.flavors = flavors;
    }

    public ReqsCapsRequest getRequirments() {
      return requirments;
    }

    public void setRequirments(ReqsCapsRequest requirments) {
      this.requirments = requirments;
    }

    public ReqsCapsRequest getCapabilities() {
      return capabilities;
    }

    public void setCapabilities(ReqsCapsRequest capabilities) {
      this.capabilities = capabilities;
    }

    public List<NodeTemplateDetail> getNodeTemplates() {
      return nodeTemplates;
    }

    public void setNodeTemplates(List<NodeTemplateDetail> nodeTemplates) {
      this.nodeTemplates = nodeTemplates;
    }

    public List<TRelationshipTemplate> getRelationTemplates() {
      return relationTemplates;
    }

    public void setRelationTemplates(List<TRelationshipTemplate> relationTemplates) {
      this.relationTemplates = relationTemplates;
    }  
}
