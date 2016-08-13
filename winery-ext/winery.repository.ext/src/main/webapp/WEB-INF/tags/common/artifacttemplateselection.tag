<%--
/*******************************************************************************
	Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 *******************************************************************************/
--%>
<%@tag description="dialog for selecting one artifacttemplate" pageEncoding="UTF-8"%>

<%@attribute name="allNamespaces" required="true" type="java.util.Collection" description="All known namespaces"%>
<%@attribute name="repositoryURL" required="true" description="the URL of Winery's repository"%>
<%@attribute name="defaultNSForArtifactTemplate" required="true" description="the default namespace of the artifact template"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="form-group-grouping">
	<!-- createArtifactTemplate class is required for artifactcreationdialog -->
	<div class="form-group createArtifactTemplate">
		<label>Artifact Template Name</label>
		<!-- name is an NCName -->
		<input class="artifactData form-control" id="artifactTemplateName" name="artifactTemplateName" type="text" required="required" autocomplete="on" placeholder="Enter name for artifact template" pattern="[\i-[:]][\c-[:]]*"/>
		<div id="artifactTemplateNameIsValid" class="invalid">
			<span id="artifactTemplateNameIsInvalidReason"></span>
		</div>
	</div>

	<t:namespaceChooser allNamespaces="${allNamespaces}" idOfInput="artifactTemplateNS" selected="${defaultNSForArtifactTemplate}"></t:namespaceChooser>
</div>

<script>
require(["artifacttemplateselection"], function(ast) {
	// configure the plugin
	ast.setRepositoryURL("${repositoryURL}");
});
</script>
