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
package de.ustutt.iaas.bpmn2bpel.model.param;

import javax.xml.namespace.QName;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * Represents a topology parameter.<br>
 * To initialize the fields nodeType and property accordingly, the expected
 * value parameter format is <code>$NodeTypeName.$PropertyName</code>, e.g.
 * <code>UbuntuVM.ImageName</code>
 * 
 * @author Sebastian Wagner
 *		
 */
public class TopologyParameter extends Parameter {
	
	private QName nodeTemplateId;
	
	private String property;
	
	
	public QName getNodeTemplateId() {
		return nodeTemplateId;
	}
	
	/**
	 * Set the node template id
	 * @param name - the node template id with template prefix. 
	 */
	public void setNodeTemplateId(QName name) {
		this.nodeTemplateId = name;
	}
	
	public String getProperty() {
		return property;
	}
	
	public void setProperty(String name) {
		this.property = name;
	}
	
	@Override
	public ParamType getType() {
		return ParamType.TOPOLOGY;
	}
	
	@Override
	public void setValue(String value) {
		/*
		 * Decompose String into fully qualified NodeTemplate name and property
		 * name, e.g. value "{http://example.com}UbuntuVM.ImageID" will be
		 * decomposed into {http://example.com}UbuntuVM and ImageId
		 */
		int idx = value.lastIndexOf(".");
		
		if (idx == -1) {
			throw new RuntimeException(TopologyParameter.class + " expects value in format '$QualifiedNodeTypeName.$PropertyName' but invalid value " + value + " was provided.");
		}
		
		String nodeTemplateName = value.substring(0, idx);
		String properyName = value.substring(idx + 1);
		 
		setNodeTemplateId(QName.valueOf(nodeTemplateName));
		setProperty(properyName);
		super.setValue(value);
		
	}
}
