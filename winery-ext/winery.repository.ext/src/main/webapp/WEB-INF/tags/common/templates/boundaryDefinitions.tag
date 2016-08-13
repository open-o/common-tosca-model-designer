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

<%@tag language="java" pageEncoding="UTF-8" description="Renders the boundary definitions of the service template"%>

<%@attribute name="serviceTemplateURL" required="true" type="java.lang.String" description="The topologyTemplate URL"%>
<%@attribute name="serviceTemplateName" required="true" type="java.lang.String" description="The serviceTemplate Name"%>
<%@attribute name="ns" description="service template namespace" required="true" type="java.lang.String"%>
<%@attribute name="palette" description="palette or view mode" required="false" type="java.lang.Boolean" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link rel="stylesheet" href="css/boundarydefinition.css" />
<div id="boundaryDefinition" class="right-menu" style="display: none;">
	<div class="title">
		<h4 style="display:inline"><%=serviceTemplateName%></h4>
		<button id="closeBoundaryBtn" type="button" class="close">&times;</button>
		<div class="menuContainerHead">
			<label>描述:</label>
			<input class="form-control desc-input" type="text" name="description" 
				<c:if test="${!palette}">disabled</c:if>/>
		</div>
	</div>
	<ul class="nav title-list">
		<li class="active">
	      	<a href="#boundaryInputs" data-toggle="tab">输入参数</a>
	   	</li>	
		<li>
	      	<a href="#boundaryMetaData" data-toggle="tab">元数据</a>
	   	</li>
	   	<li style="display:none;">
	      	<a href="#boundaryReqAndCap" data-toggle="tab">需求能力</a>
	   	</li>
	   	<li>
	      	<a href="#boundaryScript" data-toggle="tab">脚本</a>
	   	</li>
	</ul>		
	<div class="tab-content">
		<div class="tab-pane fade in active" id="boundaryInputs">
			<form role="form" class="form-horizontal">
				<c:if test="${palette}">
				<div class="form-group" style="margin-left:0">
					<div class="col-sm-4" style="padding-left:0"><input class="form-control" type="text" placeholder="名称"></div>
					<div class="col-sm-4" style="padding-left:0;display:none">
						<select class="form-control" style="height:30px;padding-top:3px;">
							<option value="string">string</option>
						</select>
					</div>
					<div class="col-sm-4" style="padding-left:0"><input class="form-control" type="text" placeholder="描述"></div>
					<div class="col-sm-3" style="padding-left:0"><input class="form-control" type="text" placeholder="默认值"></div>
					<button id="addInputBtn" class="add">
						<i class="fa fa-plus"></i>
					</button>
				</div>
				</c:if>
				<div id="boundaryInputsTable_wrapper"></div>
			</form>
		</div>
		<div class="tab-pane fade" id="boundaryMetaData">
			<form role="form" class="form-horizontal">
				<c:if test="${palette}">
				<div class="form-group" style="margin-left:0">
					<div class="col-sm-4" style="padding-left:0"><input class="form-control" type="text" placeholder="名称"></div>
					<div class="col-sm-4" style="padding-left:0"><input class="form-control" type="text" placeholder="值"></div>
					<button id="addMetaDataBtn" class="add">
						<i class="fa fa-plus"></i>
					</button>
				</div>
				</c:if>
				<div id="boundaryMetaDataTable_wrapper"></div>
			</form>
		</div>
		<div class="tab-pane fade" id="boundaryReqAndCap">
			<div>需求</div>
			<div id="boundaryRequirementsTable_wrapper"></div>
			<div>能力</div>
			<div id="boundaryCapabilitiesTable_wrapper"></div>
		</div>
		<div class="tab-pane fade" id="boundaryScript">
			<c:if test="${palette}">
			<form id="scriptFileupload" role="form" enctype="multipart/form-data">
		        <div class="input-group fileupload-btn">
		           <div id="scriptFileName" class="form-control file-input">
		           </div>
		           <span class="input-group-btn">
		           		<span class="btn btn-primary fileinput-button">
		                	<span>选择</span>
		                	<input type="file" name="file" multiple>
		            	</span>
		              	<button id="scriptFilesubmit" class="btn btn-default" type="submit" disabled>
		                	<span>上传</span>
		              	</button>
		           </span>
		        </div>
			</form>
			</c:if>
			<div id="boundaryScriptTable_wrapper"></div>
		</div>
	</div>
</div>

<script>
$(function() {
	require(["winery-datatable", "jquery.fileupload"], function(wd) {
		//true是编辑模式, false是查看模式
		var palette = ${palette};
		if(!palette) {
			$("#boundaryDefinition input").attr("disabled", "disabled");
		}

		//初始化表格
		var initTable = function(info, callback) {
			wd.initTable(info, function(){
				//编辑按钮
				$("#" + info.id + " tbody").find(".fa-edit").click(function(){
					var row = $(this).attr("data-row");
					//隐藏按钮edit，显示check确定按钮
					$(this).hide();
					$(this).next().show().click(function(){
						$(this).hide();
						$(this).prev().show();

						var data = {};
						//获取当前表格修改行的数据
						var rowData = $("#" + info.id).dataTable().fnGetData(row);
						//第一列为名称，不能修改
						var oTds = $(this).parent().siblings(":not(:first)");
						var activeTab = $("#boundaryDefinition li.active a").attr("href");
						switch(activeTab) {
							case "#boundaryInputs" :
								data = {
									name : rowData.name,
									desc : oTds.eq(0).children().val(),
									value : oTds.eq(1).children().val(),
									type : rowData.type
								}
								break;
							case "#boundaryMetaData" :
								data = {
									key : rowData.key,
									value : oTds.eq(0).children().val(),
									tag : rowData.tag
								}
								break;
						}

						//提交修改的值
						$.ajax({
							url : info.url,
							type : "POST",
							data : JSON.stringify(data),
				        	contentType : "application/json",
							success : function(resp) {
								initTable(info, callback);
							}
						});
					});

					//第一列为名称，不能修改
					var oTds = $(this).parent().siblings(":not(:first)");
					//给td添加编辑框
					$.each(oTds, function(index, element){
						var value = $(element).text();
						var inputHtml = '<input class="form-control" type="text" value="'+value+'">';
						$(element).html(inputHtml);
					});
					//第一个输入框获得焦点
					oTds.eq(0).children().focus();
				});

				//删除按钮
				$("#" + info.id + " tbody").find(".fa-trash").click(function(){
					var key = $(this).attr("data-key");
					var delUrl = info.url + info.delParam + key;
					$.ajax({
						url : delUrl,
						type : "DELETE",
						success : function() {
							initTable(info, callback);
						}
					});
				});
				if(callback) {
					callback();
				}
			});
		}

		//inputs table
		var operationInputRender = function(obj) {
			var operationHtml = '<i class="fa fa-edit icon-del" data-row="'
				+ obj.iDataRow + '" title="编辑" style="margin-right:3px;"></i>'
				+ '<i class="fa fa-check icon-del" title="确定" style="margin-right:3px;display:none;"></i>'
			  	+ '<i class="fa fa-trash icon-del" data-key="'+obj.aData.name+'" title="删除"></i>';
			return operationHtml;
		}
		var inputInfo = {
			divId : "boundaryInputsTable_wrapper",
			id : "boundaryInputsTable",
			url : "${serviceTemplateURL}/boundarydefinitions/properties/inputs",
			delParam : "?name=",
			columns : [
				{"mData": "name", "name": "名称", "sWidth" : "30%"}, 
				{"mData": "desc", "name": "描述", "sWidth" : "25%"},
				{"mData": "value", "name": "默认值", "sWidth" : "25%"},
				{"mData": "type", "name": "类型", "bVisible": false, "sDefaultContent" : "string"},
				{"mData": null, "name": "操作", "sWidth" : "20%",
				 "fnRender" : operationInputRender, "bVisible": palette}
			]
		}
		initTable(inputInfo);

		//增加input参数
		$("#addInputBtn").click(function(e) {
			//阻止默认提交表单
			e.preventDefault();

			var inputUrl = "${serviceTemplateURL}/boundarydefinitions/properties/inputs";
			var inputs = $("#boundaryInputs input");
			var name = inputs.eq(0).val();
			if(!name) { //名字不能为空
				return;
			}
			var type = $("#boundaryInputs option:selected");
			var inputObj = {
				name : name,
				type : "string",//type.val(),
				desc : inputs.eq(1).val(),
				value : inputs.eq(2).val()
			}

			$.ajax({
				url : inputUrl,
				type : "POST",
				data : JSON.stringify(inputObj),
	        	contentType : "application/json",
				success : function(resp) {
					initTable(inputInfo);
				}
			});

			inputs.val("");
		});

		//metaData table
		var operationMetaRender = function(obj) {
			var operationHtml = '<i class="fa fa-edit icon-del" data-row="'
				+ obj.iDataRow + '" title="编辑" style="margin-right:3px;"></i>'
				+ '<i class="fa fa-check icon-del" title="确定" style="margin-right:3px;display:none;"></i>';
			//继承而来的属性不能删除
			if(obj.aData.tag != "inherit") {
			  	operationHtml += '<i class="fa fa-trash icon-del" data-key="'+obj.aData.key+'" title="删除"></i>';
			}
			return operationHtml;
		}
		var metaInfo = {
			divId : "boundaryMetaDataTable_wrapper",
			id : "boundaryMetaDataTable",
			url : "${serviceTemplateURL}/boundarydefinitions/properties/metadata",
			delParam : "?key=",
			columns : [
				{"mData": "key", "name": "名称"},
				{"mData": "value", "name": "值"},
				{"mData": "tag", "name": "tag", "bVisible" : false},
				{"mData": null, "name": "操作", "sWidth" : "25%", 
				 "fnRender" : operationMetaRender, "bVisible": palette}
			]
		}

		initTable(metaInfo);
		//增加metadata参数
		$("#addMetaDataBtn").click(function(e) {
			//阻止默认提交表单
			e.preventDefault();

			var metaDataUrl = "${serviceTemplateURL}/boundarydefinitions/properties/metadata";
			var inputs = $("#boundaryMetaData input");
			var key = inputs.eq(0).val();
			if(!key) {
				reuturn;
			}
			var inputObj = {
				key : key,
				value : inputs.eq(1).val()
			}

			$.ajax({
				url : metaDataUrl,
				type : "POST",
				data : JSON.stringify(inputObj),
	        	contentType : "application/json",
				success : function(resp) {
					initTable(metaInfo);
				}
			});
			inputs.val("");
		});

		//requirements table
		var requirementsInfo = {
			divId : "boundaryRequirementsTable_wrapper",
			id : "boundaryRequirementsTable",
			columns : [
				{"mData": "nodeName", "name": "节点", "sWidth":"30%"},
				{"mData": "type", "name": "类型", "sWidth":"30%"},
				{"mData": "sessionType", "name": "值", "sWidth":"40%"},
				{"mData": "name", "name": "名称", "bVisible": false},
				{"mData": "ref", "name": "引用ID", "bVisible": false}
			]
		}
		wd.initTableWithData(requirementsInfo, []);

		//capabilities table
		var capabilitiesInfo = {
			divId : "boundaryCapabilitiesTable_wrapper",
			id : "boundaryCapabilitiesTable",
			columns : [
				{"mData": "nodeName", "name": "节点", "sWidth":"30%"},
				{"mData": "type", "name": "类型", "sWidth":"30%"},
				{"mData": "sessionType", "name": "值", "sWidth":"40%"},
				{"mData": "name", "name": "名称", "bVisible": false},
				{"mData": "ref", "name": "引用ID", "bVisible": false}
			]
		}
		wd.initTableWithData(capabilitiesInfo, []);

		//script table
		var delScriptRender = function(obj) {
			return '<i class="fa fa-trash icon-del" data-key="'+obj.aData.name+'" title="删除"></i>';
		}
		var scriptInfo = {
			divId : "boundaryScriptTable_wrapper",
			id : "boundaryScriptTable",
			url : "${serviceTemplateURL}/plans",
			columns : [
				{"mData": "name", "name": "名称"}, 
				{"mData": null, "name": "操作", "fnRender" : delScriptRender, "bVisible": palette}
			]
		}

		var initScriptTable = function(scriptInfo) {
			$.ajax({
				type : "get",
				dataType : "json",
				url : scriptInfo.url,
				success : function(resp) {
					resp = resp || [];
					//构造表格数据
					var tableData = [];
					$.each(resp, function(index, element){
						var row = {
							name : element
						}
						tableData.push(row);
					});

					wd.initTableWithData(scriptInfo, tableData, function(){
						$("#boundaryScriptTable tbody").find("i").click(function(){
							var key = $(this).attr("data-key");
							var delUrl = scriptInfo.url + "/" + key;
							$.ajax({
								url : delUrl,
								type : "DELETE",
								success : function() {
									initScriptTable(scriptInfo);
								},
								error : function() {
									vShowAJAXError("脚本删除失败");
								}
							});
						});
					});
				},
				error : function() {
					wd.initTableWithData(scriptInfo, []);
				}
			});
		}
		initScriptTable(scriptInfo);

		//初始化上传控件
		$("#scriptFileupload").fileupload({
	        url : "${serviceTemplateURL}/plans",
	        maxNumberOfFiles : 1,
	        autoUpload : false,
	        add : function(e, data) {
	            $("#scriptFileName").text(data.files[0].name);
	            $("#scriptFilesubmit").attr("disabled", false);

	            $("#scriptFilesubmit").remove();
	            $('<button id="scriptFilesubmit" class="btn btn-default" type="button"/>').text("上传")
	                .appendTo($(".input-group-btn")[0])
	                .click(function () {
	                	var extData = {
	                        planName : data.files[0].name,
	                        planType : "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan",
	                        planLanguage : "http://docs.oasis-open.org/wsbpel/2.0/process/executable"
	                    };
	                    $("#scriptFileupload").fileupload({ formData : extData });

	                    data.submit();
	                });
	        },
	        fail : function(e, data) {
	            vShowAJAXError("脚本上传失败");
	        },
	        always : function(e, data) {
	            initScriptTable(scriptInfo);
	        }
	    }); // end fileupload

	    //初始化描述
	    var documentationURL = "${serviceTemplateURL}/extendProperties";
	    $.ajax({
			type : "GET",
			url : documentationURL,
			dataType : "json",
			success : function(resp) {
				if(resp && resp.documentation) {
					$("#boundaryDefinition .menuContainerHead input").val(resp.documentation);
				}
			}
		});

	}); // end require

	$("#closeBoundaryBtn").click(function(){
		$("#boundaryDefinition").hide();
	});
});
</script>
