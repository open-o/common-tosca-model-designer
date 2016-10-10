/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     K¨¢lm¨¢n K¨¦pes - initial API and implementation and/or initial documentation
 *     Oliver Kopp - adapted to new storage model and to TOSCA v1.0
 *******************************************************************************/
/*
 * Modifications Copyright 2016 ZTE Corporation.
 */

package org.eclipse.winery.repository.export;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.selfservice.Application.Options;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.Prefs;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.datatypes.ids.admin.NamespacesId;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;
import org.eclipse.winery.repository.resources.servicetemplates.selfserviceportal.SelfServicePortalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.io.Files;

import de.ustutt.iaas.bpmn2bpel.BPMN4Tosca2BPEL;

/**
 * This class exports a CSAR crawling from the the given GenericId<br/>
 * Currently, only ServiceTemplates are supported<br />
 * commons-compress is used as an output stream should be provided. An alternative implementation is
 * to use Java7's Zip File System Provider
 */
public class CSARExporter {

    public static final String PATH_TO_NAMESPACES_PROPERTIES = "winery/Namespaces.properties";

    private static final Logger logger = LoggerFactory.getLogger(CSARExporter.class);

    private static final String DEFINITONS_PATH_PREFIX = "Definitions/";

    private List<File> tempFiles = new ArrayList<File>();

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
        return CSARExporter.getDefinitionsName(id) + Constants.SUFFIX_TOSCA_DEFINITIONS;
    }

    public static String getDefinitionsPathInsideCSAR(TOSCAComponentId id) {
        return CSARExporter.DEFINITONS_PATH_PREFIX + CSARExporter.getDefinitionsFileName(id);
    }

    /**
     * Writes a complete CSAR containing all necessary things reachable from the given service
     * template
     * 
     * @param id the id of the service template to export
     * @param out the outputstream to write to
     * @throws JAXBException
     */
    public void writeCSAR(TOSCAComponentId entryId, OutputStream out) throws ArchiveException,
            IOException, XMLStreamException, JAXBException {
        CSARExporter.logger.trace("Starting CSAR export with {}", entryId.toString());

        Map<RepositoryFileReference, String> refMap =
                new HashMap<RepositoryFileReference, String>();
        Collection<String> definitionNames = new ArrayList<>();

        final ArchiveOutputStream zos =
                new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

        TOSCAExportUtil exporter = new TOSCAExportUtil();
        Map<String, Object> conf = new HashMap<>();

        ExportedState exportedState = new ExportedState();

        TOSCAComponentId currentId = entryId;
        do {
            String defName = CSARExporter.getDefinitionsPathInsideCSAR(currentId);
            definitionNames.add(defName);

            zos.putArchiveEntry(new ZipArchiveEntry(defName));
            Collection<TOSCAComponentId> referencedIds;
            try {
                referencedIds = exporter.exportTOSCA(currentId, zos, refMap, conf,null);
            } catch (IllegalStateException e) {
                // thrown if something went wrong inside the repo
                out.close();
                // we just rethrow as there currently is no error stream.
                throw e;
            }
            zos.closeArchiveEntry();

            exportedState.flagAsExported(currentId);
            exportedState.flagAsExportRequired(referencedIds);

            currentId = exportedState.pop();
        } while (currentId != null);

        // if we export a ServiceTemplate, data for the self-service portal might exist
        if (entryId instanceof ServiceTemplateId) {
            this.addSelfServiceMetaData((ServiceTemplateId) entryId, refMap);
        }

        // now, refMap contains all files to be added to the CSAR

        // write manifest directly after the definitions to have it more at the beginning of the ZIP
        // rather than having it at the very end
        this.addManifest(entryId, definitionNames, refMap, zos);

        // used for generated XSD schemas
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tFactory.newTransformer();
        } catch (TransformerConfigurationException e1) {
            CSARExporter.logger.debug(e1.getMessage(), e1);
            throw new IllegalStateException("Could not instantiate transformer", e1);
        }

        // write all referenced files
        for (RepositoryFileReference ref : refMap.keySet()) {
            String archivePath = refMap.get(ref);

            archivePath = replaceBPMN4Tosca2BPELPath(archivePath, ref.getFileName(), entryId);
            ref = replaceBPMN4Tosca2BPELRef(archivePath, ref);

            CSARExporter.logger.trace("Creating {}", archivePath);
            ArchiveEntry archiveEntry = new ZipArchiveEntry(archivePath);
            zos.putArchiveEntry(archiveEntry);
            if (ref instanceof DummyRepositoryFileReferenceForGeneratedXSD) {
                CSARExporter.logger.trace("Special treatment for generated XSDs");
                Document document =
                        ((DummyRepositoryFileReferenceForGeneratedXSD) ref).getDocument();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(zos);
                try {
                    transformer.transform(source, result);
                } catch (TransformerException e) {
                    CSARExporter.logger.debug("Could not serialize generated xsd", e);
                }
            } else {
                try (InputStream is = Repository.INSTANCE.newInputStream(ref)) {
                    IOUtils.copy(is, zos);
                } catch (Exception e) {
                    CSARExporter.logger.error("Could not copy file content to ZIP outputstream", e);
                }
            }
            zos.closeArchiveEntry();
        }

        this.addNamespacePrefixes(zos);

        deleteFiles(tempFiles.toArray(new File[tempFiles.size()]));
        zos.finish();
        zos.close();
    }

    private RepositoryFileReference replaceBPMN4Tosca2BPELRef(String archivePath,
            RepositoryFileReference ref) {
        String fileName = ref.getFileName();
        if (!fileName.endsWith(".bpmn4tosca") && !fileName.endsWith("file.json")) {
            return ref;
        }

        String bpelName = getPlanName(archivePath);
        String newFileName = getNewFileNameByPlanName(fileName, bpelName);
        return new RepositoryFileReference(ref.getParent(), newFileName);
    }

    private String getNewFileNameByPlanName(String fileName, String bpelName) {
        String newFileName = null;
        if (fileName.endsWith(".bpmn4tosca")) {
            newFileName = fileName.replace(".bpmn4tosca", ".zip");
        } else if (fileName.endsWith("file.json")) {
            newFileName = fileName.replace("file.json", bpelName + ".zip");
        }
        return newFileName;
    }

    private String replaceBPMN4Tosca2BPELPath(String archivePath, String fileName,
            TOSCAComponentId entryId) {
        if (!fileName.endsWith(".bpmn4tosca") && !fileName.endsWith("file.json")) {
            return archivePath;
        }

        String repositoryRoot = Repository.INSTANCE.getRepositoryRootPath() + "/";
        String srcFileName = repositoryRoot + archivePath;
        File srcFile = new File(srcFileName);
        if (!srcFile.exists()) {
            return archivePath;
        }

        String tempPath = repositoryRoot + "temp/";
        File tempDir = new File(tempPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        String bpelName = getPlanName(srcFileName);
        String nameSpace = entryId.getNamespace().getDecoded();
        String csarName = entryId.getXmlId().getDecoded();

        String uriPrefix = "file:/";
        String tempSrcFileName = tempPath + fileName;
        String tempTargetFileName = tempPath + bpelName + ".zip";
        String targeFileName = getNewFileNameByPlanName(srcFileName, bpelName);
        File tempSrcFile = new File(tempSrcFileName);
        File tempTargetFile = new File(tempTargetFileName);
        File targeFile = new File(targeFileName);

        // delete temp files and target file before convert.
        deleteFiles(tempSrcFile, tempTargetFile, targeFile);

        try {
            Files.copy(srcFile, tempSrcFile);
            tempFiles.add(tempSrcFile);

            URI srcUrl = new URI(uriPrefix + tempSrcFileName);
            URI targetUrl = new URI(uriPrefix + tempTargetFileName);
            BPMN4Tosca2BPEL transformer = new BPMN4Tosca2BPEL();

            transformer.transform(srcUrl, targetUrl, bpelName, nameSpace, csarName + ".csar",
                    nameSpace, csarName);

            if (tempTargetFile.exists()) {
                Files.copy(tempTargetFile, targeFile);
                tempFiles.add(tempTargetFile);
                tempFiles.add(targeFile);
            }
        } catch (Exception e) {
            CSARExporter.logger.trace("convert bpmn4tosca to bpel failed!");
            return archivePath;
        }

        return getNewFileNameByPlanName(archivePath, bpelName);
    }

    private String getPlanName(String path) {
        String[] split = path.split("/");
        int length = split.length;
        return length > 1 ? split[length - 2] : null;
    }

    private void deleteFiles(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
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
            ArchiveEntry archiveEntry =
                    new ZipArchiveEntry(CSARExporter.PATH_TO_NAMESPACES_PROPERTIES);
            zos.putArchiveEntry(archiveEntry);
            try {
                pconf.save(zos);
            } catch (ConfigurationException e) {
                CSARExporter.logger.debug(e.getMessage(), e);
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

            refMap.put(res.data_xml_ref, Constants.DIRNAME_SELF_SERVICE_METADATA + "/" + "data.xml");

            // The schema says that the images have to exist
            // However, at a quick modeling, there might be no images
            // Therefore, we check for existence
            if (Repository.INSTANCE.exists(res.icon_jpg_ref)) {
                refMap.put(res.icon_jpg_ref, Constants.DIRNAME_SELF_SERVICE_METADATA + "/"
                        + "icon.jpg");
            }
            if (Repository.INSTANCE.exists(res.image_jpg_ref)) {
                refMap.put(res.image_jpg_ref, Constants.DIRNAME_SELF_SERVICE_METADATA + "/"
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
                            refMap.put(ref, Constants.DIRNAME_SELF_SERVICE_METADATA + "/" + url);
                        } else {
                            CSARExporter.logger
                                    .error("Data corrupt: pointing to non-existent file " + ref);
                        }
                    }

                    url = option.getPlanInputMessageUrl();
                    if (Util.isRelativeURI(url)) {
                        RepositoryFileReference ref = new RepositoryFileReference(id, url);
                        if (Repository.INSTANCE.exists(ref)) {
                            refMap.put(ref, Constants.DIRNAME_SELF_SERVICE_METADATA + "/" + url);
                        } else {
                            CSARExporter.logger
                                    .error("Data corrupt: pointing to non-existent file " + ref);
                        }
                    }
                }
            }
        }
    }

    private void addManifest(TOSCAComponentId id, Collection<String> definitionNames,
            Map<RepositoryFileReference, String> refMap, ArchiveOutputStream out)
            throws IOException {
        String entryDefinitionsReference = CSARExporter.getDefinitionsPathInsideCSAR(id);

        out.putArchiveEntry(new ZipArchiveEntry("TOSCA-Metadata/TOSCA.meta"));
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
            if (archivePath.endsWith(".bpmn4tosca") || archivePath.endsWith("file.json")) {
                String bpelName = getPlanName(archivePath);
                String newFileName = getNewFileNameByPlanName(archivePath, bpelName);
                pw.println("Name: " + newFileName);
                pw.println("Content-Type: application/octet-stream");
            } else {
                pw.println("Name: " + archivePath);
                String mimeType;
                if (ref instanceof DummyRepositoryFileReferenceForGeneratedXSD) {
                    mimeType = MimeTypes.MIMETYPE_XSD;
                } else {
                    mimeType = Repository.INSTANCE.getMimeType(ref);
                }
                pw.println("Content-Type: " + mimeType);
            }
            pw.println();
        }
        pw.flush();
        out.closeArchiveEntry();
    }
}
