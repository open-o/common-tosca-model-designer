/*
 * Copyright 2016 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function(Application){

	Application.registerElement(Application.View.Element.extend({

		css: function(){
			return {
		        "-webkit-border-radius": "8px",
		        "-moz-border-radius": "8px",
		        "border-radius": "8px",
		        "border": "2px solid #000",
		        "font-size": "10px",
		        "padding": "10px 15px"
  			};
		},

	


		dialog: function(){
			var dialog = new Application.View.Dialog({
				id: (this.model.get("id") + "_dialog"),
				model: this.model,
				title: "Edit Public Rest Task"
			});
			dialog.on("confirm", function(event){
				var publicInterface = this.$el.find("#publicInterface")[0].selectize.options[this.$el.find("#publicInterface")[0].selectize.getValue()];
				
				var generateVariableInfo = function(id) {
					return "_bpelVar_" + id;
				};

				var paramParser = function(parent, node) {
					var type = node.rp_rest_type;
					var key = node.rp_key;
					var value = node.rp_value;
					var bpelVarType = node.rp_bpel_type;

					if(type == "string" || type == "number") {
						var varibaleKey = generateVariableInfo(node.id);
						parent[key] = varibaleKey;

						var bodyParam = {"type":bpelVarType, "value":value, "jsonType":type, "id":node.id};
						root.varParam[parent[key]] = bodyParam;
					} else {
						if((typeof value) != "undefined" && "" != value) {
							var varibaleKey = generateVariableInfo(node.id);

							if(parent instanceof Array) {
								parent.push(varibaleKey);
							} else {
								parent[key] = varibaleKey;
							}

							var bodyParam = {"type":bpelVarType, "value":value, "jsonType":type};
							root.varParam[varibaleKey] = bodyParam;
						} else {
							var obj;
							if(type == "Array") {
								obj = [];
							} else {
								obj = {};
							}
							for(var i=0, len=node.children.length; i<len; i++) {
								paramParser(obj, node.children[i]);
							}

							if(parent instanceof Array) {
								parent.push(obj);
							} else {
								parent[key] = obj;
							}
						}
					} 
				};

				

				var root = {varParam:{}};
				if($('#restParam').length == 0) {
					
				} else {
					var treeObj = $.fn.zTree.getZTreeObj("restParam");
					var node = treeObj.getNodeByTId("restParam_1");
					paramParser(root, node);
				}


				var data = {
					name: event.dialog.$el.find("#name").val(),
					microservice: event.dialog.$el.find("#microservice").val(),
					publicInterface: event.dialog.$el.find("#publicInterface").val(),
					method: publicInterface.method,
					accept : publicInterface.accept,
					contentType:publicInterface.contentType,
					url : publicInterface.url,
						
					path: _.object(_.map(event.dialog.$el.find(".parameter.path"), function(parameter){
						var dialogParameter = Backbone.$(parameter).data("parameter");
						return [dialogParameter.name(), dialogParameter.toJSON()];
					})),
					query: _.object(_.map(event.dialog.$el.find(".parameter.query"), function(parameter){
						var dialogParameter = Backbone.$(parameter).data("parameter");
						return [dialogParameter.name(), dialogParameter.toJSON()];
					})),
					body: root.varParam,
					requestBody:root.param,
					output: _.object(_.map(event.dialog.$el.find(".parameter.output"), function(parameter){
						var dialogParameter = Backbone.$(parameter).data("parameter");
						return [dialogParameter.name(), dialogParameter.toJSON()];
					}))
				};
				this.model.set(data);

			});
			dialog.on("show", function(event){
				event.dialog.$el.find("form").append(""
					+"<div class=\"form-group\">"
						+"<label for=\"\" class=\"col-sm-3 control-label\">Name</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"name\" name=\"name\" value=\"" + this.model.get("name") + "\" /></div>"
					+"</div>"

					+"<div class=\"form-group\">"
						+"<label for=\"microservice\" class=\"col-sm-3 control-label\">Micro Service</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"microservice\" name=\"microservice\" /></div>"
					+"</div>"
					
					+"<div class=\"form-group\">"
						+"<label for=\"publicInterface\" class=\"col-sm-3 control-label\">Public Interface</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"publicInterface\" name=\"publicInterface\" /></div>"
					+"</div>"

					+"<hr/>"
				+"");
			});
			dialog.on("shown", function(event){
				var interfaceDropdown = event.dialog.$el.find("#publicInterface").selectize({
					maxItems: 1,
					options: []
				})[0].selectize;

				var initParam = function (bodyParam, model, paramElement) {

						var addDiyDom = function(treeId, treeNode) {
							// 添加类型
							var typeStr = "<span id='restParam_" +treeNode.id+ "_rpType'>[" + treeNode.rp_rest_type + "]</span>";
							var icoName = $("#" + treeNode.tId + "_ico");
							icoName.after(typeStr);

							// 添加 冒号
							var aObj = $("#" + treeNode.tId + "_a");
							var editStr = "<span id='restParam_" +treeNode.id+ "_colons'>:</span>";
							aObj.append(editStr);

							// 添加value
							var editStr = "<span id='restParam_" +treeNode.id+ "_rpValue'>" + treeNode.rp_value + "</span>";
							aObj.append(editStr);
						};

						var addSubNode = function(parentId, node) {
							node.id = nodeId;
							nodeId ++;
							node.pId = parentId;
							nodeMap["" + node.id] = node;

							if(node.rp_rest_type == "Array" || node.rp_rest_type == "object") {
								for(var i=0, len=node.children.length; i<len;i++) {
									addSubNode(node.id, node.children[i]);
								}
							}
						};

						var zTreeOnRemove = function (event, treeId, treeNode) {
							var nodes = treeNode.getParentNode().children;
							var currentId = treeNode.id;

							for(var i=0, len=nodes.length; i<len; i++) {
								if(nodes[i].id > currentId) {
									var tmp = parseInt(nodes[i].name) - 1;
									nodes[i].name = "" + tmp;
									$('#restParam_' + nodes[i].id + '_span').text(nodes[i].name);
								}
							}

						};

						var addHoverDom = function(treeId, treeNode) {
							if ($("#diyBtn_"+treeNode.id).length>0) return;
							$.fn.zTree.getZTreeObj("restParam").setting.edit.showRemoveBtn = false;

							var aObj = $("#" + treeNode.tId + "_a");
							

							// 添加编辑按钮
							var paramElement = new Application.View.DialogParameterTree(
								_.extend(
									{editable: false, type: "string"}, 
									{
										direction: "input",
										model: model,
										name: treeNode.name,
										sources: ["concat", "string", "plan", "object"],
										type: treeNode.rp_bpel_type,
										value : treeNode.rp_value
									}
								)
							).render().el;

							aObj.after(paramElement);
							$("#" + treeNode.tId + "_a + div").attr("id", 'diyBtn_' + treeNode.id).css({ "display": "inline-block", "margin-top": "-19px", "width":"60%" });
							var btn = $("#diyBtn_"+treeNode.id + " input");
							if (btn) btn.bind("change", function(){
								var bpelVariableType = $("#diyBtn_"+treeNode.id + " input").attr("data-type");

								var val = $("#diyBtn_"+treeNode.id + " input").val();
								if(treeNode.rp_rest_type =="number" && bpelVariableType == "string" && isNaN(val)) {
									$("#diyBtn_" + treeNode.id + " .input-group").addClass("has-error");
								} else {
									$("#diyBtn_" + treeNode.id + " .input-group").removeClass("has-error");
									$("#restParam_" + treeNode.id+ "_rpValue").text(val);
									treeNode.rp_bpel_type = bpelVariableType;
									treeNode.rp_value = val;
								}

							});


							if(treeNode.rp_rest_type == "Array") {
								// 数组节点增加添加子节点按钮
								if (treeNode.editNameFlag || $("#diyBtn_"+treeNode.id + "_add").length>0) return;
								var addStr = "<span class='button add' id='diyBtn_" + treeNode.id
									+ "_add' title='add node' onfocus='this.blur();'></span>";
								aObj.append(addStr);
								var addBtn = $("#diyBtn_"+treeNode.id + "_add");
								if (addBtn) addBtn.bind("click", function(){
									var node = nodeMap[treeNode.id].children[0];
									if(treeNode.children) {
										node.name = "" + treeNode.children.length;
									} else {
										node.name = "0";
									}
									addSubNode(treeNode.id, node);

									var zTree = $.fn.zTree.getZTreeObj("restParam");
									zTree.addNodes(treeNode, node);
									return false;
								});

							} else if(treeNode.getParentNode() && treeNode.getParentNode().rp_rest_type == "Array") {
								// 数组节点的子节点，添加删除按钮
								$.fn.zTree.getZTreeObj("restParam").setting.edit.showRemoveBtn = true;
							}
							
						};

						var removeHoverDom = function(treeId, treeNode) {

							if(treeNode.rp_rest_type == "Array") {
								$("#diyBtn_" + treeNode.id + "_add").unbind().remove();
								$.fn.zTree.getZTreeObj("restParam").setting.edit.showRemoveBtn = false;
							}

							$("#diyBtn_"+treeNode.id + " input").unbind();
							$("#diyBtn_"+treeNode.id).remove();
						};

						// var param = {
						//   "oid": "string",
						//   "moc": "string",
						//   "mocName": "string",
						//   "name": "string",
						//   "ipAddress": "string",
						//   "positionX": 0,
						//   "positionY": 0,
						//   "vendor": "string",
						//   "version": "string",
						//   "relations": [
						//     {
						//       "resourceId": "string",
						//       "resourceType": "string",
						//       "relatedId": "string",
						//       "relationType": "string"
						//     }
						//   ],
						//   "vimId": "string",
						//   "cpuNum": 0,
						//   "memorySize": 0,
						//   "storageSize": 0,
						//   "customPara": "string",
						//   "monitoringParameter": "string"
						// };


						var createParamNode = function(key, value) {
							var node = {
								"name":key, "rp_key":key, "open":true,
								"rp_bpel_type":"string", "rp_rest_type":"", "rp_value":"", 
							};

							var type = typeof value;
							if (type == "string" || type == "number") {
								node.rp_rest_type = type;
								node.rp_value = value;
								return node;
							}

							if(value instanceof Array) {
								node.rp_rest_type = "Array";
								node.children = [];

								for(var i=0, len=value.length; i<len; i++) {
									var child = createParamNode("" + i, value[i]);
									node.children[i] = child;
								}

								return node;
							}

							if(type == "object") {
								node.rp_rest_type = "object";
								node.children = [];

								for(var childKey in value) {
									var child = createParamNode(childKey, value[childKey]);
									node.children[node.children.length] = child;
								}

								return node;
							}
						};

						var nodeId = 1;
						var nodeMap = {};

						var changeParam2Node = function (key, value, param, variables) {
							var node = {
								"name":key, "id":"" + nodeId, "open":true, 
								"rp_key":key, "rp_bpel_type":"string", "rp_value":""
							};
							nodeId +=1;
							nodeMap[node.id] = createParamNode(key, param);

							var type = typeof value;
							if (type == "string") {
								if(value.indexOf("_bpelVar_") != -1) {
									var varInfo = variables[value];  // 获取变量信息 {"type":bpelVarType, "value":value, "jsonType":type, "id":node.id}
									node.rp_rest_type = varInfo.jsonType;
									node.rp_value = varInfo.value;
									node.rp_bpel_type = varInfo.type;

									if(varInfo.jsonType == "object") {
										node.children = [];

										for(var childKey in param) {
											var child = changeParam2Node(childKey, param[childKey], param[childKey], variables);
											node.children[node.children.length] = child;
										}
									}
								} else {
									node.rp_rest_type = type;
									node.rp_value = value;
								}
								return node;
							} else if (type == "number") {
								node.rp_rest_type = type;
								node.rp_value = value;
								return node;
							}

							if(value instanceof Array) {
								node.rp_rest_type = "Array";
								node.children = [];

								for(var i=0, len=value.length; i<len; i++) {
									var child = changeParam2Node("" + i, value[i], param[0], variables);
									node.children[i] = child;
								}

								return node;
							}

							if(type == "object") {
								node.rp_rest_type = "object";
								node.children = [];

								for(var childKey in value) {
									var child = changeParam2Node(childKey, value[childKey], param[childKey], variables);
									node.children[node.children.length] = child;
								}

								return node;
							}
						};

						var setting = {
							edit:{
								enable:true,
								showRenameBtn:false,
								showRemoveBtn:false
							},
							view:{
								addHoverDom: addHoverDom,
								removeHoverDom: removeHoverDom,
								addDiyDom:addDiyDom
							},
							callback: {
								onRemove: zTreeOnRemove
							}
						};

						var nodes;
						var bodyParamJson = JSON.parse(bodyParam.replace(/\s/g,""));
						if(model.attributes.requestBody) {
							nodes = changeParam2Node("param", model.attributes.requestBody, bodyParamJson, model.attributes.body);
						} else {
							nodes = changeParam2Node("param", bodyParamJson, bodyParamJson, null);
						}
						$.fn.zTree.init($(paramElement), setting, nodes);

					};



				interfaceDropdown.on("change", _.bind(function(value){
					if(this.$el.find("#publicInterface")[0].selectize.getValue()){
						var microservice = this.$el.find("#microservice")[0].selectize.options[this.$el.find("#microservice")[0].selectize.getValue()];
						var publicInterface = this.$el.find("#publicInterface")[0].selectize.options[this.$el.find("#publicInterface")[0].selectize.getValue()];
						this.$el.find(".parameter, hr, .ztree").remove();

						this.model.collection.options.msb.microServiceInterfaceParameter(microservice.value, publicInterface.value, _.bind(function(parameters){
							_.each({"input": {editable: false, type: "string"}, "output": {editable: false}}, function(constraints, type){
								if(parameters[type].length > 0) event.dialog.$el.find("form").append("<hr/>");

								var bodyParam = null;
								_.each(parameters[type], function(parameter){
									// var parameter = this.model.has(type) ? this.model.get(type)[name] : false;
									
									if(parameter.in == "body") {
										bodyParam = parameter;
									} else {
										var direction = (type == "input"? parameter.in:"output");
										event.dialog.$el.find("form").append(new Application.View.DialogParameter(_.extend(constraints, {
											direction: direction,
											model: this.model,
											name: parameter.name,
											sources: (type == "output" ? ["topology", "plan"] : ["concat", "string", "plan"]),
											type: (type == "output" ? "topology" : "string" )
										})).render().el);
									}

								}, this);

								if(bodyParam) {
									event.dialog.$el.find("form").append("<hr/>");
									event.dialog.$el.find("form").append('<ul class="ztree ' + type + '" id="restParam"></ul>');
									initParam(bodyParam.sample, this.model, event.dialog.$el.find("form").find(".ztree." + type));
								}
								
							}, this);
							
						}, this));

						if(this.$el.find("#name").val().match(/^Unnamed|^$/i)) this.$el.find("#name").val(publicInterface.text + " " + microservice.text);
					}
				}, event.dialog));
				

				var microserviceDropdown = event.dialog.$el.find("#microservice").selectize({
					maxItems: 1,
					options: [],
				})[0].selectize;

				microserviceDropdown.on("change", _.bind(function(value){
					var microserviceSelectize = this.$el.find("#microservice")[0].selectize;
					var microservice = microserviceSelectize.options[microserviceSelectize.getValue()];
					var publicInterfaceSelectize = this.$el.find("#publicInterface")[0].selectize;

					this.model.collection.options.msb.microserviceInterfaces(microservice.value, function(publicInterfaces){
						interfaceDropdown.clearOptions();
						_.each(publicInterfaces, function(publicInterface){
							interfaceDropdown.addOption({text: publicInterface.name, id: publicInterface.id, value: publicInterface.id, method: publicInterface.method, accept : publicInterface.accept, contentType : publicInterface['Content-Type'], url : publicInterface.url});					
						});
						interfaceDropdown.refreshOptions(false);
						interfaceDropdown.setValue(event.dialog.model.get("publicInterface"));									
					});

				}, event.dialog));

				event.dialog.model.collection.options.msb.microservices(function(microservices){
					microserviceDropdown.clearOptions();
					_.each(microservices, function(microservice){
						microserviceDropdown.addOption({text: microservice.name, id: microservice.id, namespace: microservice.namespace, value: microservice.id});					
					});
					microserviceDropdown.refreshOptions(false);
					microserviceDropdown.setValue(event.dialog.model.get("microservice"));					
				});
				
			});
			dialog.show();
		}

	}, {type: "RestTask"}));

})(window.BPMN4TOSCAModeler);