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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.common.ids.elements.PlansId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.yamlmodel.Input;
import org.eclipse.winery.repository.ext.yamlmodel.Plan;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplatesResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author 10090474
 *
 */
public class PlansXml2YamlSubSwitch extends AbstractXml2YamlSubSwitch {
  private static final Logger logger = LoggerFactory.getLogger(PlansXml2YamlSubSwitch.class);


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
      yplan.setReference(buildReference(tPlan));
      // planLanguage
      yplan.setPlanLanguage(validataPlanLanguage(tPlan.getPlanLanguage()));
      // inputs
      String s = readInputsInfoFromFile(tPlan);
      logger.info("ReadInputsInfoFromFile s = " + s);
      yplan.getInputs().putAll(parseInputs(s));
//      TPlan.InputParameters tinputs = tPlan.getInputParameters();
//      if (tinputs != null && tinputs.getInputParameter() != null && !tinputs.getInputParameter().isEmpty()) {
//        for (TParameter tinput: tinputs.getInputParameter()) {
//          Input yinput = new Input();
//          yinput.setType(Xml2YamlSwitchUtils.convert2YamlType(tinput.getType()));
//          yinput.setEntry_schema(Xml2YamlSwitchUtils.convert2YamlEntrySchema(tinput.getType()));
//          yinput.setRequired(Xml2YamlSwitchUtils.convert2YamlValue(tinput.getRequired()));
//          yplan.getInputs().put(tinput.getName(), yinput);
//        }
//      }
      
      return Xml2YamlSwitchUtils.buildEntry(tPlan.getName(), yplan);
    }
    
    /**
     * @param s
     * @return
     */
    private Map<String, Input> parseInputs(String s) {
      JsonArray jsonArray = new Gson().fromJson(s, JsonArray.class);
      if (jsonArray != null) {
        for (JsonElement jsonElement : jsonArray) {
          if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = (JsonObject)jsonElement;
            if (jsonObject.has("name") && jsonObject.has("output")) {
              String name = jsonObject.get("name").getAsString();
              if ("StartEvent".equalsIgnoreCase(name)) {
                JsonElement jInputs = jsonObject.get("output");
                if (jInputs.isJsonObject()) {
                  return parseInputs((JsonObject)jInputs);
                }
              }
            }
          }
        }
      }
      
      return new HashMap<>();
    }

    /**
     * @param jInputs
     * @return
     */
    private Map<String, Input> parseInputs(JsonObject jInputs) {
      Map<String, Input> inputs = new HashMap<>();
      Iterator<Entry<String, JsonElement>> iterator = jInputs.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, JsonElement> next = iterator.next();
        String name = next.getKey();
        JsonObject value = (JsonObject) next.getValue();
        
        Input input = new Input();
        input.setType(value.get("type").getAsString());
        input.setDefault(value.get("value").getAsString());
        
        inputs.put(name, input);
      }
      
      return inputs;
    }

    private final String INPUTS_INFO_FILE_NAME = "file.json";

    private String readInputsInfoFromFile(TPlan tPlan) {
      TServiceTemplate tst = getTServiceTemplate();
      ServiceTemplateResource str = new ServiceTemplatesResource().
          getComponentInstaceResource(Util.URLencode(tst.getTargetNamespace()), tst.getId());
      PlansId plansId = new PlansId((ServiceTemplateId) str.getId());
      PlanId planId = new PlanId(plansId, new XMLId(tPlan.getId(), false));
      
      InputStream ins = null;
      try {
        RepositoryFileReference rfr = new RepositoryFileReference(planId, INPUTS_INFO_FILE_NAME);
        ins = Repository.INSTANCE.newInputStream(rfr);
        byte b[] = new byte[1024*1024];   
        int len = 0;
        int temp = 0;
        while((temp = ins.read()) != -1){
            b[len]=(byte)temp;
            len ++;
        }
        return new String(b, 0, len); 
      } catch (IOException e) {
        logger.warn("Read inputs from file.json failed. Plan name = " + tPlan.getName(), e);
      } finally {
        if (ins != null) {
          try {
            ins.close();
          } catch (IOException e) {
          }
        }
      }
      
      return "";
    }

    private String buildReference(TPlan tPlan) {
      String fileName = tPlan.getOtherAttributes().get(new QName(Constants.PLAN_NAME));
      if (fileName != null && !fileName.isEmpty()) {
        return "plan/" + fileName;
      }
      
      String tReference = tPlan.getPlanModelReference().getReference();
      if (tReference == null) {
        return "";
      }

      return "plan/" + new File(tReference).getName();
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
