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
package de.ustutt.iaas.bpmn2bpel.planwriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import de.ustutt.iaas.bpmn2bpel.model.ManagementTask;
import de.ustutt.iaas.bpmn2bpel.model.param.Parameter;
import de.ustutt.iaas.bpmn2bpel.model.param.TopologyParameter;


/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * Add convenience methods to the {@link ManagementTask} that can be used 
 * in the Velocity template.
 * 
 * @author Sebastian Wagner
 *
 */
public class ManagementTaskTemplateWrapper extends ManagementTask {

	public ManagementTaskTemplateWrapper(ManagementTask task) {
		super();
		setId(task.getId());
		setName(task.getName());
		setInterfaceName(task.getInterfaceName());
		setNodeTemplateId(task.getNodeTemplateId());
		setNodeOperation(task.getNodeOperation());
		setInputParameters(task.getInputParameters());
		setOutputParameters(task.getOutputParameters());
		
		//add by qinlihan
		setTaskTypeDetail(task.getTaskTypeDetail());
	}
	
	
	
	
	public List<QName> getInputNodeTemplateIds() {
		// Velocity does just support java lists in templates but not sets; 
		return new ArrayList<QName>(getNodeTemplateIds(getInputParameters())); 
	}
	
//	public List<String> getInputNodeTemplateIdsAsLocalNames() {
//		// Velocity does just support java lists in templates but not sets; 
//		List<String> localNames = new ArrayList<String>();
//		Iterator<QName> iter = getNodeTemplateIds(getInputParameters()).iterator();
//		
//		while (iter.hasNext()) {
//			localNames.add(iter.next().getLocalPart());
//		}
//		return localNames;
//	}
	
	public List<QName> getOutputNodeTemplateIds() {
		// Velocity does just support java lists in templates but not sets; 
				return new ArrayList<QName>(getNodeTemplateIds(getOutputParameters())); 
	}
	
//	public List<String> getOutputNodeTemplateIdsAsLocalNames() {
//		// Velocity does just support java lists in templates but not sets; 
//		List<String> localNames = new ArrayList<String>();
//		Iterator<QName> iter = getNodeTemplateIds(getOutputParameters()).iterator();
//		
//		while (iter.hasNext()) {
//			localNames.add(iter.next().getLocalPart());
//		}
//		return localNames;
//	}
	
//	public List<String> getAllNodeTemplateIdsAsQNames() {
//		List<String> qnamesAsString = new ArrayList<String>();
//		Iterator<QName> iter = getAllNodeTemplateIds().iterator();
//		
//		while (iter.hasNext()) {
//			qnamesAsString.add(iter.next().toString());
//		}
//		return qnamesAsString;
//		
//	}
	
//	public List<String> getAllNodeTemplateIdsAsLocalNames() {
//		List<String> localNames = new ArrayList<String>();
//		Iterator<QName> iter = getAllNodeTemplateIds().iterator();
//		
//		while (iter.hasNext()) {
//			localNames.add(iter.next().getLocalPart());
//		}
//		return localNames;
//		
//	}
	
	/**
	 * @return The set union of all ids of node templates used by the task.
	 */
	public List<QName> getAllNodeTemplateIds() {
		Set<QName> allNodeTemplates = getNodeTemplateIds(getInputParameters());
		allNodeTemplates.addAll(getNodeTemplateIds(getOutputParameters()));
		return new ArrayList<QName>(allNodeTemplates);
	}
	
	/**
	 * Helper that returns a set of ids of the node templates used by the parameters.  
	 * @param parameters
	 * @return Set of node template ids as QNames
	 */
	private Set<QName> getNodeTemplateIds(List<Parameter> parameters) {
		Set<QName> nodeTemplateIds = new HashSet<QName>();
		
		Iterator<Parameter> iter = parameters.iterator();
		while (iter.hasNext()) {
			Parameter parameter = (Parameter) iter.next();
			/* Just topology parameter refer to node templates */
			if (parameter instanceof TopologyParameter) {
				nodeTemplateIds.add(((TopologyParameter) parameter).getNodeTemplateId());
				
			}
			
		}
		return nodeTemplateIds;
	
	}

}
