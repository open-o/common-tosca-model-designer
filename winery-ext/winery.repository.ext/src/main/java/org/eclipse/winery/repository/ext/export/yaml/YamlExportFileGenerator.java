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
package org.eclipse.winery.repository.ext.export.yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.common.ids.elements.PlansId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.ext.export.custom.ExportFileGenerator;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.utils.YamlBeansUtils;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.esotericsoftware.yamlbeans.emitter.EmitterException;
import com.esotericsoftware.yamlbeans.parser.Event;

/**
 * @author 10090474
 *
 */
public class YamlExportFileGenerator extends ExportFileGenerator {
    private static final Logger logger = LoggerFactory
            .getLogger(YamlExportFileGenerator.class);

    /* (non-Javadoc)
     * @see org.eclipse.winery.repository.ext.export.custom.ExportFileGenerator#makeFile(org.eclipse.winery.model.tosca.Definitions, java.io.OutputStream)
     */
    @Override
    public String[] makeFile(Definitions definition, OutputStream out) {
        logger.info("makeFile begin.");
        String[] yamlFullFileName = new String[] {};
        TServiceTemplate serviceTemplate = getTServiceTemplate(definition);
        try {
            yamlFullFileName = makeModelFile(definition, out);
            if (serviceTemplate != null) {
                makeIAFile(serviceTemplate, out);
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (ExportCommonException e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            try {
                closeEntry((ArchiveOutputStream) out);
            } catch (IOException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }

        logger.info("makeFile end. yamlFileName = " + yamlFullFileName);
        return yamlFullFileName;
    }

    private String[] makeModelFile(Definitions definition, OutputStream out)
            throws IOException, ExportCommonException {
        Xml2YamlSwitch switcher = new Xml2YamlSwitch(definition);
        ServiceTemplate st = switcher.convert();

        String yamlFileName = buildYamlFileName(definition);
        String yamlFullFileName = "Definitions/" + yamlFileName;
        createEntry((ArchiveOutputStream) out, yamlFullFileName);
        write2OutputStreamWithoutTags(st, out);
        // write2OutputStream(st, out);
        logger.info("makeFile end. yamlFileName = " + yamlFullFileName);
        return new String[] { yamlFullFileName };
    }
    
    private void makeIAFile(TServiceTemplate serviceTemplate, OutputStream out) throws IOException {
        String iaDirName = "ImplementArtifact";

        if (serviceTemplate != null && serviceTemplate.getPlans() != null && serviceTemplate.getPlans().getPlan() != null) {
            List<TPlan> plans = serviceTemplate.getPlans().getPlan();
            for (TPlan plan : plans) {
                PlanId planId = getPlanId(serviceTemplate, plan);
                writeFileToZip(iaDirName, out, plan, planId);
            }
        }
    }

    private void writeFileToZip(String iaDirName, OutputStream out, TPlan plan, PlanId planId)
            throws FileNotFoundException, IOException {
        if (Repository.INSTANCE.exists(planId)) {
            RepositoryFileReference fileRef = getFileRef(plan, planId);
            createEntry((ArchiveOutputStream) out,
                    iaDirName + File.separator + fileRef.getFileName());
            InputStream is = Repository.INSTANCE.newInputStream(fileRef);
            IOUtils.copy(is, out);
        }
    }

    private PlanId getPlanId(TServiceTemplate serviceTemplate, TPlan plan) {
        ServiceTemplateId stId =
                new ServiceTemplateId(serviceTemplate.getTargetNamespace(),
                        serviceTemplate.getId(), false);
        PlansId psId = new PlansId(stId);
        PlanId pId = new PlanId(psId, new XMLId(plan.getId(), false));
        return pId;
    }

    private RepositoryFileReference getFileRef(TPlan plan, PlanId planId) {
        String reference = plan.getPlanModelReference().getReference();
        File f = new File(reference);
        return new RepositoryFileReference(planId, f.getName());
    }

    private TServiceTemplate getTServiceTemplate(Definitions definition) {
        List<?> tNodeList = definition.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        if (tNodeList != null && !tNodeList.isEmpty()) {
            for (Object tNode : tNodeList) {
                if (tNode instanceof TServiceTemplate) {
                    return (TServiceTemplate) tNode;
                }
            }
        }

        return null;
    }
    
    private static void write2OutputStreamWithoutTags(ServiceTemplate st,
            OutputStream out) throws ExportCommonException {
        String yamlStr = YamlBeansUtils.convertToYamlStr(st);
        yamlStr = yamlStr.replaceAll("!.+\n", "\n");
        // yamlStr = yamlStr.replaceAll("defaultValue:", "default:");
        try {
            OutputStreamWriter osWriter = new OutputStreamWriter(out);
            osWriter.write(yamlStr);
            osWriter.flush();
        } catch (IOException e) {
            throw new ExportCommonException("Write to file failed.", e);
        }
    }

    // public static void main(String[] args) throws FileNotFoundException,
    // ExportCommonException {
    // ServiceTemplate st = new ServiceTemplate();
    // Info info1 = new Info("hunter", "22");
    // Info info2 = new Info("alex", "11");
    // st.getDsl_definitions().put("hunter", info1);
    // st.getDsl_definitions().put("alex", info2);
    //
    // st.getMetadata().put("hunter_m", info1);
    // st.getMetadata().put("alex_m", info2);
    //
    // FileOutputStream out = new FileOutputStream("E:\\1.yml");
    // write2OutputStreamWithoutTags(st, out);
    // }
    //
    // public static class Info {
    // String name;
    // String age;
    //
    // public Info() {
    // }
    //
    // public Info(String name, String age) {
    // super();
    // this.name = name;
    // this.age = age;
    // }
    //
    // public String getName() {
    // return name;
    // }
    //
    // public void setName(String name) {
    // this.name = name;
    // }
    //
    // public String getAge() {
    // return age;
    // }
    //
    // public void setAge(String age) {
    // this.age = age;
    // }
    // }

    private void write2OutputStream(ServiceTemplate st, OutputStream out) {
        YamlWriter writer = null;
        try {
            OutputStreamWriter osWriter = new OutputStreamWriter(out);
            writer = new YamlWriter(osWriter);
            writer.getConfig().writeConfig.setWriteRootTags(false);
            writer.getConfig().writeConfig.setWriteRootElementTags(false);
            writer.getConfig().writeConfig.setAutoAnchor(false);
            writer.getConfig().writeConfig.setIndentSize(2);
            YamlBeansUtils.setPropertyDefaultType(writer.getConfig());

            writer.write(st);
        } catch (final YamlException e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            if (writer != null) {
                closeYamlWriter(writer);
            }
        }
    }

    private void closeYamlWriter(YamlWriter writer) {
        try {
            writer.clearAnchors();
            // writer.defaultValuePrototypes.clear();
            writer.getEmitter().emit(Event.STREAM_END);
            // writer.getEmitter().close();
        } catch (final YamlException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (EmitterException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private String buildYamlFileName(Definitions definition)
            throws ExportCommonException {
        List<?> tNodeList = definition
                .getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        if (tNodeList == null || tNodeList.isEmpty()) {
            throw new ExportCommonException(
                    "ServiceTemplateOrNodeTypeOrNodeTypeImplementation is empty.");
        }

        Object tNode = tNodeList.get(0);
        if (tNode instanceof TServiceTemplate) {
            String id = ((TServiceTemplate) tNode).getId();
            String namespace = ((TServiceTemplate) tNode).getTargetNamespace();
            return buildYamlFileName(id, namespace);
        }

        if (tNode instanceof TEntityType) {
            String id = ((TEntityType) tNode).getName();
            String namespace = ((TEntityType) tNode).getTargetNamespace();
            return buildYamlFileName(id, namespace);
        }

        throw new ExportCommonException("Unrecognized Node.");
        // String yamlFileName = definition.getId() + ".yml";
        // return yamlFileName.replaceFirst("^winery-defs-for_",
        // "").replaceFirst(
        // "-", "__");
    }

    private String buildYamlFileName(String id, String namespace) {
        return NamespacesResource.getPrefix(namespace) + "__"
                + (new XMLId(id, false)).getEncoded() + ".yml";
    }
}
