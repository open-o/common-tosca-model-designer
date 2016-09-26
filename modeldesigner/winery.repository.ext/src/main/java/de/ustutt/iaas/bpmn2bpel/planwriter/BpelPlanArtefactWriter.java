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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ustutt.iaas.bpmn2bpel.model.EndTask;
import de.ustutt.iaas.bpmn2bpel.model.Link;
import de.ustutt.iaas.bpmn2bpel.model.ManagementFlow;
import de.ustutt.iaas.bpmn2bpel.model.ManagementTask;
import de.ustutt.iaas.bpmn2bpel.model.Node;
import de.ustutt.iaas.bpmn2bpel.model.StartTask;
import de.ustutt.iaas.bpmn2bpel.model.Task;
import de.ustutt.iaas.bpmn2bpel.model.param.Parameter;
import de.ustutt.iaas.bpmn2bpel.parser.JsonKeys;

public class BpelPlanArtefactWriter {

    private ManagementFlow mangagementFlow;

    public static String TEMPLATE_PATH = "./src/main/resources/templates/";

    private static Logger log = LoggerFactory.getLogger(BpelPlanArtefactWriter.class);

    public BpelPlanArtefactWriter(ManagementFlow mangagementFlow) {
        this.mangagementFlow = mangagementFlow;
        try {
            Velocity.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String completePlanTemplate(String planName, String planNameSpace, String csarID,
            String serviceTemplateIDNamespaceURI, String serviceTemplateIDLocalPart,
            List<Parameter> variables, List<String> iaResponseVariables)
            throws ResourceNotFoundException, ParseErrorException, Exception {
        log.debug("Completing BPEL process template");

        /*
         * Traverse the management flow and add the management tasks in the order of their execution
         * to a list
         */
        List<Task> taskSeq = new ArrayList<Task>();
        List<String> inputVariables = new ArrayList<String>();

        GraphIterator<Node, Link> iterator = new DepthFirstIterator<Node, Link>(mangagementFlow);
        while (iterator.hasNext()) {
            Node node = iterator.next();
            /* In this version the templates do only support management tasks */
            if (node instanceof Task) {

                // modify by qinlihan
                Task manageTask = (Task) node;

                if (JsonKeys.TASK_TYPE_DETAIL_IA.equals(manageTask.getTaskTypeDetail())) {
                    ManagementTaskTemplateWrapper taskWrapper =
                            new ManagementTaskTemplateWrapper((ManagementTask) manageTask);
                    taskSeq.add(taskWrapper);
                } else if (JsonKeys.TASK_TYPE_START_EVENT.equals(manageTask.getTaskTypeDetail())) {
                    for (Parameter param : manageTask.getOutputParameters()) {
                        inputVariables.add(param.getName());
                    }
                } else {
                    taskSeq.add(manageTask);
                }
            }
        }

        VelocityContext context = new VelocityContext();
        // Template planTemplate = Velocity.getTemplate(TEMPLATE_PATH +
        // "bpel_management_plan_template.xml");
        Template planTemplate = getTemplate("bpel_management_plan_template.xml");
        context.put("mngmtTaskList", taskSeq);

        // added by lvbo 20160105
        context.put("planName", planName);
        context.put("planNameSpace", planNameSpace);
        context.put("csarID", csarID);
        context.put("serviceTemplateIDNamespaceURI", serviceTemplateIDNamespaceURI);
        context.put("serviceTemplateIDLocalPart", serviceTemplateIDLocalPart);

        context.put("inputVariables", inputVariables);
        context.put("iaResponseVariables", iaResponseVariables);
        context.put("variables", variables);

        StringWriter planWriter = new StringWriter();
        planTemplate.merge(context, planWriter);

        String bpelProcessContent = planWriter.toString();

        log.debug("Completed BPEL process template" + bpelProcessContent);

        return bpelProcessContent;

    }

    public String completePlanWsdlTemplate(String planName, String planNameSpace)
            throws ResourceNotFoundException, ParseErrorException, Exception {
        log.debug("Completing BPEL WSDL template");

        VelocityContext context = new VelocityContext();

        // add by lvbo 20160105
        /*
         * Traverse the management flow and add the management tasks in the order of their execution
         * to a list
         */
        GraphIterator<Node, Link> iterator = new DepthFirstIterator<Node, Link>(mangagementFlow);
        StartTask startTask = null;
        EndTask endTask = null;
        while (iterator.hasNext()) {
            Node node = iterator.next();
            /* In this version the templates do only support management tasks */
            if (node instanceof StartTask) {
                startTask = (StartTask) node;
            } else if (node instanceof EndTask) {
                endTask = (EndTask) node;
            }
        }

        context.put("planName", planName);
        context.put("planNameSpace", planNameSpace);
        context.put("inputParams", startTask.getOutputParameters());
        context.put("outputParam", endTask.getOutputParameters());

        // Template wsdlTemplate = Velocity.getTemplate(TEMPLATE_PATH +
        // "management_plan_wsdl_template.xml");
        Template wsdlTemplate = getTemplate("management_plan_wsdl_template.xml");

        StringWriter wsdlWriter = new StringWriter();
        wsdlTemplate.merge(context, wsdlWriter);

        String bpelProcessWSDL = wsdlWriter.toString();

        log.debug("Completed BPEL WSDL template" + bpelProcessWSDL);

        return bpelProcessWSDL;
    }

    public String completeInvokerWsdlTemplate() throws ResourceNotFoundException,
            ParseErrorException, Exception {
        log.debug("Retrieving service invoker WSDL");

        VelocityContext context = new VelocityContext();
        // Template invokerWsdlTemplate = Velocity.getTemplate(TEMPLATE_PATH +
        // "invoker.wsdl");
        Template invokerWsdlTemplate = getTemplate("invoker.wsdl");

        StringWriter wsdlWriter = new StringWriter();
        invokerWsdlTemplate.merge(context, wsdlWriter);

        return wsdlWriter.toString();
    }

    public String completeInvokerXsdTemplate() throws ResourceNotFoundException,
            ParseErrorException, Exception {
        log.debug("Retrieving service invoker XSD");

        VelocityContext context = new VelocityContext();
        // Template invokerXsdTemplate = Velocity.getTemplate(TEMPLATE_PATH +
        // "invoker.xsd");
        Template invokerXsdTemplate = getTemplate("invoker.xsd");

        StringWriter xsdWriter = new StringWriter();
        invokerXsdTemplate.merge(context, xsdWriter);

        return xsdWriter.toString();
    }

    public String completeDeploymentDescriptorTemplate(String planName, String planNameSpace)
            throws ResourceNotFoundException, ParseErrorException, Exception {
        log.debug("Retrieving Apache ODE deployment descriptor");

        VelocityContext context = new VelocityContext();

        // add by lvbo 20160105
        context.put("planName", planName);
        context.put("planNameSpace", planNameSpace);

        // Template invokerXsdTemplate = Velocity.getTemplate(TEMPLATE_PATH +
        // "deploy.xml");
        Template invokerXsdTemplate = getTemplate("deploy.xml");

        StringWriter xsdWriter = new StringWriter();
        invokerXsdTemplate.merge(context, xsdWriter);

        return xsdWriter.toString();
    }

    private Template getTemplate(String fileName) {
        // if(fromJar) {
        return getTemplateInClass(fileName);
        // } else {
        // return Velocity.getTemplate(TEMPLATE_PATH + fileName);
        // }

    }

    private Template getTemplateInClass(String templateName) {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        Template t = null;
        try {
            ve.init();
            t = ve.getTemplate("../templates/" + templateName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

}
