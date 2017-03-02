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
 *    Uwe Breitenb¨¹cher - initial API and implementation and/or initial documentation
 *    Oliver Kopp - improvements to fit updated index.jsp
 *    Yves Schubert - switch to bootstrap 3
 *******************************************************************************/
 
 /*******************************************************************************
 * Modifications Copyright 2016-2017 ZTE Corporation.
 *******************************************************************************/
--%>

<%@tag language="java" pageEncoding="UTF-8" description="Renders the boundary definitions of the service template"%>

<%@attribute name="serviceTemplateURL" required="true" type="java.lang.String" description="The topologyTemplate URL"%>
<%@attribute name="serviceTemplateName" required="true" type="java.lang.String" description="The serviceTemplate Name"%>
<%@attribute name="ns" description="service template namespace" required="true" type="java.lang.String"%>
<%@attribute name="palette" description="palette or view mode" required="false" type="java.lang.Boolean" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link rel="stylesheet" href="css/boundarydefinition.css" />
<div id="boundaryDefinition" class="right-menu" style="display: none;">
	<div class="right-menu-content">
		<div class="title">
			<h4 style="display:inline"><%=serviceTemplateName%></h4>
			<button id="closeBoundaryBtn" type="button" class="close">&times;</button>
			<div class="menuContainerHead">
				<div class="item-group">
					<label id="winery-boundary-template-desc" name_i18n="winery_i18n"></label>
					<input class="form-control desc-input description" type="text"
						<c:if test="${!palette}">disabled</c:if>/>
				</div>
				<div class="item-group">
					<label id="winery-boundary-template-csarType" name_i18n="winery_i18n"></label>
					<input class="form-control desc-input csarType" disabled/>
				</div>
				<div class="item-group">
					<label id="winery-boundary-template-csarVersion" name_i18n="winery_i18n"></label>
					<input type="text" class="form-control desc-input csarVersion" <c:if test="${!palette}">disabled</c:if>/>
				</div>
				<div class="item-group">
					<label id="winery-boundary-template-csarProvider" name_i18n="winery_i18n"></label>
					<input type="text" class="form-control desc-input csarProvider" <c:if test="${!palette}">disabled</c:if>/>
				</div>
			</div>
		</div>
		<ul class="nav title-list">
			<li class="active">
		      	<a href="#boundaryInputs" data-toggle="tab" class="tab-multi">
		      		<span id="winery-boundary-tab-input" name_i18n="winery_i18n"></span>
		      	</a>
		   	</li>	
			<li>
		      	<a href="#boundaryMetaData" data-toggle="tab" class="tab-multi">
		      		<span id="winery-boundary-tab-property" name_i18n="winery_i18n"></span>
		      	</a>
		   	</li>
		   	<li>
		      	<a href="#boundaryReqAndCap" data-toggle="tab" class="tab-multi">
		      		<span id="winery-boundary-tab-reqAndCap" name_i18n="winery_i18n"></span>
		      	</a>
		   	</li>
		   	<li style="display:none;">
		      	<a href="#boundaryScript" data-toggle="tab" class="tab-multi">
		      		<span id="winery-boundary-tab-script" name_i18n="winery_i18n"></span>
		      	</a>
		   	</li>
		   	<li>
		      	<a href="#boundaryPolicy" data-toggle="tab" class="tab-multi">
		      		<span id="winery-boundary-tab-policy" name_i18n="winery_i18n"></span>
		      	</a>
		   	</li>
			<li>
		      	<a href="#serverGroups" data-toggle="tab" class="tab-multi">
		      		<span id="winery-boundary-tab-groups" name_i18n="winery_i18n"></span>
		      	</a>
		   	</li>
		   	<li style="display:none;">
		      	<a href="#boundaryVnffg" data-toggle="tab" class="tab-multi">
		      		<span id="winery-boundary-tab-vnffg" name_i18n="winery_i18n"></span>
		      	</a>
		   	</li>
		</ul>		
		<div class="tab-content">
			<div class="tab-pane fade in active" id="boundaryInputs">
				<form role="form" class="form-horizontal">
					<c:if test="${palette}">
					<div class="form-group" style="margin-left:0">
						<div class="col-sm-4" style="padding-left:0"><input class="form-control" type="text" placeholder="winery-boundary-input-table-name" name_i18n="winery_i18n"></div>
						<div class="col-sm-4" style="padding-left:0;display:none">
							<select class="form-control" style="height:30px;padding-top:3px;">
								<option value="string">string</option>
							</select>
						</div>
						<div class="col-sm-4" style="padding-left:0"><input class="form-control" type="text" placeholder="winery-boundary-input-table-desc" name_i18n="winery_i18n"></div>
						<div class="col-sm-3" style="padding-left:0"><input class="form-control" type="text" placeholder="winery-boundary-input-table-value" name_i18n="winery_i18n"></div>
						<button id="addInputBtn" class="add">
							<i class="fa fa-plus"></i>
						</button>
					</div>
					</c:if>
					<div id="boundaryInputsTable_div"></div>
				</form>
			</div>
			<div class="tab-pane fade" id="boundaryMetaData">
				<form role="form" class="form-horizontal">
					<c:if test="${palette}">
					<div class="form-group" style="margin-left:0">
						<div class="col-sm-4" style="padding-left:0"><input class="form-control" type="text" placeholder="winery-boundary-metadata-table-name" name_i18n="winery_i18n"></div>
						<div class="col-sm-4" style="padding-left:0"><input class="form-control" type="text" placeholder="winery-boundary-metadata-table-value" name_i18n="winery_i18n"></div>
						<button id="addMetaDataBtn" class="add">
							<i class="fa fa-plus"></i>
						</button>
					</div>
					</c:if>
					<div id="boundaryMetaDataTable_div"></div>
				</form>
			</div>
			<div class="tab-pane fade" id="boundaryReqAndCap">
				<c:if test="${palette}">
				<div style="text-align:right;">
					<button id="addReqOrCapBtn" class="zte-btn zte-white">
						<span id="winery-boundary-reqAndCap-btn-add" name_i18n="winery_i18n"></span>
					</button>
				</div>
				</c:if>
				<div><span id="winery-boundary-reqAndCap-text-req" name_i18n="winery_i18n"></span></div>
				<div id="boundaryRequirementsTable_div"></div>
				<div><span id="winery-boundary-reqAndCap-text-cap" name_i18n="winery_i18n"></span></div>
				<div id="boundaryCapabilitiesTable_div"></div>
			</div>
			<div class="tab-pane fade" id="boundaryScript">
				<c:if test="${palette}">
				<form id="scriptFileupload" role="form" enctype="multipart/form-data" class="form-horizontal">
					<div class="form-group">
						<label class="control-label col-sm-4">
							<span id="winery-boundary-script-text-prefixPath" name_i18n="winery_i18n"></span>
						</label>
						<div class="col-sm-8">
							<select class="form-control" id="scriptPrefixPath">
								<option>/SoftwareImages/</option>
								<option>/AppSoftwares/</option>
								<option>/Scripts/</option>
								<option>/Policies/</option>
								<option>/</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-4">
							<span id="winery-boundary-script-text-path" name_i18n="winery_i18n"></span>
						</label>
						<div class="col-sm-8">
							<input class="form-control" type="text" id="scriptPath" placeholder="a/b/c">
						</div>
					</div>
			        <div class="input-group fileupload-btn">
			           <div id="scriptFileName" class="form-control file-input">
			           </div>
			           <span class="input-group-btn">
			           		<span class="btn btn-primary fileinput-button">
			                	<span id="winery-boundary-script-btn-choose" name_i18n="winery_i18n"></span>
			                	<input type="file" name="file" multiple>
			            	</span>
			              	<button id="scriptFilesubmit" class="btn btn-default" type="submit" disabled>
			                	<span id="winery-boundary-script-btn-upload" name_i18n="winery_i18n"></span>
			              	</button>
			           </span>
			        </div>
			        <div class="progress progress-striped active" style="margin-top:10px;"> 
					    <div id="scriptProgress" class="progress-bar progress-bar-info" role="progressbar" 
					         aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"> 
					    </div> 
					</div>
				</form>
				</c:if>
				<div id="boundaryScriptTable_div"></div>
			</div>
			<div class="tab-pane fade" id="boundaryPolicy">
				<c:if test="${palette}">
				<button id="addPolicyBtn" class="zte-btn zte-white zte-multi boundary-btn">
					<span id="winery-boundary-policy-btn-add" name_i18n="winery_i18n"></span>
				</button>
				</c:if>
				<div id="boundaryPolicyTable_div"></div>
			</div>
			<div class="tab-pane fade" id="serverGroups">
				<c:if test="${palette}">
				<button id="addGroupsBtn" class="zte-btn zte-white zte-multi boundary-btn">
					<span id="winery-boundary-groups-btn-add" name_i18n="winery_i18n"></span>
				</button>
				</c:if>
				<div id="serverGroupsTable_div"></div>
			</div>
			<div class="tab-pane fade" id="boundaryVnffg">
				<c:if test="${palette}">
				<button id="addVnffgBtn" class="zte-btn zte-white zte-multi boundary-btn">
					<span id="winery-boundary-vnffg-btn-add" name_i18n="winery_i18n"></span>
				</button>
				</c:if>
				<div id="boundaryVnffgTable_div"></div>
			</div>
		</div>
	</div>
</div>

<%-- add policy dialog --%>
<div class="modal fade" id="policyDialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<span id="winery-boundary-policy-dialog-title" name_i18n="winery_i18n"></span>
				</h4>
			</div>
			<div class="modal-body">				
				<form id="policyForm" class="form-horizontal" role="form">
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-policy-dialog-name" name_i18n="winery_i18n"></span><span class="require-red-star">*</span>
						</label>
						<div class="col-sm-7">
					    	<input class="form-control" id="policy_name" name="policy_name" />
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-policy-dialog-desc" name_i18n="winery_i18n"></span><span class="require-red-star">*</span>
						</label>
						<div class="col-sm-7">
					    	<input class="form-control" id="policy_desc" name="policy_desc" />
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-policy-dialog-type" name_i18n="winery_i18n"></span><span class="require-red-star">*</span>
						</label>
						<div class="col-sm-7">
					    	<select class="form-control" id="policy_type" name="policy_type">
					    	</select>
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-policy-dialog-target" name_i18n="winery_i18n"></span><span class="require-red-star star-hide">*</span>
						</label>
						<div class="col-sm-7">
					    	<select class="form-control" id="policy_target" name="policy_target" multiple="multiple">
					    	</select>
					    </div>
					</div>
					<div class="arrow-separator">
						<span id="winery-boundary-policy-dialog-properties" name_i18n="winery_i18n"></span>
						<div class="arrow"></div>
						<div class="arrow arrow-outer"></div>						
					</div>		
					<div id="policy_properties"></div>					
					</form>
					<div class="policy-editor" id="policyeditor" hidden="hidden">
						<form class="form-horizontal" role="form">
							<div class="form-group">
								<label class="col-sm-4 control-label">
									<span id="winery-boundary-policy-file-name" name_i18n="winery_i18n"></span><span class="require-red-star">*</span>
								</label>
								<div class="col-sm-7">
									<input class="form-control" id="policy_file_name" name="policy_file_name" />
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-4 control-label">
									<span id="winery-boundary-policy-rule" name_i18n="winery_i18n"></span><span class="require-red-star">*</span>
								</label>
								<div class="col-sm-7">
									<textArea class="policy-editor-area" id="policy_file_content"></textArea>
								</div>
							</div>
						</form>					
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="zte-btn zte-primary" id="policyConfirm">
					<span id="winery-btn-confirm" name_i18n="winery_i18n"></span>
				</button>
				<button type="button" class="zte-btn zte-default" data-dismiss="modal">
					<span id="winery-btn-cancel" name_i18n="winery_i18n"></span>
				</button>
			</div>
		</div>
	</div>
</div>

<%-- add groups dialog --%>
<div class="modal fade" id="groupsDialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<span id="winery-boundary-groups-dialog-title" name_i18n="winery_i18n"></span>
				</h4>
			</div>
			<div class="modal-body">
				<form id="groupsForm" class="form-horizontal" role="form">
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-groups-dialog-type" name_i18n="winery_i18n"></span>
							<span class="required" aria-required="true">*</span>
						</label>
						<div class="col-sm-7">
					    	<select class="form-control" id="groups_type" name="groups_type">				
					    	</select>
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-groups-dialog-name" name_i18n="winery_i18n"></span>
							<span class="required" aria-required="true">*</span>
						</label>
						<div class="col-sm-7">
					    	<input class="form-control" id="groups_name" name="groups_name" />
					    </div>
					</div>									
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-groups-dialog-target" name_i18n="winery_i18n"></span>
							<span class="required" aria-required="true">*</span>
						</label>
						<div class="col-sm-7">
					    	<select class="form-control" id="groups_target" name="groups_target" multiple="multiple">
					    	</select>
					    </div>
					</div>					
					<div id="groups_properties"></div>												
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="zte-btn zte-primary" id="groupsConfirm">
					<span id="winery-btn-confirm" name_i18n="winery_i18n"></span>
				</button>
				<button type="button" class="zte-btn zte-default" data-dismiss="modal">
					<span id="winery-btn-cancel" name_i18n="winery_i18n"></span>
				</button>
			</div>
		</div>
	</div>
	<div id="groupsPropertieContainer" style="display: none;"></div>	
</div>

<%-- add reqorcap dialog --%>
<div class="modal fade" id="boundaryReqOrCapDialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<span id="winery-boundary-reqAndCap-dialog-title" name_i18n="winery_i18n"></span>
				</h4>
			</div>
			<div class="modal-body">
				<form id="boundaryReqOrCapForm" class="form-horizontal" role="form">
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-reqAndCap-table-name" name_i18n="winery_i18n"></span>
						</label>
						<div class="col-sm-7">
					    	<input class="form-control" id="reqOrCap_name" name="reqOrCap_name" />
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-reqAndCap-dialog-type" name_i18n="winery_i18n"></span>
						</label>
						<div class="col-sm-7">
					    	<select class="form-control" id="reqOrCap_type">
					    		<option value="req" id="winery-boundary-reqAndCap-text-req" name_i18n="winery_i18n">
					    		</option>
					    		<option value="cap" id="winery-boundary-reqAndCap-text-cap" name_i18n="winery_i18n">
					    		</option>
					    	</select>
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label" id="reqOrCap_label">
							<span id="winery-boundary-reqAndCap-dialog-reqAndCap" name_i18n="winery_i18n"></span>
						</label>
						<div class="col-sm-7">
					    	<select class="form-control" id="reqOrCap_list" name="reqOrCap_list">
					    	</select>
					    </div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="zte-btn zte-primary" id="boundaryReqOrCapConfirm">
					<span id="winery-btn-confirm" name_i18n="winery_i18n"></span>
				</button>
				<button type="button" class="zte-btn zte-default" data-dismiss="modal">
					<span id="winery-btn-cancel" name_i18n="winery_i18n"></span>
				</button>
			</div>
		</div>
	</div>
</div>

<%-- add VNFFG dialog --%>
<div class="modal fade" id="vnffgDialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<span id="winery-boundary-vnffg-dialog-title" name_i18n="winery_i18n"></span>
				</h4>
			</div>
			<div class="modal-body">
				<form id="vnffgForm" class="form-horizontal" role="form">
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-vnffg-dialog-type" name_i18n="winery_i18n"></span>
						</label>
						<div class="col-sm-7">
					    	<select class="form-control" id="vnffg_type" name="vnffg_type">
					    	</select>
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-vnffg-dialog-name" name_i18n="winery_i18n"></span>
						</label>
						<div class="col-sm-7">
					    	<input class="form-control" id="vnffg_name" name="vnffg_name" />
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-vnffg-dialog-version" name_i18n="winery_i18n"></span>
						</label>
						<div class="col-sm-7">
					    	<input class="form-control" id="vnffg_prop_version" name="vnffg_prop_version" />
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">
							<span id="winery-boundary-vnffg-dialog-vendor" name_i18n="winery_i18n"></span>
						</label>
						<div class="col-sm-7">
					    	<input class="form-control" id="vnffg_prop_vendor" name="vnffg_prop_vendor" />
					    </div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="zte-btn zte-primary" id="vnffgConfirm">
					<span id="winery-btn-confirm" name_i18n="winery_i18n"></span>
				</button>
				<button type="button" class="zte-btn zte-default" data-dismiss="modal">
					<span id="winery-btn-cancel" name_i18n="winery_i18n"></span>
				</button>
			</div>
		</div>
	</div>
</div>
<%-- add complex property dialog --%>
<div class="modal fade" id="groupsInputDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<span id="winery-property-dialog-title-edit" name_i18n="winery_i18n"></span>
				</h4>
			</div>
			<div class="modal-body">
				<div id="groupsPropertyEditor"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="zte-btn zte-primary" id="groupsInputConfirm">
					<span id="winery-btn-confirm" name_i18n="winery_i18n"></span>
				</button>
				<button type="button" class="zte-btn zte-default" data-dismiss="modal">
					<span id="winery-btn-cancel" name_i18n="winery_i18n"></span>
				</button>
			</div>
		</div>
	</div>
</div>
<script>
$(function() {
	function initReqAndCapTab(datatable) {
		var addTableData = function(tableId, rowData) {
			var dataTable = $("#" + tableId).dataTable();
			dataTable.fnAddData(rowData);
		}

		$("#addReqOrCapBtn").click(function(){
			$("#reqOrCap_type").trigger('change');
			$("#boundaryReqOrCapDialog").modal("show");
		});

		$("#boundaryReqOrCapConfirm").click(function(){
			var option = $("#reqOrCap_list").find("option:selected");
			var reqOrCapObj = {
				nodeName : option.attr("data-nodename"),
				refName : option.text(),
				ref : option.attr("id"),
				name : $("#reqOrCap_name").val()
			}

			var type = $("#reqOrCap_type").val();
			var tableId = "";
			if(type == "req") {
				tableId = "boundaryRequirementsTable";
			} else {
				tableId = "boundaryCapabilitiesTable";
			}
			addTableData(tableId, reqOrCapObj);

			$("#boundaryReqOrCapDialog").modal("hide");
		});
		
		$("#reqOrCap_type").on('change', function(){
			var type = $(this).val();
			var nodeTemplates = $(".NodeTemplateShape:not('.hidden')");
			var elementClass = "";
			if(type == "req") {
				$("#reqOrCap_label").text($.i18n.prop("winery-boundary-reqAndCap-text-req"));
				elementClass = ".requirements";
			} else {
				$("#reqOrCap_label").text($.i18n.prop("winery-boundary-reqAndCap-text-cap"));
				elementClass = ".capabilities";
			}

			$("#reqOrCap_list").children().remove();
			$.each(nodeTemplates, function(index, nodeTemplate){
				var nodeTemplateId = $(nodeTemplate).attr("id");
				var reqorcaps = $("#" + nodeTemplateId).find(elementClass);
				var nodeName = $("#" + nodeTemplateId).children(".fullName").text();

				var optgroup = $("<optgroup>", {"label": nodeName});
				$.each(reqorcaps, function(index, element){
					var id = $(element).find(".id").text();
					var name = $(element).find(".name").text();
					var type = $(element).find(".type").children().text();
						
					var option = $("<option>", {"id": id, "value": id, "data-nodename": nodeName});
					option.text(name);
					optgroup.append(option);
				});
				$("#reqOrCap_list").append(optgroup);
			});

			$("#reqOrCap_list").select2();
		});

		//requirements table
		initReqOrCapTable(datatable, "boundaryRequirementsTable", "requirements");
		//capabilities table
		initReqOrCapTable(datatable, "boundaryCapabilitiesTable", "capabilities");
	}

	function delTableData(tableId, tr) {
		$("#" + tableId).dataTable().fnDeleteRow(tr);
	}

	function initReqOrCapTable(datatable, tableId, urlPath) {
		var operationRender = function(obj) {
			return '<i class="fa fa-trash icon-del" id="winery-btn-delete" name_i18n="winery_i18n"></i>';
		}

		var tableInfo = {
			divId: tableId + "_div",
			id: tableId,
			url: "${serviceTemplateURL}/boundarydefinitions/" + urlPath + "/list",
			columns : [
				{"mData": "name", "name": $.i18n.prop("winery-boundary-reqAndCap-table-name"), "sWidth": "25%"},
				{"mData": "nodeName", "name": $.i18n.prop("winery-boundary-reqAndCap-table-nodeName"), "sWidth": "30%"},
				{"mData": "refName", "name": $.i18n.prop("winery-boundary-reqAndCap-table-refName"), "sWidth": "30%"},
				{"mData": "ref", "name": "引用ID", "bVisible": false},
				{"mData": null, "name": $.i18n.prop("winery-table-operation"), "sWidth":"15%", "fnRender": operationRender}
			]
		}
		datatable.initTable(tableInfo);

		$("#" + tableId).on('draw', function(){
			//删除按钮
			$(this).find("tbody").find(".fa-trash").off("click")
				.on("click", function(){
					var tr = $(this).parent().parent()[0];
					delTableData(tableId, tr);
			});
		});
	}

	function initScriptTab(datatable, palette) {
		var delScriptRender = function(obj) {
			return '<i class="fa fa-trash icon-del" id="winery-btn-delete" name_i18n="winery_i18n"></i>';
		}
		var scriptInfo = {
			divId : "boundaryScriptTable_div",
			id : "boundaryScriptTable",
			url : "${serviceTemplateURL}/servicefiles",
			columns : [
				{"mData": "fileName", "name": $.i18n.prop("winery-boundary-script-table-fileName"), "sWidth":"35%"}, 
				{"mData": "path", "name": $.i18n.prop("winery-boundary-script-table-path"), "sWidth":"40%"}, 
				{"mData": null, "name": $.i18n.prop("winery-table-operation"), "sWidth":"25%", "fnRender" : delScriptRender, "bVisible": palette}
			]
		}

		var initScriptTable = function(scriptInfo) {
			$.ajax({
				type : "GET",
				dataType : "json",
				url : scriptInfo.url,
				success : function(resp) {
					var tableData = resp || [];
				
					datatable.initTableWithData(scriptInfo, tableData, function(){
						$("#boundaryScriptTable tbody").find("i").click(function(){
							var oTds = $(this).parent().siblings();
							var scriptObj = {
								fileName: oTds.eq(0).text(),
								path: oTds.eq(1).text()
							}
							$.ajax({
								url : scriptInfo.url,
								type : "DELETE",
								data : JSON.stringify(scriptObj),
								contentType : "application/json",
								success : function() {
									initScriptTable(scriptInfo);
								},
								error : function() {
									vShowAJAXError($.i18n.prop("winery-boundary-script-message-fail"));
								}
							});
						});
					});
				},
				error : function() {
					datatable.initTableWithData(scriptInfo, []);
				}
			});
		}
		initScriptTable(scriptInfo);

		//initialize upload control
		$("#scriptFileupload").fileupload({
	        url : scriptInfo.url,
	        maxNumberOfFiles : 1,
	        autoUpload : false,
	        add : function(e, data) {
	            $("#scriptFileName").text(data.files[0].name);
	            $("#scriptFilesubmit").attr("disabled", false);
	            $("#scriptProgress").css('width', 0).parent().addClass("active");

	            $("#scriptFilesubmit").remove();
	            $('<button id="scriptFilesubmit" class="btn btn-default" type="button"/>')
	            	.text($.i18n.prop("winery-boundary-script-btn-upload"))
	                .appendTo($(".input-group-btn")[0])
	                .click(function () {
	                	var prefixPath = $("#scriptPrefixPath").val();
	                	var path =  $("#scriptPath").val();
  
                		path = prefixPath + path;
                		
	                	var extData = {
	                        path: path
	                    };
	                    $("#scriptFileupload").fileupload({ formData : extData });

	                    data.submit();
	                });
	        },
	        fail : function(e, data) {
	            vShowAJAXError($.i18n.prop("winery-boundary-script-message-fail"));
	        },
	        always : function(e, data) {
	            initScriptTable(scriptInfo);
	            $("#scriptProgress").css('width', '100%').parent().removeClass("active");
	        },
	        progressall : function(e ,data) {
                var progress = parseInt(data.loaded / data.total * 90, 10);
                $("#scriptProgress").css('width', progress + '%');
            }
	    });
	}

	function initDocumentation() {
		var documentationURL = "${serviceTemplateURL}/extendProperties";
	    $.ajax({
			type : "GET",
			url : documentationURL,
			dataType : "json",
			success : function(resp) {
				if(resp && resp.documentation) {
					$("#boundaryDefinition .menuContainerHead .description").val(resp.documentation);
				}
			}
		});
	}

	function initPolicy(dataTable, palette) {
		var operationPolicyRender = function(obj) {
			var operationHtml = '<i class="fa fa-edit icon-del" data-row="'+ obj.iDataRow 
				+ '" id="winery-btn-edit" name_i18n="winery_i18n" style="margin-right:3px;"></i>'
				+ '<i class="fa fa-trash icon-del" id="winery-btn-delete" name_i18n="winery_i18n"></i>';
			return operationHtml;
		}

		var policyInfo = {
			divId : "boundaryPolicyTable_div",
			id : "boundaryPolicyTable",
			url : "${serviceTemplateURL}/boundarydefinitions/policies/query",
			addUrl: "${serviceTemplateURL}/boundarydefinitions/policies/add",
			delUrl: "${serviceTemplateURL}/boundarydefinitions/policies/delete",
			updateUrl: "${serviceTemplateURL}/boundarydefinitions/policies/update",
			columns : [
				{"mData": "name", "name": $.i18n.prop("winery-boundary-policy-dialog-name"), "sWidth":"40%"},
				{"mData": "desc", "name": $.i18n.prop("winery-boundary-policy-dialog-desc"), "sWidth":"30%"},
				{"mData": "target", "name": $.i18n.prop("winery-boundary-policy-dialog-target"), "bVisible": false},
				{"mData": "type", "name": $.i18n.prop("winery-boundary-policy-dialog-type"), "bVisible": false},
				{"mData": "properties", "name": $.i18n.prop("winery-boundary-policy-dialog-properties"), "bVisible": false},
				{"mData": null, "name": $.i18n.prop("winery-table-operation"), "sWidth" : "30%", 
				 	"fnRender" : operationPolicyRender, "bVisible": palette}
			]
		}

		var delPolicyTableData = function(name, rowData) {
			var delPolicyFile = function(){
				var type = rowData.type;
				var drl_file_url = rowData.properties.drl_file_url;
				var fname = drl_file_url.split("/Policies/")[1].split(".drl")[0];
				if("{http://www.open-o.org/tosca/nfv}tosca.policies.Drools" == type){
					var namespace = "<%=ns%>";
					var namespace_encode = encodeURIComponent(encodeURIComponent(namespace));
					var id = "<%=serviceTemplateName%>";
					var url = "/modeldesigner/servicetemplates/" + namespace_encode + "/" + id + "/policies?fileName=" + fname;
					$.ajax({
						type: "DELETE",
						url: url,
						dataType: "json",
						success: function(resp) {
							var resp = resp || {};							
						}
					});
				}
			}
			$.ajax({
				type: "DELETE",
				url: policyInfo.delUrl + "?name=" + name,
				success: function(resp) {
					delPolicyFile();
					initTable();
				}
			});
		}

		var editPolicyTableData = function(rowId) {
			var rowData = $("#" + policyInfo.id).dataTable().fnGetData(rowId);
			initPolicyDialog(rowData);
		}

		var initTable = function() {
			dataTable.initTable(policyInfo);
			
			$("#" + policyInfo.id).on('draw', function(){
				//edit button
				$(this).find("tbody").find(".fa-edit").off("click")
					.on("click", function(){
						var rowId = $(this).attr("data-row");
						editPolicyTableData(rowId);
				});

				//delete button
				$(this).find("tbody").find(".fa-trash").off("click")
					.on("click", function(){
						var name = $(this).parent().siblings().eq(0).text();
						var rowId = $(this).prev().attr("data-row");
						var rowData = $("#" + policyInfo.id).dataTable().fnGetData(rowId);
						delPolicyTableData(name, rowData);
				});
			});
		}
		initTable();

		var generatePolicyProperty = function(key, value) {
			var div_group = $("<div>", {"class": "form-group"});
			var label = $("<label>", {"class": "control-label col-sm-4"}).text(key);
			var div_input = $("<div>", {"class": "col-sm-7"});
			var input = $("<input>", {"class": "form-control", 
					"name": key});
			var value = value || "";
			input.val(value);

			div_input.append(input);
			div_group.append(label).append(div_input);
			$("#policy_properties").append(div_group);
		}

		var initPolicyDialog = function(rowData) {
			if(rowData) {
				$("#policy_name").val(rowData.name);
				$("#policy_desc").val(rowData.desc);

				var updateUrl = policyInfo.updateUrl + "?name=" + rowData.name;
				$("#policyConfirm").attr("data-url", updateUrl);
				
				var type = rowData.type;
				var drl_file_url = rowData.properties.drl_file_url;
				var fname = drl_file_url.split("/Policies/")[1].split(".drl")[0];
				if("{http://www.open-o.org/tosca/nfv}tosca.policies.Drools" == type){
					var namespace = "<%=ns%>";
					var namespace_encode = encodeURIComponent(encodeURIComponent(namespace));
					var id = "<%=serviceTemplateName%>";
					var url = "/modeldesigner/servicetemplates/" + namespace_encode + "/" + id + "/policies?fileName=" + fname;
					$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(resp) {
							var resp = resp || {};
							var fileName = $("#policy_file_name").val(resp.name);
							var fileContent = $("#policy_file_content").val(resp.content);
							try{
								$("#policyeditor").show();
								$("#policy_properties input[name='drl_file_url']").parent().parent().hide();
							} catch(ignore){}
						}
					});
				} else {
					try{
						$("#policyeditor").hide();
						$("#policy_properties input[name='drl_file_url']").parent().parent().show();
					} catch(ignore){}					
				}
			} else {
				$("#policy_name").val("");
				$("#policy_desc").val("");
				$("#policy_file_name").val("");
				$("#policy_file_content").val("");
			}

			$("#policy_properties").children().remove();
			//initialize policy type
			$.ajax({
				type: "GET",
				url: "/modeldesigner/policytypes/",
				dataType: "json",
				success: function(resp) {
					resp = resp || [];
					$("#policy_type").children().remove();
					$.each(resp, function(index, type){
						var qname = "{" + type.namespace + "}" + type.id;
						var option = $("<option>", {"value": qname}).text(type.id);
						$("#policy_type").append(option);
					});

					if(rowData) {
						$("#policy_type").val(rowData.type);
						$.each(rowData.properties, function(key, value){
							generatePolicyProperty(key, value);
						});
					} else {
						$("#policy_type").trigger('change');
					}
				}
			});

			//initialize policy target
			$("#policy_target").children().remove();
			var nodeTemplates = $(".NodeTemplateShape:not('.hidden')");
			$.each(nodeTemplates, function(index, template){
				var name = $(template).children(".fullName").text();
				var option = $("<option>").text(name);
				$("#policy_target").append(option);
			});
			
			$("#policy_target").select2();
			if(rowData && rowData.target.length > 0) {
				$("#policy_target").val(rowData.target).trigger('change');
			}

			//initialize validator rules
			var rules = {
				policy_name : {required: true},
				policy_type : {required: true},
				policy_target : {required: false}
			}
			initValidate("policyForm", rules);

			$("#policyDialog").modal("show");
		}

		//add policy button
		$("#addPolicyBtn").click(function(){
			initPolicyDialog();
			$("#policyConfirm").attr("data-url", policyInfo.addUrl);
		});

		$("#policy_type").on('change', function(){
			var qname = $(this).val();
			if(!qname) return;

			var namespace = qname.substring(1, qname.indexOf("}"));
			var namespace_encode = encodeURIComponent(encodeURIComponent(namespace));
			var id = qname.substring(qname.indexOf("}") + 1);
			var url = "/modeldesigner/policytypes/" + namespace_encode + "/" + id 
					+ "/propertiesdefinition/winery/list";

			$.ajax({
				type: "GET",
				url: url,
				async: false,
				dataType: "json",
				success: function(resp) {
					resp = resp || [];
					$("#policy_properties").children().remove();

					$.each(resp, function(index, property){
						generatePolicyProperty(property.key, property.value);
					});
				},
				error: function() {
					$("#policy_properties").children().remove();
				}
			});
			
			if("tosca.policies.Drools" == id){
				$("#policy_properties input[name='drl_file_url']").parent().parent().hide();
				$("#policyeditor").show();
			} else {
				$("#policy_properties input[name='drl_file_url']").parent().parent().show();
				$("#policyeditor").hide();
			}
		});

		//add policy upload button in dialog
		var policyUpload = function(){
			var uploadStatus = false;
			var namespace = "<%=ns%>";
			var namespace_encode = encodeURIComponent(encodeURIComponent(namespace));
			var id = "<%=serviceTemplateName%>";
			var url = "/modeldesigner/servicetemplates/" + namespace_encode + "/" + id + "/policies";
			var fileName = $("#policy_file_name").val();
			var fileContent = $("#policy_file_content").val();
			
			var rowData = {"name": fileName, "content": fileContent};
			var drl_file_url = "";			
			if("" == fileName){
				vShowError($.i18n.prop("winery-upload-file-name-empty"));
			}
			if("" == fileContent){
				vShowError($.i18n.prop("winery-upload-file-content-empty"));
			}
			$.ajax({
				type: "POST",
				url: url,
				async: false,
				contentType: "application/json",
				data: JSON.stringify(rowData),
				success: function(resp) {
					drl_file_url = resp;					
					uploadStatus = true;
				}
			});
			
			$("#policy_properties input[name='drl_file_url']").val(drl_file_url);
			return uploadStatus;
		}
		
		//add policy confirm button in dialog
		$("#policyConfirm").click(function(){
			var form = $("#policyForm");
			if(!form.valid()) {
				return;
			}
			if(!policyUpload()){
				return;
			}
			var target = $("#policy_target").val() || [];
			var properties = {};
			var inputs = $("#policy_properties").find("input");
			$.each(inputs, function(index, input){
				var name = $(input).attr("name");
				properties[name] = $(input).val();
			});
			
			var rowData = {
				name: $("#policy_name").val(),
				desc: $("#policy_desc").val(),
				type: $("#policy_type").val(),
				target: target,
				properties: properties
			}

			var url = $("#policyConfirm").attr("data-url");
			$.ajax({
				type: "POST",
				url: url,
				contentType: "application/json",
				data: JSON.stringify(rowData),
				success: function(resp) {
					initTable();
					vShowSuccess($.i18n.prop("winery-save-policy-success"));
				}
			});

			$("#policyDialog").modal("hide");
		});
	}
	
	function initGroups(dataTable, palette) {
		
		var operationGroupsRender = function(obj) {
					
			var operationHtml = '<i class="fa fa-edit icon-del" data-row="'+ obj.iDataRow 
			+ '" id="winery-btn-edit" name_i18n="winery_i18n" style="margin-right:3px;"></i>'
			+ '<i class="fa fa-trash icon-del" id="winery-btn-delete" name_i18n="winery_i18n" '
			+ 'data-id="'+ obj.aData.id +'"></i>';
			return operationHtml;
			
		}

		var groupsInfo = {
			divId : "serverGroupsTable_div",
			id : "serverGroupsTable",
			url : "${serviceTemplateURL}/grouptemplates/",
			addUrl : "${serviceTemplateURL}/grouptemplates/create/",
			columns : [
				{"mData": "name", "name": $.i18n.prop("winery-boundary-groups-dialog-name"), "sWidth":"30%"},
				{"mData": "type", "name": $.i18n.prop("winery-boundary-groups-dialog-type"), "sWidth":"50%"},		
				{"mData": null, "name": $.i18n.prop("winery-table-operation"), "sWidth" : "20%", 
				 	"fnRender" : operationGroupsRender, "bVisible": palette}
			]
		}

		var editGroupsTableData = function(rowId) {
			var rowData = $("#" + groupsInfo.id).dataTable().fnGetData(rowId);
			initGroupsDialog(rowData);
		}

		var initTable = function(rowData) {
			//dataTable.initTable(groupsInfo);

			var addBind = function(){
				//delete button
					$("#" + groupsInfo.id).find("tbody").find(".fa-trash").off("click")
						.on("click", function(){
							var tr = $(this).parent().parent()[0];
							delTableData("serverGroupsTable", tr);
					});
					//edit button
					$("#" + groupsInfo.id).find("tbody").find(".fa-edit").off("click")
						.on("click", function(){
							var rowId = $(this).attr("data-row");
							$("#groups_properties").attr("data-row",rowId);
							editGroupsTableData(rowId);
					});				
			}

			if(rowData != null){			
				dataTable.initTableWithData(groupsInfo, rowData);
				addBind();
				return		
			}
			
			$.ajax({
				type : "GET",
				dataType : "json",
				url : groupsInfo.url,
				success : function(resp) {
					var tableData = resp || [];

					tableData = $.grep(tableData,function(value,i){

						return value.namespace.indexOf("vnffg") < 0;
													
					});
					
					dataTable.initTableWithData(groupsInfo, tableData);	
					addBind();							
				}		
			});			
		}
		initTable();
		var editor;
		var initGroupsDialog = function(rowData) {
			require(["jsoneditor", "winery-util"], function(wsc, wd, util){

			//init gourpsType select
			$.ajax({
				type: "GET",
				url: "/modeldesigner/grouptypes/",
				dataType: "json",
				success: function(resp) {
					$("#groups_type").html("<option/>");
					resp = resp || [];
					resp = $.grep(resp,function(type, index){
						return type.namespace.indexOf("vnffg") < 0;
					});
					$.each(resp, function(index, type){
						var qname = "{" + type.namespace + "}" + type.id;
						var option = $("<option>", {"value": qname}).text(type.id);
						$("#groups_type").append(option);
					});	

					if(rowData) {
						$("#groups_name").val(rowData.name);
						$("#groups_type").val("{"+rowData.namespace+ "}" + rowData.type);
						$("#groups_type").trigger('change');
						for (var key in rowData.properties){
							$("#"+key).val(rowData.properties[key]);
						}
						
					} else {
						$("#groups_name").val("");
					}	
				}
			});

				/********************   
					groups type  #start
				*/ 
				$("#groups_type").unbind("change"); 
				$("#groups_type").on('change', function(){
					var qname = $(this).val();
					if(!qname) return;

					var namespace = qname.substring(1, qname.indexOf("}"));
					var namespace_encode = encodeURIComponent(encodeURIComponent(namespace));
					var id = qname.substring(qname.indexOf("}") + 1);
					var url = "/modeldesigner/grouptypes/" + namespace_encode + "/" + id 
							+ "/propertiesdefinition/complexprop/all";
					var targetTypeUrl = "/modeldesigner/grouptypes/" + namespace_encode + "/" + id + "/targetslist";		

					$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						async: false,
						success: function(resp) {
							resp = resp || [];
							$("#groups_properties").children().remove();

							var props = {};
							var properties = [];
						
							properties = $.grep(resp[0].propertyDefinitionKVList, function(property, index){
								if(property.key != "name") return true;
							});	
					
							complexProperty(resp);
							generateProperties("groups_properties", properties);

							$.ajax({
								type: "GET",
								url: targetTypeUrl,
								dataType: "json",
								async: false,
								success: function(resp){
									initTargets(resp);
								}
							});	
							
						},
						error: function() {
							$("#groups_properties").children().remove();
						}
					});
				});

				//initialize groups target
				$("#groups_target").select2();
				var initTargets = function(targetTypes){
					$("#groups_target").children().remove();
					var nodeTemplates = $(".NodeTemplateShape:not('.hidden')");
					$.each(nodeTemplates, function(index, template){
						var name = $(template).children(".fullName").text();
						$.each(targetTypes, function(index, value){
							if(name.indexOf(value) > -1){
								var option = $("<option>").text(name);
							    $("#groups_target").append(option);
							}
						});
					});
					
					if(rowData && rowData.targets.length > 0) {
						$("#groups_target").val(rowData.targets).trigger('change');
					}
				};

				/********************   
					groups type  #end
				*/ 

				$("#groupsDialog").modal("show");
			});
		}
		/********************   
			groups properties && jsoneditor #start
		*/
		//初始化json editor
		var initEditor = function(editorId, options) {
			$("#" + editorId).children().remove();
			
			var $editor = document.getElementById(editorId);
							
	        JSONEditor.defaults.options.theme = 'bootstrap3';
	        JSONEditor.defaults.options.iconlib = 'bootstrap3';
	        JSONEditor.defaults.options.object_layout = 'normal';
	        JSONEditor.defaults.options.show_errors = 'change';
	        JSONEditor.plugins.selectize.enable = true;
	        var en = JSONEditor.defaults.languages.en;
	        en.error_notempty = $.i18n.prop("winery-property-validate-required");

	        var editor = new JSONEditor($editor, options);
		
	        return editor;
		  
		}
		//复杂属性---
		var generateSelectHtml = function(options, name, value, disabledAttr) {
			var select = $('<select>', { 
					id: name, 
					name: name,  
					class: "form-control control-edit",
					style: "width: 87%"
				});
			if(disabledAttr) {
				select.attr("disabled", disabledAttr);
			}
			$.each(options, function(index, optionValue){
				var option = $('<option>').text(optionValue);
				if(value == optionValue) {
					option.attr("selected", "selected");
				}
				select.append(option);
			});

			return select;
		}

		var generateProperties = function(id, properties) {
			var propertyInfo = $("#" + id);
			propertyInfo.children().remove();
		
			var inputDiv = $('<div>'); //属性来源于输入参数
			var metaDataDiv = $('<div>'); //属性来源于元数据
			var needValidateProperties = []; //需要校验的数据

			for(var i=0; i<properties.length; i++) {
				var property = properties[i]; 				
				var name = property.key;
				var value = property.value
				if(value == "Empty") {
					value = "";
				}
				//value = value.replace(/\"/g, '&#34;'); //对于复杂数据，要把引号替换为转译字符

				var type = property.type;
				var tag = property.tag;
				var required = property.required; //是否必填
				var validValue;
				if(property.constraint != null){
					validValue = property.constraint.validValue;
				}
				var validator = { name: name, rules: {}}; //属性校验规则				

				var groupDiv = $('<div>', {class: "form-group"});
				var label = $('<label>', {class: "col-sm-4 control-label"}).text(name);
				if(required == "true") {
					var requiredSpan = $('<span>', {class: "required", "aria-required": "true"}).text("*");
					label.append(requiredSpan);
				}
				
				var colDiv = $('<div>', {class: "col-sm-8"});
				var input = $('<input>', {
						class: "form-control control-edit",
						id: name,
						name: name,
						value: value,
						style: "width:87%; float:left;"
					});
				var disabledAttr = "";
				groupDiv.append(label).append(colDiv);

				if(validValue) { //枚举类型
					var options = validValue.split(",");
					var select = generateSelectHtml(options, name, value, disabledAttr);
					colDiv.append(select);
				} else {
					switch(type) {  //type不同，输入方式不同
						case "xsd:boolean" :
							var options = ["", "true", "false"];
							var select = generateSelectHtml( options, name, value, disabledAttr);
							colDiv.append(select);
							break;
						case "xsd:integer" :
							input.attr("placeholder", $.i18n.prop("winery-property-validate-digits"));
							colDiv.append(input);
							validator.rules.digits = true;
							break;
						case "string":
						case "xsd:string":
							var suggestDiv = $('<div>', {class: "search_suggest"});
							colDiv.append(input);
							colDiv.append(suggestDiv);
							break;
						default :
							var editSpan = $('<span>', {"class": "editIcon", "data-type": type});
							var editIcon = $('<i>', {class: "fa fa-edit icon-edit", "editvalue": value});
							input.attr("disabled", true);
							input.css("background-color", "white");
							editSpan.append(editIcon);
							colDiv.append(input);
							colDiv.append(editSpan);
					}
				}
			
				inputDiv.append(groupDiv);
				
				if(property.required) {
					needValidateProperties.push(property.key);
				}
			}
			
			propertyInfo.append(metaDataDiv).append(inputDiv);

			//添加校验
			//initialize validator rules
			var rules = {
				groups_name : {required: true},
				groups_target : {required: true},
				groups_type : {required: true}
			}
			
			$.each(needValidateProperties, function(index, key){
				//$("#" + validator.name).on("keyup", function(){
				//});
				rules[key] = {required: true}

			});
			initValidate("groupsForm", rules);

			$("#groups_properties .editIcon").click(function(e) {
				var $input = $(this).prev();
				var json = $input.val();
				var attrName = $input.attr("name");
				$("#groupsInputDiag").attr("name", attrName);
				var title;
				if(palette) {
					title = $.i18n.prop("winery-property-dialog-title-edit") + " " + attrName;
				} else {
					title = $.i18n.prop("winery-property-dialog-title-view") + " " + attrName;
				}
				$("#groupsInputDiag .modal-title").text(title);

				var schema = { 
					title: attrName
				}
				var options = {
					schema: schema,
					disable_properties: true,
					disable_collapse: true,							
					disable_edit_json: true
				}
				if(json) { //赋值
					options.startval = JSON.parse(json);			
				}
				

				var type = $(this).attr("data-type");	

				extractProperties(type, schema, options);		
						
				editor = initEditor("groupsPropertyEditor", options);

				$("#groupsInputDiag").modal("show");
			});

			$("#groupsInputConfirm").off('click').on('click', function(){
				var errors = editor.validate();
				if(errors.length) return;

				var json = JSON.stringify(editor.getValue());
				var name = $("#groupsInputDiag").attr("name");
				$("#groups_properties input[name='"+name+"']").val(json);
				updatePropertyDataToContainer(name, json);
				//editor.destroy();
				$("#groupsInputDiag").modal("hide");
			});
		}
		//添加关联的复杂属性
		var complexProperty = function(data){		
			for(var i=1, len=data.length; i<len; i++){
				var wineryPropertyDefinitions = data[i];
				if(wineryPropertyDefinitions && wineryPropertyDefinitions.propertyDefinitionKVList) {
					//group的复杂属性在界面只生成一次,作为类型模版
					var propertiesDiv = $("#groupsPropertieContainer")
						.children("[name='" + wineryPropertyDefinitions.elementName + "']");
					if(propertiesDiv.html() != null) return ;

					var propertiesContainer = $("#groupsPropertieContainer");
					var propertyDefinitionKVList = wineryPropertyDefinitions.propertyDefinitionKVList;
					var wineryPropertiesHtml = "";
					var str = '<div class="content" name="' + wineryPropertyDefinitions.elementName + '">'
						+ '<span class="elementName">' + wineryPropertyDefinitions.elementName + '</span>'
						+ '<span class="namespace">' + wineryPropertyDefinitions.namespace + '</span>'
						+ '<table><tbody></tbody></table></div>'
					propertiesContainer.append(str);
					$.each(propertyDefinitionKVList, function(index, property){
						//属性值或者属性定义的默认值
						var value =  property.value;
						if(!value) {
							value = "";
						}
						var validValue = "";
						if(property.constraint) {
							validValue = property.constraint.validValue || "";
						}

						wineryPropertiesHtml += '<tr class="KVProperty">'
							+ '<td><span class="' + property.key + ' KVPropertyKey">' + property.key 
							+ '</span></td><td><a class="KVPropertyValue" href="#" data-type="text"' 
							+ 'data-title="Enter ' + property.key + '">' + value
							+ '</a></td><td><span class="KVPropertyType">' + property.type + '</span></td>'
							+ '<td><span class="KVPropertyTag">' + property.tag + '</span></td>'
							+ '<td><span class="KVPropertyRequired">' + property.required + '</span></td>'
							+ '<td><span class="KVPropertyValidValue">' + validValue + '</span></td>'
							+ '</tr>';
					});
					var propertiesTable = $("#groupsPropertieContainer")
							.children("[name='" + wineryPropertyDefinitions.elementName + "']");
					propertiesTable.find("tbody").html(wineryPropertiesHtml);

				}
			}
		}
		var extractProperties = function(defineType, schema, options) {
			defineType = defineType.split("_");
			var propertyType = defineType[0];
			var propertyName = defineType[1];
			var props = {};

			switch(propertyType) {
				case "list":
					schema.type = "array";
					schema.format = "table";
					schema.items = {
						type: "string"
					}
					break;
				case "objlist":
					schema.type = "array";
					schema.format = "table";
					schema.items = {
						type: "object",
						properties: props
					}
					break;
				case "map":
					schema.type = "object";
					//schema.properties = props;
					schema.additionalProperties = {
	                    type: "string"
	                };
					options.disable_properties = false;
					break;
			
				default: 
					schema.type = "object";
					schema.properties = props;
			}

			var propertiesTrs = $("#groupsPropertieContainer")
				.children("[name='" + propertyName + "']").find("tr");
			for(var i=0; i<propertiesTrs.length; i++) {
				var property = propertiesTrs.eq(i).children();
				var name = property.children(".KVPropertyKey").text();
				var value = property.children(".KVPropertyValue").text();
				var type = property.children(".KVPropertyType").text();
				var tag = property.children(".KVPropertyTag").text(); 
				var required = property.children(".KVPropertyRequired").text(); //是否必填
				var validValue = property.children(".KVPropertyValidValue").text(); //枚举值

				if(value == "Empty") {
					value = "";
				}

				if(type.indexOf(":") > -1) {
					type = type.substring(type.indexOf(":") + 1);
					if(name == "size") type = "string";
					props[name] = {
						type: type,
						default: value
					};

					if(type == "boolean") {
						props[name].type = "string";
						props[name].enum = ["true", "false"];
					}
					if(validValue) {
						props[name].enum = validValue.split(",");
					}
					if(required == "true") {
						props[name].minLength = 1;
					}
				} else {
					props[name] = {};
					extractProperties(type, props[name], options);
				}
			}
		}			
		/******************************** 
			groups properties && jsoneditor #end
		*/

		$("#groupsConfirm").click(function(event){
			event.stopPropagation(); 
			var form = $("#groupsForm");
			if($("#groups_type").val() == "") return;
			if(!form.valid()) {
				return;
			}		
			var target = $("#groups_target").val() || [];
			var qname = $("#groups_type").val();
			var namespace = qname.substring(1, qname.indexOf("}"));
			var properties = {"name": $("#groups_name").val()};
			var inputs = $("#groups_properties").find("input");
			var selects = $("#groups_properties").find("select");

			$.each(inputs, function(index, input){
				var name = $(input).attr("name");
				properties[name] = $(input).val();
			});
			$.each(selects, function(index, select){
				var name = $(select).attr("name");
				properties[name] = $(select).val();
			});
			var rowData = {
				name: $("#groups_name").val(),
				type: $("#groups_type").val().split("}")[1],
				namespace: namespace,
				targets: target,					
				properties: properties																				
			}
			
			
			var url = $("#groupsConfirm").attr("data-url");
			var dataTable = $("#serverGroupsTable").dataTable();
			var rowId = parseInt($("#groups_properties").attr("data-row"));
			if(isNaN(rowId) ){
				dataTable.fnAddData(rowData);			
			}else{
				dataTable.fnUpdate(rowData, rowId);
			}

			initTable(dataTable.fnGetData());
			
			$("#groupsDialog").modal("hide");
		});

		$("#addGroupsBtn").click(function(){
			var rowId = $("#groups_properties").attr("data-row","");
			initGroupsDialog();
			$("#groups_properties").html("");
			$("#groups_name").val("");
			$("#groups_type").val("");
			$("#groups_target").children().remove();
			//$("#groupsConfirm").attr("data-url", groupsInfo.addUrl);
		});



	}
	
	function initVnffg(dataTable, palette) {
		var operationVnffgRender = function(obj) {
			var operationHtml = '<i class="fa fa-edit icon-del" data-id="'+ obj.aData.id +'"'
				+ ' id="winery-btn-edit" name_i18n="winery_i18n" style="margin-right:3px;"></i>'
				+ '<i class="fa fa-trash icon-del" id="winery-btn-delete" name_i18n="winery_i18n" data-id="'+ obj.aData.id +'"></i>';
			return operationHtml;
		}

		var versionRender = function(obj) {
			return obj.aData.properties.version || "";
		}

		var vendorRender = function(obj) {
			return obj.aData.properties.vendor || "";
		}

		var vnffgInfo = {
			divId : "boundaryVnffgTable_div",
			id : "boundaryVnffgTable",
			url : "${serviceTemplateURL}/grouptemplates/",
			addUrl : "${serviceTemplateURL}/grouptemplates/create/",
			columns : [
				{"mData": "id", "name": "ID", "bVisible": false},
				{"mData": "name", "name": $.i18n.prop("winery-boundary-vnffg-dialog-name"), "sWidth" : "65%"},
				/*{"mData": null, "name": $.i18n.prop("winery-boundary-vnffg-dialog-name"), "sWidth" : "25%",
					"fnRender" : versionRender},
				{"mData": null, "name": $.i18n.prop("winery-boundary-vnffg-dialog-name"), "sWidth" : "25%",
					"fnRender" : vendorRender},*/
				{"mData": null, "name": $.i18n.prop("winery-table-operation"), "sWidth" : "25%", 
				 	"fnRender" : operationVnffgRender, "bVisible": palette}
			]
		}

		var delVnffgTableData = function(id) {
			$.ajax({
				type: "DELETE",
				url: vnffgInfo.url + id + "/groupandtargets/",
				success: function(resp) {
					initTable();
				}
			});
		}

		var editVnffg = function(id) {
			//save service template
			require(["winery-topologymodeler-AMD"], function(wt) {
				wt.saveServiceTemplate(function(){
					var path = window.location.pathname + "vnffg.jsp";
					var params = window.location.search + "&groupId=" + id;
					window.location = path + params;
				});
			});
		}

		var initTable = function() {
			$.ajax({
				type : "GET",
				dataType : "json",
				url : vnffgInfo.url,
				success : function(resp) {
					var tableData = resp || [];
					tableData = $.grep(tableData,function(value,i){

						return value.namespace.indexOf("vnffg") > 0;
													
					});
						
					dataTable.initTableWithData(vnffgInfo, tableData);
				
					//delete button
					$("#"+vnffgInfo.id).find("tbody").find(".fa-edit").off("click")
						.on("click", function(){
							var id = $(this).attr("data-id");
							editVnffg(id);
					});
					//edit button
					$("#"+vnffgInfo.id).find("tbody").find(".fa-trash").off("click")
						.on("click", function(){
							var id = $(this).attr("data-id");
							delVnffgTableData(id);
					});											
				}		
			});				

	
		}
		initTable();

		//add vnffg button
		$("#addVnffgBtn").click(function(){
			//initialize type
			$("#vnffg_type").children().remove();
			$.ajax({
				type: "GET",
				url: "/modeldesigner/grouptypes/",
				dataType: "json",
				success: function(resp) {
					resp = resp || [];
					resp = $.grep(resp,function(type, index){
						return type.namespace.indexOf("vnffg") > 0;
					});
					$.each(resp, function(index, type){
						var option = $("<option>", {value: type.namespace}).text(type.id);
						$("#vnffg_type").append(option);
					});
				}
			});

			//initialize validator rules
			var rules = {
				vnffg_type: {required: true},
				vnffg_name: {required: true}
			}
			initValidate("vnffgForm", rules);

			$("#vnffgDialog").modal("show");
		});

		//add vnffg confirm button in dialog
		$("#vnffgConfirm").click(function(){
			var form = $("#vnffgForm");
			if(!form.valid()) {
				return;
			}
			
			var rowData = {
				type: $("#vnffg_type option:selected").text(),
				namespace: $("#vnffg_type option:selected").val(),
				name: $("#vnffg_name").val(),
				properties: {
					version: $("#vnffg_prop_version").val(),
					vendor: $("#vnffg_prop_vendor").val()
				}
			}

			$.ajax({
				type: "POST",
				url: vnffgInfo.addUrl,
				contentType: "application/json",
				data: JSON.stringify(rowData),
				success: function(resp) {
					initTable();
				}
			});

			$("#vnffgDialog").modal("hide");
		});
	}

	require(["winery-datatable", "winery-util", "jquery.fileupload", "select2"], 
		function(wd, util) {

		//true is edit mode, false is view mode
		var palette = ${palette};
		if(!palette) {
			$("#boundaryDefinition input").attr("disabled", "disabled");
		}

		var initTable = function(info, callback) {
			wd.initTable(info, function(){
				if(callback) {
					callback();
				}
			});

			$("#" + info.id).on('draw', function(){
				//edit button
				$("#" + info.id + " tbody").find(".fa-edit").off("click")
					.on("click", function(){
						editTableData(this, info.id);
				});

				//delete button
				$("#" + info.id + " tbody").find(".fa-trash").off("click")
					.on("click", function(){
						var tr = $(this).parent().parent()[0];
						delTableData(info.id, tr);
				});
			});
		}

		var editTableData = function(selector, tableId) {
			//submit editing row data
			$("#" + tableId).find(".fa-check:visible").click();

			//hide edit button, show check button
			$(selector).hide();
			$(selector).next(".fa-check").show().click(function(){
				$(this).hide();
				$(selector).show();
				//$(this).prev().show();

				var data = {};
				//get row data from current table
				var row = parseInt($(this).prev(".fa-edit").attr("data-row"));
				var dataTable = $("#" + tableId).dataTable();
				var rowData = dataTable.fnGetData(row);
				//get editable columns
				var oTds = $(this).parent().siblings(".edit");
				var activeTab = $("#boundaryDefinition li.active a").attr("href");
				switch(activeTab) {
					case "#boundaryInputs" :
						data = {
							name : rowData.name,
							desc : oTds.eq(0).children().val(),
							value : oTds.eq(1).children().val(),
							type : rowData.type,
							tag : rowData.tag
						}
						break;
					case "#boundaryMetaData" :
						data = {
							key : rowData.key,
							value : oTds.eq(0).children().val(),
							tag : rowData.tag,
							required : rowData.required
						}
						break;
				}

				dataTable.fnUpdate(data, row);
			});

			//get editable columns
			var oTds = $(selector).parent().siblings(".edit");
			//add edit box
			$.each(oTds, function(index, element){
				var value = $(element).text();
				var inputHtml = '<input class="form-control" type="text" value="'+value+'">';
				$(element).html(inputHtml);
			});
			//The first input box gains focus
			oTds.eq(0).children().focus();
		}

		var delTableData = function(tableId, tr) {
			$("#" + tableId).dataTable().fnDeleteRow(tr);
		}

		var addTableData = function(tableId, rowData) {
			var dataTable = $("#" + tableId).dataTable();
			dataTable.fnAddData(rowData);
		}

		//inputs table
		var operationInputRender = function(obj) {
			var operationHtml = '<i class="fa fa-edit icon-del" data-row="'
				+ obj.iDataRow + '" id="winery-btn-edit" name_i18n="winery_i18n" style="margin-right:3px;"></i>'
				+ '<i class="fa fa-check icon-del" id="winery-btn-confirm" name_i18n="winery_i18n" style="margin-right:3px;display:none;"></i>'
			  	+ '<i class="fa fa-trash icon-del" id="winery-btn-delete" name_i18n="winery_i18n"></i>';
			return operationHtml;
		}
		var inputInfo = {
			divId : "boundaryInputsTable_div",
			id : "boundaryInputsTable",
			url : "${serviceTemplateURL}/boundarydefinitions/properties/inputs",
			columns : [
				{"mData": "name", "name": $.i18n.prop("winery-boundary-input-table-name"), "sWidth" : "30%"}, 
				{"mData": "desc", "name": $.i18n.prop("winery-boundary-input-table-desc"), "sWidth" : "25%", "sClass": "edit"},
				{"mData": "value", "name": $.i18n.prop("winery-boundary-input-table-value"), "sWidth" : "25%", "sClass": "edit"},
				{"mData": "type", "name": $.i18n.prop("winery-boundary-input-table-type"), "bVisible": false, "sDefaultContent" : "string"},
				{"mData": null, "name": $.i18n.prop("winery-table-operation"), "sWidth" : "20%",
				 "fnRender" : operationInputRender, "bVisible": palette}
			]
		}
		initTable(inputInfo);

		//add input param
		$("#addInputBtn").click(function(e) {
			e.preventDefault();

			var inputs = $("#boundaryInputs input");
			var name = inputs.eq(0).val();
			if(!name) { //name not empty
				return;
			}
			//var type = $("#boundaryInputs option:selected");
			var inputObj = {
				name : name,
				type : "string",
				desc : inputs.eq(1).val(),
				value : inputs.eq(2).val(),
				tag : util.addTag("", util.PROPERTY)
			}
			addTableData(inputInfo.id, inputObj);

			inputs.val("");
		});

		//metaData table
		var operationMetaRender = function(obj) {
			var operationHtml = '<i class="fa fa-edit icon-del" data-row="'
				+ obj.iDataRow + '" id="winery-btn-edit" name_i18n="winery_i18n" style="margin-right:3px;"></i>'
				+ '<i class="fa fa-check icon-del" id="winery-btn-confirm" name_i18n="winery_i18n" style="margin-right:3px;display:none;"></i>';
			//继承而来的属性不能删除
			if(!util.hasTag(obj.aData.tag, util.ATTRIBUTE)) {
			  	operationHtml += '<i class="fa fa-trash icon-del" data-row="'+obj.iDataRow+'" id="winery-btn-delete" name_i18n="winery_i18n"></i>';
			}
			return operationHtml;
		}
		var requiredRender = function(obj) {
			return obj.aData.required || "false";
		}
		var metaInfo = {
			divId : "boundaryMetaDataTable_div",
			id : "boundaryMetaDataTable",
			url : "${serviceTemplateURL}/boundarydefinitions/properties/metadata",
			columns : [
				{"mData": "key", "name": $.i18n.prop("winery-boundary-metadata-table-name"), "sWidth" : "35%"},
				{"mData": "required", "name": $.i18n.prop("winery-boundary-metadata-table-required"), "sWidth" : "21%", "fnRender" : requiredRender},
				{"mData": "value", "name": $.i18n.prop("winery-boundary-metadata-table-value"), "sWidth" : "24%", "sClass": "edit"},
				{"mData": "tag", "name": "tag", "bVisible" : false},
				{"mData": null, "name": $.i18n.prop("winery-table-operation"), "sWidth" : "20%", 
				 	"fnRender" : operationMetaRender, "bVisible": palette}
			]
		}

		//not display csarType
		var afterInit = function() {
			var table = $("#" + metaInfo.id).dataTable();
			var data = table.fnGetNodes();
			var isExistCsarType = false;
			$.each(data, function(index, trElem){
				var key = $(trElem.cells[0]).text();
				var value = $(trElem.cells[2]).text() || "";
				switch(key) {
					case "csarType":
						isExistCsarType = true;
						$("#boundaryDefinition").find("." + key).val(value);
						table.fnDeleteRow(trElem);
						break;
					case "csarVersion":
					case "csarProvider":
						$("#boundaryDefinition").find("." + key).val(value);
						table.fnDeleteRow(trElem);
						break;
				}
			});
			if(!isExistCsarType) {
				var namespace = "${ns}";
				var csarType = "";
				switch(namespace) {
					case "http://www.zte.com.cn/tosca/nfv/ns":
					case "http://www.open-o.org/tosca/nfv/ns":
						csarType = "NSAR";
						break;
					case "http://www.open-o.org/tosca/sdn/ns":
						csarType = "SSAR";
						break;
					case "http://www.open-o.org/tosca/gso":
						csarType = "GSAR";
						break;
					default:
						csarType = "NFAR";
				}
				$("#boundaryDefinition").find(".csarType").val(csarType);
			}
		}

		initTable(metaInfo, afterInit);
		//add metadata param
		$("#addMetaDataBtn").click(function(e) {
			e.preventDefault();

			var metaInputs = $("#boundaryMetaData input");
			var key = metaInputs.eq(0).val();
			if(!key) {
				reuturn;
			}
			var metaObj = {
				key : key,
				value : metaInputs.eq(1).val(),
				tag : "",
				required : "false"
			}
			addTableData(metaInfo.id, metaObj);

			metaInputs.val("");
		});

		//req or cap table
		initReqAndCapTab(wd);

		//script table
		initScriptTab(wd, palette);

		//policy table
		initPolicy(wd, palette);
		
		//groups table
		initGroups(wd, palette);

		//vnffg table
	    initVnffg(wd, palette);

	    //initialize description
	    initDocumentation();

	}); // end require

	$("#closeBoundaryBtn").click(function(){
		$("#boundaryDefinition").hide();
	});

	var namespace = "${ns}";
	if(namespace == "http://www.zte.com.cn/tosca/nfv/ns" || namespace == "http://www.open-o.org/tosca/nfv/ns") {		
		$("#boundaryDefinition").find('a[href="#boundaryVnffg"]').parent().show();
	} else if(namespace == "http://www.zte.com.cn/tosca/nfv/vnf" || namespace == "http://www.open-o.org/tosca/nfv/vnf") {
		$("#boundaryDefinition").find('a[href="#boundaryScript"]').parent().show();
	}

	function initValidate(formId, rules) {
		require(["jquery.validate"], function(){
			var form = $("#" + formId);
			var error = $('.alert-danger', form);
			var success = $('.alert-success', form);

			$.extend($.validator.messages, {
				required: $.i18n.prop("winery-boundary-validate-required"),
				digits: $.i18n.prop("winery-boundary-validate-digits")
			});

			form.validate({
	            doNotHideMessage : true,
	            errorElement : 'span',
	            errorClass : 'help-block',
	            focusInvalid : false,
	            ignore : "",
	            rules: rules || {},
	            errorPlacement : function(error, element) {
	                error.insertAfter(element);
	            },
	            invalidHandler : function(event, validator) {
	                success.hide();
	                error.show();
	            },
	            highlight : function(element) {
	                $(element).closest("div").removeClass("has-success").addClass("has-error");
	            },
	            unhighlight: function (element) {
	                $(element).closest("div").removeClass("has-error");
	            },
	            success : function(label) {
	                label.addClass("valid").closest(".form-group").removeClass("has-error");
	            },
	            submitHandler: function (form) {
	                success.show();
	                error.hide();
	            }
	        });
		});
	}

	function addValidateRule(id, rules) {
		require(["jquery.validate"], function(){
			$("#" + id).rules("add", rules);
		});
	}
});
</script>
