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


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="o" tagdir="/WEB-INF/tags/common/orioneditor"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions"%>

<link type="text/css" href="${pageContext.request.contextPath}/css/myDefCss.css" rel="stylesheet" />

<%-- Upload functionality inspired by plans.jsp. That code could be generalized somehow in a .tag file --%>

<ul class="nav nav-tabs" id="myTab" style="display: none;">
	<li style="display: none;"><a href="#description">Description</a></li>
	<li style="display: none;"><a href="#images">Images</a></li>
	<li class="active"><a href="#options">Options</a></li>
	<li style="display: none;"><a href="#xml" id="showXMLTab">XML</a></li>
</ul>

<div class="tab-content">

	<div class="tab-pane" id="description">
		<div class="form-group">
			<label class="label-form">Name</label>
			<a href="#" class="form-control" data-send="always" id="displayName" data-url="selfserviceportal/displayname" data-tile="Enter Display Name">${it.application.displayName}</a>
		</div>

		<div class="form-group">
			<label class="label-form">Description</label>
			<div class="form-control" id="applicationDescriptionDiv">${it.application.description}</div>
		</div>
	</div>

	<div class="tab-pane" id="images">

		<t:imageUpload
		label="Icon"
		URL="selfserviceportal/icon.jpg"
		id="upIcon"
		width="16px"
		accept="image/*"/>

		<t:imageUpload
		label="Preview"
		URL="selfserviceportal/image.jpg"
		id="upImage"
		width="100px"
		accept="image/*"/>

	</div>

	<div class="tab-pane active" id="options">
		<div style="margin: 5px;">
		    <button class="rightbutton zte-btn zte-primary" name="remove" onclick="deleteOnServerAndInTable(optionsTableInfo, 'Option', 'selfserviceportal/options/', 0, 1);">
				<span id="winery-btn-delete" name_i18n="winery_i18n"></span>
		    </button>
			<button class="rightbutton zte-btn zte-primary" name="add" onclick="$('#addOptionDiag').modal('show');">
				<span id="winery-btn-add" name_i18n="winery_i18n"></span>
			</button>
			<!--  <button class="rightbutton btn btn-xs btn-default" name="edit" onclick="openOptionEditor();">Edit</button> -->
		</div>

		<table id="optionsTable"  style="margin-top: 5px;">
			<thead>
				<tr>
					<th class="plan_td">
						<span id="winery-plan-option-table-id" name_i18n="winery_i18n"></span>
					</th>
					<th class="plan_td">
						<span id="winery-plan-option-table-name" name_i18n="winery_i18n"></span>
					</th>
					<th class="plan_td">
						<span id="winery-plan-option-table-icon" name_i18n="winery_i18n"></span>
					</th>
					<th class="plan_td">
						<span id="winery-plan-option-table-serviceName" name_i18n="winery_i18n"></span>
					</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${not empty it.application}">
					<c:forEach var="option" items="${it.application.options.option}">
						<tr>
							<td class="plan_td">${option.id}</td>
							<td class="plan_td">${option.name}</td>
							<td class="plan_td"><img src="selfserviceportal/options/${w:URLencode(option.id)}/icon.jpg" style="width:50px;"></td>
							<td class="plan_td">${option.planServiceName}</td>
						</tr>
					</c:forEach>
				</c:if>
			</tbody>
		</table>
	</div>

	<div class="tab-pane" id="xml">
		<o:orioneditorarea areaid="XML" url="selfserviceportal/" reloadAfterSuccess="true">${it.applicationAsXMLStringEncoded}</o:orioneditorarea>
	</div>

</div>

<script>
function letUserChooseAFile() {
	$('#fileInput').trigger('click');
	$('#chooseBtn').focus();
}

$('#showXMLTab').on('shown.bs.tab', function (e) {
	window.winery.orionareas['XML'].fixEditorHeight();
});


function getPlanList(callback)
{
    $.ajax({
		url: "planslist/",
		type: "GET",
		async: false,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not read BPEL plan", jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
		    callback(data);
		}
	});
}

//begin auto produce plan soapenv parameters
$(document).ready(function(){
	getPlanList(function(planList)
	{
	    if(undefined == planList)
	    {
	        return;
	    }
	    var planCount = planList.length;
	    for(var i = 0; i < planCount; i++)
	    {
	        // Current support only bpel and bpmn4tosca
	        if("BPEL 2.0 (executable)" == planList[i][4])
	        {
	            $("<option></option>").val(planList[i][2]).text(planList[i][2] + " (BPEL)").appendTo("#planServiceNameSel");
	        }
	        
	        if("BPMN4TOSCA 2.0" == planList[i][4])
	        {
	            $("<option></option>").val(planList[i][2]).text(planList[i][2] + " (BPMN4TOSCA)").appendTo("#planServiceNameSel");
	        }
	    }
	    
	    $('#planServiceNameSel').val(-1);
	    //$('#planServiceName').val($("#planServiceNameSel").val());
	});
	
	$('#txtPlanServiceName').bind('input oninput', function() {  
	    $('#planServiceName').val($('#txtPlanServiceName').val());
	});
});

var initPara = ['serviceTemplateId','containerapiUrl','resourceUrl','statusUrl','instanceId','roUrl','flavor','flavorParams','vnfmId','iaUrl','callbackId'];
var count = initPara.length;
var planType;
var planNameSpace;
var requestVal;

function isInitPara(name)
{
	for ( var int = 0; int < count; int++)
	{
		if(name == initPara[int])
		{
			return true;
		}
	}
	
	return false;
}

function addParaTab(id, name, value)
{
    var status = isInitPara(name)?'false':'checked';
    var tag = "<div class=\"form-group\" style=\"height: 35px;\">"
		+"<label for=\"\" class=\"col-sm-3 control-label\" style=\"padding-left: 0px; margin-top:6px;\">" + name + "</label>"
		+"<div class=\"col-sm-9\">"
			+"<div name=\"planParaDiv\" class=\"input-group\">"
				+"<input type=\"text\" id=\"" + id + name + "\" class=\"form-control initPa\" data-type=\"text\" data-name=\"" + name + "\" name=\"" + name + "\" value=\"" + value + "\" />"
				+"<div class=\"input-group-btn\">"
				    +"<div class=\"checkbox\" title=\"Display\">"
				        +"<input class=\"checkboxDis\" type=\"checkbox\" id=\"" + name + "Dis\" value=\"0\" name=\"" + name + "\" />"
						+"<label for=\"" + name + "Dis\"></label>"
					+"</div>"
					+"<div class=\"checkbox\" title=\"Required\">"
						+"<input class=\"checkboxReq\" type=\"checkbox\" id=\"" + name + "Req\" value=\"0\" name=\"" + name + "\" />"
						+"<label for=\"" + name + "Req\"></label>"
					+"</div>"
				+"</div>"
				+"<div class=\"input-group-btn\">"
					+"<div class=\"checkboxs\">"
						+"<span style=\"font-size: 8px;\">Display</span>"
					+"</div>"
					+"<div class=\"checkboxs\">"
						+"<span style=\"font-size: 8px;\">Required</span>"
					+"</div>"
				+"</div>"
			+"</div>"
		+"</div>"
	+"</div>";
	
	$('#'+id).append(tag);
}

function initView()
{ 
	$("#planParas input[type='checkbox']").each(function() {
        var type = $(this).attr('name');
        
		if(!isInitPara(type))
		{
		    if("checkboxDis" == $(this).attr('class'))
		    {
		        $(this).prop("checked", "checked"); 
		    }
			
			$(this).click(function(){
				setInputParaToTextarea();
			});
		}
		else
		{
		    $(this).attr("disabled","disabled");
		}
    });
    
    $("#planParas input[type='text']").each(function() {
        var name = $(this).attr('name');
        
		if(isInitPara(name))
		{
			$(this).parent().parent().parent().css("display","none"); 
		}
    });
    
    setInputParaToTextarea();
    
    $('.initPa').bind('input oninput', function() {  
	    setInputParaToTextarea();
	});
}

function setBPELPara(obj)
{
    $.ajax({
		url: "plans/" + obj.value + "/wsdl",
		type: "GET",
		async: false,
		contentType: false, // jQuery automatically sets multipart/form-data; boundary=...
		dataType: 'json',
		processData: false,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not read BPEL plan", jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
		    planNameSpace = data[0].namespace;
		    requestVal =  data[1].request;
		    
		    var para = data[2].paraname;
			for(var key in para){  
			   addParaTab('planParas', key, para[key].value);
			} 
			
			initView();
		}
	});
}

function setBPMN4TOSCAPara(obj)
{
    // read json file
    $.ajax({
		url: "plans/" + obj.value + "/file",
		type: "GET",
		async: false,
		contentType: false, // jQuery automatically sets multipart/form-data; boundary=...
		dataType: 'json',
		processData: false,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not read BPMN4TOSCSA plan", jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
		    var size = data.length;
		    for(var i = 0; i < size; i++)
		    {
		        if ('StartEvent' == data[i].type)
				{
				    var para = data[i].output;
					for(var key in para){  
					   addParaTab('planParas', key, para[key].value);
					} 
				}
		    }
		    
		    initView();
		}
	});
}


function setInputParaToTextarea()
{
    var namespace = "";
    var requestPara = "";
    
    if("BPMN4TOSCA" == planType)
    {
        namespace = "${it.nameSpace}";
        requestPara = "planInput";
    }
    else if("BPEL" == planType)
    {
       namespace = planNameSpace;
       requestPara = requestVal;
    }
    
    var planInputMessageHead = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                               +" xmlns:build=\"" + namespace + "\">\n"
                               +"<soapenv:Header/>\n"
                               +"<soapenv:Body>\n"
                                   +"<build:" + requestPara + ">\n";
                               
    var planInputMessageEnd = "</build:" + requestPara + ">\n"
                          +"</soapenv:Body>\n"
                          +"</soapenv:Envelope>";
    $("#planParas div[name='planParaDiv']").each(function() {
        var input = $(this).find("input[type='text']");
        var checkedDis = $(this).find("input[class='checkboxDis']").is(':checked');
        var checkedReq = $(this).find("input[class='checkboxReq']").is(':checked');
        var strDis = "";
        var strReq = "";
        
        if(checkedDis)
        {
            strDis = " display=\"true\"";
        }
        
        if(checkedReq)
        {
            strReq = " required=\"true\"";
        }
        
        planInputMessageHead += "<build:" + input.attr('name') + strDis + strReq +">" + input.val() + "</build:" + input.attr('name') + ">\n";
        
    });
    
    //console.log(planInputMessageHead+planInputMessageEnd);
    $("#planInputMessage").text(planInputMessageHead+planInputMessageEnd);
}

function fillPlanServiceName(obj)
{ 
    $('#planServiceName').val(obj.value + "Service");
    $('#txtPlanServiceName').val(obj.value + "Service");
    $('#planParas').empty();
    // auto produce plan soapenv parameters
    if(-1 < obj.options[obj.selectedIndex].text.indexOf("BPEL"))
    {
        planType = "BPEL";
        setBPELPara(obj);
    }
    else if(-1 < obj.options[obj.selectedIndex].text.indexOf("BPMN4TOSCA"))
    {
        planType = "BPMN4TOSCA";
        setBPMN4TOSCAPara(obj);
    }
}
//end auto produce plan soapenv parameters
</script>

<div class="modal fade" id="addOptionDiag">
	<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h4 class="modal-title">
				<span id="winery-plan-option-dialog-title" name_i18n="winery_i18n"></span>
			</h4>
		</div>
		<div class="modal-body">
			<form id="addOptionForm" enctype="multipart/form-data" action="selfserviceportal/options/" method="post">
				<div class="form-group">
					<label class="control-label">
						<span id="winery-plan-option-dialog-name" name_i18n="winery_i18n"></span>
					</label>
					<input name="name" type="text" class="form-control" required="required">
				</div>

				<div class="form-group">
					<label class="control-label">
						<span id="winery-plan-option-dialog-description" name_i18n="winery_i18n"></span>
					</label>
					<textarea id="optionDescription" name="description" class="form-control" required="required"></textarea>
				</div>

				<div class="form-group">
					<label class="control-label" for="fileDiv">
						<span id="winery-plan-option-dialog-icon" name_i18n="winery_i18n"></span>
					</label>
					<div style="display: block; width: 100%" id="iconDiv">
						<input id="fileInput" name="file" type="file" style="display:none" accept="image/*">
						<input name="fileText" id="fileText" type="text" class="form-control" style="width:300px; display:inline;" onclick="letUserChooseAFile();" required="required">
						<button type="button" id="chooseBtn" class="btn btn-default btn-xs" onclick="letUserChooseAFile();">
							<span id="winery-plan-option-dialog-choose" name_i18n="winery_i18n"></span>
						</button>
					</div>
				</div>

				<div class="form-group">
					<label class="control-label">
						<span id="winery-plan-option-dialog-serviceName" name_i18n="winery_i18n"></span>
					</label>
					<input name="planServiceName" id="planServiceName" style="display: none;" type="text" class="form-control" required="required">
					
					<div style="position:relative;">
					    <select id="planServiceNameSel" style="width:48%; float:left; margin-right:10px;" class="form-control" onchange="fillPlanServiceName(this)">
	                    </select>
                        <input type="text" class="form-control" id="txtPlanServiceName" value="" style="width:50%; height:34px;" />  
                    </div>
				</div>

				<div class="form-group">
					<label class="control-label">
						<span id="winery-plan-option-dialog-message" name_i18n="winery_i18n"></span>
					</label>
					<div id="planParas" class="form-group" style="height: auto; margin-top: 15px"></div>
					<textarea id="planInputMessage" style="display: none;" name="planInputMessage" class="form-control" required="required" rows="20">&lt;soapenv:Envelope xmlns:soapenv=&quot;http://schemas.xmlsoap.org/soap/envelope/&quot;&gt;
&lt;soapenv:Header/&gt;
&lt;soapenv:Body&gt;
&lt;/soapenv:Body&gt;
&lt;/soapenv:Envelope&gt;</textarea>
                    
				</div>

			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-default" data-dismiss="modal">
				<span id="winery-btn-cancel" name_i18n="winery_i18n"></span>
			</button>
			<button type="button" class="btn btn-primary" data-loading-text="Uploading..." id="addOptionBtn">
				<span id="winery-btn-add" name_i18n="winery_i18n"></span>
			</button>
		</div>
	</div>
	</div>
</div>


<script>
$("#displayName").editable({
	ajaxOptions: {type: "PUT"},
	success: function() {
		vShowSuccess("Successfully updated display name");
	},
	error: function(response) {
		vShowError("Could not update display name: " + response.status + " " + response.responseText);
	}
});

$("#applicationDescriptionDiv").editable({
	type: "wysihtml5",
	send: "always",
	url: "selfserviceportal/description",
	ajaxOptions: {type: "PUT"},
	success: function() {
		vShowSuccess("Successfully updated description");
	},
	error: function(response) {
		vShowError("Could not update description: " + response.status + " " + response.responseText);
	}
});

$("#optionDescription").wysihtml5();

var optionsTableInfo = {
	id: '#optionsTable'
};

$('#myTab a').click(function (e) {
	e.preventDefault();
	$(this).tab('show');
});

$(function() {
	// initialize table and hide first column
	require(["winery-support"], function(ws) {
		ws.initTable(optionsTableInfo, {
			"aoColumnDefs": [
				{ "bSearchable": false, "bVisible": false, "aTargets": [ 0 ] }
			]
		});
	});

	$("#addOptionDiag").on("hidden.bs.modal", function() {
		// we currently do not send data back from the server
		// we emulate the AJAX refresh by a reaload
		doTheTabSelection(function() {
			$('#myTab a[href="#options"]').tab('show');
		});
	});

	loadi18n('winery-topologymodeler-i18n', '/modeldesigner-topologymodeler/i18n/');
});

function createOption(data) {
	if (highlightRequiredFields()) {
		vShowError("Please fill out all required fields");
		return;
	}
	data.submit();
}

requirejs(["jquery.fileupload"], function(){
	$('#addOptionForm').fileupload({
		// dropping should only be available in the addOptionDialog. This, however, does not work correctly
		dropZone: $("#addOptionDiag")
	}).bind("fileuploadadd", function(e, data) {
		$.each(data.files, function (index, file) {
			$("#fileText").val(file.name);
		});
		$("#addOptionBtn").off("click");
		$("#addOptionBtn").on("click", function() {
			createOption(data);
		});
	}).bind("fileuploadstart", function(e) {
		$("#addOptionBtn").button("loading");
	}).bind('fileuploadfail', function(e, data) {
		vShowAJAXError("Could not add option", data.jqXHR, data.errorThrown);
		$("#addOptionBtn").button("reset");
	}).bind('fileuploaddone', function(e, data) {
		vShowSuccess("Option created successfully.");

		// reset the add button
		$("#addOptionBtn").button("reset");
		// do not allow submission of the old files on a click if the dialog is opened another time
		$("#addOptionBtn").off("click");

		// TODO: add data
		//embeddedPlansTableInfo.table.fnAddData(data.result.tableData);
		// current workaround: event on hidden.bs.modal

		$('#addOptionDiag').modal('hide');
	});
});

</script>
