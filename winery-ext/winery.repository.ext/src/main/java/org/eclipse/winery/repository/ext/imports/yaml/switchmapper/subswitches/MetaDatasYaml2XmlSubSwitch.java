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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.boundaryproperty.MetaData;
import org.eclipse.winery.common.boundaryproperty.MetaDatas;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;

/**
 * This class supports processing of node types from a YAML service template.
 */
public class MetaDatasYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

    public MetaDatasYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
        super(parentSwitch);
    }

    /**
     * Processes every in-memory node type and creates a corresponding YAML node type.
     */
    @Override
    public void process() {
        Map<String, Object> yMetadatas = getServiceTemplate().getMetadata();
        if (yMetadatas != null && yMetadatas.size() > 0) {
            List<MetaData> tMetaDataList = new ArrayList<>();
            for (Map.Entry<String, Object> yMetaData : yMetadatas.entrySet()) {
                tMetaDataList.add(buildMetaData(yMetaData));
            }

            MetaDatas tMetaDatas = new MetaDatas();
            tMetaDatas.setMetadatas(tMetaDataList);

            BoundaryPropertyDefinition tbpd = getBoundaryPropertyDefinition();
            tbpd.setMetadatas(tMetaDatas);

            TBoundaryDefinitions.Properties tProperties = new TBoundaryDefinitions.Properties();
            tProperties.setAny(BoundaryPropertyUtil.getPropertiesAny(tbpd));

            TBoundaryDefinitions tBoundaryDefinitions = new TBoundaryDefinitions();
            tBoundaryDefinitions.setProperties(tProperties);

            TServiceTemplate tServiceTemplate = getTServiceTemplate();
            if (tServiceTemplate != null) {
                tServiceTemplate.setBoundaryDefinitions(tBoundaryDefinitions);
            }
        }
    }

    private MetaData buildMetaData(Map.Entry<String, Object> yMetaData) {
        MetaData tMetaData = new MetaData();
        tMetaData.setKey(yMetaData.getKey());
        tMetaData.setValue(yMetaData.getValue().toString());
        return tMetaData;
    }

}
