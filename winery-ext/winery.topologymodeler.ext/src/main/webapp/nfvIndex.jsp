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

<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page buffer="none" %>
<%@page import="java.util.Collection"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.SortedSet"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.eclipse.winery.model.tosca.TArtifactTemplate"%>
<%@page import="org.eclipse.winery.model.tosca.TArtifactType"%>
<%@page import="org.eclipse.winery.model.tosca.TCapability"%>
<%@page import="org.eclipse.winery.model.tosca.TCapabilityType"%>
<%@page import="org.eclipse.winery.model.tosca.TEntityTemplate"%>
<%@page import="org.eclipse.winery.model.tosca.TTopologyTemplate"%>
<%@page import="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@page import="org.eclipse.winery.model.tosca.TNodeType"%>
<%@page import="org.eclipse.winery.model.tosca.TPolicyType"%>
<%@page import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@page import="org.eclipse.winery.model.tosca.TRelationshipTemplate"%>
<%@page import="org.eclipse.winery.model.tosca.TRelationshipTemplate.SourceElement"%>
<%@page import="org.eclipse.winery.model.tosca.TRelationshipTemplate.TargetElement"%>
<%@page import="org.eclipse.winery.model.tosca.TRequirement"%>
<%@page import="org.eclipse.winery.model.tosca.TRequirementType"%>
<%@page import="org.eclipse.winery.common.constants.Namespaces" %>
<%@page import="org.eclipse.winery.common.ids.definitions.ArtifactTemplateId"%>
<%@page import="org.eclipse.winery.common.ids.definitions.ServiceTemplateId" %>
<%@page import="org.eclipse.winery.common.interfaces.QNameWithName"%>
<%@page import="org.eclipse.winery.common.constants.QNames" %>
<%@page import="org.eclipse.winery.common.ModelUtilities"%>
<%@page import="org.eclipse.winery.common.Util"%>
<%@page import="org.eclipse.winery.repository.client.WineryRepositoryClientFactory"%>
<%@page import="org.eclipse.winery.repository.client.IWineryRepositoryClient"%>
<%@page import="org.eclipse.winery.repository.client.WineryRepositoryClient"%>
<%@page import="org.eclipse.winery.topologymodeler.WineryUtil"%>
<%@page import="com.sun.jersey.api.client.WebResource"%>
<%@page import="com.sun.jersey.api.client.Client"%>
<%@page import="com.sun.jersey.api.client.ClientResponse"%>
<%@page import="com.sun.jersey.api.client.config.ClientConfig"%>
<%@page import="com.sun.jersey.api.client.config.DefaultClientConfig"%>

<%-- nc.. = non-common .. --%>
<%@taglib prefix="ncnt"  tagdir="/WEB-INF/tags/templates/nodetemplates" %>
<%@taglib prefix="ncrt"  tagdir="/WEB-INF/tags/templates/relationshiptemplates" %>
<%@taglib prefix="tntrq" tagdir="/WEB-INF/tags/templates/nodetemplates/reqscaps" %>

<%@taglib prefix="ct"    tagdir="/WEB-INF/tags/common" %>
<%@taglib prefix="tmpl"  tagdir="/WEB-INF/tags/common/templates" %>
<%@taglib prefix="bdf"  tagdir="/WEB-INF/tags/common/templates" %>
<%@taglib prefix="nt"    tagdir="/WEB-INF/tags/common/templates/nodetemplates" %>
<%@taglib prefix="ntrq"  tagdir="/WEB-INF/tags/common/templates/nodetemplates/reqscaps" %>
<%@taglib prefix="pol"   tagdir="/WEB-INF/tags/common/policies" %>

<%@taglib prefix="t"     tagdir="/WEB-INF/tags" %>

<%@taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="wc"    uri="http://www.eclipse.org/winery/functions" %>

<%@taglib prefix="tc"   tagdir="/WEB-INF/tags/common/topologycompletion"%>

<jsp:include page="./jsp/csarDialogs.jsp" />

<%-- 从软件仓库选择nodeTemplate界面 --%>
<iframe name="template_iframe" id="template_iframe" src="" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" WIDTH="100%" height="100%" 
style="display:none"></iframe>

<%
	String repositoryURL = request.getParameter("repositoryURL");
	if (StringUtils.isEmpty(repositoryURL)) {
		repositoryURL = "http://localhost:8080/winery";
	} else if (repositoryURL.endsWith("/")) {
		repositoryURL = repositoryURL.substring(0, repositoryURL.length()-1);
	}

	String ns = request.getParameter("ns");
	if (StringUtils.isEmpty(ns)) {
%>
		A namespace has to be provided by using the query parameter &ldquo;ns.&rdquo; Please start the modeler using the <a href="<%=repositoryURL%>">repository</a>.
<%
		return;
	}

	String id = request.getParameter("id");
	if (StringUtils.isEmpty(id)) {
%>
		An id has to be provided by using the query parameter &ldquo;id.&rdquo; Please start the modeler using the <a href="<%=repositoryURL%>">repository</a>.
<%
		return;
	}

	// initialize client dependend on useproxy URL parameter
	IWineryRepositoryClient client;
	if (request.getParameterMap().containsKey("useproxy")) {
		// debugging - using fiddler:
		client = new WineryRepositoryClient(true);
		System.out.println("Using a proxy...");
	} else {
		// production:
		client = WineryRepositoryClientFactory.getWineryRepositoryClient();
	}

	client.addRepository(repositoryURL);

	if (!client.primaryRepositoryAvailable()) {
%>
		The repository is not available.
<%
		return;
	}

	QName serviceTemplateQName = new QName(ns, id);
	TTopologyTemplate topologyTemplate = client.getTopologyTemplate(serviceTemplateQName);
	if (topologyTemplate == null) {
%>
		Something went wrong in the repository: topology template not found.
<%
		return;
	}
	String serviceTemplateURL = repositoryURL + "/servicetemplates/" + Util.DoubleURLencode(serviceTemplateQName);

	String topologyTemplateURL = repositoryURL + "/servicetemplates/" + Util.DoubleURLencode(serviceTemplateQName) + "/topologytemplate/";

	String serviceTemplateName = client.getName(new ServiceTemplateId(serviceTemplateQName));

	Collection<TRelationshipType> relationshipTypes = client.getAllTypes(TRelationshipType.class);
%>
<!DOCTYPE html>
<html>
<head>
	<title>OPEN&ndash;O NS</title>
	<meta http-equiv="content-type" content="text/html;charset=utf-8" />

	<link rel="icon" href="favicon.png" type="image/png">

	<link rel="stylesheet" href="components/bootstrap/dist/css/bootstrap.css" />
	<link rel="stylesheet" href="components/bootstrap/dist/css/bootstrap-theme.css" />
	<link rel="stylesheet" href="components/bootstrap-spinedit/css/bootstrap-spinedit.css" />

	<link rel="stylesheet" href="components/font-awesome/css/font-awesome.min.css"  />

	<link rel="stylesheet" href="components/blueimp-file-upload/css/jquery.fileupload.css" />
	<link rel="stylesheet" href="components/blueimp-file-upload/css/jquery.fileupload-ui.css" />

	<link type="text/css" href="components/pnotify/jquery.pnotify.default.css" media="all" rel="stylesheet" />
	<link type="text/css" href="components/pnotify/jquery.pnotify.default.icons.css" media="all" rel="stylesheet" />

	<!-- select2 -->
	<link type="text/css" href="components/select2/select2.css" media="all" rel="stylesheet" />
	<link type="text/css" href="components/select2/select2-bootstrap.css" media="all" rel="stylesheet" />

	<!-- x-editable -->
	<link type="text/css" href="components/x-editable/dist/bootstrap3-editable/css/bootstrap-editable.css" media="all" rel="stylesheet" />

	<link rel="stylesheet" type="text/css" href="components/editor/built-editor.css"/>

	<!--  Winery as last: Winrey also overwrites some definitions from above -->
	<link rel="stylesheet" href="css/winery-common.css" />
	<link rel="stylesheet" href="css/topologytemplatecontent.css" />
	<link rel="stylesheet" href="css/topologymodeler.css" />
	<!-- add paas css -->
	<link rel="stylesheet" href="css/paas.css" />
</head>

<body data-demo-id="drawingarea" data-library="jquery">

<div id="loading">loading...</div>

<script type='text/javascript' src='${pageContext.request.contextPath}/components/requirejs/require.js'></script>
<script>
	require.config({
		baseUrl: "${pageContext.request.contextPath}/js",
		paths: {
			"datatables": "../components/datatables/media/js/jquery.dataTables",
			"jquery": "../components/jquery/jquery",

			"jquery.fileupload": "../components/blueimp-file-upload/js/jquery.fileupload",
			"jquery.fileupload-ui": "../components/blueimp-file-upload/js/jquery.fileupload-ui",
			"jquery.fileupload-process": "../components/blueimp-file-upload/js/jquery.fileupload-process",
			"jquery.ui.widget": "../components/blueimp-file-upload/js/vendor/jquery.ui.widget",

			// required for jsplumb
			"jquery.ui": "../3rdparty/jquery-ui/js/jquery-ui",

			"jsplumb": "../components/jsPlumb/dist/js/jquery.jsPlumb-1.5.4",

			"keyboardjs": "../components/KeyboardJS/keyboard",
			// "orioneditor": "http://eclipse.org/orion/editor/releases/6.0/built-editor-amd",
			"pnotify": "../components/pnotify/jquery.pnotify",
			"select2": "../components/select2/select2",
			"tmpl": "../components/blueimp-tmpl/js/tmpl",
			"XMLWriter": "../components/XMLWriter/XMLWriter"
		}
	});
	
	 
    function ArrayList(){ 
	    this.arr=[], 
	    this.size=function(){ 
	    return this.arr.length; 
	    }, 
	    this.add=function(){ 
	    if(arguments.length==1){ 
	    this.arr.push(arguments[0]); 
	    }else if(arguments.length>=2){ 
	    var deleteItem=this.arr[arguments[0]]; 
	    this.arr.splice(arguments[0],1,arguments[1],deleteItem)
	    } 
	    return this; 
	    }, 
	    this.get=function(index){ 
	    return this.arr[index]; 
	    }, 
	    this.removeIndex=function(index){ 
	    this.arr.splice(index,1); 
	    }, 
	    this.removeObj=function(obj){ 
	    this.removeIndex(this.indexOf(obj)); 
	    }, 
	    this.indexOf=function(obj){ 
	    for(var i=0;i<this.arr.length;i++){ 
	    if (this.arr[i]===obj) { 
	    return i; 
	    }; 
	    } 
	    return -1; 
	    }, 
	    this.isEmpty=function(){ 
	    return this.arr.length==0; 
	    }, 
	    this.clear=function(){ 
	    this.arr=[]; 
	    }, 
	    this.contains=function(obj){ 
	    return this.indexOf(obj)!=-1; 
	    } 

    }; 


    /** 比较两个组件是否重叠 **/
    function isOverlap(idOne,idTwo){ 
    	var objOne=$("#"+idOne), 
    	objTwo=$("#"+idTwo), 
    	offsetOne = objOne.offset(), 
    	offsetTwo = objTwo.offset(), 
    	top1=offsetOne.top, 
    	top2=offsetTwo.top, 
    	left1=offsetOne.left, 
    	left2=offsetTwo.left, 
    	width1 = objOne.width(), 
    	width2 = objTwo.width(), 
    	height1 = objOne.height(), 
    	height2 = objTwo.height(); 
    	
    	if(left2 < left1 + 4 + width1 && left1 - 4 < left2 + width2)
    	{
    		if(top1+ 4 + height1 > top2 && top1 - 4 < top2 + height2)
    		{
    			return true;
    		}
    		else
    		{
    			return false;
    		}
    	}
    } 
    
    function writeObj(obj){ 
    	    var description = ""; 
    	    for(var i in obj){   
    	        var property=obj[i];   
    	        description+=i+" = "+property+"\n";  
    	    }   
    	    alert(description); 
    } 
    
    // 生成随机数
    function RndNum(n){
    	var rnd="";
    	for(var i=0;i<n;i++)
    	rnd+=Math.floor(Math.random()*10);
    	return rnd;
    }

</script>

<script type='text/javascript' src='components/jquery/jquery.js'></script>

<script type='text/javascript' src='3rdparty/jquery-ui/js/jquery-ui.js'></script>

<script type='text/javascript' src='components/bootstrap/dist/js/bootstrap.js'></script>

<script type='text/javascript' src='components/bootstrap-spinedit/js/bootstrap-spinedit.js'></script>

<!--  udpate of jquery widget by jQuery-File-Upload, which requires jQuery UI 1.9 at least -->
<!-- <script type="text/javascript" src="components/jQuery-File-Upload/js/vendor/jquery.ui.widget.js"></script> -->

<script type='text/javascript' src='components/jsPlumb/dist/js/jquery.jsPlumb-1.5.4.js'></script>

<script type="text/javascript" src="components/jquery-typing/plugin/jquery.typing-0.3.2.js"></script>

<script type="text/javascript" src="components/select2/select2.js"></script>

<script type="text/javascript" src="components/x-editable/dist/bootstrap3-editable/js/bootstrap-editable.js"></script>

<script type="text/javascript" src="js/winery-common.js"></script>
<script type="text/javascript" src="js/winery-topologymodeler.js"></script>

<jsp:include page="/jsp/shared/dialogs.jsp" />

<%
  // only required for generating the CSS for each node type
  Collection<TNodeType> allNodeTypes = client.getAllTypes(TNodeType.class);
%>

<tmpl:CSSForTypes nodeTypes="<%=allNodeTypes%>" relationshipTypes="<%=relationshipTypes%>"/>

<tmpl:propertiesBasic />

<t:about />

<script>
// global variable hodling data for relationship templates
if (!winery) winery = {};

// winery.connections is a hashmap from jsPlumb id to a data structure with
// .id = winery id; // The two ids DO NOT match. Explanation is at winery-common-topologyrendering.js
// ...
winery.connections = {};

// all x-editable popups should be placed in a way to fit "perfectly" on the screen
$.fn.editable.defaults.placement = "auto";

//configuration for pnotify
require(["jquery", "pnotify"], function() {
	$.pnotify.defaults.styling = "bootstrap3";
});
</script>

<%-- Begin: Add&Edit Req/Cap --%>

<%List<QName> allTypes = client.getQNameListOfAllTypes(TRequirementType.class);%>
<tntrq:addorupdatereqorcap requirementOrCapability="requirement" shortName="Req" cssClassPrefix="requirements" headerLabel="Requirement" allTypes="<%=allTypes%>" clazz="<%=TRequirement.class%>" repositoryURL="<%=repositoryURL%>" />

<%allTypes = client.getQNameListOfAllTypes(TCapabilityType.class);%>
<tntrq:addorupdatereqorcap requirementOrCapability="capability" shortName="Cap" cssClassPrefix="capabilities" headerLabel="Capability" allTypes="<%=allTypes%>" clazz="<%=TCapability.class%>" repositoryURL="<%=repositoryURL%>" />

<%-- End: Add&Edit Req/Cap --%>

<%allTypes = client.getQNameListOfAllTypes(TPolicyType.class);%>
<pol:policydiag allPolicyTypes="<%=allTypes%>" repositoryURL="<%=repositoryURL%>" />

<script>
"use strict";

// for debugging
//$.pnotify.defaults.remove = false;

// global data structure
// .repositoryURL - the URL of the repository
// DOES NOT end with /
winery.repositoryURL = "<%=repositoryURL%>";

</script>

<script>
/**
 * Uses global variables currentlySelectedNodeTemplate and currentlySelectedDeploymentArtifactDiv
 *
 * @param atDelete (optional). Function to be called if the DA has been successfully deleted
 */
function askForDeploymentArtifactDeletion(atDelete) {
	// we have to remove it on both the node template locally and on the server
	// reason: if the user does NOT save after deletion, the repository would present him
	// the DA again. Possibly with a dangeling reference to the artifact template,
	// which might have been deleted if this DA was the last reference to it

	var daName = currentlySelectedDeploymentArtifactDiv.children("div.name").text();
	var url = "<%=topologyTemplateURL%>nodetemplates/" + encodeID(currentlySelectedNodeTemplate) + "/deploymentartifacts/" + daName;
	deleteResource("this deployment artifact", url, function() {
		currentlySelectedDeploymentArtifactDiv.remove();
		$("#DeploymentArtifactInfo").modal("hide");
		if (atDelete) {
			atDelete();
		}
	});
}

/**
 * Queries the user for deletion of the currently selected DA.
 * If the associated artifact template points to the current DA only, he is also ask if he wants to delete that template, too.
 */
function deleteDeploymentArtifact() {
	var templateURL = $("#DAArtifactTemplate").attr("href");
	// an artifact template exists
	// we have to ask the user if the wants to delete is as well
	// IF this deployment artifact is the only reference to it
	// The user has to be first asked for DA deletion and then for artifact template deletion
	// Therefore, we pass a function to be executed AFTER deletion of the deployment artifact.
	if (templateURL) {
		// check for count
		$.ajax({
			type: "GET",
			async: false,
			url: templateURL + "?referenceCount",
			dataType: "text",
			error: function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could not get count of artifact template usage", jqXHR, errorThrown);
			},
			success: function(resData, textStatus, jqXHR) {
				if (resData == "1") {
					var atDelete = function() {
						deleteResource("the associated artifact template", templateURL, function() {});
					};
					askForDeploymentArtifactDeletion(atDelete);
				} else {
					// more than one reference to the artifact template
					// just ask for deletion of the DA and not of the AT
					askForDeploymentArtifactDeletion();
				}
			}
		});
	} else {
		askForDeploymentArtifactDeletion();
	}
}

/**
 * The user might have updated the XML information
 * This has to be copied into the "storage" of the currently selected node template
 *
 * Uses global variable currentlySelectedDeploymentArtifactDiv
 */
function updateDeploymentArtifact() {
	require(["winery-support-common"], function(wsc) {
		var newVal = $("#DAXML").val();
		if (wsc.checkXMLValidityAndShowErrorIfInvalid(newVal)) {
			currentlySelectedDeploymentArtifactDiv.children("textarea").val(newVal);
			$("#DeploymentArtifactInfo").modal("hide");
		}
	});
}
</script>
<div class="modal fade" id="DeploymentArtifactInfo">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Artifact Information</h4>
			</div>
			<div class="modal-body">
				<form><fieldset>
					<div class="form-group">
						<label for="DADname" class="control-label">Name</label>
						<div id="DAname" class="form-control"></div>
					</div>

					<div class="form-group">
						<label for="DAArtifactType" class="control-label">Artifact Type</label>
						<a id="DAArtifactType" class="form-control" target="_blank"></a>
					</div>
					<div class="form-group">
						<label for="DAArtifactTemplate" class="control-label">Artifact Template</label>
						<a id="DAArtifactTemplate" class="form-control" target="_blank"></a>
					</div>
					<div class="form-group">
						<label for=DAXML" class="control-label">XML</label>
						<textarea id="DAXML" cols=50 rows=7 class="form-control"></textarea>
					</div>
				</fieldset></form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-danger" onclick="deleteDeploymentArtifact();">Delete</button>
				<button type="button" class="btn btn-primary" onclick="updateDeploymentArtifact();">Ok</button>
			</div>
		</div>
	</div>
</div>

<!-- BEGIN TOPOLOGY COMPLETION -->
<tc:selectionDialogs repositoryURL='<%=repositoryURL%>' serviceTemplateName='<%=serviceTemplateName%>' topologyTemplateURL='<%=topologyTemplateURL%>'/>
<!-- END TOPOLOGY COMPLETION -->

<ct:artifactcreationdialog
	URL="getURLForDeploymanetArtifactGeneration()"
	repositoryURL="<%=repositoryURL%>"
	name="Deployment"
	onSuccessfulArtifactCreationFunction="artifactAddedSuccessfully"
	allNamespaces="<%=client.getNamespaces()%>"
	isDeploymentArtifact="true"
	allArtifactTypes="<%=client.getQNameListOfAllTypes(TArtifactType.class)%>"
	defaultNSForArtifactTemplate="<%=ns%>"
	>
</ct:artifactcreationdialog>

<script>
function getURLForDeploymanetArtifactGeneration() {
	return "<%=topologyTemplateURL%>nodetemplates/" + encodeID(currentlySelectedNodeTemplate) + "/deploymentartifacts/"
}
</script>

<%
// we want to display the name of the artifact template, not the id
Collection<QNameWithName> artifactTemplateList = client.getListOfAllInstances(ArtifactTemplateId.class);
%>

<div id="winery">
	<ncnt:propertiesOfOneNodeTemplate repositoryURL="<%=repositoryURL%>" palette="true"/>
	<ncrt:propertiesOfOneRelationshipTemplate relationshipTypes="<%=relationshipTypes%>" repositoryURL="<%=repositoryURL%>"/>
	<%-- add boundaryDefinitions in paas --%>
	<bdf:boundaryDefinitions serviceTemplateURL="<%=serviceTemplateURL%>" 
	serviceTemplateName="<%=serviceTemplateName%>" ns="<%=ns%>" palette="true"/>

	<div id="topbar">
		<button class="zte-btn zte-white" onclick="history.go(-1);" id="goBack" data-loading-text="loading...">返回</button>
		<button class="zte-btn zte-white zte-multi" onclick="winery.events.fire(winery.events.name.command.SAVE);" id="saveBtn" data-loading-text="Saving...">保存草稿</button>
		<button class="zte-btn zte-white zte-multi" id="templateParams">模板参数</button>
		<!--<a class="zte-btn zte-white" id="deployCSARbtn" href="<%=topologyTemplateURL%>../?deploy" target="_blank">发布</a>-->
		<button class="zte-btn zte-white" onclick="doLayout();">布局</button>
		<!--<a class="btn btn-primary topbutton" id="deployCSARbtn" href="<%=topologyTemplateURL%>../?deploy" target="_blank">Deploy CSAR</a>-->
		<a class="zte-btn zte-white" id="exportCSARbtn" href="<%=topologyTemplateURL%>../?csar&exportType">导出</a>
		<div class="btn-group" style="display: none;">
			<button class="btn btn-default" onclick="doLayout();">Layout</button>
			<button class="btn btn-default" onclick="horizontalAlignment();">Align-h (|)</button>
			<button class="btn btn-default" onclick="verticalAlignment();">Align-v (-)</button>
		</div>

		<tmpl:toggleButtons />

		<%-- confusing, because DELETE at this place in the management part deletes the whole entity, not just the selected one
		<button class="btn btn-danger selectionOnly" onclick="winery.events.fire(winery.events.name.command.DELETE_SELECTION);">Delete</button>
		--%>

		<button style="display: none;" data-toggle="button" class="btn btn-default" onclick="togglePrintView(!$(this).hasClass('active'));">Print View</button>

		<div class="btn-group" style="display: none;">
			<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Other <span class="caret"></span></button>

			<ul class="dropdown-menu" role="menu">
				<li><a href="#" onclick="completeTopology();">Complete Topology</a></li>
				<li><a id="exportCSARbtn" href="<%=topologyTemplateURL%>../?csar" target="_blank">Export CSAR</a></li>
				<li><a href="#" onclick="showAbout();">about</a></li>
			</ul>
		</div>


		<script>
		$("#templateParams").on("click", function(){
			$("#boundaryDefinition").show();
		});
		$("#deployCSARbtn").on("click", function(evt)  {
			var url = $(this).attr("href");
			dConfirmYesNo("Do you really want to deploy " + "<%=serviceTemplateName%>" + "?", function(){
				//self.location.href = url;
				$.get(url, function(data) {
					//console.log(data);
					if(data[0].result == "OK")
					{
						vShowSuccess("Successfully deployed " + "<%=serviceTemplateName%>" + "!");   
					}
					else
					{
						vShowError("Deployed " + "<%=serviceTemplateName%>" + " failed!");
					}
					restDialog();
				},'json');
			}, "Please confirm");
			return false;
		});
		
		/*$("#exportCSARbtn").tooltip({
			placement: 'bottom',
			html: false,
			title: "Hold CTRL key to export XML only."
		});
		$("#exportCSARbtn").on("click", function(evt)  {
			var url = $(this).attr("href");
			if (evt.ctrlKey) {
				url = url.replace(/csar$/, "definitions");
			}
			window.open(url);
			return false;
		});*/
		</script>

		<script>
			function togglePrintView(showPrintView) {
				if (showPrintView) {
					winery.events.fire(winery.events.name.command.UNSELECT_ALL_NODETEMPLATES);
					if (hidePalette) hidePalette();
					// showTypes(true);
					$("#drawingarea").addClass("printview");
					$("#drawingarea").removeClass("editview");

					// move labels 10 px up
					// we have to do it here as jsPlumb currently paints the label on the line instead of above of it
					// See https://groups.google.com/d/msg/jsplumb/zdyAdWcRta0/K6F2MrHBH1AJ
					$(".relationshipTypeLabel").each(function(i, e) {
						var pos = $(e).offset();
						pos.top = pos.top - 10;
						$(e).offset(pos);
					});
				} else {
					$("#drawingarea").removeClass("printview");
					$("#drawingarea").addClass("editview");

					// move labels 10 px down
					// we have to do it here as jsPlumb currently paints the label on the line instead of above of it
					$(".relationshipTypeLabel").each(function(i, e) {
						var pos = $(e).offset();
						pos.top = pos.top + 10;
						$(e).offset(pos);
					});
				}
			}
		</script>
		</div>

	<tmpl:defineCreateConnectorEndpointsFunction relationshipTypes="<%=relationshipTypes%>"/>
	<t:palette client="<%=client%>" relationshipTypes="<%=relationshipTypes%>" repositoryURL="<%=repositoryURL%>" ns="<%=ns%>"/>

	<div class="unselectable" id="editorArea">
		<div id="drawingarea" class="unselectable editview">

<%
		List<TEntityTemplate> templateList = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
		List<TRelationshipTemplate> relationshipTemplates = new LinkedList<TRelationshipTemplate>();
		int cpCount = 0;
		int vlCount = 0;
		int vnfcCount = 0;
		for (TEntityTemplate template: templateList) {
			if (template instanceof TRelationshipTemplate) {
				TRelationshipTemplate tr = (TRelationshipTemplate) template;
				if(!tr.getOwn().equals("1"))
				{
					relationshipTemplates.add(tr);
				}
			} else {
				TNodeTemplate nodeTemplate = (TNodeTemplate) template;
				
				String type = client.getType(nodeTemplate.getType(), TNodeType.class).getName();
				if (type.indexOf("CP") > -1)
				{
					cpCount++;
				}
				else if (type.indexOf("VL") > -1)
				{
					vlCount++;
				}
				else
				{
					vnfcCount++;
				}

				// Get saved position
				// x and y are stored as attributes of other namespaces
				String left = ModelUtilities.getLeft(nodeTemplate);
				String top = ModelUtilities.getTop(nodeTemplate);
				
				if (type.indexOf("CP") > -1)
				{
			%>
					<nt:nodeTemplateRenderer nodeCount="<%=cpCount%>" client="<%=client%>" relationshipTypes="<%=relationshipTypes%>" repositoryURL="<%=repositoryURL%>" nodeTemplate="<%=nodeTemplate%>" top="<%=top%>" left="<%=left%>"/>
			<%
				}
				else if (type.indexOf("VL") > -1)
				{
			%>
					<nt:nodeTemplateRenderer nodeCount="<%=vlCount%>" client="<%=client%>" relationshipTypes="<%=relationshipTypes%>" repositoryURL="<%=repositoryURL%>" nodeTemplate="<%=nodeTemplate%>" top="<%=top%>" left="<%=left%>"/>
			<%
				}
				else
				{
			%>
					<nt:nodeTemplateRenderer nodeCount="<%=vnfcCount%>" client="<%=client%>" relationshipTypes="<%=relationshipTypes%>" repositoryURL="<%=repositoryURL%>" nodeTemplate="<%=nodeTemplate%>" top="<%=top%>" left="<%=left%>"/>
			<%
				
				}
			}
		}
		%>
		</div>
	</div>

	<script>
	var multiDNDmode = false;
	var multiDNDdata = {};

	$(document).on("dragstart", "div.NodeTemplateShape", function(e) {
		var nodeTemplateShape = $(this);
		hideNodeTemplateShapeChangeBoxes(nodeTemplateShape);
		if (nodeTemplateShape.hasClass("selected")) {
			var allSelectedShapes = $("div.NodeTemplateShape.selected");
			if (allSelectedShapes.length > 1) {
				// console.log("start: multiDNDmode");
				multiDNDdata.x = e.clientX;
				multiDNDdata.y = e.clientY;
				multiDNDdata.shapes = $("div.NodeTemplateShape.selected").not("#" + e.currentTarget.id);
				multiDNDmode = true;
			}
		} else {
			// "mousedown" event handling already took care about
			// deselect everything else
			// select shape
		}
	});
</script>
<script>
	$(document).on("drag", "div.NodeTemplateShape", function(e) {
		if (multiDNDmode) {
			// TODO possibly, this has to be put in a queue to avoid racing events?
			var dx = e.clientX - multiDNDdata.x;
			var dy = e.clientY - multiDNDdata.y;
			multiDNDdata.x = e.clientX;
			multiDNDdata.y = e.clientY;
			multiDNDdata.shapes.each(function(i, n) {
				n = $(n);
				var offset = n.offset();
				offset.left += dx;
				offset.top += dy;
				n.offset(offset);
			});
			jsPlumb.repaintEverything();
		}
	});

</script>
<script>

	function doLayout() {
		var editor = $("#editorArea");
		var nodeTemplates = editor.find(".NodeTemplateShape");
		require(["winery-sugiyamaLayouter"], function(layouter) {
			layouter.layout(nodeTemplates);
		});
	}

</script>
<script>

	function horizontalAlignment() {

		var counter = 0;
		var aggregatedLeft = 0;

		$("div.NodeTemplateShape.selected").each(function() {
			aggregatedLeft = aggregatedLeft + $(this).position().left;
			counter = counter + 1;
		});

		var newLeft = aggregatedLeft / counter;

		$("div.NodeTemplateShape.selected").each(function() {
			jsPlumb.animate($(this).attr("id"), {
				left : newLeft
			}, {
				duration : 500,
				easing : 'easeOutBack'
			});
		});

	}

</script>
<script>

	function verticalAlignment() {

		var counter = 0;
		var aggregatedTop = 0;

		$("div.NodeTemplateShape.selected").each(function() {
			aggregatedTop = aggregatedTop + $(this).position().top;
			counter = counter + 1;
		});

		var newTop = aggregatedTop / counter;

		$("div.NodeTemplateShape.selected").each(function() {
			jsPlumb.animate($(this).attr("id"), {
				top : newTop
			}, {
				duration : 500,
				easing : 'easeOutBack'
			});
		});

	}

</script>
<script>
require(["winery-topologymodeler-AMD"], function(wt) {
	winery.events.register(winery.events.name.command.SAVE, wt.save);
	wt.setTopologyTemplateURL("<%=topologyTemplateURL%>");
	//用于新增nodetype
	wt.setRepositoryURL("<%=repositoryURL%>"); 
	wt.setServiceTemplateURL("<%=serviceTemplateURL%>"); 
	wt.setServiceTemplateNamespace("<%=ns%>");
	wt.setServiceTemplateName("<%=serviceTemplateName%>");
});
</script>
<script>

	// "mousedown" instead of "click" enables a more Visio-like behavior
	$(document).on("mousedown", "div.NodeTemplateShape", function(e) {
		var target = $(e.target);
		
		// no special handling if x-editable popover is clicked
		if (target.parents().hasClass("popover")) {
			return false;
		}

		// no special handling if connectors are clicked
		if (target.hasClass("connectorEndpoint") || target.hasClass("connectorBox") || target.hasClass("connectorLabel")) {
			return false;
		}

		if (target.is("a") && !target.hasClass("editable")) {
			// Link clicked
			// Open in new tab
			// Delay opening for 300ms to disalbe a dragstart
			window.setTimeout(function() {
				var href = target.attr("href");
				window.open(href);
			}, 300);
			return false;
		}

		// if the deployment artifact "buttons" are clicked, handle that functionality
		// class addOrLinkDA is also present
		if (target.hasClass("addDA")) {
			var nodeTemplate = $(this);
			hideNodeTemplateShapeChangeBoxes(nodeTemplate);
			var id = nodeTemplate.attr('id');

			// adding DAs only works for existing resources
			// Reason: the POST is directed to the "deploymentartifacts" sub resource,
			// which does all the artifact template autocreation magic
			$.ajax({
				type: "HEAD",
				async: false,
				url: "<%=topologyTemplateURL%>nodetemplates/" + encodeID(id) + "/",
				error: function(jqXHR, textStatus, errorThrown) {
					vShowError("Node template does not exist on the server. Please save the topology before adding deployment artifacts");
				},
				success: function(resData, textStatus, jqXHR) {
					// setting this global variable is required for getURLForDeploymanetArtifactGeneration(), with which the dialog has been initalized.
					currentlySelectedNodeTemplate = id;
					openAddDeploymentArtifactDiag();
				}
			});

			return false;
		}

		if (target.is("button")) {
			// "Edit XML" or "Add deployment artifact" clicked
			return false;
		}

		// if the custom KV properties are clicked, handle them
		if (target.hasClass("KVPropertyValue")) {
			return false;
		}
		// OK or Cancel clicked at editable
		if ((target.hasClass("icon-ok")) || (target.hasClass("icon-remove"))) {
			return false;
		}

		if (target.hasClass("reqorcap")) {
			var reqOrCapId = undefined; // set to undefined to avoid compiler warnings
			// check if req or cap should be edited
			var parentReqOrCapDiv = undefined; // used to determine whether a req or cap is edited, set to undefined to avoid compiler warnings
			if (target.is("div")) {
				reqOrCapId = target.parent().attr("id");
				parentReqOrCapDiv = target.parent();
			} else {
				vShowError("Wrong branch. UI is not consistent with code");
			}
			var isReq = parentReqOrCapDiv.hasClass("requirements");
			if (isReq) {
				showAddOrUpdateDiagForReq(undefined, reqOrCapId);
			} else {
				//console.log(parentReqOrCapDiv.hasClass("capabilities"));
				showAddOrUpdateDiagForCap(undefined, reqOrCapId);
			}
			return false;
		}

		if (target.hasClass("policy")) {
			// click is always on the seen policy content (name, template, ...)
			// the complete element is the parent element
			var policy = $(target).parent();
			showUpdateDiagForPolicy(policy);
			return false;
		}

		if ( (e.shiftKey) || (e.ctrlKey) ) {
			// SHIFT or CTRL indicates multi select
			// toggle containment in the multi select
			$(this).toggleClass("selected");
		} else {
			// no explicit multi select
			var numSelected = $("div.NodeTemplateShape.selected").length;
			if ($(this).hasClass("selected")) {
				// selection if already exists

				// Below, we raise the selection change even in that case to provoke an update of properties etc.
				// When dragging and dropping a single node, the menu of the node is not shown any more
				// a click on the (still selected) node should reveal the menu entries.
			} else {
				// curent shape not selected

				if (numSelected > 0) {
					// other shapes are selected
					// the clicked shape is clicked
					// that means, all other shapes should be unselected
					$("div.NodeTemplateShape.selected").removeClass("selected");
				}

				// no multi select trigger
				// shape is unselected
				// finally, select the shape
				$(this).addClass("selected");
			}
		}

		winery.events.fire(winery.events.name.SELECTION_CHANGED);
		return false;
	});

</script>

<script>
	// we cannot use "$("#editorArea").on("click") as this is *always* triggered before $(document).on("click", ...)
	$(document).on("mousedown", "#editorArea", function(e) {
		hidePalette();

		winery.events.fire(winery.events.name.command.UNSELECT_ALL_NODETEMPLATES);

		hideRTViewOnTheRight();
		unselectAllConnections();

		// true because jsPlumb COULD treat this event, currently unclear
		return true;
	});

	/** marquee tool **/

	var selectionBoxMode = false;
	var selectionBox = {};

	/**
	 * This function is called when selectionBoxMode = true and the mouse gets moved
	 */
	var selectionBoxModeMouseMoveFunction = function(e) {
		selectionBox.endX = e.pageX;
		selectionBox.endY = e.pageY;

		// fix selectionbox coordinates if they are out of the window
		if (selectionBox.endX < selectionBox.minx) selectionBox.endX = selectionBox.minx;
		if (selectionBox.endX > selectionBox.maxx) selectionBox.endX = selectionBox.maxx;
		if (selectionBox.endY < selectionBox.miny) selectionBox.endY = selectionBox.miny;
		if (selectionBox.endY > selectionBox.maxy) selectionBox.endY = selectionBox.maxy;

		// we cannot show the selection box at mousedown as this conflicts somehow with jsPlumb
		// if the .offset of the selectionbox is set, jsPlumb events are not fired any more
		$("#selectionbox").show();
		// setSelectionBoxCoordinates() only works if selectionbox is shown
		setSelectionBoxCoordinates();
	}

	function setSelectionBoxCoordinates() {
		var x;
		var y;
		var height;
		var width;

		// adjust parameters for html, where top/left have to be smaller than lower right
		if (selectionBox.startX < selectionBox.endX) {
			x = selectionBox.startX;
			width = selectionBox.endX - selectionBox.startX;
		} else {
			x = selectionBox.endX;
			width = selectionBox.startX - selectionBox.endX;
		}
		if (selectionBox.startY < selectionBox.endY) {
			y = selectionBox.startY;
			height = selectionBox.endY - selectionBox.startY;
		} else {
			y = selectionBox.endY;
			height = selectionBox.startY - selectionBox.endY;
		}

		$("#selectionbox").offset({
			left : x,
			top : y
		});
		$("#selectionbox").width(width);
		$("#selectionbox").height(height);

		// console.log("realx: " + $("#selectionbox").offset().left);
		// console.log("realy: " + $("#selectionbox").offset().top);

		selectionBox.x = x;
		selectionBox.y = y;
		selectionBox.height = height;
		selectionBox.width = width;

		var area = {};
		if (lastSelectionBox == undefined) {
			// nothing selected at last, check whole new box
			area = selectionBox;
		} else {
			// TODO
			// calculate area to check
		}

		// console.log("sel: " + x + "/" + y + " --X dim: " + width + "/" + height);

		// quick hack: we just go through all node templates and check them for selection
		$("div.NodeTemplateShape:not('.hidden')").each(function(index, nodeTemplate) {
			nodeTemplate = $(nodeTemplate);
			var nx = nodeTemplate.offset().left;
			var ny = nodeTemplate.offset().top;
			var nw = nodeTemplate.width();
			var nh = nodeTemplate.height();
			/* console.log(nx + "/" + ny + " --> dim: " + nw + "/" + nh); */
			if (nx >= x &&
				ny >= y &&
				nx + nw <= x + width &&
				ny + nh <= y + height) {
				nodeTemplate.addClass("selected");
			} else {
				nodeTemplate.removeClass("selected");
			}
		});

		lastSelectionBox = selectionBox;
	}

	// register selection box handling events
	$(document).on("mousedown", "#editorArea", function(e) {
		selectionBoxMode = true;
		selectionBox.startX = e.pageX;
		selectionBox.startY = e.pageY;
		selectionBox.endX = selectionBox.startX;
		selectionBox.endY = selectionBox.startY;
		// console.log("Start: " + selectionBox.startX + "/" + selectionBox.startY)
		selectionBox.minx = document.body.scrollLeft;
		selectionBox.miny = document.body.scrollTop;
		selectionBox.maxx = selectionBox.minx + $(window).width();
		selectionBox.maxy = selectionBox.miny + $(window).height();
		lastSelectionBox = undefined;
		$(document).on("mousemove", selectionBoxModeMouseMoveFunction);
		return true;
	});
	$(document).on("mouseup", function(e) {
		// TODO: possibly, dragend could be used. With the recent libraries, it also works in Chrome
		if (selectionBoxMode) {
			$(document).off("mousemove", selectionBoxModeMouseMoveFunction);
			selectionBoxMode = false;
			
			// 创建一个DIV层
			var width = $("#selectionbox").width();
			if(width > 100)
			{
				var temp_id = "nsSelDiv";
				var count = 1;
				var id = temp_id + count;
				if ($("#" + id).length != 0) {
					var idprefix = id + "_";
					do {
						id = temp_id + count;
						count++;
					} while ($("#" + id).length != 0);
					// also adjust name
					id = temp_id + (count - 1);
				}
				
				// 创建新层并设置ID和样式
				var newDiv = $("#selectionbox").clone();
				newDiv.attr("id", id);
				newDiv.addClass("div_selectionbox");
				newDiv.addClass("ui-draggable");
				newDiv.addClass("ui-droppable");
				newDiv.addClass("layoutableComponent");
				newDiv.mouseover(function(e)
				{
					$(this).css("border", "2px solid red");
					$(this).addClass("NodeTemplateShape");
					$(this).css("z-index", "18");
					$(this).addClass("selected");
					//newDiv.css("background-image", "");
				});
				newDiv.mousemove(function(e)
				{
					//$(this).css("border", "2px solid red");
				});
				newDiv.mouseleave(function(e)
				{
					
					newDiv.removeClass("NodeTemplateShape");
					$(this).removeClass("selected");
					$(this).css("border", "2px solid black");
					//$(this).css("background-image","#655C5C!important");
				});
				
				newDiv.appendTo($("#editorArea"));
			}

			// 隐藏选择框
			$("#selectionbox").hide();
			
		} else if (multiDNDmode) {
			multiDNDmode = false;
			// console.log("end: multiDNDmode");
		}
	
	});

</script>
<script>

	/**
	 * register events / event registering / eventing
	 */
	$(function() {
		winery.events.register(
			winery.events.name.SELECTION_CHANGED,
			function() {
				var numSelected = $("div.NodeTemplateShape.selected").length;

				if (numSelected == 1) {

					var selectedNodeTemplate = $("div.NodeTemplateShape.selected");
					if (isShownNodeTemplateShapeChangeBoxes(selectedNodeTemplate)) {
						// shape change boxes are already shown. Hide them
						hideNodeTemplateShapeChangeBoxes($("div.NodeTemplateShape"));
					} else {
						// fired if
						// * a single node template is selected,
						// * no menu is shown

						// bring that shape to the front
						$("div.NodeTemplateShape").css("z-index", "20");
						selectedNodeTemplate.css("z-index", "21");

						// we show the change boxes
						showNodeTemplateShapeChangeBoxes(selectedNodeTemplate);
						hideNodeTemplateShapeChangeBoxes($("div.NodeTemplateShape:not(.selected)"));
					}
				} else {
					// hide everywhere
					hideNodeTemplateShapeChangeBoxes($("div.NodeTemplateShape"));
				}

				updateVisibilityToggleButtons();
			}
		);
		winery.events.register(
			winery.events.name.command.SELECT_ALL_NODETEMPLATES,
			function () {
				$("div.NodeTemplateShape").addClass("selected");
				winery.events.fire(winery.events.name.SELECTION_CHANGED);
			}
		);
		winery.events.register(
			winery.events.name.command.UNSELECT_ALL_NODETEMPLATES,
			function () {
				$("div.NodeTemplateShape").removeClass("selected");
				winery.events.fire(winery.events.name.SELECTION_CHANGED);
			}
		);

		winery.events.register(
			winery.events.name.command.MOVE_UP,
			function () {
				wineryMoveSelectedNodeTemplateShapes(0, -1);
				return false;
			}
		);
		winery.events.register(
			winery.events.name.command.MOVE_DOWN,
			function () {
				wineryMoveSelectedNodeTemplateShapes(0, 1);
				return false;
			}
		);
		winery.events.register(
			winery.events.name.command.MOVE_LEFT,
			function () {
				wineryMoveSelectedNodeTemplateShapes(-1, 0);
				return false;
			}
		);
		winery.events.register(
			winery.events.name.command.MOVE_RIGHT,
			function () {
				wineryMoveSelectedNodeTemplateShapes(1, 0);
				return false;
			}
		);

		winery.events.register(winery.events.name.command.DELETE_SELECTION, function() {
			if ($(":focus").length == 0) {
				// only delete something if no input field is focused
				// otherwise, a deletion of a character leads to a deletion of the selected node
				var nodesToDelete = $("div.NodeTemplateShape.selected");
				if (nodesToDelete.size() > 0) {
					nodesToDelete.each(function(idx, n) {
						var outEdges = jsPlumb.select({source:n.id});
						outEdges.detach();
						var inEdges = jsPlumb.select({target:n.id});
						inEdges.detach();
					});
					nodesToDelete.remove();
				} else {
					jsPlumb.select().each(function(connection) {
						if (connection.hasType("selected")) {
							jsPlumb.detach(connection);
							// handleConnectionRemoved is fired by detach,
							// this handles the proper data model updates
							currentlySelectedConn = null;
						}
					});
				}
			};
		});
	});

</script>
<script>

	/**
	 * Initialization code
	 */
	$(function() {
		// We need this variable to avoid adding drop targets multiple times
		// The dragenter event is triggered if the current trag leaves a sub element, too
		var dragEnterCount = 0;
		var firstDrop = true;

		// hack for firefox 19.0.2 publishing the dragenter event twice in some cases (when firebug is not active)
		var lastElement = "";

		var divDemo = $("html");
		divDemo.on("dragenter", function(event) {
			if (firstDrop) {
				$(".addnewartifacttemplate").show();
				firstDrop = false;
			}
			if (lastElement != event.target) {
				dragEnterCount++;
				lastElement = event.target;
			}
		});
		divDemo.on("dragleave", function(event) {
			dragEnterCount--;
			lastElement = "";
			if (dragEnterCount==0) {
				$(".addnewartifacttemplate").hide();
				lastElement = "";
				firstDrop = true;
			}
		});

		// disable dragover to enable drag'n'drop of files
		// see https://github.com/blueimp/jQuery-File-Upload/wiki/Multiple-File-Upload-Widgets-on-the-same-page
		$(document).on("drop dragover", function(e) {
			e.preventDefault();
			e.stopPropagation();
		});

		$(document).on("drop", function(e) {
			if (e.originalEvent.dataTransfer && e.originalEvent.dataTransfer.files) {
				e.preventDefault();
				e.stopPropagation();
			}
		});

		// A call is not necessary - the button should have been intialized correctly at the loading by having the right class (active) where appropriate
		// updateVisibilityToggleButtons();
	});
</script>
<script>
	// fire KeyboardJS initialization in parallel to loading
	require(["keyboardjs"], function(KeyboardJS) {
			KeyboardJS.on('del', function(event, keys, keyComboStr) {
				if (!keyComboAllowed()) {
					return true;
				} else {
					winery.events.fire(winery.events.name.command.DELETE_SELECTION);
				}
			});

			KeyboardJS.on('ctrl + s', function(event, keys, keyComboStr) {
				winery.events.fire(winery.events.name.command.SAVE);

				// disable triggering saving by browser
				return false;
			});

			KeyboardJS.on('ctrl + a', function(event, keys, keyComboStr) {
				if (!keyComboAllowed()) {
					// CTRL+a on an input element should trigger selecting all text
					return true;
				} else {
					// otherwise, we select all node templates
					winery.events.fire(winery.events.name.command.SELECT_ALL_NODETEMPLATES);
					return false;
				}
			});

			KeyboardJS.on('up', function(event, keys, keyComboStr) {
				if (!keyComboAllowedAndNodeTemplatesSelected()) {
					return true;
				} else {
					winery.events.fire(winery.events.name.command.MOVE_UP);
					return false;
				}
			});
			KeyboardJS.on('down', function(event, keys, keyComboStr) {
				if (!keyComboAllowedAndNodeTemplatesSelected()) {
					// down on an input, when a dialog is shown or when no nodeTemplate is selected, should trigger the default action
					return true;
				} else {
					// otherwise, we move the selected node templates down
					winery.events.fire(winery.events.name.command.MOVE_DOWN);
					return false;
				}
			});
			KeyboardJS.on('left', function(event, keys, keyComboStr) {
				if (!keyComboAllowedAndNodeTemplatesSelected()) {
					return true;
				} else {
					winery.events.fire(winery.events.name.command.MOVE_LEFT);
					return false;
				}
			});
			KeyboardJS.on('right', function(event, keys, keyComboStr) {
				if (!keyComboAllowedAndNodeTemplatesSelected()) {
					return true;
				} else {
					winery.events.fire(winery.events.name.command.MOVE_RIGHT);
					return false;
				}
			});
	});
</script>
<script>

	function updateVisibilityToggleButtons() {

		if ($("div.NodeTemplateShape.selected").size() == 0) {

			// show buttons active if all parts are visible
			// show buttons inactive if any part is not visible
			if ($("div.NodeTemplateShape:visible .deploymentArtifactsContainer:visible").size() == $("div.NodeTemplateShape:visible .deploymentArtifactsContainer").size()) {
				$('#toggleDeploymentArtifactsVisibility').addClass('active');
			} else {
				$('#toggleDeploymentArtifactsVisibility').removeClass('active');
			}
			if ($("div.NodeTemplateShape:visible .propertiesContainer:visible").size() == $("div.NodeTemplateShape:visible .propertiesContainer").size()) {
				$('#togglePropertiesVisibility').addClass('active');
			} else {
				$('#togglePropertiesVisibility').removeClass('active');
			}
			if ($("div.NodeTemplateShape:visible div.type.nodetemplate:visible").size() == $("div.NodeTemplateShape:visible div.type.nodetemplate").size()) {
				$('#toggleTypeVisibility').addClass('active');
			} else {
				$('#toggleTypeVisibility').removeClass('active');
			}
			if ($("div.NodeTemplateShape:visible div.id.nodetemplate:visible").size() == $("div.NodeTemplateShape:visible div.id.nodetemplate").size()) {
				$('#toggleIdVisibility').addClass('active');
			} else {
				$('#toggleIdVisibility').removeClass('active');
			}

		} else {
			if ($("div.NodeTemplateShape.selected .deploymentArtifactsContainer:visible").size() > 0) {
				$('#toggleDeploymentArtifactsVisibility').addClass('active');
			} else {
				$('#toggleDeploymentArtifactsVisibility').removeClass('active');
			}
			if ($("div.NodeTemplateShape.selected .propertiesContainer:visible").size() > 0) {
				$('#togglePropertiesVisibility').addClass('active');
			} else {
				$('#togglePropertiesVisibility').removeClass('active');
			}
			if ($("div.NodeTemplateShape.selected div.type.nodetemplate:visible").size() > 0) {
				$('#toggleTypeVisibility').addClass('active');
			} else {
				$('#toggleTypeVisibility').removeClass('active');
			}
			if ($("div.toggleIdVisibility.selected div.id.nodetemplate:visible").size() > 0) {
				$('#toggleTypeVisibility').addClass('active');
			} else {
				$('#toggleIdVisibility').removeClass('active');
			}
		}
	}

</script>

<script>

	function handleConnectionRemoved(data) {
		var id = data.connection.id;

		// QUICK HACK: trigger rerouting of arrows
		// jsPlumb should do it automatically, but in the winery setup, it does not
		window.setTimeout(function() {
			jsPlumb.repaint($("#" + data.targetId));
		}, 300);


		delete winery.connections[id];
	}

	jsPlumb.bind("ready", function() {
		jsPlumb.importDefaults({
			DragOptions : { cursor: "pointer", zIndex:2000 },
			HoverClass:"connector-hover"
		});

		jsPlumb.bind("connectionDrag", function(conn) {
			isInConnectionMode = true;
			winery.events.fire(winery.events.name.command.UNSELECT_ALL_NODETEMPLATES);
			return true;
		});

		jsPlumb.bind("beforeDrop", function(sourceId, targetId, scope, connection, dropEndpoint) {
			isInConnectionMode = false;
			return true;
		});

		jsPlumb.bind("connectionDetached", function(connection) {
			handleConnectionRemoved(connection);
		});

		jsPlumb.bind("click", function(conn, originalEvent) {
			if (!conn.hasType("selected")) {
				unselectAllConnections();
				conn.addType("selected");
				showRTViewOnTheRight(conn);
			} else {
				conn.removeType("selected");
				// we have to go through all connections by ourselves to find out the number of selected ones
				var selectedConn = undefined;
				var count = 0;
				jsPlumb.select().each(function(connection) {
					if (connection.hasType("selected")) {
						count++;
						selectedConn = connection;
					}
				});
				if (count == 1) {
					showRTViewOnTheRight(selectedConn);
				} else {
					hideRTViewOnTheRight();
				}
			}
		});
	// jsPlumb.ready
	});
	</script>


<%-- ===== BEGIN: enable editing properties of requirements and capabilities ===== --%>
<div class="hidden" id="skelettonContainerForRequirements">
	<%-- create property value holders for each requirement type; used for NEWLY created requirements --%>
	<c:forEach items="<%=client.getAllTypes(TRequirementType.class)%>" var="type">
		<div class="skelettonPropertyEditorForReq">
			<span class="typeQName">{${type.targetNamespace}}${type.name}</span>
			<tmpl:properties
				propertiesDefinition="${type.propertiesDefinition}"
				wpd="${wc:winerysPropertiesDefinition(type)}"
				template="<%=null%>"
				pathToImages="images/">
			</tmpl:properties>
		</div>
	</c:forEach>
</div>
<div class="hidden" id="skelettonContainerForCapabilities">
	<%-- create property value holders for each capability type; used for NEWLY created capability --%>
	<c:forEach items="<%=client.getAllTypes(TCapabilityType.class)%>" var="type">
		<div class="skelettonPropertyEditorForCap">
			<span class="typeQName">{${type.targetNamespace}}${type.name}</span>
			<tmpl:properties
				propertiesDefinition="${type.propertiesDefinition}"
				wpd="${wc:winerysPropertiesDefinition(type)}"
				template="<%=null%>"
				pathToImages="images/">
			</tmpl:properties>
		</div>
	</c:forEach>
</div>

<script>
// Initialize skeletton editor does NOT work with the current clone thing, we have to initialize the editor after the clone add addreqorcap.tag
</script>
<%-- ===== END: enable editing properties of requirements and capabilities ===== --%>


<%-- ===== BEGIN: enable editing properties of relationship types ===== --%>
<%-- idea:
 * create editor in skelettonContainerForRelationshipTemplates,
 * move it to the properties for editing,
 * and move it back to skelettonContainerForRelationshipTemplates after editing
--%>

<div class="hidden" id="skelettonContainerForRelationshipTemplates">
	<%-- create property value holders for each relationship type; used for NEWLY created relationship templates --%>
	<c:forEach items="<%=relationshipTypes%>" var="relationshipType">
		<div class="skelettonPropertyEditorForRelationshipTemplate">
			<span class="typeQName">{${relationshipType.targetNamespace}}${relationshipType.name}</span>
			<tmpl:properties
				propertiesDefinition="${relationshipType.propertiesDefinition}"
				wpd="${wc:winerysPropertiesDefinition(relationshipType)}"
				template="<%=null%>"
				pathToImages="images/">
			</tmpl:properties>
		</div>
	</c:forEach>

	<%-- create property value holders for each existing relationship template; used for existing relationship templates--%>
	<c:forEach items="<%=relationshipTemplates%>" var="relationshipTemplate">
		<%-- data-id stores the id of the relationship template --%>
		<div class="propertyEditorForRelationshipTemplate" data-id="${relationshipTemplate.id}">
			<c:set var="typeQName" value="${relationshipTemplate.type}" />
			<c:set var="relationshipType" value="<%=client.getType((javax.xml.namespace.QName) pageContext.getAttribute(\"typeQName\"), TRelationshipType.class)%>" />
			<tmpl:properties
				propertiesDefinition="${relationshipType.propertiesDefinition}"
				wpd="${wc:winerysPropertiesDefinition(relationshipType)}"
				template="${relationshipTemplate}"
				pathToImages="images/">
			</tmpl:properties>
		</div>
	</c:forEach>
</div>

<script>
// Initialize skeletton editor
// A clone also clones the editing functionality
$(".skelettonPropertyEditorForRelationshipTemplate").find(".KVPropertyValue").editable();
</script>

<script>
var tmpRelationshipTemplateProperties = {};

function onDoneRegisterConnectionTypesAndConnectNodeTemplates() {
	// as soon as all connection types are registered,
	// winery.connections[] is filled

	// winery.connections is a hashmap from jsPlumb ids to a datastructure containing the winery id
	// the jsPlumb id is NOT equal to the winery id
	// therefore, we have to create a map between winery id and jsplumb id
	var mapTOSCAIdToJSPlumbId = {};
	$.each(winery.connections, function(i, e) {
		mapTOSCAIdToJSPlumbId[e.id] = i;
	});

	// we have to move PropertyEditorForRelationshipTemplate->*ID* to winery.connections[*ID*].propertiesHTML
	// we also init x-editable
	$(".propertyEditorForRelationshipTemplate").each(function(i,e) {
		var id = $(e).data("id");
		var propertiesContainer = $(e).children("div.propertiesContainer");
		if (propertiesContainer.length != 0) {
			// properties exist
			// move the properties container
			id = mapTOSCAIdToJSPlumbId[id];
			winery.connections[id].propertiesContainer = propertiesContainer;
			propertiesContainer.find(".KVPropertyValue").editable();
		}
	});

	// we have to bind here and not in propertiesOfOneRelationshipTemplate.tag as we only want to listen to *usercreated* connections,
	// not to existing ones
	jsPlumb.bind("connection", function(data) {
		// similar to winery-common-topologyrendering -> handleConnectionCreated
		
		var conn;
		if (data.connection) {
			conn = data.connection;
		} else {
			conn = data;
		}

		var id = conn.id;
		var type = conn.getType()[0];
		var  propertiesContainer= $(".skelettonPropertyEditorForRelationshipTemplate > span:contains('" + type + "')").parent().children("div");
		winery.connections[id].propertiesContainer = propertiesContainer;
	});

	// finally, we have finished loading
	$('#loading').fadeOut();
}


</script>
<%-- ===== END: enable editing properties of relationship types ===== --%>

<tmpl:registerConnectionTypesAndConnectNodeTemplates relationshipTemplates="<%=relationshipTemplates%>" relationshipTypes="<%=relationshipTypes %>" ondone="onDoneRegisterConnectionTypesAndConnectNodeTemplates();" repositoryURL="<%=repositoryURL%>"/>

<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
	<tr class="template-upload fade">
		<td>
			<span class="preview"></span>
		</td>
		<td>
			<p class="name">{%=file.name%}</p>
			{% if (file.error) { %}
				<div><span class="label label-important">Error</span> {%=file.error%}</div>
			{% } %}
		</td>
		<td>
			<p class="size">{%=o.formatFileSize(file.size)%}</p>
			{% if (!o.files.error) { %}
				<div class="progress progress-success progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="bar" style="width:0%;"></div></div>
			{% } %}
		</td>
		<td>
			{% if (!o.files.error && !i && !o.options.autoUpload) { %}
				<button class="btn btn-primary start">
					<i class="icon-upload icon-white"></i>
					<span>Start</span>
				</button>
			{% } %}
			{% if (!i) { %}
				<button class="btn btn-warning cancel">
					<i class="icon-ban-circle icon-white"></i>
					<span>Cancel</span>
				</button>
			{% } %}
		</td>
	</tr>
{% } %}
</script>
<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
	<tr class="template-download fade">
		<td>
			<span class="preview">
				{% if (file.thumbnailUrl) { %}
					<a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" data-gallery><img src="{%=file.thumbnailUrl%}"></a>
				{% } %}
			</span>
		</td>
		<td>
			<p class="name">
				<a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" {%=file.thumbnailUrl?'data-gallery':''%}>{%=file.name%}</a>
			</p>
			{% if (file.error) { %}
				<div><span class="label label-important">Error</span> {%=file.error%}</div>
			{% } %}
		</td>
		<td>
			<span class="size">{%=o.formatFileSize(file.size)%}</span>
		</td>
		<td>
			<button class="btn btn-danger delete" data-type="{%=file.deleteType%}" data-url="{%=file.deleteUrl%}"{% if (file.deleteWithCredentials) { %} data-xhr-fields='{"withCredentials":true}'{% } %}>
				<i class="icon-trash icon-white"></i>
				<span>Delete</span>
			</button>
		</td>
	</tr>
{% } %}
</script>
</div>

<script type="text/x-tmpl" id="tmpl-deploymentArtifact">
	<div class="deploymentArtifact row" onclick="showDeploymentArtifactInformation('{%=o.nodeTemplateId%}', '{%=o.name%}');">
		<textarea class="hidden">{%=o.xml%}</textarea>
		<div class="col-xs-4 overflowhidden deploymentArtifact name">{%=o.name%}</div>
		<div class="col-xs-4 overflowhidden artifactTemplate">{% if (o.artifactTemplateName) { %}{%=o.artifactTemplateName%}{% } %}</div>
		<div class="col-xs-4 overflowhidden artifactType">{%=o.artifactTypeName%}</div>
	</div>
</script>

<script type="text/x-tmpl" id="tmpl-deploymentArtifactXML">
	<tosca:DeploymentArtifact
		name="{%=o.name%}"
		xmlns:ns1="{%=o.artifactTypeNSAndId.namespace%}"
		artifactType="ns1:{%=o.artifactTypeNSAndId.localname%}"
		{% if (o.artifactTemplateNSAndId) { %}
		xmlns:ns2="{%=o.artifactTemplateNSAndId.namespace%}"
		artifactRef="ns2:{%=o.artifactTemplateNSAndId.localname%}"
		{% } %}
		xmlns:tosca="<%=org.eclipse.winery.common.constants.Namespaces.TOSCA_NAMESPACE%>" />
</script>

<%-- param: value, selected (optional), text --%>
<script type="text/x-tmpl" id="tmpl-option">
<option value="{%=o.value%}"{% if (o.selected) { %} selected="selected"{% } %}>{%=o.text%}</option>
</script>

	</body>
</html>
