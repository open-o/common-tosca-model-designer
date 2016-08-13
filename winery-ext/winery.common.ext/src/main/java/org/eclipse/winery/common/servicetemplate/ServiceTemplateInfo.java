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
package org.eclipse.winery.common.servicetemplate;

import org.eclipse.winery.common.boundaryproperty.MetaDatas;

public class ServiceTemplateInfo {
    
    private String documentation;
    
    private SubstitutableNodeType substitutableNodeType;
    
    private MetaDatas metaDatas;

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
    
    

}
