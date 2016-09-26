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
package org.eclipse.winery.repository.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyDefinition;
import org.eclipse.winery.common.boundaryproperty.BoundaryPropertyUtil;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.common.servicetemplate.FileInfo;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.selfservice.Application.Options;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.Prefs;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.datatypes.ids.admin.NamespacesId;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.ext.export.custom.CustomFileResultInfo;
import org.eclipse.winery.repository.ext.export.custom.DefinitionResultInfo;
import org.eclipse.winery.repository.ext.export.custom.ExportFileGenerator;
import org.eclipse.winery.repository.ext.utils.MD5Util;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceFilesResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplatesResource;
import org.eclipse.winery.repository.resources.servicetemplates.selfserviceportal.SelfServicePortalResource;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class ZipExporter {

  public static final String PATH_TO_NAMESPACES_PROPERTIES = "xml/winery/Namespaces.properties";

  private static final Logger logger = LoggerFactory.getLogger(ZipExporter.class);

  private static final String DEFINITONS_PATH_PREFIX = "xml/Definitions/";


  /**
   * Returns a unique name for the given definitions to be used as filename
   */
  private static String getDefinitionsName(TOSCAComponentId id) {
    // the prefix is globally unique and the id locally in a namespace
    // therefore a concatenation of both is also unique
    String res =
        NamespacesResource.getPrefix(id.getNamespace()) + "__" + id.getXmlId().getEncoded();
    return res;
  }

  public static String getDefinitionsFileName(TOSCAComponentId id) {
    return ZipExporter.getDefinitionsName(id) + Constants.SUFFIX_TOSCA_DEFINITIONS;
  }

  public static String getDefinitionsPathInsideCSAR(TOSCAComponentId id) {
    return ZipExporter.DEFINITONS_PATH_PREFIX + ZipExporter.getDefinitionsFileName(id);
  }

  /**
   * Writes a complete CSAR containing all necessary things reachable from the given service
   * template
   * 
   * @param id the id of the service template to export
   * @param out the outputstream to write to
   * @throws JAXBException
   */
  public void writeZip(TOSCAComponentId entryId, OutputStream out) throws ArchiveException,
      IOException, XMLStreamException, JAXBException {
    ZipExporter.logger.trace("Starting CSAR export with {}", entryId.toString());

    Map<RepositoryFileReference, String> refMap = new HashMap<RepositoryFileReference, String>();
    Collection<String> definitionNames = new ArrayList<>();

    final ArchiveOutputStream zos =
        new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

    TOSCAExportUtil exporter = new TOSCAExportUtil();
    Map<String, Object> conf = new HashMap<>();

    ExportedState exportedState = new ExportedState();

    TOSCAComponentId currentId = entryId;
    do {
      logger.info("begin to scan class:" + System.currentTimeMillis());
      Reflections reflections = new Reflections("org.eclipse.winery.repository.ext");
      Set<Class<? extends ExportFileGenerator>> implenmetions =
          reflections
              .getSubTypesOf(org.eclipse.winery.repository.ext.export.custom.ExportFileGenerator.class);
      logger.info("end to scan class:" + System.currentTimeMillis());
      Iterator<Class<? extends ExportFileGenerator>> it = implenmetions.iterator();
      Collection<TOSCAComponentId> referencedIds = null;

      String defName = ZipExporter.getDefinitionsPathInsideCSAR(currentId);
      definitionNames.add(defName);

      zos.putArchiveEntry(new ZipArchiveEntry(defName));

      try {
        referencedIds = exporter.exportTOSCA(currentId, zos, refMap, conf, null);
      } catch (IllegalStateException e) {
        // thrown if something went wrong inside the repo
        out.close();
        // we just rethrow as there currently is no error stream.
        throw e;
      }
      zos.closeArchiveEntry();

      while (it.hasNext()) {
        Class<? extends ExportFileGenerator> exportClass = it.next();
        logger.trace("the " + exportClass.toString() + "begin to write file");
        try {
          if (!Modifier.isAbstract(exportClass.getModifiers())) {
            ExportFileGenerator fileGenerator = exportClass.newInstance();
            referencedIds = exporter.exportTOSCA(currentId, zos, refMap, conf, fileGenerator);
          }
        } catch (InstantiationException e) {
          logger.error("export error occur while instancing " + exportClass.toString(), e);
          out.close();
        } catch (IllegalAccessException e) {
          logger.error("export error occur", e);
          out.close();
        }
      }

      exportedState.flagAsExported(currentId);
      exportedState.flagAsExportRequired(referencedIds);

      currentId = exportedState.pop();
    } while (currentId != null);

    // if we export a ServiceTemplate, data for the self-service portal might exist
    if (entryId instanceof ServiceTemplateId) {
      this.addSelfServiceMetaData((ServiceTemplateId) entryId, refMap);
      addCsarMeta((ServiceTemplateId) entryId,zos);
    }

    // write custom file
    ArrayList<CustomFileResultInfo> customFileResultList = new ArrayList<CustomFileResultInfo>();
    if (entryId instanceof ServiceTemplateId) {
      customFileResultList = this.exportCustomFiles((ServiceTemplateId) entryId, zos);
    }

    // write manifest directly after the definitions to have it more at the beginning of the ZIP
    // rather than having it at the very end
    this.addManifest(entryId, definitionNames, refMap, zos);
    this.addManiYamlfest(entryId, exporter.getYamlExportDefResultList(), refMap, zos, exporter);
    this.addCheckSumFest(getCheckSums(exporter.getYamlExportDefResultList(), customFileResultList),
        zos);
    // used for generated XSD schemas
    TransformerFactory tFactory = TransformerFactory.newInstance();
    Transformer transformer;
    try {
      transformer = tFactory.newTransformer();
    } catch (TransformerConfigurationException e1) {
      ZipExporter.logger.debug(e1.getMessage(), e1);
      throw new IllegalStateException("Could not instantiate transformer", e1);
    }

    // write all referenced files
    for (RepositoryFileReference ref : refMap.keySet()) {
      String archivePath = refMap.get(ref);
      ZipExporter.logger.trace("Creating {}", archivePath);
      ArchiveEntry archiveEntry = new ZipArchiveEntry("xml/" + archivePath);
      zos.putArchiveEntry(archiveEntry);
      if (ref instanceof DummyRepositoryFileReferenceForGeneratedXSD) {
        ZipExporter.logger.trace("Special treatment for generated XSDs");
        Document document = ((DummyRepositoryFileReferenceForGeneratedXSD) ref).getDocument();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(zos);
        try {
          transformer.transform(source, result);
        } catch (TransformerException e) {
          ZipExporter.logger.debug("Could not serialize generated xsd", e);
        }
      } else {
        try (InputStream is = Repository.INSTANCE.newInputStream(ref)) {
          IOUtils.copy(is, zos);
        } catch (Exception e) {
          ZipExporter.logger.error("Could not copy file content to ZIP outputstream", e);
        }
      }
      zos.closeArchiveEntry();
    }

    this.addNamespacePrefixes(zos);

    zos.finish();
    zos.close();
  }

  private ArrayList<CustomFileResultInfo> exportCustomFiles(ServiceTemplateId templateId,
      ArchiveOutputStream zos) throws IOException {
    ServiceFilesResource fileResource =
        new ServiceTemplatesResource().getComponentInstaceResource(
            Util.URLencode(templateId.getQName().getNamespaceURI()),
            templateId.getXmlId().getDecoded()).getServiceFilesResource();
    return exportFiles(fileResource.getRef(), zos);
  }

  private ArrayList<CustomFileResultInfo> exportFiles(List<FileInfo> fileInfos,
      ArchiveOutputStream aos) throws IOException {
    ArrayList<CustomFileResultInfo> result = new ArrayList<CustomFileResultInfo>();
    for (FileInfo fileInfo : fileInfos) {
      String filePath = getRelativePath(fileInfo);

      aos.putArchiveEntry(new ZipArchiveEntry(filePath));
      InputStream is = Repository.INSTANCE.newInputStream(fileInfo.getRef());
      String md5 = MD5Util.md5(is, aos);
      aos.closeArchiveEntry();

      CustomFileResultInfo info = new CustomFileResultInfo();

      info.setFileFullName(filePath);
      info.setFileChecksum(md5);
      result.add(info);
    }
    return result;
  }

  private String getRelativePath(FileInfo fileInfo) {
    String filePath = fileInfo.getPath();
    if (filePath.isEmpty()) {
      return fileInfo.getFileName();
    }

    // first char is "/", remove it
    if (filePath.indexOf("/") == 0) {
      filePath = filePath.substring(1);
    }
    return filePath + "/" + fileInfo.getFileName();
  }

  private String[] getCheckSums(ArrayList<DefinitionResultInfo> resultDefList,
      ArrayList<CustomFileResultInfo> customFileList) {
    ArrayList<String> checksumList = new ArrayList<String>();
    for (DefinitionResultInfo info : resultDefList) {
      if (info.getFileChecksum() != null && !info.getFileChecksum().isEmpty()) {
        checksumList.add(info.getFileFullName() + ":" + info.getFileChecksum());
      }
    }

    for (CustomFileResultInfo info : customFileList) {
      if (info.getFileChecksum() != null && !info.getFileChecksum().isEmpty()) {
        checksumList.add(info.getFileFullName() + ":" + info.getFileChecksum());
      }
    }
    return checksumList.toArray(new String[0]);
  }

  private void addCheckSumFest(String[] checksums, ArchiveOutputStream out) throws IOException {
    out.putArchiveEntry(new ZipArchiveEntry("checksum.lst"));
    PrintWriter pw = new PrintWriter(out);
    for (String checksum : checksums) {
      pw.println(checksum);
    }
    pw.flush();
    out.closeArchiveEntry();
  }

  /**
   * Writes the configured mapping namespaceprefix -> namespace to the archive
   * 
   * This is kind of a quick hack. TODO: during the import, the prefixes should be extracted using
   * JAXB and stored in the NamespacesResource
   * 
   * @throws IOException
   */
  private void addNamespacePrefixes(ArchiveOutputStream zos) throws IOException {
    Configuration configuration = Repository.INSTANCE.getConfiguration(new NamespacesId());
    if (configuration instanceof PropertiesConfiguration) {
      // Quick hack: direct serialization only works for PropertiesConfiguration
      PropertiesConfiguration pconf = (PropertiesConfiguration) configuration;
      ArchiveEntry archiveEntry = new ZipArchiveEntry(ZipExporter.PATH_TO_NAMESPACES_PROPERTIES);
      zos.putArchiveEntry(archiveEntry);
      try {
        pconf.save(zos);
      } catch (ConfigurationException e) {
        ZipExporter.logger.debug(e.getMessage(), e);
        zos.write("#Could not export properties".getBytes());
        zos.write(("#" + e.getMessage()).getBytes());
      }
      zos.closeArchiveEntry();
    }
  }

  private void addSelfServiceMetaData(ServiceTemplateId entryId,
      Map<RepositoryFileReference, String> refMap) {
    SelfServiceMetaDataId id = new SelfServiceMetaDataId(entryId);
    if (Repository.INSTANCE.exists(id)) {
      SelfServicePortalResource res = new SelfServicePortalResource(entryId);

      refMap.put(res.data_xml_ref, "xml/" + Constants.DIRNAME_SELF_SERVICE_METADATA + "/"
          + "data.xml");

      // The schema says that the images have to exist
      // However, at a quick modeling, there might be no images
      // Therefore, we check for existence
      if (Repository.INSTANCE.exists(res.icon_jpg_ref)) {
        refMap.put(res.icon_jpg_ref, "xml/" + Constants.DIRNAME_SELF_SERVICE_METADATA + "/"
            + "icon.jpg");
      }
      if (Repository.INSTANCE.exists(res.image_jpg_ref)) {
        refMap.put(res.image_jpg_ref, "xml/" + Constants.DIRNAME_SELF_SERVICE_METADATA + "/"
            + "image.jpg");
      }

      Application application = res.getApplication();
      Options options = application.getOptions();
      if (options != null) {
        for (ApplicationOption option : options.getOption()) {
          String url = option.getIconUrl();
          if (Util.isRelativeURI(url)) {
            RepositoryFileReference ref = new RepositoryFileReference(id, url);
            if (Repository.INSTANCE.exists(ref)) {
              refMap.put(ref, "xml/" + Constants.DIRNAME_SELF_SERVICE_METADATA + "/" + url);
            } else {
              ZipExporter.logger.error("Data corrupt: pointing to non-existent file " + ref);
            }
          }

          url = option.getPlanInputMessageUrl();
          if (Util.isRelativeURI(url)) {
            RepositoryFileReference ref = new RepositoryFileReference(id, url);
            if (Repository.INSTANCE.exists(ref)) {
              refMap.put(ref, "xml/" + Constants.DIRNAME_SELF_SERVICE_METADATA + "/" + url);
            } else {
              ZipExporter.logger.error("Data corrupt: pointing to non-existent file " + ref);
            }
          }
        }
      }
    }
  }
  
  private void addCsarMeta(ServiceTemplateId entryId, ArchiveOutputStream out) throws IOException{
    AbstractComponentInstanceResource res =
            AbstractComponentsResource.getComponentInstaceResource(entryId);
    Definitions entryDefinitions = res.getDefinitions();
    TServiceTemplate  serviceTemplate = (TServiceTemplate) entryDefinitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);
    out.putArchiveEntry(new ZipArchiveEntry("csar.meta"));
    BoundaryPropertyDefinition boundaryPropertyDefinition =
            BoundaryPropertyUtil.getBoundaryPropertyDefinition(serviceTemplate.getBoundaryDefinitions().getProperties().getAny());
    StringBuffer buffer = new StringBuffer();
    buffer.append("Type:");
    buffer.append(boundaryPropertyDefinition.getMetaData("csarType"));
    buffer.append("\n");

    buffer.append("Version:");
    buffer.append(boundaryPropertyDefinition.getMetaData("csarVersion"));
    buffer.append("\n");
    
    buffer.append("Provider:");
    buffer.append(boundaryPropertyDefinition.getMetaData("csarProvider"));
    buffer.append("\n");
    
    PrintWriter pw = new PrintWriter(out);
    pw.println(buffer.toString());
    pw.flush();
    out.closeArchiveEntry();
}

  private void addManifest(TOSCAComponentId id, Collection<String> definitionNames,
      Map<RepositoryFileReference, String> refMap, ArchiveOutputStream out) throws IOException {
    String entryDefinitionsReference = ZipExporter.getDefinitionsPathInsideCSAR(id);

    out.putArchiveEntry(new ZipArchiveEntry("xml/TOSCA-Metadata/TOSCA.meta"));
    PrintWriter pw = new PrintWriter(out);
    // Setting Versions
    pw.println("TOSCA-Meta-Version: 1.0");
    pw.println("CSAR-Version: 1.0");
    String versionString = "Created-By: Winery " + Prefs.INSTANCE.getVersion();
    pw.println(versionString);
    // Winery currently is unaware of tDefinitions, therefore, we use the
    // name of the service template
    pw.println("Entry-Definitions: " + entryDefinitionsReference);
    pw.println();

    assert (definitionNames.contains(entryDefinitionsReference));
    for (String name : definitionNames) {
      pw.println("Name: " + name);
      pw.println("Content-Type: "
          + org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_TOSCA_DEFINITIONS);
      pw.println();
    }

    // Setting other files, mainly files belonging to artifacts
    for (RepositoryFileReference ref : refMap.keySet()) {
      String archivePath = refMap.get(ref);
      pw.println("Name: " + archivePath);
      String mimeType;
      if (ref instanceof DummyRepositoryFileReferenceForGeneratedXSD) {
        mimeType = MimeTypes.MIMETYPE_XSD;
      } else {
        mimeType = Repository.INSTANCE.getMimeType(ref);
      }
      pw.println("Content-Type: " + mimeType);
      pw.println();
    }
    pw.flush();
    out.closeArchiveEntry();
  }

  private String addManiYamlfest(TOSCAComponentId id,
      ArrayList<DefinitionResultInfo> defResultList, Map<RepositoryFileReference, String> refMap,
      ArchiveOutputStream out, TOSCAExportUtil exporter) throws IOException {
    if (defResultList.size() == 0) {
      return "";
    }

    String content = getToscaMetaContent(defResultList, refMap, exporter);
    out.putArchiveEntry(new ZipArchiveEntry("TOSCA-Metadata/TOSCA.meta"));
    PrintWriter pw = new PrintWriter(out);
    pw.println(content);
    pw.flush();
    out.closeArchiveEntry();
    return MD5Util.md5(content);
  }

  private String getToscaMetaContent(ArrayList<DefinitionResultInfo> defResultList,
      Map<RepositoryFileReference, String> refMap, TOSCAExportUtil exporter) throws IOException {
    String entryDefinitionsReference = exporter.getYamlEntryDefinitionsReference();
    StringBuffer buffer = new StringBuffer();
    // Setting Versions
    buffer.append("TOSCA-Meta-Version: 1.0\n");
    buffer.append("CSAR-Version: 1.0\n");
    String versionString = "Created-By: Winery " + Prefs.INSTANCE.getVersion();
    buffer.append(versionString);
    buffer.append("\n");
    // Winery currently is unaware of tDefinitions, therefore, we use the
    // name of the service template
    buffer.append("Entry-Definitions: " + entryDefinitionsReference);
    buffer.append("\n");
    buffer.append("\n");

    // assert (defResultList.contains(entryDefinitionsReference));
    for (DefinitionResultInfo info : defResultList) {
      buffer.append("Name: " + info.getFileFullName() + "\n");
      buffer.append("Content-Type: "
          + org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_TOSCA_DEFINITIONS);
      buffer.append("\n");
      buffer.append("\n");
    }

    // Setting other files, mainly files belonging to artifacts
    for (RepositoryFileReference ref : refMap.keySet()) {
      String archivePath = refMap.get(ref);
      buffer.append("Name: " + archivePath + "\n");
      String mimeType;
      if (ref instanceof DummyRepositoryFileReferenceForGeneratedXSD) {
        mimeType = MimeTypes.MIMETYPE_XSD;
      } else {
        mimeType = Repository.INSTANCE.getMimeType(ref);
      }
      buffer.append("Content-Type: " + mimeType);
      buffer.append("\n");
      buffer.append("\n");
    }
    return buffer.toString();
  }
}
