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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.eclipse.winery.repository.ext.yamlmodel.DataType;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public DefinitionResultInfo[] makeFile(Definitions definition, OutputStream out) {
    logger.info("makeFile begin.");
    try {
      List<DefinitionResultInfo> result = makeModelFile(definition, out);
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

  private List<DefinitionResultInfo> makeModelFile(Definitions definition, OutputStream out)
      throws IOException, ExportCommonException {

    List<DefinitionResultInfo> defResultInfos = new ArrayList<DefinitionResultInfo>();

    Xml2YamlSwitch switcher = new Xml2YamlSwitch(definition);
    ServiceTemplate st = switcher.convert();

    writeDataTypes(definition, out, st, defResultInfos);

    writeDefinitions(definition, out, defResultInfos, st);

    return defResultInfos;
  }

  private void writeDefinitions(Definitions definition, OutputStream out,
      List<DefinitionResultInfo> defResultInfos, ServiceTemplate st) throws ExportCommonException,
      IOException {
    String yamlFileName = buildYamlFileName(definition);
    String yamlFullFileName = "Definitions/" + yamlFileName;
    DefinitionResultInfo result = writeFile(definition, yamlFullFileName, st, out);
    defResultInfos.add(result);
  }

  private void writeDataTypes(Definitions definition, OutputStream out, ServiceTemplate st,
      List<DefinitionResultInfo> defResultInfos) throws IOException, ExportCommonException {
    Map<String, DataType> data_types = st.getData_types();
    if (data_types.isEmpty()) {
      return;
    }
    String namespace = st.getTosca_default_namespace();
    for (Iterator<Entry<String, DataType>> it = data_types.entrySet().iterator(); it.hasNext();) {
      Entry<String, DataType> dataType = it.next();
      String yamlFullFileName = buildYamlFullFileName(dataType.getKey(), namespace);
      if (!isDupicate(yamlFullFileName)) {
        ServiceTemplate serviceTemplate = buildST4DataType(dataType, namespace);
        DefinitionResultInfo result = writeFile(definition, yamlFullFileName, serviceTemplate, out);
        defResultInfos.add(result);
      }
      st.getImports().add(yamlFullFileName);
      it.remove();
    }
  }

  private ServiceTemplate buildST4DataType(Entry<String, DataType> dataType, String namespace) {
    ServiceTemplate serviceTemplate = new ServiceTemplate();
    serviceTemplate.getData_types().put(dataType.getKey(), dataType.getValue());
    serviceTemplate.setTosca_definitions_version("tosca_simple_yaml_1_0");
    serviceTemplate.setTosca_default_namespace(namespace);
    return serviceTemplate;
  }

  private DefinitionResultInfo writeFile(Definitions definition, String yamlFullFileName,
      ServiceTemplate st, OutputStream out) throws IOException, ExportCommonException {
    createEntry((ArchiveOutputStream) out, yamlFullFileName);
    String checksum = write2OutputStreamWithoutTags(st, out);
    if (!Utils.isServiceTemplateDefinition(definition)) {
      checksum = "";
    }
    logger.info("makeFile end. yamlFileName = " + yamlFullFileName);
    DefinitionResultInfo result = new DefinitionResultInfo();
    result.setFileFullName(yamlFullFileName);
    result.setFileChecksum(checksum);
    return result;
  }

  private String buildYamlFullFileName(String name, String namespace) {
    return "Definitions/" + buildYamlFileName(name, namespace);
  }

  private boolean isDupicate(String fullName) {
    for (DefinitionResultInfo archiveFileInfo : archiveFiles) {
      if (fullName.equals(archiveFileInfo.getFileFullName())) {
        return true;
      }
    }
    return false;
  }

  private static String write2OutputStreamWithoutTags(ServiceTemplate st, OutputStream out)
      throws ExportCommonException {
    String yamlStr = YamlBeansUtils.convertToYamlStr(st);
    yamlStr = yamlStr.replaceAll("!.+\n", "\n");
    // yamlStr = yamlStr.replaceAll("defaultValue:", "default:");
    try {
      OutputStreamWriter osWriter = new OutputStreamWriter(out);
      osWriter.write(yamlStr);
      osWriter.flush();
      return MD5Util.md5(yamlStr);
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

  private String buildYamlFileName(Definitions definition) throws ExportCommonException {
    List<?> tNodeList = definition.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
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
