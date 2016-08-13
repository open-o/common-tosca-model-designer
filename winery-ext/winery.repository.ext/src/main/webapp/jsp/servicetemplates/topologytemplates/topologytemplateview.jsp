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

<%@page import="org.eclipse.winery.common.interfaces.IWineryRepository"%>
<%@page import="org.eclipse.winery.repository.Prefs" %>
<%@page import="org.eclipse.winery.repository.client.WineryRepositoryClientFactory"%>
<%@page import="org.eclipse.winery.repository.client.IWineryRepositoryClient"%>
<%@page import="org.eclipse.winery.repository.client.WineryRepositoryClient"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions"%>

<html>
<head>
	<meta name="application-name" content="Winery" />
	<meta charset="UTF-8">
	<link rel="icon" href="${w:topologyModelerURI()}/favicon.png" type="image/png">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/bootstrap/dist/css/bootstrap.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/bootstrap/dist/css/bootstrap-theme.css" />
	<%-- paas处理，引用winery-topologymodeler的文件 --%>
	<link rel="stylesheet" href="${w:topologyModelerURI()}/components/bootstrap-spinedit/css/bootstrap-spinedit.css" />
	<link rel="stylesheet" href="${w:topologyModelerURI()}/components/font-awesome/css/font-awesome.min.css"  />
	<link rel="stylesheet" href="${w:topologyModelerURI()}/css/paas.css" />
	<link rel="stylesheet" href="${w:topologyModelerURI()}/css/boundarydefinition.css" />
	<link rel="stylesheet" href="${w:topologyModelerURI()}/css/propertiesview.css" />

	<script type='text/javascript' src='${pageContext.request.contextPath}/components/requirejs/require.js'></script>

	<!-- jquery and jquery UI have to be loaded using the old fashioned way to avoid incompatibilities with bootstrap v3 -->
	<script type='text/javascript' src='${pageContext.request.contextPath}/components/jquery/jquery.js'></script>
	<script type='text/javascript' src='${pageContext.request.contextPath}/3rdparty/jquery-ui/js/jquery-ui.js'></script>
	<script type='text/javascript' src='${pageContext.request.contextPath}/components/bootstrap/dist/js/bootstrap.js'></script>
	<%-- paas处理，引用winery-topologymodeler的文件 --%>
	<script type='text/javascript' src='${w:topologyModelerURI()}/components/bootstrap-spinedit/js/bootstrap-spinedit.js'></script>
	<script>
		require.config({
			baseUrl: "${pageContext.request.contextPath}/js",
			paths: {
				"datatables": "../components/datatables/media/js/jquery.dataTables",
				"jquery": "../components/jquery/jquery",

				// required for jsplumb
				"jquery.ui": "../3rdparty/jquery-ui/js/jquery-ui",

				"jsplumb": "../components/jsPlumb/dist/js/jquery.jsPlumb-1.5.4",

				"winery-sugiyamaLayouter": "${w:topologyModelerURI()}/js/winery-sugiyamaLayouter",
				//paas处理，引用winery-topologymodeler的文件
				"winery-datatable": "${w:topologyModelerURI()}/js/winery-datatable",

				"jquery.fileupload": "../components/blueimp-file-upload/js/jquery.fileupload",
				"jquery.fileupload-ui": "../components/blueimp-file-upload/js/jquery.fileupload-ui",
				"jquery.fileupload-process": "../components/blueimp-file-upload/js/jquery.fileupload-process",
				"jquery.ui.widget": "../components/blueimp-file-upload/js/vendor/jquery.ui.widget",
			}
		});
	</script>
	<c:if test="${not empty it.additionalScript}">
	<script type='text/javascript' src='${it.additionalScript}'></script>
	</c:if>
</head>
<body>
<div style="margin:10px;">
	<button class="zte-btn zte-white" onclick="history.go(-1);">返回</button>
	<a class="zte-btn zte-white" id="newtab" href="${it.location}" >编辑</a>
	<button class="zte-btn zte-white zte-multi" id="templateParams">模板参数</button>
</div>
<script>
	$("#templateParams").on("click", function(){
		$("#boundaryDefinition").show();
	});
</script>
<t:topologyTemplateRenderer topology="${it.topologyTemplate}" repositoryURL="<%=Prefs.INSTANCE.getResourcePath()%>" client="${it.client}" fullscreen="true" additonalCSS="${it.additonalCSS}" autoLayoutOnLoad="${it.autoLayoutOnLoad}" location="${it.location}"/>

</body>
</html>
