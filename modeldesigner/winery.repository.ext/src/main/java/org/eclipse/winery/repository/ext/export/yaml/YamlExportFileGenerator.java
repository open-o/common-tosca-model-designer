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
/**
 * 
 */
package org.eclipse.winery.repository.ext.export.yaml;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.ext.export.custom.DefinitionResultInfo;
import org.eclipse.winery.repository.ext.export.custom.ExportFileGenerator;
import org.eclipse.winery.repository.ext.export.yaml.switcher.Xml2YamlSwitch;
import org.eclipse.winery.repository.ext.utils.MD5Util;
import org.eclipse.winery.repository.ext.utils.YamlBeansUtils;
//import org.eclipse.winery.repository.ext.yamlmodel.DataType;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 10090474
 *
 */
public class YamlExportFileGenerator extends ExportFileGenerator {
  private static final Logger logger = LoggerFactory.getLogger(YamlExportFileGenerator.class);

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.winery.repository.ext.export.custom.ExportFileGenerator#makeFile(org.eclipse.
   * winery.model.tosca.Definitions, java.io.OutputStream)
   */
  @Override
  public DefinitionResultInfo[] makeFile(Definitions definitions, OutputStream out) {
    logger.info("makeFile begin.");
    try {
      List<DefinitionResultInfo> result = makeModelFiles(out, definitions);
      logger.info("makeFile success.");
      return result.toArray(new DefinitionResultInfo[result.size()]);
    } catch (IOException e) {
      logger.error("make file io fail.", e);
    } catch (ExportCommonException e) {
      logger.error("make file export fail.", e);
    } finally {
      closeOutputStream(out);
      logger.info("makeFile end.");
    }
    return new DefinitionResultInfo[0];
  }

  private void closeOutputStream(OutputStream out) {
    try {
      closeEntry((ArchiveOutputStream) out);
    } catch (IOException e) {
      logger.warn("close outputstream fail.", e);
    }
  }

  private List<DefinitionResultInfo> makeModelFiles(OutputStream out, Definitions definitions)
      throws IOException, ExportCommonException {
    Xml2YamlSwitch switcher = new Xml2YamlSwitch(definitions);
    ServiceTemplate st = switcher.convert();

    List<DefinitionResultInfo> defResultInfos = new ArrayList<>();
//    // DataTypes
//    defResultInfos.addAll(writeDataTypes(out, st, Utils.isServiceTemplateDefinition(definitions)));
    
    // plan to an Alone file
    DefinitionResultInfo dr = savePlanServiceTemplate2AloneFile(out, st);
    // Service Template
    ServiceTemplate restServiceTemplate = buildRestServiceTemplate(st);
    String yamlFilePath = buildYamlFilePath(definitions);
    defResultInfos.add(writeFile(out, restServiceTemplate, yamlFilePath, Utils.isServiceTemplateDefinition(definitions)));
    
    if (dr != null) defResultInfos.add(dr);

    return defResultInfos;
  }

  private DefinitionResultInfo savePlanServiceTemplate2AloneFile(OutputStream out,
      ServiceTemplate st) throws IOException, ExportCommonException {
    if (st.getPlans().isEmpty()) {
      return null;
    }
    
    String yamlFilePath = "Definitions" + File.separator + "plans.yaml";
    return writeFile(out, buildPlanServiceTemplate(st), yamlFilePath, false);
  }

  private ServiceTemplate buildPlanServiceTemplate(ServiceTemplate st) {
    ServiceTemplate tmp = new ServiceTemplate();
    tmp.setPlans(st.getPlans());
    return tmp;
  }

  private ServiceTemplate buildRestServiceTemplate(ServiceTemplate st) {
    st.getPlans().clear();
    return st;
  }

  private String buildYamlFilePath(Definitions definitions) throws ExportCommonException {
    return "Definitions/" + buildYamlFileName(definitions);
  }

//  private List<DefinitionResultInfo> writeDataTypes(OutputStream out, ServiceTemplate st, boolean isServiceTemplateDefinition)
//      throws IOException, ExportCommonException {
//    Map<String, DataType> data_types = st.getData_types();
//    if (data_types.isEmpty()) {
//      return new ArrayList<>();
//    }
//    
//    List<DefinitionResultInfo> defResultInfos = new ArrayList<>();
//    String namespace = st.getTosca_default_namespace();
//    for (Iterator<Entry<String, DataType>> it = data_types.entrySet().iterator(); it.hasNext();) {
//      Entry<String, DataType> dataType = it.next();
//      String yamlFilePath = "Definitions/" + buildYamlFileName(dataType.getKey(), namespace);
//      if (!isDupicate(yamlFilePath)) {
//        ServiceTemplate serviceTemplate = buildST4DataType(dataType, namespace);
//        defResultInfos.add(writeFile(out, serviceTemplate, yamlFilePath, isServiceTemplateDefinition));
//      }
//      st.getImports().add(yamlFilePath);
//      it.remove();
//    }
//    
//    return defResultInfos;
//  }

//  private ServiceTemplate buildST4DataType(Entry<String, DataType> dataType, String namespace) {
//    ServiceTemplate serviceTemplate = new ServiceTemplate();
//    serviceTemplate.getData_types().put(dataType.getKey(), dataType.getValue());
//    serviceTemplate.setTosca_definitions_version("tosca_simple_yaml_1_0");
//    serviceTemplate.setTosca_default_namespace(namespace);
//    return serviceTemplate;
//  }

  private DefinitionResultInfo writeFile(OutputStream out, ServiceTemplate st,
      String yamlFilePath, boolean isServiceTemplateDefinition) throws IOException, ExportCommonException {
    createEntry((ArchiveOutputStream) out, yamlFilePath);
    
    String strServiceTemplate = convert2StrServiceTemplateWithoutTags(st);
    write2OutputStream(strServiceTemplate, out);
    logger.info("makeFile end. yamlFileName = " + yamlFilePath);
    
    DefinitionResultInfo result = new DefinitionResultInfo();
    result.setFileFullName(yamlFilePath);
    result.setFileChecksum(buidCheckSum(out, strServiceTemplate, isServiceTemplateDefinition));
    return result;
  }
  
  private static String convert2StrServiceTemplateWithoutTags(ServiceTemplate st) throws ExportCommonException {
    String strServiceTemplate = YamlBeansUtils.convertToYamlStr(st);
    // yamlStr = yamlStr.replaceAll("defaultValue:", "default:");
    return strServiceTemplate.replaceAll("!.+\n", "\n");
  }

  private String buidCheckSum(OutputStream out, String strServiceTemplate, boolean isServiceTemplateDefinition)
      throws ExportCommonException {
    if (isServiceTemplateDefinition) {
      return MD5Util.md5(strServiceTemplate);
    } else {
      return "";
    }
  }

//  private boolean isDupicate(String fullName) {
//    for (DefinitionResultInfo archiveFileInfo : archiveFiles) {
//      if (fullName.equals(archiveFileInfo.getFileFullName())) {
//        return true;
//      }
//    }
//    return false;
//  }

  private static void write2OutputStream(String strServiceTemplate, OutputStream out)
      throws ExportCommonException {
    try {
      OutputStreamWriter osWriter = new OutputStreamWriter(out);
      osWriter.write(strServiceTemplate);
      osWriter.flush();
    } catch (IOException e) {
      throw new ExportCommonException("Write to file failed.", e);
    }
  }

  private String buildYamlFileName(Definitions definitions) throws ExportCommonException {
    List<?> tNodeList = definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
    if (tNodeList == null || tNodeList.isEmpty()) {
      throw new ExportCommonException("ServiceTemplateOrNodeTypeOrNodeTypeImplementation is empty.");
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
  }

  private String buildYamlFileName(String id, String namespace) {
    return NamespacesResource.getPrefix(namespace) + "__" + (new XMLId(id, false)).getEncoded()
        + ".yaml";
  }
}
