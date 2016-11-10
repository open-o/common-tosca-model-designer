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
import java.util.Map.Entry;

import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.ArtifactType;

/**
 * This class supports processing of node type of a YAML artifact type.
 */
public class ArtifactTypesXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

  public ArtifactTypesXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
    super(parentSwitch);
  }

  @Override
  public void process() {
    List<?> tNodeList = getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
    if (tNodeList != null) {
        for (Object tNode : tNodeList) {
            if (tNode instanceof TArtifactType) {
                Entry<String, ArtifactType> yArtifactType = createArtifactType((TArtifactType) tNode);
                getServiceTemplate().getArtifact_types().put(yArtifactType.getKey(), yArtifactType.getValue());
            }

        }
    }
  }

  private Entry<String, ArtifactType> createArtifactType(TArtifactType tArtifactType) {
    ArtifactType yArtifactType = new ArtifactType();

    String name = Xml2YamlTypeMapper.mappingArtifactType(tArtifactType.getName());
    // derived_from
    if (tArtifactType.getDerivedFrom() == null) {
        yArtifactType.setDerived_from(Xml2YamlTypeMapper.mappingArtifactTypeDerivedFrom(null, name));
    } else {
        yArtifactType.setDerived_from(
            Xml2YamlTypeMapper.mappingArtifactTypeDerivedFrom(
                Xml2YamlSwitchUtils.getNamefromQName(tArtifactType.getDerivedFrom().getTypeRef()), name));
    }

    // description
    String description =
            Xml2YamlSwitchUtils.convert2Description(tArtifactType.getDocumentation());
    if (null != description) {
        yArtifactType.setDescription(description);
    }

    // properties
    yArtifactType.setProperties(
        Xml2YamlSwitchUtils.convert2PropertyDefinitions(tArtifactType.getAny()));
        return Xml2YamlSwitchUtils.buildEntry(name, yArtifactType);
  }

}
