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
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" tagdir="/WEB-INF/tags/parameters" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<link type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet" />
<link type="text/css" href="${pageContext.request.contextPath}/css/myDefCss.css" rel="stylesheet" />

<script>

var embeddedPlansTableInfo = {
	id: '#embeddedPlansTable'
};

var linkedPlansTableInfo = {
		id: '#linkedPlansTable'
};

var mEmbeddedPlansTableData = ${it.embeddedPlansTableData};

$(function() {
	require(["winery-support"], function(ws) {
		ws.initTable(embeddedPlansTableInfo, {
			"aoColumns": [
							{ "bVisible": false, "bSearchable": false}, // ID column
							{ "sTitle": "Precondition" , "sClass" : "plan_td"},
							{ "sTitle": "Name" , "sClass" : "plan_td"},
							{ "sTitle": "Type" , "sClass" : "plan_td"},
							{ "sTitle": "Language" , "sClass" : "plan_td"}
						],
			"aaData" : ${it.embeddedPlansTableData}
		});

		/*ws.initTable(linkedPlansTableInfo, {
			"aoColumns": [
							{ "bVisible": false, "bSearchable": false}, // ID column
							{ "sTitle": "Precondition" },
							{ "sTitle": "Name" },
							{ "sTitle": "Type" },
							{ "sTitle": "Language" },
							{ "sTitle": "Reference" }
						],
			"aaData" : ${it.linkedPlansTableData}
		});*/
	});
});

function editIOParameters() {
	if (embeddedPlansTableInfo.selectedRow) {
		require(["winery-support"], function(ws) {
			if (ws.isEmptyTable(embeddedPlansTableInfo)) {
				vShowError("No plans available");
				return;
			}
			updateInputAndOutputParameters(getPlanURL());
			$("#editParametersDiag").modal("show");
		});
	} else {
		vShowError("No plan selected");
	}
}
	function createPlan(data) {
		if (highlightRequiredFields()) {
			vShowError("Please fill out all required fields");
			return;
		}
		data.submit();
	}

	function getPlanURL() {
		var id = embeddedPlansTableInfo.table.fnGetData(embeddedPlansTableInfo.selectedRow, 0);
		return "plans/" + encodeURIComponent(id) + "/";
	}

	function openPlanEditor() {
		if (embeddedPlansTableInfo.selectedRow) {
			var isEmptyTable = embeddedPlansTableInfo.table.children("tbody").children("tr").first().children("td").hasClass("dataTables_empty");
			if (isEmptyTable) {
				vShowError("No plans available");
				return;
			}
			//window.open(getPlanURL() + "?edit", "_blank");
			
			self.location.href = getPlanURL() + "?edit";
		} else {
			vShowError("No plan selected");
		}
	}

	function letUserChooseAPlan() {
		$('#planFileInput').trigger('click');
		$('#planChooseBtn').focus();
	}

	requirejs(["jquery.fileupload"], function(){
		$('#addPlanForm').fileupload().bind("fileuploadadd", function(e, data) {
			$.each(data.files, function (index, file) {
				$("#planFileText").val(file.name);
			});
			$("#addPlanBtnFUP").off("click");
			$("#addPlanBtnFUP").on("click", function() {
				createPlan(data);
			});
		}).bind("fileuploadstart", function(e) {
			$("#addPlanBtnFUP").button("loading");
		}).bind('fileuploadfail', function(e, data) {
			vShowAJAXError("Could not add plan", data.jqXHR, data.errorThrown);
			$("#addPlanBtnFUP").button("reset");
		}).bind('fileuploaddone', function(e, data) {
			vShowSuccess("Plan created successfully");

			// reset the add button
			$("#addPlanBtnFUP").button("reset");
			// do not allow submission of the old files on a click if the dialog is opened another time
			$("#addPlanBtnFUP").off("click");

			// TODO: if id is already present in table, delete row in table

			embeddedPlansTableInfo.table.fnAddData(data.result.tableData);

			$('#addPlanDiag').modal('hide');
		});
	});
</script>

<div class="modal fade" id="addPlanDiag">
	<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h4 class="modal-title">Add Plan</h4>
		</div>
		<div class="modal-body">
			<form id="addPlanForm" enctype="multipart/form-data" action="plans/" method="post">
				<div class="form-group">
					<label class="control-label">Name</label>
					<input name="planName" id="planName" type="text" class="form-control" required="required">
				</div>

				<t:typeswithshortnameasselect label="Type" type="plantype" selectname="planType" typesWithShortNames="${it.planTypes}">
				</t:typeswithshortnameasselect>

				<t:typeswithshortnameasselect label="Language" type="planlanguage" selectname="planLanguage" typesWithShortNames="${it.planLanguages}">
				</t:typeswithshortnameasselect>

				<div class="form-group" id="fileDiv">
					<label class="control-label" for="planFileDiv">Archive</label>
					<div style="display: block; width: 100%" id="planFileDiv">
						<input id="planFileInput" name="file" type="file" style="display:none">
						<input name="fileText" id="planFileText" type="text" class="form-control" style="width:300px; display:inline;" onclick="letUserChooseAPlan();" required="required">
						<button type="button" id="planChooseBtn" class="btn btn-default btn-xs" onclick="letUserChooseAPlan();">Choose</button>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			<button type="button" class="btn btn-primary" data-loading-text="Uploading..." id="addPlanBtnFUP">Add</button>
			<button type="button" class="btn btn-primary" style="display:none;" id="addPlanBtnBPMN4TOSCA">Add</button>
		</div>
	</div>
	</div>
</div>

<script>
$("#planLanguage").on("change", function(e) {
	var lang = $("#planLanguage").val();
	if (lang == "http://www.opentosca.org/bpmn4tosca") {
		$("#fileDiv").hide();
		$("#addPlanBtnFUP").hide();
		$("#addPlanBtnBPMN4TOSCA").show();
	} else {
		$("#fileDiv").show();
		$("#addPlanBtnFUP").show();
		$("#addPlanBtnBPMN4TOSCA").hide();
	}
});

$("#addPlanBtnBPMN4TOSCA").on("click", function() {
	var data = new FormData();
	data.append("planName", $("#planName").val());
	data.append("planType", $("#planType").val());
	data.append("planLanguage", $("#planLanguage").val());

	$.ajax({
		url: "plans/",
		type: "POST",
		async: false,
		contentType: false, // jQuery automatically sets multipart/form-data; boundary=...
		data: data,
		processData: false,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not add BPMN4TOSCSA plan", jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
			//typesTableInfo.table.fnAddData([$('#shortname').val(), $('#type').val()]);
			$('#addPlanDiag').modal('hide');
			vShowSuccess("Successfully added plan. Please refresh the page.");
			
			setTimeout('window.location.reload()', 1000);
		}
	});

});
</script>

<p:parametersJS></p:parametersJS>

<div class="modal fade" id="editParametersDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Edit Parameters</h4>
			</div>
			<div class="modal-body">
				<p:parametersInput baseURL="getPlanURL()"></p:parametersInput>
				<p:parametersOutput baseURL="getPlanURL()"></p:parametersOutput>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

	<div style="margin:5px" align="right">
		<button class="btn btn-success" onclick="$('#addPlanDiag').modal('show');">Add</button>
		<button class="btn btn-primary hidden" onclick="openPlanEditor();" id="planEdit">Edit</button>
		<button class="btn btn-danger" onclick="deleteOnServerAndInTable(embeddedPlansTableInfo, 'Plan', 'plans/', 0, 2);">Remove</button>
		<button class="rightbutton btn btn-xs btn-default hidden" onclick="editIOParameters();">I/O Parameters</button>

	</div>
	<div id="managementPlans">
<%
if (org.eclipse.winery.repository.Prefs.INSTANCE.isPlanBuilderAvailable()) {
%>
	<script>
	function generateBuildPlan() {
		$("#btnGenerateBuildPlan").button('loading');
		$.ajax({
			url: 'topologytemplate/',
			// targeting method triggerGenerateBuildPlan in TopologyTemplateResource.java
			dataType: "text"
		}).fail(function(jqXHR, textStatus, errorThrown) {
			$("#btnGenerateBuildPlan").button('reset');
			vShowAJAXError("Could not trigger plan generation.", jqXHR, errorThrown);
		}).done(function(data, textStatus, jqXHR) {
			$("#btnGenerateBuildPlan").button('reset');
			var resultText = "Successfully generated build plan. Please refresh the page.";
			vShowSuccess(resultText);
		});
	}
	</script>
	<button id="btnGenerateBuildPlan" class="btn btn-xs btn-default" data-loading-text="Generating..." onclick="generateBuildPlan();">Generate Build Plan</button>
<%
}
%>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="embeddedPlansTable"></table>

		<br /><br />
		<h4 class="hidden">Linked Plans</h4>
		<table cellpadding="0" cellspacing="0" border="0" class="hidden" id="linkedPlansTable"></table>
	</div>
