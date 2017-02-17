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
 *    Uwe Breitenbücher - initial API and implementation and/or initial documentation
 *    Oliver Kopp - improvements to fit updated index.jsp
 *    Yves Schubert - switch to bootstrap 3
 *******************************************************************************/
  
 /*******************************************************************************
 * Modifications Copyright 2016-2017 ZTE Corporation.
 *******************************************************************************/
--%>

<%@tag language="java" pageEncoding="UTF-8" description="Renders the properies of one node tempate on the right"%>

<%@attribute name="repositoryURL" required="true" type="java.lang.String" description="The repository URL"%>
<%@attribute name="palette" required="false" type="java.lang.Boolean" description="palette mode or view mode"%>
<%@attribute name="serviceTemplateURL" required="true" type="java.lang.String" description="The topologyTemplate URL"%>
<%@attribute name="allArtifactTypes" required="true" type="java.util.Collection" description="All available artifact types"%>

<%@tag import="javax.xml.namespace.QName"%>
<%@tag import="java.util.List"%>

<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common" %>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>

<div id="NTPropertiesView" class="right-menu" style="display: none;">
	<div class="right-menu-content">
		<div class="title">
			<div class="header"><h4 id="NTPropertiesView_name">NodeTemplate</h4></div>
			<div class="title-group-btn">
				<button id="delNodeTemplate" type="button" class="zte-btn zte-default zte-multi title-delete">
					<i class="fa fa-trash" style="margin-right: 5px;" id="winery-btn-delete" name_i18n="winery_i18n"></i>
				</button>
				<button id="closeNTPropertiesViewBtn" type="button" class="close">&times;</button>
			</div>
		</div>
		<form id="propertiesForm" class="form-horizontal" role="form">
			<ul class="nav title-list">
				<li class="active">
					<a href="#basicInfo" data-toggle="tab" class="tab-multi">
						<span id="winery-property-tab-basic" name_i18n="winery_i18n"></span>
					</a>
				</li>	
				<li>
					<a href="#propertiesInfo" data-toggle="tab" class="tab-multi">
						<span id="winery-property-tab-property" name_i18n="winery_i18n"></span>
					</a>
				</li>
				<li>
					<a href="#reqAndCapInfo" data-toggle="tab" class="tab-multi">
						<span id="winery-property-tab-reqAndCap" name_i18n="winery_i18n"></span>
					</a>
				</li>
				<li>
					<a href="#boundaryNodeScript" data-toggle="tab" class="tab-multi">
						<span id="winery-boundary-tab-artifact" name_i18n="winery_i18n"></span>
					</a>
				</li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane fade in active" id="basicInfo">
					<div id="nodeTemplateInformationSection">
						<%--
							If this is layouted strangely, maybe a <form> wrapper has to be added
							Be aware that nested buttons then trigger a submission of the form (-> ct:spinnerwithinphty)
						--%>
						<fieldset>
							<div class="form-group">
								<label for="nodetemplateid" class="col-sm-3 control-label">Id</label>
								<div class="col-sm-8">
									<input id="nodetemplateid" disabled="disabled" class="form-control"></input>
								</div>
							</div>
							<div class="form-group">
								<label for="nodetemplatename" class="col-sm-3 control-label">Name</label>
								<div class="col-sm-8">
									<input id="nodetemplatename" name="name" class="form-control"/>
								</div>
							</div>
							<div class="form-group">
								<label for="nodetemplateType" class="col-sm-3 control-label">Type</label>
								<%-- filled by fillInformationSection --%>
								<div class="col-sm-8">
									<a id="nodetemplateType" target="_blank" href="#" class="form-control"></a>
								</div>
							</div>
							<div class="form-group">
								<label for="nodetemplateDesc" class="col-sm-3 control-label">Description</label>
								<div class="col-sm-8">
									<input id="nodetemplateDesc" class="form-control"/>
								</div>
							</div>
							<%-- 隐藏min和max --%>
							<div style="display:none">
							<ct:spinnerwithinphty min="0" width="10" changedfunction="minInstancesChanged" label="min" id="minInstances" />
							<ct:spinnerwithinphty min="1" width="10" changedfunction="maxInstancesChanged" label="max" id="maxInstances" withinphty="true" />
							</div>
						</fieldset>
					</div>
				</div>
				<div class="tab-pane fade" id="propertiesInfo">
					<div id="propertiesContent"></div>
				</div>
				<div class="tab-pane fade" id="reqAndCapInfo">
					<div><span id="winery-property-reqAndCap-req" name_i18n="winery_i18n"></span></div>
					<div id="nodeRequirementsTable_div"></div>
					<div><span id="winery-property-reqAndCap-cap" name_i18n="winery_i18n"></span></div>
					<div id="nodeCapabilitiesTable_div"></div>
				</div>
				<div class="tab-pane fade" id="boundaryNodeScript">
					<c:if test="${palette}">
						<div class="form-group">
							<label class="control-label col-sm-4">
								<span id="winery-property-artifact-name" name_i18n="winery_i18n"></span>
								<span class="required" aria-required="true">*</span>
							</label>
							<div class="col-sm-8">
								<input class="form-control" type="text" id="artifactNameInput" name="name">
							</div>
						</div>					
						<div class="form-group">
							<label class="control-label col-sm-4">
								<span id="winery-property-artifact-file" name_i18n="winery_i18n"></span>
								<span class="required" aria-required="true">*</span>
							</label>
							<div class="col-sm-8">
								<select name="artifactFileName" class="form-control" id="artifactFileName">								
								</select>	
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-sm-4">
								<span id="winery-property-artifact-type" name_i18n="winery_i18n"></span>
								<span class="required" aria-required="true">*</span>
								
							</label>
							<div class="col-sm-8">
								<select name="artifactType" class="form-control" id="artifactTypeInput">				
								<c:forEach var="t" items="${allArtifactTypes}">
									<option value="${t.toString()}">${t.localPart}</option>
								</c:forEach>						
								</select>						
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-sm-4"><span id="winery-property-artifact-repository" name_i18n="winery_i18n"></span></label>
							<div class="col-sm-8">
								<input class="form-control" type="text" id="nodeRepositoryInput" placeholder="">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-sm-4"><span id="winery-property-artifact-deploy_path" name_i18n="winery_i18n"></span></label>
							<div class="col-sm-8">
								<input class="form-control" type="text" id="deploy_path" name="deploy_path"></input>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-sm-4"><span id="winery-property-artifact-description" name_i18n="winery_i18n"></span></label>
							<div class="col-sm-8">
								<input class="form-control" type="text" id="nodeDescriptionInput" name="description" placeholder="">
							</div>
						</div>
						
			
		
						<input id="artifact-file-add" class="btn btn-primary btn-sm" value="添加" onclick="addArtifactFile()" style="margin-top:-15px;width:80px;float:right">

						
					</c:if>
					
					<div id="boundaryNodeScriptTable_div">
						<div id="boundaryNodeScriptTable_wrapper" class="dataTables_wrapper" role="grid"></div>
						<table class="zte-table dataTable" id="boundaryNodeScriptTable">
							<thead><tr role="row" class="heading"><th class="sorting_disabled" role="columnheader" rowspan="1" colspan="1" style="width: 35%;">名称</th>
								<th class="sorting_disabled" role="columnheader" rowspan="1" colspan="1" style="width: 40%;">文件名</th>
								<th class="sorting_disabled" role="columnheader" rowspan="1" colspan="1" style="width: 25%;">操作</th></tr></thead>
							<tbody role="alert" aria-live="polite" aria-relevant="all">
						
							</tbody>
						</table>
					</div>
					
				</div>
			</div>
		</form>
	</div>
</div>

<%-- add complex property dialog --%>
<div class="modal fade" id="propertyInputDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<span id="winery-property-dialog-title-edit" name_i18n="winery_i18n"></span>
				</h4>
			</div>
			<div class="modal-body">
				<div id="complexPropertyEditor"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="zte-btn zte-primary" id="propertyInputConfirm">
					<span id="winery-btn-confirm" name_i18n="winery_i18n"></span>
				</button>
				<button type="button" class="zte-btn zte-default" data-dismiss="modal">
					<span id="winery-btn-cancel" name_i18n="winery_i18n"></span>
				</button>
			</div>
		</div>
	</div>
</div>

<%-- req or cap property dialog --%>
<div class="modal fade" id="reqAndCapPropertyDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<span id="winery-property-dialog-title-edit" name_i18n="winery_i18n"></span>
				</h4>
			</div>
			<div class="modal-body">
				<div id="reqAndCapPropertyEditor"></div>
				<form id="reqAndCapPropertyForm" role="form" class="form-horizontal">
				</form> 
			</div>
			<div class="modal-footer">
				<button type="button" class="zte-btn zte-primary" id="reqAndCapPropertyConfirm">
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
	function minInstancesChanged(event, ui) {
		var val;
		if (ui === undefined) {
			val = $("#minInstances").val();
		} else {
			val = ui.value;
		}
		ntMin.html(val);
	}

	function maxInstancesChanged(event, ui) {
		var val;
		if (ui === undefined) {
			val = $("#maxInstances").val();
		} else {
			val = ui.value;
		}
		ntMax.html(val);
	}

	// the name input field of the properties section
	var nameInput = $("#nodetemplatename");

	// the min/max fields of the currently selected node template
	var ntMin;
	var ntMax;
	var editor;
	function fillInformationSection(nodeTemplate) {
		require(["winery-support-common", "winery-datatable", "winery-util", "jsoneditor", "winery-support-common"], 
		function(wsc, wd, util) {
			// currently doesn't help for a delayed update
			//informationSection.slideDown();

			var id = nodeTemplate.attr("id");
			$("#nodetemplateid").val(id);

			var headerContainer = nodeTemplate.children("div.headerContainer");

			// copy name
			var nameField = headerContainer.children("div.name");
			var fullNameField = nodeTemplate.children("span.fullName");
			var name = nameField.text();
			nameInput.val(name);

			// copy type
			var typeQName = headerContainer.children("span.typeQName").text();
			var href = wsc.makeNodeTypeURLFromQName("${repositoryURL}", typeQName);
			var type = headerContainer.children("div.type").text();
			$("#nodetemplateType").attr("href", href).text(type);

			// we could use jQuery-typing, but it is not possible to replace key events there
			nameInput.off("keyup");
			nameInput.on("keyup", function() {
				nameField.text($(this).val());
				fullNameField.text($(this).val());
			});

			// handling of min and max
			ntMin = nodeTemplate.children(".headerContainer").children(".minMaxInstances").children(".minInstances");
			$("#minInstances").val(ntMin.text());
			ntMax = nodeTemplate.children(".headerContainer").children(".minMaxInstances").children(".maxInstances");
			$("#maxInstances").val(ntMax.text());

			//title
			$("#NTPropertiesView_name").text(name);

			//documentation
			var documentation = nodeTemplate.children(".headerContainer").children(".documentation").text();
			$("#nodetemplateDesc").val(documentation);

			//properties
			var palette = ${palette};  //true为编辑模式，false为查看模式
			var propertiesTrs = $("#" + id).children(".propertiesContainer")
					.children("[name='Properties']").find("tr");

			var generateSelectHtml = function(nodeTemplateId, options, name, value, disabledAttr) {
				var select = $('<select>', { 
						id: name, 
						name: name,  
						class: "form-control control-edit",
						style: "width: 80%"
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

				if(nodeTemplateId && !value) {
					//没有值，默认为第一个，把值更新到propertiesContainer中
					updatePropertyDataToContainer(nodeTemplateId, name, options[0]);
				}
				return select;
			}

			var generateProperties = function(id, propertiesTrs, nodeTemplateId) {
				var propertyInfo = $("#" + id);
				propertyInfo.children().remove();
			
				var inputDiv = $('<div>'); //属性来源于输入参数
				var metaDataDiv = $('<div>'); //属性来源于元数据
				var needValidateProperties = []; //需要校验的数据

				for(var i=0; i<propertiesTrs.length; i++) {
					var property = propertiesTrs.eq(i).children();
					var name = property.children(".KVPropertyKey").text();
					var value = property.children(".KVPropertyValue").text();
					if(value == "Empty") {
						value = "";
					}
					//value = value.replace(/\"/g, '&#34;'); //对于复杂数据，要把引号替换为转译字符

					var type = property.children(".KVPropertyType").text();
					var tag = property.children(".KVPropertyTag").text(); 
					var required = property.children(".KVPropertyRequired").text(); //是否必填
					var validValue = property.children(".KVPropertyValidValue").text(); //枚举值

					var validator = { name: name, rules: {}}; //属性校验规则				

					var groupDiv = $('<div>', {class: "form-group"});
					var label = $('<label>', {class: "col-sm-4 control-label"}).text(name);
					if(required == "true") {
						var requiredSpan = $('<span>', {class: "required", "aria-required": "true"}).text("*");
						label.append(requiredSpan);
					}
					
					var colDiv = $('<div>', {class: "col-sm-8"});
					var fixedDiv = $('<div>', {class: "col-fixed"});
					var input = $('<input>', {
							class: "form-control control-edit",
							id: name,
							name: name,
							value: value,
							style: "width:80%; float:left;"
						});

					//属性来源于元数据或者属性设置为只读，不可编辑
					var disabledAttr = "";
					if(!palette || util.hasTag(tag, util.ATTRIBUTE) || util.hasTag(tag, util.READ_ONLY)) {
						disabledAttr = "disabled";
						input.attr("disabled", disabledAttr);
					}
					if(util.hasTag(tag, util.PASSWORD)) { //密码类型数据
						input.attr("type", "password");
					}

					colDiv.append(fixedDiv);
					groupDiv.append(label).append(colDiv);

					if(validValue) { //枚举类型
						var options = validValue.split(",");
						var select = generateSelectHtml(nodeTemplateId, options, name, value, disabledAttr);
						fixedDiv.append(select);
					} else {
						switch(type) {  //type不同，输入方式不同
							case "xsd:boolean" :
								var options = ["", "true", "false"];
								var select = generateSelectHtml(nodeTemplateId, options, name, value, disabledAttr);
								fixedDiv.append(select);
								break;
							case "xsd:integer" :
								input.attr("placeholder", $.i18n.prop("winery-property-validate-digits"));
								fixedDiv.append(input);
								validator.rules.digits = true;
								break;
							case "xsd:string":
								var suggestDiv = $('<div>', {class: "search_suggest"});
								fixedDiv.append(input);
								fixedDiv.append(suggestDiv);
								break;
							default :
								var editSpan = $('<span>', {"class": "editIcon", "data-type": type});
								var editIcon = $('<i>', {class: "fa fa-edit icon-edit"});
								input.attr("disabled", true);
								editSpan.append(editIcon);
								fixedDiv.append(input);
								fixedDiv.append(editSpan);
						}
					}

					//属性分组，来源于输入参数还是元数据
					if(util.hasTag(tag, util.ATTRIBUTE)) {
						metaDataDiv.append(groupDiv);
					} else {
						inputDiv.append(groupDiv);
					}

					if(Object.getOwnPropertyNames(validator.rules).length) {
						needValidateProperties.push(validator);
					}
				}
				
				propertyInfo.append(metaDataDiv).append(inputDiv);

				//添加校验
				$.each(needValidateProperties, function(index, validator){
					$("#" + validator.name).on("keyup", function(){
						//only digits can be entered
						//this.value = this.value.replace(/[^\d]+/, "");
					});
				});
			}

			generateProperties("propertiesContent", propertiesTrs, id);

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
				
				var selects = $("#" + editorId).children().find("select");
											   
				//this.addproperty_controls.style.display="block";
		        
		        //实例化输入提示的JS
		        var oDiv = $('<div>', {class: 'col-fixed-34'});
		        var sDiv = $('<div>', {class: 'search_suggest'});
		        var inputs = $("#" + editorId).find('input[type="text"]').wrap(oDiv).after(sDiv);
				oSearchSuggest(inputs);

		        return editor;
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

				var propertiesTrs = $("#" + id).children(".propertiesContainer")
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
			

			$("#propertiesInfo .editIcon").click(function(e) {
				var $input = $(this).prev();
				var json = $input.val();
				var attrName = $input.attr("name");
				$("#propertyInputDiag").attr("name", attrName);
				var title;
				if(palette) {
					title = $.i18n.prop("winery-property-dialog-title-edit") + " " + attrName;
				} else {
					title = $.i18n.prop("winery-property-dialog-title-view") + " " + attrName;
				}
				$("#propertyInputDiag .modal-title").text(title);

				var schema = { 
					title: attrName
				}
				var options = {
					schema: schema,
					disable_properties: true
				}
				if(json) { //赋值
					options.startval = JSON.parse(json);			
				}
				var type = $(this).attr("data-type");
			
				extractProperties(type, schema, options);			
					
				editor = initEditor("complexPropertyEditor", options);

				$("#propertyInputDiag").modal("show");
			});

			//编辑属性对话框点击确定事件
			$("#propertyInputConfirm").off('click').on('click', function(){
				var errors = editor.validate();
				if(errors.length) return;

				var json = JSON.stringify(editor.getValue());
				var name = $("#propertyInputDiag").attr("name");
				$("#propertiesInfo input[name='"+name+"']").val(json);

				//把输入的属性值更新到propertiesContainer中
				var nodeTemplate = $("div.NodeTemplateShape.selected");
				var nodeTemplateId = nodeTemplate.attr("id");
				updatePropertyDataToContainer(nodeTemplateId, name, json);

				//editor.destroy();
				$("#propertyInputDiag").modal("hide");
			});

			// 把输入的属性值更新到propertiesContainer中
			var bindEventsToUpdatePropertiesContainer = function() {
				var updatePropertiesContainer = function($element, nodeTemplate) {
					var propertyName = $element.attr("name");
					var propertyValue = $.trim($element.val());
					var nodeTemplateId = nodeTemplate.attr("id");
					updatePropertyDataToContainer(nodeTemplateId, propertyName, propertyValue);
				}
				//绑定input控件同步属性值的事件
				$("#propertiesInfo input").focusout(function(){
					updatePropertiesContainer($(this), nodeTemplate);
				}).keyup(function(){
					updatePropertiesContainer($(this), nodeTemplate);
				}).focus(function(){
					//实例化输入提示的JS
					oSearchSuggest($(this));
				});
				//绑定select控件同步属性值的事件
				$("#propertiesInfo select").change(function(){
					updatePropertiesContainer($(this), nodeTemplate);
				});
			}
			bindEventsToUpdatePropertiesContainer();			
			
			
			//把右侧属性的值更新到propertiesContainer中
			function updatePropertyDataToContainer(nodeTemplateId, propertyName, propertyValue) {
				var oTrs = $("#" + nodeTemplateId).children(".propertiesContainer")
						.children("[name='Properties']").find("tr");
				for(var i=0; i<oTrs.length; i++) {
					var tr = oTrs.eq(i);
					var name = tr.find(".KVPropertyKey").text();
					if(name == propertyName) {
						tr.find(".KVPropertyValue").text(propertyValue);
						if(propertyValue) {
							tr.find(".KVPropertyValue").removeClass("editable-empty");
						} else {
							tr.find(".KVPropertyValue").addClass("editable-empty");
						}						
						break;
					}
				};
			}
			
			
			//get_input输入提示
			function oSearchSuggest($input) {
			    var suggestWrap;
			    var selectInput;
			    var init = function() {  
			        $input.on('keyup', sendKeyWord);  
			        $input.on('blur', function(){
			        	setTimeout(hideSuggest, 100);
			        });
			    }  
			    var hideSuggest = function(){  
			    	if(suggestWrap) suggestWrap.hide();
			    }
			      
			    //发送请求，根据关键字到后台查询  
			    var sendKeyWord = function(event){ 
			    	suggestWrap = $(this).next(".search_suggest");
			    	selectInput = $(this);
			        //键盘选择下拉项
			        if(suggestWrap.css('display') == 'block'
			        	&& (event.keyCode == 38 || event.keyCode == 40)){  
			            var current = suggestWrap.find('li.hover');  
			            if(event.keyCode == 38) {  
			                if(current.length>0) {  
			                    var prevLi = current.removeClass('hover').prev();  
			                    if(prevLi.length>0){  
			                        prevLi.addClass('hover');  
			                        $(this).val(prevLi.html());  
			                    }
			                } else {  
			                    var last = suggestWrap.find('li:last');  
			                    last.addClass('hover');  
			                    $(this).val(last.html());  
			                }
			            } else if(event.keyCode == 40) {  
			                if(current.length>0){  
			                    var nextLi = current.removeClass('hover').next();  
			                    if(nextLi.length>0){  
			                        nextLi.addClass('hover');  
			                        $(this).val(nextLi.html());  
			                    }  
			                } else {
			                    var first = suggestWrap.find('li:first');  
			                    first.addClass('hover');  
			                    $(this).val(first.html());  
			                }  
			            }
			        //输入字符  
			        } else {
			            var valText = $.trim($(this).val());  
			            if(valText ==''){ 
			                suggestWrap.hide(); 
			                return;  
			            }  
			            sendKeyWordToBack(valText);  
			        }
			    }  
			    //请求返回后，执行数据展示  
			    var dataDisplay = function(data){  
			        if(data.length<=0){  
			            suggestWrap.hide();  
			            return;  
			        }
			        //往搜索框下拉建议显示栏中添加条目并显示  
			        var li;
			        suggestWrap.html('<ul></ul>');
			        var ul = suggestWrap.find('ul');
			        for(var i=0; i<data.length; i++){  
			            li = document.createElement('li');
			            li.innerHTML = data[i];
			            ul.append(li);
			        } 
			        suggestWrap.show();  
			          
			        //为下拉选项绑定鼠标事件  
			        suggestWrap.find('li').hover(function(){  
			             	suggestWrap.find('li').removeClass('hover');  
			                $(this).addClass('hover');  
			            },function(){  
			                $(this).removeClass('hover');  
			        }).mousedown(function(){  
			            selectInput.val(this.innerHTML);  
			            suggestWrap.hide();  
			        });
			    }  

			    var sendKeyWordToBack = function(keyword) {
					//获取input表格参数，在boundaryDefinitions.tag中定义了input
					var dataTable = $("#boundaryInputsTable").dataTable();
					var inputsData = dataTable.fnGetData();
					var data = [];
					$.each(inputsData, function(index, element) {
						var input_name = '{"get_input":"' + element.name;
						//提示规则，当输入g时给出提示，当是{"get_input":开头时，需要过滤name
						if(keyword.indexOf("g") == 0 || 
							((keyword.indexOf('{"get_input"') == 0 || '{"get_input"'.indexOf(keyword) > -1) && input_name.indexOf(keyword) > -1)) {
							var str = '{"get_input":"' + element.name + '"}';
							data.push(str);
			            }
					});

					//将返回的数据传递给实现搜索输入框的输入提示js类
		        	dataDisplay(data);
				}

			    init();
			}
						
			
			var extractProperties4rc = function(propertiesTrs) {
			
				var props = {};
				
				for(var i=0; i<propertiesTrs.length; i++) {
					var property = propertiesTrs.eq(i).children();
					var name = property.children(".KVPropertyKey").text();
					var value = property.children(".KVPropertyValue").text();
					var type = property.children(".KVPropertyType").text();
					var tag = property.children(".KVPropertyTag").text(); 
					var required = property.children(".KVPropertyRequired").text(); //是否必填
					var validValue = property.children(".KVPropertyValidValue").text(); //枚举值
					
									
					if(validValue != "") {
						props[name] ={
							type: "string",
							enum: validValue.split(",")
						};
					}else{
						props[name] = martchingType(type, value);
					}	
				}
				
				return props;
			}
			//匹配属性类型
			var  martchingType = function(type, value){
				var propertyValue ;
				
				var mapType ;
				if(value == "Empty") {
					value = "";
				}else{
					if(type.indexOf(":") > -1) {
						type = type.substring(type.indexOf(":") + 1);						
					}else if(type.indexOf("_") > -1){
						mapType = type.split("_")[1];	
						type = type.split("_")[0];													
					}																									
				}	
				switch(type) {  //type不同，输入方式不同
							case "string":
								propertyValue = {
										default:value,
										type:"string"
								};
								break;
							case "list":
								if(value == ""){
									propertyValue = {
										default:[],
										type:"array"
									};
								}else{
									propertyValue = {
										default:JSON.parse(value),
										type:"array"
									};	
								}
								break;
							case "map":	
								if(value == ""){
									var schema = { "additionalProperties" : {"type":mapType} ,
									"properties" : {},
									 "options": {"disable_properties": false, disable_edit_json: false}									
									};
								}else{
									var schema = { 
										"additionalProperties" : {"type":mapType },										
										default:JSON.parse(value),
										"options": {"disable_properties": false, disable_edit_json: false}	,
										type:"object"
									}
								}		
								propertyValue = schema;					
								break;														
							case "boolean":
								if(value == ""){
									propertyValue = {
										default:false,
										type:"boolean"
									};
								}else{
									propertyValue = {
										default:JSON.parse(value),
										type:"boolean"
									};
								}							
								break;							
							case "object": 
								if(value == ""){
									propertyValue = {
										default:{},
										type:"object",
										"options": {"disable_properties": false, disable_edit_json: false}	
									};
								}else{
									propertyValue = {
										default:JSON.parse(value),
										type:"object",
										"options": {"disable_properties": false, disable_edit_json: false}	
									};
								}
								break;
							default:								
								if(value == ""){
									propertyValue = {
										type:type
									};
								}else{
									propertyValue = {
										default:JSON.parse(value),
										type:type
									};
								}
													
				}
				
				return propertyValue;
			
			}
		
			//nodetemplate的requirements和capabilities展示
			var generateReqOrCapTable = function(nodeTemplate, elementClass, tableId, visible) {
				var reqorcaps = $("#" + nodeTemplate.attr("id")).find(elementClass);
				var reqorcapTableData = [];
				$.each(reqorcaps, function(index, element){
					var id = $(element).find(".id").text();
					var name = $(element).find(".name").text();
					var rowData = {
						id: id,
						type: name
					}
					reqorcapTableData.push(rowData);
				});
				//初始化表格数据
				var operationRender = function(obj) {
					var operationHtml = '<i class="fa fa-edit icon-del" data-id="'+ obj.aData.id
						+ '" id="winery-btn-edit" name_i18n="winery_i18n" style="margin-right:3px;"></i>';
					return operationHtml;
				}
				var tableInfo = {
					divId : tableId + "_div",
					id : tableId,
					columns : [
						{"mData": "id", "name": "ID", "bVisible": false},
						{"mData": "type", "name": $.i18n.prop("winery-property-reqAndCap-table-name"), "sWidth":"70%"},
						{"mData": null, "name": $.i18n.prop("winery-table-operation"), "sWidth":"30%", "fnRender" : operationRender, "bVisible": visible}
					]
				}
				
				wd.initTableWithData(tableInfo, reqorcapTableData, function(){
					$("#" + tableInfo.id).find("tbody .fa-edit").off("click").on("click", function(){	
									
						var attrName = $("#" + $(this).attr("data-id")).find(".name").text();
						
						$("#reqAndCapPropertyDiag").attr("name", attrName);
						var title;
						if(palette) {
						
							title = $.i18n.prop("winery-property-dialog-title-edit") + " " + attrName;
						} else {
							title = $.i18n.prop("winery-property-dialog-title-view") + " " + attrName;
						}
						$("#reqAndCapPropertyDiag .modal-title").text(title);

						var schema = { 
							title: attrName							
						}
						var options = {
							schema: schema,
							disable_collapse: false,							
							disable_edit_json: true,
							disable_properties: true
						}
							
						
						schema.type = "object";
						
						var id = $(this).attr("data-id");
						$("#reqAndCapPropertyDiag").attr("data-id", id);
						var propertiesTrs = $("#" + id).children(".propertiesContainer").find("tr");
						
						schema.properties = extractProperties4rc(propertiesTrs);
						editor = initEditor("reqAndCapPropertyEditor", options);
						
												
						$("#reqAndCapPropertyDiag").modal("show");
						
						/**
							var id = $(this).attr("data-id");
							var propertiesTrs = $("#" + id).children(".propertiesContainer").find("tr");

							$("#reqAndCapPropertyForm").children().remove();
							$("#reqAndCapPropertyDiag").attr("data-id", id);

							generateProperties("reqAndCapPropertyForm", propertiesTrs);
							$("#reqAndCapPropertyDiag").modal("show");
						*/
					});
				});
			}
			generateReqOrCapTable(nodeTemplate, ".requirements", "nodeRequirementsTable", false);
			generateReqOrCapTable(nodeTemplate, ".capabilities", "nodeCapabilitiesTable", palette);

			$("#reqAndCapPropertyConfirm").click(function(){				
				var id = $("#reqAndCapPropertyDiag").attr("data-id");
				var rows = $("#reqAndCapPropertyEditor").find(".row");
							
				var json = editor.getValue();	
				
				for(var i=0,len = rows.length; i<len; i++){
					var property = $(rows[i]).children().eq(0);
					var propertyName = property.attr("data-schemapath").split(".")[1];
					//var propertyType = property.find("select").val();					
					var propertyValue ;
					
					for ( var p in json ){	
						if(p == propertyName){
							propertyValue = json[p];
						}
					}
					
					updateReqOrCapPropertyDataToContainer(id, propertyName, propertyValue );	
				}
				
				$("#"+id).attr("json","");
				$("#"+id).attr("json",JSON.stringify(json));
				
				 
							
				$("#reqAndCapPropertyDiag").modal("hide");
			});
			
			//把需求和能力的属性的值更新到propertiesContainer中
			function updateReqOrCapPropertyDataToContainer	(reqOrCapId, propertyName, propertyValue) {
				var oTrs = $("#" + reqOrCapId).children(".propertiesContainer").find("tr");
																		
				for(var i=0, len=oTrs.length; i<len ; i++ ){

					var name = oTrs.eq(i).find(".KVPropertyKey").text();
					var type = typeof(propertyValue);			
					
					if(name == propertyName) {
						if(type == "object"){	
							oTrs.eq(i).find(".KVPropertyValue").text(JSON.stringify(propertyValue));							
						}else{
							oTrs.eq(i).find(".KVPropertyValue").text(propertyValue);			
						}			
												
						if(propertyValue) {
							oTrs.eq(i).find(".KVPropertyValue").removeClass("editable-empty");
						} else {
							oTrs.eq(i).find(".KVPropertyValue").addClass("editable-empty");
						}						
					}
														
				}
								
			}
			
			
			//初始化脚本表格,初始化artifact
			initArtifactFileTable();
			
		});
	}
	
	var qnameNamespace = "{http://www.open-o.org/tosca/nfv}";	
	//添加artifacts属性
	function addArtifactFile(){
		require(["winery-support-common"], 
		function(wsc) {
			
			if(!artifactValidate()) return ;

			var nodeTemplate = $("div.NodeTemplateShape.selected");
			var content = nodeTemplate.children("div.deploymentArtifactsContainer").children("div.content");			
			
			var fileIndex = (new Date()).getTime();
			var deploy_path = wsc.qname2href("", null, qnameNamespace+$("#deploy_path").val());
			var artifactFileName = wsc.qname2href("", null, qnameNamespace+$("#artifactFileName").val());
			var nodeDescriptionInput = wsc.qname2href("", null, qnameNamespace+$("#nodeDescriptionInput").val());
			var nodeRepositoryInput = wsc.qname2href("", null, qnameNamespace+$("#nodeRepositoryInput").val());			
			var artifactTypeInput = wsc.qname2href("", null, $("#artifactTypeInput").val());
			var artifactName = wsc.qname2href("", null, qnameNamespace+$("#artifactNameInput").val());
			
			var rowId = "artifactFiles_" + fileIndex ;
			
			content.append("<div class='deploymentArtifact row' id='" + rowId + "'></div>");
															
			var nodeArtifactInfo = $(document.getElementById(rowId));
			
			nodeArtifactInfo.append("<div class='artifactName'>" + artifactName + "</div>");
			nodeArtifactInfo.append("<div class='artifactFileName'>" + artifactFileName + "</div>");
			nodeArtifactInfo.append("<div class='artifactType'>" + artifactTypeInput + "</div>");
			nodeArtifactInfo.append("<div class='deploy_path'>" + deploy_path + "</div>");	
			nodeArtifactInfo.append("<div class='nodeDescriptionInput'>" + nodeDescriptionInput + "</div>");
			nodeArtifactInfo.append("<div class='nodeRepositoryInput'>" + nodeRepositoryInput + "</div>");

			var artifactFile = $("#artifactFileName").val().split("/");
			var str = "<tr class='odd'>"
				+"<td>"+artifactName+"</td>"
				+"<td>"+artifactFile[artifactFile.length - 1]+"</td>"
				+"<td><i class='fa fa-edit icon-del' id='winery-btn-edit' name_i18n='winery_i18n' "
				+"onclick='showArtifactUpdate(" + rowId + ")' ></i>"				
				+"<i class='fa fa-trash icon-del' id='winery-btn-delete' name_i18n='winery_i18n' style='margin-left:8px;' "
				+"onclick='dellArtifactFile($(this).parent().parent(),"+rowId+");'></i></td></tr>"
				
			
			$("#boundaryNodeScriptTable tbody").append(str);
			
			if($("#artifact-file-update").length != 0){
				$("#artifact-file-update")[0].remove();
			}
			
			artifactValidate();
		});	
	
	}
	
	//删除ArtifactFile
	function dellArtifactFile(tr,rowId){

		tr[0].parentNode.removeChild(tr[0]);
		rowId.remove();
		
		if($("#artifact-file-update").length != 0){
			$("#artifact-file-update")[0].remove();
		}		
	};
	
	//显示修改ArtifactFile
	function showArtifactUpdate(rowId){
		var artifactDiv = $(rowId);	
				
		$("#artifactNameInput").val(artifactDiv.children(".artifactName").children().html());
		$("#artifactFileName").val(artifactDiv.children(".artifactFileName").children().html());
		$("#artifactTypeInput").val(artifactDiv.children(".artifactType").children().attr("data-qname"));
		$("#deploy_path").val(artifactDiv.children(".deploy_path").children().html());
		$("#nodeDescriptionInput").val(artifactDiv.children(".nodeDescriptionInput").children().html());
		$("#nodeRepositoryInput").val(artifactDiv.children(".nodeRepositoryInput").children().html());	
		
		var updateBtn = "<input id='artifact-file-update' class='btn btn-info btn-sm' value='修改' "
			+ "onclick='artifactUpdate(" + artifactDiv.attr('id') + ")' style='margin-top:-15px;width:80px;float:right;margin-right:15px'>";
		
		if($("#artifact-file-update").length != 0){
			$("#artifact-file-update")[0].remove();
		}
	
		$("#artifact-file-add").after(updateBtn);
	
		
		
		 
	}
	//修改ArtifactFile
	function artifactUpdate(rowId){
		require(["winery-support-common"], 
			function(wsc) {
			if(!artifactValidate()) return ;
			var artifactDiv = $(rowId);			
			
			var artifactName = wsc.qname2href("", null, qnameNamespace+$("#artifactNameInput").val());
			var deploy_path = wsc.qname2href("", null, qnameNamespace+$("#deploy_path").val());		
			var artifactFileName = wsc.qname2href("", null, qnameNamespace+$("#artifactFileName").val());		
			var artifactType = wsc.qname2href("", null, $("#artifactTypeInput").val());
			var nodeDescriptionInput = wsc.qname2href("", null, qnameNamespace+$("#nodeDescriptionInput").val());
			var nodeRepositoryInput = wsc.qname2href("", null, qnameNamespace+$("#nodeRepositoryInput").val());			
		
			
			artifactDiv.children(".artifactName").html(artifactName);
			artifactDiv.children(".artifactFileName").html(artifactFileName);
			artifactDiv.children(".artifactType").html(artifactType);
			artifactDiv.children(".deploy_path").html(deploy_path);
			artifactDiv.children(".nodeDescriptionInput").html(nodeDescriptionInput);
			artifactDiv.children(".nodeRepositoryInput").html(nodeRepositoryInput);
			
			$("#artifact-file-update")[0].remove();

			initArtifactFileTable();
			
		});
	}
	
	//初始化artifact表格
	function initArtifactFileTable(){
		var nodeTemplate = $("div.NodeTemplateShape.selected");
		var artifacts = nodeTemplate.children("div.deploymentArtifactsContainer").children("div.content").children("div.deploymentArtifact");

		$("#boundaryNodeScriptTable tbody").html("");
		
		for(var ai=0 ; ai<artifacts.length ; ai++){
			var artifactDiv = $(artifacts[ai]);	
								
			var artifactName = artifactDiv.children(".artifactName").children().html();
			var artifactFile = artifactDiv.children(".artifactFileName").children().html().split("/");
			var artifactFileName = artifactFile[artifactFile.length - 1];
			var artifactType = artifactDiv.children(".artifactType").children().html();
			var deploy_path = artifactDiv.children(".deploy_path").children().html();
			var nodeDescriptionInput = artifactDiv.children(".nodeDescriptionInput").children().html();
			var nodeRepositoryInput = artifactDiv.children(".nodeRepositoryInput").children().html();	
			
			
			var str = "<tr class='odd'  ondblclick='showArtifactUpdate(" + artifactDiv.attr('id') + ")'>"
					+"<td>"+artifactName+"</td>"
					+"<td>"+artifactFileName+"</td>"
					+"<td><i class='fa fa-edit icon-del' id='winery-btn-edit' name_i18n='winery_i18n' "
					+"onclick='showArtifactUpdate(" + artifactDiv.attr("id") + ")' ></i>"				
					+"<i class='fa fa-trash icon-del' id='winery-btn-delete' name_i18n='winery_i18n' style='margin-left:8px;' "
					+"onclick='dellArtifactFile($(this).parent().parent()," + artifactDiv.attr("id") + ");'></i></td></tr>"
	
			$("#boundaryNodeScriptTable tbody").append(str);
														
		}
		
		$("#artifactNameInput").val("");
		$("#artifactFileName").val("");
		$("#artifactTypeInput").val("");
		$("#deploy_path").val("");
		$("#nodeDescriptionInput").val("");
		$("#nodeRepositoryInput").val("");	
		
		if($("#artifact-file-update").length != 0){
			$("#artifact-file-update")[0].remove();
		}
		
		//初始化artifactFile列表
		$("#artifactFileName").html("");
		$.ajax({
				type : "GET",
				dataType : "json",
				url : "${serviceTemplateURL}/servicefiles",
				success : function(resp) {
					var filesData = resp || [];
					for(var i=0,len = filesData.length; i<len; i++){
						$("#artifactFileName").append(
						"<option value='" + filesData[i].path + "/" + filesData[i].fileName + "'>"+ filesData[i].fileName +"</option>"
						);
					}					
				}
		});	
		
	}

	//artifact数据校验
	function artifactValidate(){
		
		if($("#artifactNameInput").val() == "" ){
			var tagName = "*" + $("#winery-boundary-tab-artifact").text() + "*";
			var inputName = "*" + $("#artifactNameInput").parent().prev().text().replace(/[\r\n]/g,"");
			vShowError($.i18n.prop("winery-template-validate-artifact-add", tagName, inputName));
			return false;
		}
		
		if($("#artifactFileName").val() == "" ){
			var tagName = "*" + $("#winery-boundary-tab-artifact").text() + "*";
			var inputName = "*" + $("#artifactFileName").parent().prev().text().replace(/[\r\n]/g,"");
			vShowError($.i18n.prop("winery-template-validate-artifact-add", tagName, inputName));
			return false;
		}

		if($("#artifactTypeInput").val() == "" ){
			var tagName = "*" + $("#winery-boundary-tab-artifact").text() + "*";
			var inputName = "*" + $("#artifactTypeInput").parent().prev().text().replace(/[\r\n]/g,"");
			vShowError($.i18n.prop("winery-template-validate-artifact-add", tagName, inputName));
			return false;
		}

		return true;
	}
	
	function showViewOnTheRight(nid) {
		//隐藏boundary
		$("#boundaryDefinition").hide();
		
		$("#NTPropertiesView").fadeIn();
	}

	function hideViewOnTheRight() {
		$("#NTPropertiesView").fadeOut();
		//boundary也隐藏
		$("#boundaryDefinition").hide();
	}

	function showPropertyInputDialog() {
	//showPropertyInputDialog(\''+name+'\');
		$('#propertyInputDiag').modal('show');
	}

	//add new function
	function deleteBoundaryProperty(e) {
		$(e.target).parent.remove();
	}



$(function() {
	winery.events.register(
		winery.events.name.SELECTION_CHANGED,
		function() {
			// min/max instances do not lost focus if other shape is clicked
			// workaround
			if ($("#minInstances").is(":focus")) {
				minInstancesChanged();
			}
			if ($("#maxInstances").is(":focus")) {
				maxInstancesChanged();
			}
			var nodeTemplate = $("div.NodeTemplateShape.selected");
			var numSelected = nodeTemplate.length;
			if (numSelected == 1) {
				fillInformationSection(nodeTemplate);
				showViewOnTheRight(nodeTemplate.attr("id"));

				//同步documentation数据，放在这里是因为如果当nodetemplate失去焦点了，那么当前
				//就没有选择的nodetemplate了，也就没法同步数据了
				$("#nodetemplateDesc").off("focusout");
				$("#nodetemplateDesc").on("focusout", function(){
					var newDocumentation = $(this).val();
					//var nodeTemplate = $("div.NodeTemplateShape.selected"); //可能会获取不到
					nodeTemplate.children(".headerContainer").children(".documentation").text(newDocumentation);
				});
			} else {
				hideViewOnTheRight();
			}
		}
	);

	//关闭右侧属性框
	$("#closeNTPropertiesViewBtn").click(function() {
		$("#NTPropertiesView").hide();
	});
	
	//点击右侧属性删除按钮
	$("#delNodeTemplate").click(function(){
		$("#NTPropertiesView").hide();
		
		$(this).trigger("blur"); //使当前按钮失去焦点，才能删除节点
		winery.events.fire(winery.events.name.command.DELETE_SELECTION);
	});

	//查看模式，禁用属性
	var palette = ${palette};
	if(!palette) {
		$("#delNodeTemplate").hide();
		$("#nodeTemplateInformationSection").find("input").attr("disabled", true);
	}
		
	
});
</script>