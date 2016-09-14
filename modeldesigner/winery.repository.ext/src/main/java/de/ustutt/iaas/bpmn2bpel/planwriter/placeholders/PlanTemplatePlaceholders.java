/**
 * Copyright 2016 [ZTE] and others.
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
package de.ustutt.iaas.bpmn2bpel.planwriter.placeholders;

public interface PlanTemplatePlaceholders {
	
	public static final String PLAN_NAMESPACE = "plan_namespace";
	
	public static final String PLAN_NAME = "plan_name";
	
	public static final String PLAN_WSDL_NAME = "plan_wsdl_name";
	
	public static final String PLT_CLIENT_NAME = "plt_client_name";
	
	public static final String PL_CLIENT_NAME = "pl_client_name";
	
	public static final String PL_CLIENT_MY_ROLE = "pl_client_pl_myrole_name";
	
	public static final String PL_CLIENT_PARTNER_ROLE = "pl_client_partnerrole_name";
	
	public static final String PLAN_INPUT_VAR_MSG_TYPE = "plan_input_var_msg_type";
	
	public static final String PLAN_INPUT_VAR_NAME = "plan_input_var_name";
	
	public static final String PLAN_INIT_RCV_NAME = "plan_init_rcv_name";
	
	public static final String PLAN_INIT_RCV_OPERATION = "plan_init_rcv_operation";
	
	public static final String PLAN_INIT_RCV_PORT_TYPE = "plan_init_rcv_pt";
	
	public static final String PLAN_OUTPUT_VAR_NAME = "plan_output_var_name";
	
	public static final String PLAN_OUTPUT_VAR_MSG_TYPE = "plan_output_var_msg_type";
	
	public static final String PLAN_END_INV_NAME = "plan_end_inv_name";
	
	public static final String PLAN_END_INV_OPERATION = "plan_end_inv_operation";
	
	public static final String PLAN_END_INV_PORT_TYPE = "plan_end_inv_callback_pt";
	
	public static final String SERVICE_INVOKER_WSDL = "service_invoker_wsdl";
	
	public static final String SERVICE_INVOKER_XSD = "service_invoker_xsd";
	

}
