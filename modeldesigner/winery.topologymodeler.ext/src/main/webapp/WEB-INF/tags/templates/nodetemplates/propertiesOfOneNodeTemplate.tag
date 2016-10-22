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
--%>

<%@tag language="java" pageEncoding="UTF-8" description="Renders the properies of one node tempate on the right"%>

<%@attribute name="repositoryURL" required="true" type="java.lang.String" description="The repository URL"%>
<%@attribute name="palette" required="false" type="java.lang.Boolean" description="palette mode or view mode"%>

<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common" %>

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
			      	<a href="#basicInfo" data-toggle="tab">
			      		<span id="winery-property-tab-basic" name_i18n="winery_i18n"></span>
			      	</a>
			   	</li>	
				<li>
			      	<a href="#propertiesInfo" data-toggle="tab">
			      		<span id="winery-property-tab-property" name_i18n="winery_i18n"></span>
			      	</a>
			   	</li>
			   	<li>
			      	<a href="#reqAndCapInfo" data-toggle="tab">
			      		<span id="winery-property-tab-reqAndCap" name_i18n="winery_i18n"></span>
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

	function fillInformationSection(nodeTemplate) {
		require(["winery-support-common", "winery-datatable", "winery-util", "jsoneditor"], 
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
		        if(!${palette}) {
		        	editor.disable();
		        }

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
			
			var editor;
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
					$("#" + tableInfo.id).find("tbody .fa-edit").off("click")
						.on("click", function(){
							var id = $(this).attr("data-id");
							var propertiesTrs = $("#" + id).children(".propertiesContainer").find("tr");

							$("#reqAndCapPropertyForm").children().remove();
							$("#reqAndCapPropertyDiag").attr("data-id", id);

							generateProperties("reqAndCapPropertyForm", propertiesTrs);
							$("#reqAndCapPropertyDiag").modal("show");
					});
				});
			}
			generateReqOrCapTable(nodeTemplate, ".requirements", "nodeRequirementsTable", false);
			generateReqOrCapTable(nodeTemplate, ".capabilities", "nodeCapabilitiesTable", palette);

			$("#reqAndCapPropertyConfirm").click(function(){
				var id = $("#reqAndCapPropertyDiag").attr("data-id");
				var inputs = $("#reqAndCapPropertyForm").find("input");
				inputs.each(function(index, input){
					var name = $(input).attr("name");
					var value = $(input).val();
					updateReqOrCapPropertyDataToContainer(id, name, value);
				});
				$("#reqAndCapPropertyDiag").modal("hide");
			});
			//属性的值更新到propertiesContainer中
			function updateReqOrCapPropertyDataToContainer(reqOrCapId, propertyName, propertyValue) {
				var oTrs = $("#" + reqOrCapId).children(".propertiesContainer").find("tr");
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
		});
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