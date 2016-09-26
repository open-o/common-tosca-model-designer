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
package de.ustutt.iaas.bpmn2bpel.parser;

public interface JsonKeys {
	
	
	/*
	 * Field names of BPMN4Tosca Model
	 */
	
	public static final String NAME = "name";
	
	public static final String ID = "id";
	
	public static final String TYPE = "type";
	
	public static final String INPUT = "input";
	
	public static final String OUTPUT = "output";
	
	public static final String VALUE = "value";
	
	public static final String NODE_TEMPLATE = "node_template";
	
	public static final String NODE_OPERATION = "node_operation";
	
	public static final String NODE_INTERFACE_NAME = "node_interface";
	
	public static final String CONNECTIONS = "connections";
	
	//add by qinlihan
	public static final String TASK_TYPE_DETAIL_REST = "rest";
	public static final String TASK_TYPE_DETAIL_IA = "ia";
	
	public static final String OPERATION_TYPE = "operation_type";
	
	public static final String REST_METHOD = "method";
	public static final String REST_URL = "url";
	public static final String REST_ACCEPT = "accept";
	public static final String REST_CONTENT_TYPE = "contentType";
	public static final String REST_REQUEST_BODY = "requestBody";
	public static final String PATH_PARAMS = "path";
	public static final String QUERY_PARAMS = "query";
	public static final String BODY_PARAMS = "body";
	
	public static final String DECISION_CONDITIONS = "conditions";
	public static final String CONDITION = "condition";
	public static final String DECISION_CONDITION_ID = "id";
	public static final String DECISION_CONDITION_INDEX = "index";
	
	public static final String VARIABLES = "variable";
	public static final String ASSIGN_PARAMS = "params";
	public static final String EXTENSION = "extension";
	
	/*
	 * Event, Management-Task Types 
	 * 
	 */
	public static final String TASK_TYPE_MGMT_TASK = "ToscaNodeManagementTask";
	
	public static final String TASK_TYPE_START_EVENT = "StartEvent";
	
	public static final String TASK_TYPE_END_EVENT = "EndEvent";
	
	//add by qinlihan
	public static final String TASK_TYPE_REST_TASK = "RestTask";
	public static final String TASK_TYPE_MERGE = "Merge";
	public static final String TASK_TYPE_DECISION = "Decision";
	public static final String TASK_TYPE_FORK = "Fork";
	public static final String TASK_TYPE_JOIN = "Join";
	public static final String TASK_TYPE_WHILE = "While";
	public static final String TASK_TYPE_REPEAT_UNTIL = "RepeatUntil";
	public static final String TASK_TYPE_LOOP = "Loop";
	public static final String TASK_TYPE_ASSIGN = "Assign";
	
	/*
	 * Parameter Types
	 */
	public static final String PARAM_TYPE_VALUE_STRING = "string";
	
	public static final String PARAM_TYPE_VALUE_TOPOLOGY = "topology";
	
	public static final String PARAM_TYPE_VALUE_PLAN = "plan";
	
	public static final String PARAM_TYPE_VALUE_CONCAT = "concat";
	
	public static final String PARAM_TYPE_VALUE_IA = "implementation_artifact";
	
	public static final String PARAM_TYPE_VALUE_DA = "deployment_artifact";

    public static final String PARAM_TYPE_VALUE_EXPRESSION = "expression";
}
