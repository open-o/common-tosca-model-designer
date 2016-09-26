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
package org.eclipse.winery.repository.ext.overview;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.Constants;


/**
 * @author 10186401
 *
 */
public class ServiceTemplateOverviewInfo {
    private String id;
    private String namespace;
    private String domaintype;
    private String sourcetype;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDomaintype() {
        return domaintype;
    }

    public void setDomaintype(String domaintype) {
        this.domaintype = domaintype;
    }

    public String getSourcetype() {
        return sourcetype;
    }

    public void setSourcetype(String sourcetype) {
        this.sourcetype = sourcetype;
    }

    public ServiceTemplateOverviewInfo(TServiceTemplate tServiceTemplate) {
        super();
        String source =
                tServiceTemplate.getOtherAttributes().get(new QName(Constants.TEMPLATE_SOURCE));
        source = null == source ? Constants.TEMPLATE_SOURCE_DERIVED : source;
        setId(tServiceTemplate.getId());
        setSourcetype(source);
        setNamespace(tServiceTemplate.getTargetNamespace());
    }

    public ServiceTemplateOverviewInfo() {
        super();
    }

    public boolean equals(ServiceTemplateOverviewInfo info) {
        return id.equals(info.getId()) && namespace.equals(info.getNamespace());
    }
}
