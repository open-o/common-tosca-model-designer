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
package de.ustutt.iaas.bpmn2bpel.model;

import javax.xml.namespace.QName;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Sebastian Wagner
 *
 */
public class ManagementTask extends Task {
	
	private String interfaceName;;
	
	private QName nodeTemplateId;
	
	private String nodeOperation;

	public QName getNodeTemplateId() {
		return nodeTemplateId;
	}

	public void setNodeTemplateId(QName nodeTemplateId) {
		this.nodeTemplateId = nodeTemplateId;
	}

	public String getNodeOperation() {
		return nodeOperation;
	}
  
	public void setNodeOperation(String nodeOperation) {
		this.nodeOperation = nodeOperation;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

}
