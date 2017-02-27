/**
 * Copyright 2017 ZTE Corporation.
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
package org.eclipse.winery.repository.resources.entitytypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.repository.AbstractRepositoryTest;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.properties.ext.ComplexPropertiesDefinitionResource;
import org.junit.Test;

/**
 * @author 10186401
 *
 */
public class ComplexPropertiesDefinitionResourceTest extends AbstractRepositoryTest {

  private ComplexPropertiesDefinitionResource res = getComplexPropertiesDefinitionResource();

  private ComplexPropertiesDefinitionResource getComplexPropertiesDefinitionResource() {
    QName type = new QName("http://www.open-o.org/tosca/nfv", "tosca.nodes.nfv.ext.ImageFile");
    NodeTypeResource nodetypeRes =
        (NodeTypeResource) new NodeTypesResource().getComponentInstaceResource(type);
    return nodetypeRes.getPropertiesDefinitionResource().getComplexPropertiesDefinitionResource();
  }

  @Test
  public void testGetAll() {
    List<WinerysPropertiesDefinition> allProperties = res.getAllProperties();
    assertEquals(1, allProperties.size());
    WinerysPropertiesDefinition propDef = allProperties.get(0);
    assertEquals("Properties", propDef.getElementName());
  }

  @Test
  public void testGetByElementName() {
    PropertyDefinitionKVList properties = res.getPropertiesByEleName("Properties");
    assertEquals(5, properties.size());
  }

  @Test
  public void testAddProperty() {
    addProperty();

    assertEquals(2, res.getAllProperties().size());
    PropertyDefinitionKVList prop = res.getPropertiesByEleName("Test");
    assertNotNull(prop);

    res.deleteProperty("Test");
  }

  private void addProperty() {
    PropertyDefinitionKVList prop = buildPropertyDefinitionKVList();
    res.addProperty("Test", prop);
  }

  private PropertyDefinitionKVList buildPropertyDefinitionKVList() {
    PropertyDefinitionKVList list = new PropertyDefinitionKVList();
    PropertyDefinitionKV kv = new PropertyDefinitionKV("key", "value");
    list.add(kv);
    return list;
  }
}
