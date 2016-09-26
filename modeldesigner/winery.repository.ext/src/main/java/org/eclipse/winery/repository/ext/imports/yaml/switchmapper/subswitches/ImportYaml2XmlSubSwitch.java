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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

import java.util.List;

import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;


/**
 * This class supports processing of node types from a YAML service template.
 */
public class ImportYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

    public ImportYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
        super(parentSwitch);
    }

    /**
     * Processes every in-memory node type and creates a corresponding YAML node type.
     */
    @Override
    public void process() {
        List<String> yImportList = getServiceTemplate().getImports();
        if (yImportList != null && !yImportList.isEmpty()) {
            for (String yImport : yImportList) {
                TImport tImport = createImport(yImport);
                getDefinitions().getImport().add(tImport);
            }
        }
    }

    /**
     * @param yImport
     * @return
     */
    private TImport createImport(String yImport) {
    	
    	if(yImport.endsWith(".xsd")) //.xsd
    	{
            TImport tImport = new TImport();
            tImport.setImportType(CommonConst.XMLSCHEMA_NS);
            tImport.setNamespace(getParent().getUsedNamespace()+CommonConst.PROPERTIES_DEFINITION_PREFIX);
            tImport.setLocation(yImport);
            return tImport;
    	}
    	
    	//.tosca
        TImport tImport = new TImport();
        tImport.setImportType(CommonConst.TOSCA_IMPORT_TYPE);
        tImport.setNamespace(getParent().getUsedNamespace()+CommonConst.PROPERTIES_DEFINITION_PREFIX);
        tImport.setLocation(yImport);
        return tImport;
    }

    /**
     * @param yImport
     * @return
     */
    private String convert2XmlFileName(String yImport) {
        if (yImport == null || yImport.equals("")) {
            return null;
        }

        return yImport.replaceFirst(".yaml$", ".tosca");
    }

}
