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
package org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher;

import java.util.List;

import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;

/**
 * This class supports processing of node types from a YAML service template.
 */
public class ImportXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

    public ImportXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
        super(parentSwitch);
    }

    /**
     * Processes every in-memory node type and creates a corresponding YAML node type.
     */
    @Override
    public void process() {
        List<?> tImportList = getDefinitions().getImport();
        if (tImportList != null) {
            for (Object tImport : tImportList) {
                if (tImport instanceof TImport) {
                    String yImport = createImport((TImport) tImport);
                    if (yImport != null) {
                        getServiceTemplate().getImports().add(yImport);
                    }
                }

            }
        }
    }

    /**
     * @param tImport
     * @return
     */
    private String createImport(TImport tImport) {
        String yImport = tImport.getLocation();
        if (yImport == null || yImport.equals("") || yImport.startsWith("..")) {
            return null;
        }
        
        return yImport.replaceFirst(".tosca$", ".yaml");
    }

}
