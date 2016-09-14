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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.common.CommonConst;
import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;

public class PlansYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {

	private final TServiceTemplate tServiceTemplateInstance;
	
    public PlansYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch,TServiceTemplate tServiceTemplateInstance) {
        super(parentSwitch);
        this.tServiceTemplateInstance = tServiceTemplateInstance;
    }
    
	@Override
	public void process() {
		// TODO Auto-generated method stub
		
		TPlans tPlans = new TPlans();
		
		TPlan tPlan = new TPlan();
		//attribute-id,name,plan type,plan language
		tPlan.setId("placeholder");
		tPlan.setName("placeholder");
		tPlan.setPlanType(CommonConst.PLAN_TYPE);
		tPlan.setPlanLanguage(CommonConst.PLAN_LANGUAGE);
		
		//element-plan model reference
		TPlan.PlanModelReference tPlanRef = new TPlan.PlanModelReference();
		tPlanRef.setReference("placeholder");
		
		tPlans.getPlan().add(tPlan);
		
	//	this.tServiceTemplateInstance.setPlans(tPlans);
		
	}

}
