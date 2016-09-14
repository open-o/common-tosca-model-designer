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
package org.eclipse.winery.repository.ext.overview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 10186401
 *
 */
public class ServiceTemplatesOverviewConfigration {

    private List<ServiceTemplateOverviewInfo> templates;

    public List<ServiceTemplateOverviewInfo> getTemplates() {
        return templates;
    }

    public void setTemplates(List<ServiceTemplateOverviewInfo> templates) {
        this.templates = templates;
    }

    public void addInfo(ServiceTemplateOverviewInfo info) {
        if (null == info || null == info.getId() || null == info.getNamespace()) {
            return;
        }

        if (null == templates) {
            templates = new ArrayList<ServiceTemplateOverviewInfo>();
        }

        for (ServiceTemplateOverviewInfo ele : templates) {
            if (ele.equals(info)) {
                return;
            }
        }
        templates.add(info);
    }

    public void deleteInfo(ServiceTemplateOverviewInfo info) {
        if (null == info || null == info.getId() || null == info.getNamespace()) {
            return;
        }

        if (null == templates) {
            return;
        }

        for (Iterator<ServiceTemplateOverviewInfo> it = templates.iterator(); it.hasNext();) {
            ServiceTemplateOverviewInfo ele = it.next();
            if (ele.equals(info)) {
                it.remove();
            }
        }
    }


}
