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
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div>
	<button class="rightbutton btn btn-danger" type="button" onclick="deleteOnServerAndInTable(typesTableInfo, 'Type', '${it.URL}', 1);">Remove</button>
	<button class="rightbutton btn btn-success" type="button" onclick="$('#addTypeShortnameDiag').modal('show');">Add</button>
</div>

<table id="typeswithshortnametable">

<thead>
	<tr>
		<th>Short name</th>
		<th>Long Name</th>
	</tr>
</thead>

<tbody>
	<c:forEach var="type" items="${it.types}">
		<tr>
			<td class="shortname editable">${type.shortName}</td>
			<td>${type.type}</td>
		</tr>
	</c:forEach>
</tbody>

</table>


<div class="modal fade" id="addTypeShortnameDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Add short name</h4>
			</div>
			<div class="modal-body">
				<form id="addTypeShortnameForm" enctype="multipart/form-data">
					<fieldset>
						<div class="form-group">
							<label for="shortname">Short name</label>
							<input name="shortname" id="shortname" class="form-control" type="text" />
						</div>
						<div class="form-group">
							<label for="type">Type</label>
							<input name="type" id="type" class="form-control" type="text" />
						</div>
					</fieldset>
				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary" onclick="addShortNameAndType();">Add</button>
			</div>
		</div>
	</div>
</div>


<script>
$(document).on("click", "td.shortname",
		vCreateTdClickFunction(
			"${it.URL}",
			"shortname",
			"type"));

var typesTableInfo = {
		id: '#typeswithshortnametable'
};
require(["winery-support"], function(ws) {
	ws.initTable(typesTableInfo);
});

function addShortNameAndType() {
	$.ajax({
		url: "${it.URL}",
		type: "POST",
		async: false,
		data: $('#addTypeShortnameForm').serialize(),
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not add type information", jqXHR, errorThrown);
		},
		success: function(data, textSTatus, jqXHR) {
			typesTableInfo.table.fnAddData([$('#shortname').val(), $('#type').val()]);
			$('#addTypeShortnameDiag').modal('hide');
			vShowSuccess("Successfully added type information.");
		}
	});
}

</script>
