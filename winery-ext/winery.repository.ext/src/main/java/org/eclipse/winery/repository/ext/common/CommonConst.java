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
package org.eclipse.winery.repository.ext.common;

/**
 * @author 10090474
 *
 */
public class CommonConst {
	
	//common
    public static final String TOSCA_PREFIX="tosca";
    public static final String TOSCA_NS="http://docs.oasis-open.org/tosca/ns/2011/12";
    
    public static final String WINERY_PREFIX="winery";
    public static final String WINERY_NS="http://www.opentosca.org/winery/extensions/tosca/2013/02/12";
   
    public static final String WINERY_MODEL_PREFIX="winerymodel";
    public static final String WINERY_MODEL_NS="http://www.eclipse.org/winery/model/selfservice";
    
    public static final String DEFINITIONS_ID="definitionId";
    public static final String DEFINITIONS_NAME="definitionName";
    
    public static final String PLAN_TYPE="http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan";
    public static final String PLAN_LANGUAGE="http://docs.oasis-open.org/wsbpel/2.0/process/executable";
       
    //nfv
    public static final String VNF_NS="http://www.zte.com.cn/tosca/nfv/vnf";
    public static final String VNF_PREFIX="ztevnf";    

    public static final String TARGET_PREFIX="ztenfv";
    public static final String TARGET_NS="http://www.zte.com.cn/tosca/nfv";
    
    public static final String SERVICE_NS="http://www.zte.com.cn/paas/service";
    public static final String SERVICE_PREFIX="passservice";
    
    public static final String NS_NS="http://www.zte.com.cn/tosca/nfv/ns";
    public static final String NS_PREFIX="ztens";
    
    public static final String TOSCA_IMPORT_TYPE = "http://docs.oasis-open.org/tosca/ns/2011/12";
	public static final String XMLSCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	
    public static final String PROPERTIES_DEFINITION="propertiesdefinition/winery";
    public static final String PROPERTIES_DEFINITION_PREFIX="ztedefinition";
    public static final String PROPERTIES_LOCALPART="Properties";
    
    public static final String SERVICE_TEMPLATE_ID="servicetemplate";
    public static final String SERVICE_TEMPLATE_NAME="ServiceTemplate"; 
      
    //NodeTemplate Position
    public static final String NODETEMPLATE_POSITIONX_LOCALPART = "x";
    public static final String NODETEMPLATE_POSITIONY_LOCALPART = "y";
    public static final String NODETEMPLATE_POSITION_NAMESPACE = "http://www.opentosca.org/winery/extensions/tosca/2013/02/12";
    public static final int NODETEMPLATE_POSITIONX_DEFAULTVALUE = 100;
    public static final int NODETEMPLATE_POSITIONY_DEFAULTVALUE = 100;
    public static final int NODETEMPLATE_POSITION_INTERVAL = 100;
    //////////////////////////////////////////////////////////////////////////////////
    
    public static String getNSByType(String elementName, String defaultNameSpace)
    {
       	if(elementName==null || elementName.isEmpty())
       	{
       		return "";
       	}
       	
       	if(elementName.contains(".Service"))
       	{
       		return SERVICE_NS;
       	}
       	
       	if(elementName.contains(".VNF"))
       	{
       		return VNF_NS;
       	}
       	
       	if(elementName.contains(".NS"))
       	{
       		return NS_NS;
       	}
       	
       	return defaultNameSpace;
    }
    
    public static String getPrefixByType(String elementName)
    {
       	if(elementName==null || elementName.isEmpty())
       	{
       		return "";
       	}
       	
       	if(elementName.contains(".Service"))
       	{
       		return SERVICE_PREFIX;
       	}
       	
       	if(elementName.contains(".VNF"))
       	{
       		return VNF_PREFIX;
       	}
       	
       	if(elementName.contains(".NS"))
       	{
       		return NS_PREFIX;
       	}
       	
       	return TARGET_PREFIX;
    }
    //////////////////////////////////////////////////////////////////////////////////
    
    /**
     * The XML-Namespace of XML-Schemas.
     */
 //   public static final String XMLSCHEMA_NS = "http://www.w3.org/2001/XMLSchema";

    /**
     * The XML-Namespace of the created document.
     */
    public static final String NS = "http://www.example.org/tosca/yamlgen";

    /**
     * The default user input.
     */
    public static final String DEFAULT_USER_INPUT = "DEFAULTUSERINPUT";

    /**
     * The XML-Namespace of the types.
     */
    public static final String TYPES_NS = "http://www.example.org/tosca/yamlgen/types";



    public static final String SPECIFIC_TYPES_NS = "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes";

    public static final String BASE_TYPES_NS = "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes";

    public static final String TOSCA_NS_PREFIX = "tosca";
    public static final String BASE_TYPES_PREFIX = "ns1";
    public static final String SPECIFIC_TYPES_PREFIX = "ns2";

    public static final String TARGET_NS_PREFIX = "target";

}
