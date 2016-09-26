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
package de.ustutt.iaas.bpmn2bpel.planwriter;

public interface TemplateDefaultValues {
	
public static final String PLAN_NAMESPACE = "http://iaas.uni-stuttgart.de/bpmn4tosca";
	
	public static final String PLAN_NAME = "ManagementPlan";
	
	public static final String PLAN_WSDL_NAME = "managementplan.wsdl";
	
	public static final String PLT_CLIENT_NAME = "tns:PLT_ManagementPlan";
	
	public static final String PL_CLIENT_NAME = "client";
	
	public static final String PL_CLIENT_MY_ROLE = "PlanProvider";
	
	public static final String PL_CLIENT_PARTNER_ROLE = "PlanRequester";
	
	public static final String PLAN_INPUT_VAR_MSG_TYPE = "tns:PlanRequestMessage";
	
	public static final String PLAN_INPUT_VAR_NAME = "input";
	
	public static final String PLAN_INIT_RCV_NAME = "initiatePlan";
	
	public static final String PLAN_INIT_RCV_OPERATION = "initiate";
	
	public static final String PLAN_INIT_RCV_PORT_TYPE = "tns:ManagementPlanPT";
	
	public static final String PLAN_OUTPUT_VAR_NAME = "output";
	
	public static final String PLAN_OUTPUT_VAR_MSG_TYPE = "tns:PlanResponseMessage";
	
	public static final String PLAN_END_INV_NAME = "callbackClient";
	
	public static final String PLAN_END_INV_OPERATION = "onResult";
	
	public static final String PLAN_END_INV_PORT_TYPE = "tns:ManagementPlanCallbackPT";
	
	public static final String SERVICE_INVOKER_WSDL = "service_invoker.wsdl";
	
	public static final String SERVICE_INVOKER_XSD = "service_invoker.xsd";
	
	

}
