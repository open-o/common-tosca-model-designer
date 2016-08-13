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
<%@tag description="Renders the list of requirements or capabilties" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions"%>
<%@taglib prefix="nt" tagdir="/WEB-INF/tags/common/templates/nodetemplates/reqscaps" %>
<%@taglib prefix="props" tagdir="/WEB-INF/tags/common/templates" %>

<%@attribute name="list" required="false" type="java.util.List" %>
<%@attribute name="headerLabel" required="true" description="Used for the heading" %>
<%@attribute name="cssClassPrefix" required="true" %>
<%@attribute name="TReqOrCapTypeClass" required="true" type="java.lang.Class" %>
<%@attribute name="repositoryURL" required="true" %>
<%@attribute name="typeURLFragment" required="true" description="requirementtypes|capabilitytypes"%>
<%@attribute name="shortName" required="true" description="Used for diag id, function name suffix, Req|Cap"%>
<%@attribute name="client" required="true" description="IWineryRepository" type="org.eclipse.winery.common.interfaces.IWineryRepository"%>
<%@attribute name="pathToImages" required="true" description="The path (URI path) to the image/ url, where xml.png is available. Has to end with '/'"%>

<%@tag import="org.eclipse.winery.common.ModelUtilities"%>

<div class="${cssClassPrefix}Container">
	<div class="header">${headerLabel}</div>
	<div class="content">
		<c:forEach var="item" items="${list}">
			<div class="reqorcap ${cssClassPrefix} row" id="${item.id}">
				<div class="col-xs-4 id reqorcap">${item.id}</div>
				<div class="col-xs-4 name reqorcap">${item.name}</div>
				<div class="col-xs-4 type reqorcap">${wc:qname2href(repositoryURL, TReqOrCapTypeClass, item.type)}</div>
				<c:set var="type" value="${wc:getType(client, item.type, TReqOrCapTypeClass)}" />
				<props:properties
					template="${item}"
					propertiesDefinition="${type.propertiesDefinition}"
					wpd="${wc:winerysPropertiesDefinition(type)}"
					pathToImages="${pathToImages}" />
			</div>
		</c:forEach>
		<div class="addnewreqorcap row" style="display:none;">
			<button class="btn btn-default btn-xs center-block" onclick="showAddOrUpdateDiagFor${shortName}($(this).parent().parent().parent().parent().attr('id'));">Add new</button>
		</div>
	</div>
</div>

<%-- parameters: o.id, o.name, o.type, o.xml. Has to be consistent with above HTML --%>
<script type="text/x-tmpl" id="tmpl-${shortName}">
	<div class="reqorcap ${cssClassPrefix}" id="{%=o.id%}">
		<div class="col-xs-4 id reqorcap">{%=o.id%}</div>
		<div class="col-xs-4 name reqorcap">{%=o.name%}</div>
		<div class="col-xs-4 type reqorcap">{%#require("winery-support-common").qname2href("${repositoryURL}", "${typeURLFragment}", o.type)%}</div>
		<div id="toBeReplacedByProperties"></div>
	</div>
</script>
