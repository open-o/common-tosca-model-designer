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
/**
 * 
 */
package org.eclipse.winery.repository.ext.imports.yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.repository.ext.imports.custom.DefinitionGenerator;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
import org.eclipse.winery.repository.ext.utils.YamlBeansUtils;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * 
 * @author 10090474
 * 
 */
public class YamlDefinitionGenerator extends DefinitionGenerator {
    private static final Logger logger = LoggerFactory
            .getLogger(YamlDefinitionGenerator.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.winery.repository.ext.imports.custom.DefinitionGenerator#
     * accept(java.nio.file.Path)
     */
    @Override
    public boolean accept(Path path) {
        return path.toString().endsWith(".yml") || path.toString().endsWith("yaml");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.winery.repository.ext.imports.custom.DefinitionGenerator#
     * makeDefinitions(java.nio.file.Path)
     */
    @Override
    public TDefinitions makeDefinitions(Path path) {
        logger.info("makeDefinitions begin. path = " + path);

        ServiceTemplate st = readServiceTemplateFromFile(path);
        st.setTemplate_name(getServiceTemplateName(path.toFile().getName()));
        Yaml2XmlSwitch switcher = new Yaml2XmlSwitch(st);
        Definitions definitions = switcher.convert();

        logger.info("makeDefinitions end. path = " + path);
        return definitions;
    }

    private String getServiceTemplateName(String filename) {
        int startIndex = filename.indexOf("__");
        int endIndex = filename.indexOf(".yaml");
        if (startIndex != -1 && endIndex != -1) {
            return filename.substring(startIndex + 2, endIndex);
        }
        return "";
    }
    
    private ServiceTemplate readServiceTemplateFromFile(Path path) {
        ServiceTemplate st = null;
        YamlReader reader = null;
        try {
            reader = new YamlReader(new FileReader(path.toFile()));
            YamlBeansUtils.setPropertyDefaultType(reader.getConfig());
            st = reader.read(ServiceTemplate.class);

        } catch (final FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (final YamlException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return st;
    }



}
