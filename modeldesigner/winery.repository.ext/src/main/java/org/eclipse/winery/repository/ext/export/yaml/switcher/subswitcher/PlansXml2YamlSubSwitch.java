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
package org.eclipse.winery.repository.ext.export.yaml.switcher.subswitcher;


import java.util.List;
import java.util.Map.Entry;

import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.Input;
import org.eclipse.winery.repository.ext.yamlmodel.Plan;

/**
 * 
 * @author 10090474
 *
 */
public class PlansXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {

	public PlansXml2YamlSubSwitch(Xml2YamlSwitch parentSwitch) {
		super(parentSwitch);
	}

    /**
     * 
     */
	@Override
	public void process() {
        TServiceTemplate tServiceTemplate = getTServiceTemplate();
        if (tServiceTemplate == null) {
              return;
        }
        
        TPlans tPlans = tServiceTemplate.getPlans();
        if (tPlans == null || tPlans.getPlan() == null
                || tPlans.getPlan().isEmpty()) {
            return;
        }

        List<TPlan> tPlanList = tPlans.getPlan();
        for (TPlan tPlan : tPlanList) {
            Entry<String, Plan> yplan = convert2Plan(tPlan);
            getServiceTemplate().getPlans().put(yplan.getKey(), yplan.getValue());
        }
	}

    /**
     * @param tPlan
     * @return
     */
    private Entry<String, Plan> convert2Plan(TPlan tPlan) {
      Plan yplan = new Plan();
      
      // description
      yplan.setDescription(Xml2YamlSwitchUtils.convert2Description(tPlan.getDocumentation()));
      // reference
      yplan.setReference(tPlan.getPlanModelReference().getReference());
      // planLanguage
      yplan.setPlanLanguage(validataPlanLanguage(tPlan.getPlanLanguage()));
      // inputs
      TPlan.InputParameters tinputs = tPlan.getInputParameters();
      if (tinputs != null && tinputs.getInputParameter() != null && !tinputs.getInputParameter().isEmpty()) {
        for (TParameter tinput: tinputs.getInputParameter()) {
          Input yinput = new Input();
          yinput.setType(Xml2YamlSwitchUtils.convert2YamlType(tinput.getType()));
          yinput.setEntry_schema(Xml2YamlSwitchUtils.convert2YamlEntrySchema(tinput.getType()));
          yinput.setRequired(Xml2YamlSwitchUtils.convert2YamlValue(tinput.getRequired()));
          yplan.getInputs().put(tinput.getName(), yinput);
        }
      }

      
      return Xml2YamlSwitchUtils.buildEntry(tPlan.getName(), yplan);
    }

    /**
     * @param planLanguage
     * @return
     */
    private String validataPlanLanguage(String planLanguage) {
      if (planLanguage == null || planLanguage.equals("")) {
        return "bpmn4tosca";
      }
      
      if (planLanguage.toLowerCase().indexOf("bpel") > 0) {
        return "bpel";
      }
      
      return "bpmn4tosca";
    }

}
