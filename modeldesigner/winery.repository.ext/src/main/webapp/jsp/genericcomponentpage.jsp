<%--
/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
/*
 * Modifications Copyright 2016 ZTE Corporation.
 */

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="v"  uri="http://www.eclipse.org/winery/repository/functions" %>
<%@taglib prefix="t"  tagdir="/WEB-INF/tags" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions" %>

<%-- In English, one can usually form a plural by adding an "s". Therefore, we resue the label to form the window title --%>
<t:genericpage windowtitle="${it.label}s" selected="${it.type}" cssClass="${it.CSSclass}">

<c:choose>
<c:when test="${empty pageContext.request.contextPath}">
<c:set var="URL" value="/" />
</c:when>
<c:otherwise>
<c:set var="URL" value="${pageContext.request.contextPath}/" />
</c:otherwise>
</c:choose>
<t:simpleSingleFileUpload
	title="Upload CSAR"
	text="CSAR file"
	URL="${URL}"
	type="POST"
	id="upCSAR"
	accept="application/zip,.csar"/>

<t:addComponentInstance
	label="${it.label}"
	typeSelectorData="${it.typeSelectorData}"
	/>

<div class="middle" id="ccontainer">
 	<div class="zte-btn-group">
 		<c:choose>
 		<c:when test="${it.type eq 'ServiceTemplate'}">
 		<div class="btn-group" style="vertical-align:inherit">
			<button type="button" class="zte-btn zte-primary dropdown-toggle" data-toggle="dropdown">
			<!-- <i class="addPlus fa-plus-circle"></i> -->
			<span>创建</span><span class="caret"></span>
			</button>
			<ul id="templateDropdownMenu" class="dropdown-menu" role="menu">
			</ul>
		</div>
		</c:when>
		<c:otherwise>
			<button type="button" class="zte-btn zte-primary" onclick="openNewCIdiag();">
			<!-- <i class="addPlus fa-plus-circle"></i> -->
			<span id="winery-nodetype-btn-create" name_i18n="winery_i18n"></span>
			</button>
		</c:otherwise>
		</c:choose>
		<button type="button" class="zte-btn zte-primary" onclick="importCSAR();" style="margin-right: 10px;">
			<span id="winery-toolbar-btn-import" name_i18n="winery_i18n"></span>
		</button>
	</div>
	<table class="zte-table" style="margin-left:20px;,margin-right:20px;width:98%">
		<thead>
			<tr>
				<th><span id="winery-nodetype-table-name" name_i18n="winery_i18n"></span></th>
				<th><span id="winery-nodetype-table-namespace" name_i18n="winery_i18n"></span></th>
				<th><span id="winery-table-operation" name_i18n="winery_i18n"></span></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach var="t" items="${it.componentInstanceIds}" varStatus="loopStatus">
		<tr class="entityContainer ${it.CSSclass} ${loopStatus.index % 2 != 0 ? 'even' : 'myodd'}" id="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/">
			<%-- even though the id is an invalid XML, it is used for a simple implementation on a click on the graphical rendering to trigger opening the editor --%>
			<td>
			<div class="left">
				<c:if test="${it.type eq 'NodeType'}">
					<a href="./${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?edit">
						<img src='./${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/visualappearance/50x50' style='margin-top: 21px; margin-left: 30px; height: 40px; width: 40px;display: none;' />
					</a>
				</c:if>
			</div>
			<div class="informationContainer">
				<c:if test="${it.type eq 'ServiceTemplate'}">
					<%--paas修改，直接跳转到查看界面--%>
					<a class="name link" href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/topologytemplate?view">${wc:escapeHtml4(t.xmlId.decoded)}</a>
				</c:if>
				<c:if test="${it.type eq 'NodeType'}">
					<div class="name">
					${wc:escapeHtml4(t.xmlId.decoded)}
				</div>
				</c:if>
				<div class="namespace" alt="${wc:escapeHtml4(t.namespace.decoded)}" style="display:none;">
					${wc:escapeHtml4(t.namespace.decoded)}
				</div>
			</div>
			</td>
			<td>${t.namespace}</td>
			<td class="center">
				<%--
				<c:if test="${it.type eq 'ServiceTemplate'}">
					<a href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?deploy&deployType" lang="${v:URLencode(t.xmlId.encoded)}" class="zte-btn zte-small deployBtn">发布</a>
				</c:if>--%>
				<a href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?csar&exportType" class="zte-btn zte-small">
					<span id="winery-toolbar-btn-export" name_i18n="winery_i18n"></span>
				</a>
				<%--
				<a href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?csar" class="zte-btn zte-small">导出</a>--%>
				<a href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?edit" class="zte-btn zte-small editBtn">
					<span id="winery-toolbar-btn-edit" name_i18n="winery_i18n"></span>
				</a>
				<%-- we need double encoding of the URL as the passing to javascript: decodes the given string once --%>
				<a href="javascript:deleteCI('${wc:escapeHtml4(t.xmlId.decoded)}', '${wc:escapeHtml4(t.namespace.decoded)}', '${v:URLencode(v:URLencode(t.namespace.encoded))}/${v:URLencode(v:URLencode(t.xmlId.encoded))}/');" class="zte-btn zte-small" onclick="element = $(this).parent().parent();">
					<span id="winery-btn-delete" name_i18n="winery_i18n"></span>
				</a>
				<div class="right"></div>
			</td>
		</tr>
		</c:forEach>
		</tbody>
	</table>
	<%--
	<table cellpadding=0 cellspacing=0 style="margin-top: 40px; width:100%;" >
		<tr>
			<td valign="top" style="padding-top: 0px;" width="20%">

			</td>
			<td valign="top" width="60%" style="min-width: 630px;">
				<div id="overviewtopshadow" style="display: none;"></div>
				<div id="overviewbottomshadow" style="display:none;"></div>
				
				<div id="searchBoxContainer">

					<input id="searchBox"/>

					<script>

						$('#searchBox').keyup(function() {
							var searchString = $(this).val();
							searchString = searchString.toLowerCase();

							$(".entityContainer").each (function() {
								var name = $(this).find(".informationContainer > .name").text();
								var namespace = $(this).find(".informationContainer > .namespace").text();

								var t = name + namespace;
								t = t.toLowerCase();

								if (t.indexOf(searchString) == -1) {
									$(this).hide();
								} else {
									$(this).show();
								}

							});

						});

					</script>

				</div>
			<table class="zte-table">
				<thead>
					<tr>
						<th style="cursor:pointer;">Name</th>
						<th style="cursor:pointer;">Operation</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach var="t" items="${it.componentInstanceIds}" varStatus="loopStatus">
				<tr class="entityContainer ${it.CSSclass} ${loopStatus.index % 2 != 0 ? 'even' : 'myodd'}" id="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/">
					<%--
					<%-- even though the id is an invalid XML, it is used for a simple implementation on a click on the graphical rendering to trigger opening the editor --%>
					<%-- 
					<td>
					<div class="left">
						<c:if test="${it.type eq 'NodeType'}">
							<a href="./${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?edit">
								<img src='./${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/visualappearance/50x50' style='margin-top: 21px; margin-left: 30px; height: 40px; width: 40px;display: none;' />
							</a>
						</c:if>
					</div>
					<div class="informationContainer">
						<div class="name">
							${wc:escapeHtml4(t.xmlId.decoded)}
						</div>
						<div class="namespace" alt="${wc:escapeHtml4(t.namespace.decoded)}" style="display:none;">
							${wc:escapeHtml4(t.namespace.decoded)}
						</div>
					</div>
					</td>
					<td class="center">
						<c:if test="${it.type eq 'ServiceTemplate'}">
							<a href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?deploy" lang="${v:URLencode(t.xmlId.encoded)}" class="deployButton"></a>
						</c:if>
						<a href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?csar" class="exportButton"></a>
						<a href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?edit" class="editButton"></a>
						--%>
						<%-- we need double encoding of the URL as the passing to javascript: decodes the given string once --%>
						<%--
						<a href="javascript:deleteCI('${wc:escapeHtml4(t.xmlId.decoded)}', '${v:URLencode(v:URLencode(t.namespace.encoded))}/${v:URLencode(v:URLencode(t.xmlId.encoded))}/');" class="deleteButton" onclick="element = $(this).parent().parent();"></a>
						<div class="right"></div>
					</td>
				</tr>
				</c:forEach>
				</tbody>
			</table>
			</td>
			<td valign="top" width="20%">

			</td>
		</tr>
	</table>
	--%>
</div>

<script>

function entityContainerClicked(e) {
	var target = $(e.target);
	if (target.is("a")) {
		// do nothing as a nested a element is clicked
	} else {
		var ec = target.parents("div.entityContainer");
		var url = ec.attr('id');
		if (e.ctrlKey) {
			// emulate browser's default behavior to open a new tab
			window.open(url);
		} else {
			window.location = url;
		}
	}
}

$("div.entityContainer").on("click", entityContainerClicked);

/**
 * deletes given component instance
 * uses global variable "element", which stores the DOM element to delete upon successful deletion
 */
function deleteCI(name, namespace, URL) {
	if(namespace.indexOf("nfv") > -1) {
		//nfv中创建的nodetype也要删除
		URL += "?delSubstitutableNodeType=true";
	}
	deleteResource(name, URL, function() {
		element.remove();
	});
}

function importCSAR() {
	$('#upCSARDiag').modal('show');
}

// If export button is clicked with "CTRL", the plain XML is shown, not the CSAR
// We use "on" with filters instead as new elements could be added when pressing "Add new" (in the future)
// contained code is the same as the code of the CSAR button at the topology modeler (see index.jsp)
$(document).on("click", ".exportBtn", function(evt) {
	var url = $(this).attr("href");
	if (evt.ctrlKey) {
		url = url.replace(/csar$/, "definitions");
	}
	window.open(url);
	return false;
});


$(document).on("click", ".deployBtn", function(evt) {
	var url = $(this).attr("href");
	var name = $(this).attr("lang");
	cConfirmYesNo("Do you really want to deploy " + name + "?", function(){
		//self.location.href = url;
		$.get(url, function(data) {
			 //console.log(data);
			if(data[0].result == "OK")
			{
				vShowSuccess("Successfully deployed " +name+ "!");  
			}
			else
			{
				vShowError("Deployed " + name + " failed!");  
			}
			restDialog();
		},'json');
	}, "Please confirm");
	return false;
});

<%-- Special feature in the case of the service template --%>
<c:if test="${it.type eq 'ServiceTemplate'}">
//If edit button is clicked with "CTRL", the topology modeler is opened, not the service template editor
//We use "on" with filters instead as new elements could be added when pressing "Add new" (in the future)
$(document).on("click", ".editBtn", function(evt) {
	var url = $(this).attr("href");
	//编辑按钮直接跳到编辑界面
	url = url.replace(/\?edit$/, "topologytemplate/?edit");
	window.location = url;
	/*if (evt.ctrlKey) {
		url = url.replace(/\?edit$/, "topologytemplate/?edit");
		// open in new tab
		var newWin = window.open(url);
		// focussing the new window does not work in Chrome
		newWin.focus();
	} else {
		// normal behavior
		window.location = url;
	}*/
	evt.preventDefault();
});
</c:if>

$(".exportButton").tooltip({
	placement: 'bottom',
	html: true,
	title: "Export CSAR.<br/>Hold CTRL key to export XML only."
});
$(".deployButton").tooltip({
	placement: 'bottom',
	html: true,
	title: "Deploy CSAR."
});
$(".editButton").tooltip({
	placement: 'bottom',
	html: true,
	title: <c:if test="${it.type eq 'ServiceTemplate'}">"Edit.<br/>Hold CTRL key to directly open the topology modeler."</c:if><c:if test="${not (it.type eq 'ServiceTemplate')}">"Edit"</c:if>
});
$(".deleteButton").tooltip({
	placement: 'bottom',
	title: "Delete"
});
</script>

</t:genericpage>

<jsp:include page="./csarDialogs.jsp" />

<script>
<c:if test="${it.type eq 'ServiceTemplate'}">
$(function(){
	//nfv菜单
    loadAndParseFile("nfvDropdownMenu.json");
    //paas菜单
    loadAndParseFile("paasDropdownMenu.json");

    function loadAndParseFile(filename) {
        $.ajax({
            url: "/modeldesigner/conf/" + filename,
            async: false,
            dataType: 'json',
            success: function(data) {
                //创建按钮下拉菜单
                var liHtml = $("#templateDropdownMenu").html();
                data = data || [];
                for(var i=0; i<data.length; i++) {
                    var menu = data[i];                   
                    liHtml += '<li><a href="/modeldesigner/'+menu.url+'">'+menu.name_zh+'</a></li>';
                }
                $("#templateDropdownMenu").html(liHtml);
            }
        });
    }
});
</c:if>
</script>
