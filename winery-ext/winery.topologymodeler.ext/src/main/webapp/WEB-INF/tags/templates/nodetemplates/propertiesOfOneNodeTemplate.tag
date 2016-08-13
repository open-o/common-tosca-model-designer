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

<%@tag language="java" pageEncoding="UTF-8" description="Renders the properies of one node tempate on the right"%>

<%@attribute name="repositoryURL" required="true" type="java.lang.String" description="The repository URL"%>
<%@attribute name="palette" required="false" type="java.lang.Boolean" description="palette mode or view mode"%>

<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common" %>


<div id="NTPropertiesView" class="right-menu" style="display: none;">
	<div class="title">
		<h4 id="NTPropertiesView_name">NodeTemplate</h4>
		<button id="delNodeTemplate" type="button" class="zte-btn zte-default zte-multi title-delete">
			<i class="fa fa-trash" style="margin-right: 5px;"></i>删除
		</button>
		<button id="closeNTPropertiesViewBtn" type="button" class="close">&times;</button>
	</div>
	<form class="form-horizontal" role="form">
		<ul class="nav title-list">
			<li class="active">
		      	<a href="#basicInfo" data-toggle="tab">概况</a>
		   	</li>	
			<li>
		      	<a href="#propertiesInfo" data-toggle="tab">属性</a>
		   	</li>
		   	<li>
		      	<a href="#reqAndCapInfo" data-toggle="tab">需求和能力</a>
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
						<ct:spinnerwithinphty min="0" width="10" changedfunction="minInstancesChanged" label="min" id="minInstances" 
						palette="${palette}" />
						<ct:spinnerwithinphty min="1" width="10" changedfunction="maxInstancesChanged" label="max" id="maxInstances" withinphty="true" palette="${palette}"/>
					</fieldset>
				</div>
			</div>
			<div class="tab-pane fade" id="propertiesInfo">
				<div id="propertiesContent"></div>
			</div>
			<div class="tab-pane fade" id="reqAndCapInfo">
				<div>需求</div>
				<div id="nodeRequirementsTable_div"></div>
				<div>能力</div>
				<div id="nodeCapabilitiesTable_div"></div>
			</div>
		</div>
	</form>
</div>

<%-- add complex property dialog --%>
<div class="modal fade" id="propertyInputDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">编辑属性</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" role="form">
					<ul class="nav nav-tabs">
						<li class="active"><a href="#propertyInputLine" data-toggle="tab">属性输入</a></li>
						<li><a href="#propertyInputTextArea" data-toggle="tab">属性值</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane fade in active" id="propertyInputLine">
							<div class="form-group insertLine">
							</div>
							<div class="inputsLine">
							</div>
						</div>
						<div class="tab-pane fade" id="propertyInputTextArea">
							<div class="form-group">
								<div class="col-sm-12">
							    	<textarea class="form-control" rows="6" disabled></textarea>
							    </div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="zte-btn zte-primary" id="propertyInputConfirm">确定</button>
				<button type="button" class="zte-btn zte-default" data-dismiss="modal">取消</button>
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
		require(["winery-support-common", "winery-datatable"], function(wsc, wd) {
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

			var palette = ${palette};  //true为编辑模式，false为查看模式
			//properties
			var propertiesTrs = $("#" + id + ">.propertiesContainer tr");
			var propertyInfo = $("#propertiesContent");
			//属性来源于输入参数
			var inputHtml = '';
			//属性来源于元数据
			var metaDataHtml = '';
			for(var i=0; i<propertiesTrs.length; i++) {
				var html = '';
				var requiredHtml = '';
				var property = propertiesTrs.eq(i);
				var name = property.children().children(".KVPropertyKey").text();
				var value = property.children().children(".KVPropertyValue").text();
				//对于复杂数据，要把引号替换为转译字符
				value = value.replace(/\"/g, '&#34;');
				var type = property.children().children(".KVPropertyType").text();
				var tag = property.children().children(".KVPropertyTag").text(); 
				var required = property.children().children(".KVPropertyRequired").text(); //是否必填
				if(value == "Empty") {
					value = "";
				}
				var disabledAttr = "disabled";
				if(palette && tag != "attribute") { //属性来源于元数据，不可编辑
					disabledAttr = "";
				}
				if(required == "true") { //必填属性
					requiredHtml = '<span class="required" aria-required="true">*</span>';
				}

				html += '<div class="form-group"><label class="col-sm-4 control-label">' + name + requiredHtml + '</label>' + '<div class="col-sm-8 col-fixed"><input name="' + name + '" value="'+ value + '" class="form-control" '+ disabledAttr + ' style="width:80%;float:left;"/>';

				//type不同，输入方式不同
				if((type.indexOf("objlist_") > -1) || (type.indexOf("typelist_") > -1)) {
					html += '<span name="' + type + '" class="editIcon"><i class="fa fa-edit icon-edit"></i></span>';
				} else {
					html += '<div class="search_suggest"></div>';
				}
				html += '</div></div>';

				//属性分组，来源于输入参数还是元数据
				if(tag == "attribute") {
					metaDataHtml += html;
				} else {
					inputHtml += html;
				}
			}
			propertyInfo.html(metaDataHtml + inputHtml);
			
			//生成编辑属性对话框中的新增html片段
			var generateDialogInsertHtml = function(isObject, fields) {
				if(isObject && (fields.length == 0)) {
					return;
				}
				var insertLineHtml = "";
				if(isObject) {
					for(var i=0;i<fields.length;i++) {
						insertLineHtml += '<div class="col-sm-5 col-fixed"><input class="form-control" type="text" placeholder="'+ fields[i] +'" name="'+ fields[i] +'"><div class="search_suggest"></div></div>';
					}
				} else {
					insertLineHtml += '<div class="col-sm-7 col-fixed"><input class="form-control" type="text"><div class="search_suggest"></div></div>';
				}
				
				insertLineHtml += '<button class="zte-btn zte-small action" type="button">添加</button>';
				$("#propertyInputDiag .insertLine").html(insertLineHtml);

				//点击添加按钮事件
				$("#propertyInputDiag .insertLine button").click(function() {
					var inputs = $(this).parent().find("input");

					var addInputLineHtml = '<div class="form-group">';
					if(isObject) {
						for(var i=0;i<inputs.length;i++) {
							var inputName = inputs.eq(i).attr("name");
							var inputValue = inputs.eq(i).val();
							//对于复杂数据，要把引号替换为转译字符
							inputValue = inputValue.replace(/\"/g, '&#34;');
							addInputLineHtml += '<div class="col-sm-5 col-fixed"><input class="form-control" type="text" value="'+ inputValue +'" name="'+ inputName +'" disabled></div>';
						}
					} else {
						var inputValue = inputs.val();
						addInputLineHtml += '<div class="col-sm-7 col-fixed"><input class="form-control" type="text" value="'+ inputValue +'" disabled></div>';
					}
					
					addInputLineHtml += '<button class="zte-btn zte-small action" type="button">删除</button></div>';
					$("#propertyInputDiag .inputsLine").append(addInputLineHtml);

					//清空输入值
					inputs.val("");

					//点击添加删除事件
					$("#propertyInputDiag .inputsLine button").click(function(){
						$(this).parent().remove();
					});
				});
			}

			//生成编辑属性对话框中已存在的属性html片段
			var generateDialogInputsHtml = function(name, jsonStr) {
				if(!jsonStr) {
					return;
				}
				var valueList = JSON.parse(jsonStr); 
				var inputLineHtml = '';
				for(var i=0;i<valueList.length;i++) {
					var element = valueList[i];
					inputLineHtml += '<div class="form-group">';
					if(element instanceof Object) {
						//获取属性个数，用于布局
						var propertiesNumber = Object.getOwnPropertyNames(element).length;
						propertiesNumber = Math.max(propertiesNumber, 2);
						var num = Math.ceil(10/propertiesNumber);
						var colClass = "col-sm-" + num;

						$.each(element, function(key, value){
							var disabledAttr = "disabled";
							if(palette) { 
								disabledAttr = "";
							}
							if(value instanceof Object) { //value值有可能是个对象
								value = JSON.stringify(value).replace(/\"/g, '&#34;');
							}
							inputLineHtml += '<div class="'+ colClass +' col-fixed"><input class="form-control" type="text" value="'+ value +'" name="'+ key +'" '+disabledAttr+' placeHolder="输入get_input获取输入参数"><div class="search_suggest"></div></div>';
						});
					} else {
						inputLineHtml += '<div class="col-sm-7 col-fixed"><input class="form-control" type="text" value="'+ element +'" disabled></div>';
					}
					if(palette) {
						inputLineHtml += '<button class="zte-btn zte-small action" type="button">删除</button></div>';
					} else {
						inputLineHtml += '</div>';
					}
					
				}
				$("#propertyInputDiag .inputsLine").html(inputLineHtml);

				//点击删除事件
				$("#propertyInputDiag .inputsLine button").click(function(){
					$(this).parent().remove();
				});
			}

			//初始化对话框
			var initDiag = function() {
				//清空属性
				$("#propertyInputDiag .insertLine").html("");
				$("#propertyInputDiag .inputsLine").html("");
				$("#propertyInputDiag .nav-tabs li").removeClass("active");
				$("#propertyInputDiag .nav-tabs li").eq(0).addClass("active");
				$("#propertyInputDiag .tab-content div").removeClass("in active");
				$("#propertyInputDiag .tab-content div").eq(0).addClass("in active");
			}
			
			$("#propertiesInfo .editIcon").click(function(e) {
				initDiag();

				var $input = $(this).prev();
				var name = $input.attr("name");
				$("#propertyInputDiag").attr("name", name);
				var title;
				if(palette) {
					title = "编辑属性 " + name;
				} else {
					title = "查看属性 " + name;
				}
				$("#propertyInputDiag .modal-title").text(title);

				var value = $input.val();
				var type = $(this).attr("name");
				if(type.indexOf("typelist_") > -1) {
					//数据类型是普通数组
					if(palette) {
						generateDialogInsertHtml(false);
					}
					generateDialogInputsHtml(name, value);

					$("#propertyInputDiag").attr("data-isobject", false);					
				} else if(type.indexOf("objlist_") > -1) {
					//数据类型是对象数组
					if(palette) {
						var realType = type.substring("objlist_".length);
						var fields = realType.split("_");
						generateDialogInsertHtml(true, fields);
					}
					generateDialogInputsHtml(name, value);

					$("#propertyInputDiag").attr("data-isobject", true);
				}

				//tab 切换事件
				$("#propertyInputDiag a[data-toggle='tab']").on("show.bs.tab", function(e){
					var href = $(e.target).attr("href");
					if(href == "#propertyInputTextArea") {
						var isObject = $("#propertyInputDiag").attr("data-isobject") || false;
						var json = generateJsonData(isObject);
						$("#propertyInputTextArea textarea").text(json);
					}
				});

				//实例化输入提示的JS,参数为进行查询操作时要调用的函数名  
				oSearchSuggest($("#propertyInputDiag input"));

				$("#propertyInputDiag").modal("show");
			});

			// 把输入的属性值更新到propertiesContainer中，
			$("#propertiesInfo input").focusout(function(){
				var propertyName = $(this).attr("name");
				var propertyValue = $.trim($(this).val());
				var nodeTemplateId = nodeTemplate.attr("id");
				updatePropertyDataToContainer(nodeTemplateId, propertyName, propertyValue);
			}).focus(function(){
				//实例化输入提示的JS
				oSearchSuggest($(this));
			});

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
			        suggestWrap.hide();
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
			var generateReqOrCapTable = function(nodeTemplate, elementClass, tableId) {
				var reqorcaps = $("#" + nodeTemplate.attr("id") + " " + elementClass);
				var reqorcapTableData = [];
				$.each(reqorcaps, function(index, element){
					var name = $(element).find(".name").text();
					var sessionType = $(element).find(".KVPropertyValue").text();
					var rowData = {
						type : name,
						sessionType : sessionType
					}
					reqorcapTableData.push(rowData);
				});
				//初始化表格数据
				var tableInfo = {
					divId : tableId + "_div",//"boundaryRequirementsTable_div",
					id : tableId,//"boundaryRequirementsTable",
					columns : [
						{"mData": "type", "name": "类型", "sWidth":"40%"},
						{"mData": "sessionType", "name": "值", "sWidth":"60%"}
					]
				}
				wd.initTableWithData(tableInfo, reqorcapTableData);
			}
			generateReqOrCapTable(nodeTemplate, ".requirements", "nodeRequirementsTable");
			generateReqOrCapTable(nodeTemplate, ".capabilities", "nodeCapabilitiesTable");
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

	//生成JSON数据
	function generateJsonData(isObject) {
		//判断是否是对象类型，如果是则返回对象
		var handleObjectType = function(value) {
			if((value.indexOf("{") > -1) || (value.indexOf("[") > -1)) {
				var obj = JSON.parse(value);
				return obj;
			}
			return value;
		}

		var propList = [];
		var oDivs = $("#propertyInputDiag .inputsLine .form-group");
		$.each(oDivs, function(index, element){
			var inputs = $(element).find("input");
			if(isObject == "true") {
				var propObj = {};
				$.each(inputs, function(inde, elem){
					var name = $(elem).attr("name");
					var value = $(elem).val();
					propObj[name] = handleObjectType(value);
				});
				propList.push(propObj);
			} else {
				propList.push(inputs.val());
			}
		});
		var json = JSON.stringify(propList);
		if(propList.length == 0) {
			json = "";
		}
		return json;
	}

	//把右侧属性的值更新到propertiesContainer中
	function updatePropertyDataToContainer(nodeTemplateId, propertyName, propertyValue) {
		var oTrs = $("#" + nodeTemplateId + ">.propertiesContainer tr");
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

	//编辑属性对话框点击确定事件
	var propertyInputConfirmClick = function() {
		var isObject = $("#propertyInputDiag").attr("data-isobject") || false;
		var json = generateJsonData(isObject);
		var name = $("#propertyInputDiag").attr("name");
		$("#propertiesInfo input[name='"+name+"']").val(json);

		//把输入的属性值更新到propertiesContainer中
		var nodeTemplate = $("div.NodeTemplateShape.selected");
		var nodeTemplateId = nodeTemplate.attr("id");
		updatePropertyDataToContainer(nodeTemplateId, name, json);

		$("#propertyInputDiag").modal("hide");
	}

	//对话框点击确定，生成JSON格式数据
	$("#propertyInputConfirm").click(propertyInputConfirmClick);

	//查看模式，禁用属性
	var palette = ${palette};
	if(!palette) {
		$("#delNodeTemplate").hide();
		$("#nodeTemplateInformationSection").find("input").attr("disabled", true);
	}
});
</script>