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
package org.eclipse.winery.repository.resources.entitytypes.properties.winery;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.repository.resources.EntityTypeResource;
import org.eclipse.winery.repository.resources._support.collections.withid.EntityWithIdCollectionResource;

import com.sun.jersey.api.view.Viewable;

/**
 * Supports Winery's k/v properties introducing sub resources
 * "PropertyDefinition", which defines <em>one</em> property
 */
public class PropertyDefinitionKVListResource extends EntityWithIdCollectionResource<PropertyDefinitionKVResource, PropertyDefinitionKV> {
	
	public PropertyDefinitionKVListResource(EntityTypeResource res, PropertyDefinitionKVList list) {
		super(PropertyDefinitionKVResource.class, PropertyDefinitionKV.class, list, res);
	}
	
	@Override
	public String getId(PropertyDefinitionKV entity) {
		return entity.getKey();
	}
	
	@Override
	public Viewable getHTML() {
		throw new IllegalStateException("Not yet implemented.");
	}
	
	@Override
	public Object getList() {
	    return list;
	}
}
