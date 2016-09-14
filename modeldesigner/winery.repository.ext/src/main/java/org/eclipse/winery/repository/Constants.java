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
package org.eclipse.winery.repository;

import java.util.Calendar;
import java.util.Date;

public class Constants {

    /** repository specific **/
    public static final String DEFAULT_REPO_NAME = "winery-repository";
    // this directory is checked for existence. If it does not exist
    // $HOME/DEFAULT_REPO_NAME is used
    public static final String GLOBAL_REPO_PATH_WINDOWS = "D:\\" + Constants.DEFAULT_REPO_NAME;

    /** file-system in general **/
    public static final String newline = System.getProperty("line.separator");

    // Path to images for extensions
    // Currently, we require the format <filenamextension>.png
    public static final String PATH_MIMETYPEIMAGES = "/images/mime-types/";

    // suffix for BPMN4TOSCA
    public static final String SUFFIX_BPMN4TOSCA = ".bpmn4tosca";

    // suffix for CSAR files
    public static final String SUFFIX_CSAR = ".csar";

    // suffix for files in the directory PATH_MIMETYPEIMAGES, including "."
    public static final String SUFFIX_MIMETYPEIMAGES = ".png";

    // suffix for files storing the mimetype of the belonging files
    // used in implementors if IRepository of no appropriate implementation for storing a mimetype
    // is available
    public static final String SUFFIX_MIMETYPE = ".mimetype";

    // suffix for all property files
    public static final String SUFFIX_PROPERTIES = ".properties";

    // suffix for all files storing Definitions
    // following line 2935 of TOSCA cos01
    public static final String SUFFIX_TOSCA_DEFINITIONS = ".tosca";

    // at each new start of the application, the modified date changes
    // reason: the default values of the properties or the JSP could have
    // changed
    public static final Date LASTMODIFIEDDATE_FOR_404 = Calendar.getInstance().getTime();

    public static final String TOSCA_PLANTYPE_BUILD_PLAN =
            "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan";
    public static final String TOSCA_PLANTYPE_TERMINATION_PLAN =
            "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan";

    public static final String DIRNAME_SELF_SERVICE_METADATA = "SELFSERVICE-Metadata";

    /* used for IA generation */
    // public static final String NAMESPACE_ARTIFACTTYPE_WAR = "http://www.opentosca.org/types";
    public static final String NAMESPACE_ARTIFACTTYPE_WAR = "http://www.example.com/ToscaTypes";
    public static final String LOCALNAME_ARTIFACTTYPE_WAR = "WAR";

    // used for repository ext
    public static final String SCAN_PACKAGE = "org.eclipse.winery.repository.ext";
    public static final String RES_PLUGIN_MAPPING_FILE = "namespace_plugin_mapping.properties";
    public static final String RES_TYPE_MAPPING_FILE = "namespace_queryType_mapping.properties";

    public static final String NFV_TYPE_NS = "ns";
    public static final String NFV_TYPE_VNF = "vnf";

    public static final String TEMPLATE_SOURCE = "template_source";
    public static final String TEMPLATE_SOURCE_REPLICA = "replica";
    public static final String TEMPLATE_SOURCE_DERIVED = "derived";

    public static final String REQUIREMENT_EXT_NODE = "node";
    public static final String REQUIREMENT_EXT_CAPABILITY = "capability";
    public static final String REQUIREMENT_EXT_RELATIONSHIP = "relationship";
}
