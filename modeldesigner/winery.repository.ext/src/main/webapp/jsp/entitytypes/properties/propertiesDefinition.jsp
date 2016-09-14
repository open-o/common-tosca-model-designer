<%--

    Copyright 2016 [ZTE] and others.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/myDefCss.css" />

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common"%>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions"%>

<%-- createResource of winery-support.js could be used. However, currently selects are not supported --%>
<div class="modal fade" id="addPropertyDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<span id="winery-nodetype-property-title" name_i18n="winery_i18n"></span>
				</h4>
			</div>
			<div class="modal-body">
				<form id="addPropertyForm">
					<div class="form-group">
						<label class="control-label" for="propName">
							<span id="winery-nodetype-property-name" name_i18n="winery_i18n"></span>
						</label>
						<input name="key" class="form-control" id="propName" type="text" />
					</div>

					<div class="form-group">
						<label class="control-label" for="propType">
							<span id="winery-nodetype-property-type" name_i18n="winery_i18n"></span>
						</label>
						<select name="type" class="form-control" id="propType">
							<c:forEach var="t" items="${it.availablePropertyTypes}">
								<option value="${t}">${t}</option>
							</c:forEach>
						</select>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<span id="winery-btn-cancel" name_i18n="winery_i18n"></span>
				</button>
				<button type="button" class="btn btn-primary" onclick="createProperty();">
					<span id="winery-btn-add" name_i18n="winery_i18n"></span>
				</button>
			</div>
		</div>
	</div>
</div>

<script>
function noneClicked() {
	disableKVproperties();
	clearXSDElementSelection();
	clearXSDTypeSelection();
	$.ajax({
		url:  "propertiesdefinition/",
		type: 'DELETE',
		async: true,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not remove properties definition", jqXHR, errorThrown);
		}
	});
}

function clearXSDElementSelection() {
	$("#xsdelement").editable('setValue', "", true);
}

function clearXSDTypeSelection() {
	$("#xsdtype").editable('setValue', "", true);
}

$(function(){
	$("#xsdelement").editable({
		type: "select",
		url: "post/",
		pk: 1,
		source: ${w:allXSDElementDefinitionsForTypeAheadSelection()}
	});
	$("#xsdelement").on("click", function(e){
		$("#xsdelementradio").prop("checked", true);
	});

	$("#xsdtype").editable({
		type: "select",
		source: ${w:allXSDTypeDefinitionsForTypeAheadSelection()}
	});
	$("#xsdtype").on("click", function(e){
		$("#xsdtyperadio").prop("checked", true);
	});

	/* make UI more nice: enable click on label */
	$("#textnone").on("click", function(e){
		$("#nopropdef").prop("checked", true);
		noneClicked();
	});
	$("#textxmlelement").on("click", function(e){
		$("#xsdelementradio").prop("checked", true);
		disableKVproperties();
		clearXSDTypeSelection();
	});
	$("#textxmltype").on("click", function(e){
		$("#xsdtyperadio").prop("checked", true);
		disableKVproperties();
		clearXSDElementSelection();
	});
	$("#textcustomkv").on("click", function(e){
		$("#customkv").prop("checked", true);
		updateKVpropertiesVisibility();
		setKVPropertiesOnServer();
		clearXSDElementSelection();
		clearXSDTypeSelection();
	});

	$('#kvPropsTabs a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	});

	$("#addPropertyDiag").on("shown.bs.modal", function() {
		$("#propName").focus();
	});
});
</script>

<br/>
<p style="display:none;">
	<%-- TODO: if clicked on the "label" of the input field (i.e., the content of the input tag), the input should be selected. This is not the default at HTML5 - see http://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_radio --%>
	<input id="customkv" type="radio" name="kind" value="KV" checked="checked"><span class="cursorpointer" id="textcustomkv">Custom key/value pairs</span></input>
</p>
<br/>

<div id="Properties" style="display:none; margin:10px;">
	<ul class="nav nav-tabs" id="kvPropsTabs" style="display:none;">
		<li class="active"><a href="#kvProps">Properties</a></li>
		<li><a href="#wrapper">Wrapping</a></li>
	</ul>

	<div class="tab-content">
		<div class="tab-pane active" id="kvProps">
			<div style="height:40px;" width="100%">
			&nbsp;
			<button style="margin-top:3px;" class="rightbutton zte-btn zte-primary" type="button" onclick="deleteOnServerAndInTable(propertiesTableInfo, 'Property', 'propertiesdefinition/winery/list/');">
				<span id="winery-btn-delete" name_i18n="winery_i18n"></span>
			</button>
			<button style="margin-top:3px;" class="rightbutton zte-btn zte-primary" type="button" onclick="$('#addPropertyDiag').modal('show');">
				<span id="winery-btn-add" name_i18n="winery_i18n"></span>
			</button>
			</div>
			
			<div>
				<table cellpadding="0" cellspacing="0" border="0" class="display" id="propertiesTable">
					<thead>
						<tr>
							<th class="sorting_disabled">
								<span id="winery-nodetype-property-name" name_i18n="winery_i18n"></span>
							</th>
							<th class="sorting_disabled">
								<span id="winery-nodetype-property-type" name_i18n="winery_i18n"></span>
							</th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${it.isWineryKeyValueProperties}">
							<c:forEach var="t" items="${it.propertyDefinitionKVList}">
								<tr>
									<td>${t.key}</td>
									<%-- FIXME: t.type is the short type, but we need the full type. Currently, there is no way to get the full type for a short type --%>
									<td>${t.type}</td>
								</tr>
							</c:forEach>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>

		<div class="tab-pane" id="wrapper">
			<form id="wrapperelementform" enctype="multipart/form-data">
				<fieldset>
					<div style="width:400px;">
						<div class="form-group">
							<label for="wrapperelement_name">Name of Wrapper Element</label>
							<a href="#" class="form-control" id="wrapperelement_name" data-url="propertiesdefinition/winery/elementname" data-send="always" data-title="Local Name" data-type="text" data-value="${it.elementName}"></a>
						</div>
						<t:namespaceChooser idOfInput="wrapperelement_ns" selected="${it.namespace}" allNamespaces="${w:allNamespaces()}"></t:namespaceChooser>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
</div>

<script>
function disableKVproperties() {
	$("#Properties").hide();
}

function enableKVproperties() {
	$("#Properties").show();
}

function updateKVpropertiesVisibility() {
	if ($("input[name='kind']:checked").val() == "KV") {
		enableKVproperties();
	} else {
		disableKVproperties();
	}
}

function setKVPropertiesOnServer() {
	$.ajax({
		url: "propertiesdefinition/winery/",
		type: "POST",
		async: true,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could change to custom key/value pairs", jqXHR, errorThrown);
		}
	});
}

$(function() {
	// put change function on all inputs to get notified of any change by the user
	$("input[name='kind']").on("change", function(e) {
		// we do not POST something to the server as only concrete values really trigger a change on server side
		var target = e.currentTarget.value;
		if (target == "none") {
			noneClicked();
		} else if (target == "element") { 
			disableKVproperties();
			clearXSDTypeSelection();
		} else if (target == "type") {
			disableKVproperties();
			clearXSDElementSelection();
			enableKVproperties();
		} else if (target == "KV") {
			<c:if test="${not it.isWineryKeyValuePropertiesDerivedFromXSD}">
			<%-- only empty the k/v properties if not derived from XSD--%>
			setKVPropertiesOnServer();
			</c:if>
			clearXSDElementSelection();
			clearXSDTypeSelection();
		} else {
			vShowError("UI not consistent to code");
		}
	});

	// initialization - display the custom box to enter k/vs only if KV is selected
	updateKVpropertiesVisibility();

	$("#wrapperelement_name").editable({
		ajaxOptions: {
			type: 'put'
		},
		params: function(params) {
			// adjust params according to Winery's expectations
			delete params.pk;
			params.name = params.value;
			delete params.value;
			return params;
		}
	}).on("save", function(e, params) {
		vShowSuccess("Successfully updated local name of wrapper element");
	});

	$("#wrapperelement_ns").on("change", function(e) {
		$.ajax({
			url: "propertiesdefinition/winery/namespace",
			type: "PUT",
			async: true,
			contentType: "text/plain",
			processData: false,
			data: e.val,
			error: function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could not update namespace", jqXHR, errorThrown);
			},
			success: function(data, textStatus, jqXHR) {
				vShowSuccess("Successfully updated namespace");
			}
		});
	});

	//add by wandong, hide propertiesdefinition button
	$("a[href='#propertiesdefinition']").hide();

	loadi18n('winery-topologymodeler-i18n', '/winery-topologymodeler/i18n/');
});

var propertiesTableInfo = {
	id: '#propertiesTable'
};

require(["winery-support"], function(ws) {
	ws.initTable(propertiesTableInfo);
});

function createProperty() {
	var data = {
		key: $("#propName").val(),
		type: $('#propType :selected').text()
	}
	$.ajax({
		url: "propertiesdefinition/winery/list/",
		type: "POST",
		async: true,
		contentType: "application/json",
		processData: false,
		data: JSON.stringify(data),
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError($.i18n.prop("winery-nodetype-message-error"), jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
			var name = $('#propName').val();
			var type = $('#propType :selected').text();
			var dataToAdd = [name, type];
			propertiesTableInfo.table.fnAddData(dataToAdd);
			vShowSuccess($.i18n.prop("winery-nodetype-message-success"));
			$('#addPropertyDiag').modal('hide');
		}
	});
}
</script>

