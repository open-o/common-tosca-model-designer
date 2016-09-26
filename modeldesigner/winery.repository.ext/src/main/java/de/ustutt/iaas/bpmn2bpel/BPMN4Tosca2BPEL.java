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
package de.ustutt.iaas.bpmn2bpel;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ustutt.iaas.bpmn2bpel.model.ManagementFlow;
import de.ustutt.iaas.bpmn2bpel.model.param.Parameter;
import de.ustutt.iaas.bpmn2bpel.parser.BPMN4JsonParser;
import de.ustutt.iaas.bpmn2bpel.parser.ParseException;
import de.ustutt.iaas.bpmn2bpel.planwriter.BpelPlanArtefactWriter;
import de.ustutt.iaas.bpmn2bpel.planwriter.PlanWriterException;

public class BPMN4Tosca2BPEL {

    public static final String FILE_NAME_PLAN = "management_plan.bpel";

    public static final String FILE_NAME_PLAN_WSDL = "management_plan.wsdl";

    public static final String FILE_NAME_INVOKER_WSDL = "invoker.wsdl";

    public static final String FILE_NAME_INVOKER_XSD = "invoker.xsd";

    public static final String FILE_NAME_DEPLOYMENT_DESC = "deploy.xml";

    public static final String DIR_NAME_TEMP_BPMN4TOSCA = "bpmn4tosca";


    private static Logger log = LoggerFactory.getLogger(BPMN4Tosca2BPEL.class);

    /**
     * Transforms the given BPMN4Tosca Json management into a BPEL management plan that can be
     * enacted with the OpenTosca runtime.
     * <p>
     * The created zip file contains the following files
     * 
     * - bpel plan - plan wsdl - sevice invoker wsdl - service invoker xsd - deployment descriptor
     * generated BPEL plan, the corresponding WSDL files and a deployment descriptor.
     * 
     * 
     * @param srcBpmn4ToscaJsonFile
     * @param targetBPELArchive
     *
     * @throws ParseException
     * @throws PlanWriterException
     */
    public void transform(URI srcBpmn4ToscaJsonFile, URI targetBPELArchive, String planName,
            String planNameSpace, String csarID, String serviceTemplateIDNamespaceURI,
            String serviceTemplateIDLocalPart) throws ParseException, PlanWriterException {
        // log.debug("Transforming ");

        BPMN4JsonParser parser = new BPMN4JsonParser();// Todo Statisch machen
        ManagementFlow managementFlow = parser.parse(srcBpmn4ToscaJsonFile);

        List<Parameter> varList = parser.getVarList();
        List<String> iaVarList = parser.getIAVarList();

        List<Path> planArtefactPaths = new ArrayList<Path>();
        try {
            Path tempPath = FileUtil.createTempDir(DIR_NAME_TEMP_BPMN4TOSCA);


            BpelPlanArtefactWriter writer = new BpelPlanArtefactWriter(managementFlow);

            String plan =
                    writer.completePlanTemplate(planName, planNameSpace, csarID,
                            serviceTemplateIDNamespaceURI, serviceTemplateIDLocalPart, varList,
                            iaVarList);
            // planArtefactPaths.add(FileUtil.writeStringToFile(plan, Paths.get(tempPath.toString(),
            // FILE_NAME_PLAN))); // modified by lvbo 20160105
            planArtefactPaths.add(FileUtil.writeStringToFile(plan,
                    Paths.get(tempPath.toString(), planName + ".bpel")));

            String planWsdl = writer.completePlanWsdlTemplate(planName, planNameSpace);
            // planArtefactPaths.add(FileUtil.writeStringToFile(planWsdl,
            // Paths.get(tempPath.toString(), FILE_NAME_PLAN_WSDL))); // modified by lvbo 20160105
            planArtefactPaths.add(FileUtil.writeStringToFile(planWsdl,
                    Paths.get(tempPath.toString(), planName + ".wsdl")));

            String invokerWsdl = writer.completeInvokerWsdlTemplate();
            planArtefactPaths.add(FileUtil.writeStringToFile(invokerWsdl,
                    Paths.get(tempPath.toString(), FILE_NAME_INVOKER_WSDL)));

            String invokerXsd = writer.completeInvokerXsdTemplate();
            planArtefactPaths.add(FileUtil.writeStringToFile(invokerXsd,
                    Paths.get(tempPath.toString(), FILE_NAME_INVOKER_XSD)));

            String deploymentDesc =
                    writer.completeDeploymentDescriptorTemplate(planName, planNameSpace);
            planArtefactPaths.add(FileUtil.writeStringToFile(deploymentDesc,
                    Paths.get(tempPath.toString(), FILE_NAME_DEPLOYMENT_DESC)));

            log.debug("Creating BPEL Archive...");
            FileUtil.createApacheOdeProcessArchive(Paths.get(targetBPELArchive), planArtefactPaths);
        } catch (Exception e) {
            throw new PlanWriterException(e);
        } finally {
            /* Delete created plan artifact files from temp directory */
            try {
                log.debug("Deleting temporary plan artefact files...");
                FileUtil.deleteFiles(planArtefactPaths);
            } catch (IOException e) {
                throw new PlanWriterException(e);
            }
        }


    }



}
