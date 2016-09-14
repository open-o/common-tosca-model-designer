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
package org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher;

import java.util.List;

import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.MetaData;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;

/**
 * This class supports processing of node types from a YAML service template.
 */
public class MetaDatasXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

    public MetaDatasXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
        super(parentSwitch);
    }

    /**
     * Processes every in-memory node type and creates a corresponding YAML node type.
     */
    @Override
    public void process() {
        List<MetaData> tMetaDataList = getTMetaDataList();

        if (tMetaDataList != null && !tMetaDataList.isEmpty()) {
            for (MetaData tMetaData : tMetaDataList) {
                getServiceTemplate().getMetadata().put(tMetaData.getKey(),
                        tMetaData.getValue());
            }

        }

    }

    private List<MetaData> getTMetaDataList() {
        BoundaryPropertyDefinition tbpd = getBoundaryPropertyDefinition();

        if (tbpd != null && tbpd.getMetadatas() != null) {
            return tbpd.getMetadatas().getMetadatas();
        }

        return null;
    }

}
