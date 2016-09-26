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
/**
 * This file contains supporting functions for the rendering a topology template
 */
define(
	["jsplumb", "winery-common-topologyrendering", "winery-datatable"],
	function (jsPlumb, wct, wd) {
		var module = {
			initTemplateReqAndCap: initTemplateReqAndCap,
			addReqAndCap: addReqAndCap,
			delReqAndCap: delReqAndCap
		};

		return module;

		function getNode(nodeTemplateShape) {
			var id = nodeTemplateShape.attr("id");
			var name = nodeTemplateShape.find(".fullName").text();
			var requirements = nodeTemplateShape.find(".requirementsContainer .requirements");
			var capabilities = nodeTemplateShape.find(".capabilitiesContainer .capabilities");
			var node = {
				id : id,
				name : name,
				requirements : requirements,
				capabilities : capabilities
			}
			return node;
		}

		function getNodes(nodeTemplateShapeSet) {
			var nodes = [];
			$.each(nodeTemplateShapeSet, function(index, nodeTemplateShape){
				var node = getNode($(nodeTemplateShape));
				nodes.push(node);
			});
			return nodes;
		}

		function getTableData(tableId) {
			var dataTable = $("#" + tableId).dataTable();
			var data = dataTable.fnGetData();
			return data;
		}

		function updateTableData(tableId, tableData) {
			var dataTable = $("#" + tableId).dataTable();
			dataTable.fnClearTable();
			dataTable.fnAddData(tableData);
		}

		function getIndexFromObjectArray(sessionType, reqAndCapArray) {
			reqAndCapArray = reqAndCapArray || [];
			for(var i=0;i<reqAndCapArray.length;i++) {
				if(sessionType == reqAndCapArray[i].sessionType) {
					return i;
				}
			}
			return -1;
		}

		function getServiceTempalteName() {
			return $("#boundaryDefinition h4").text();
		}

		/**
		 * 检查如果两个节点已经存在连线了，则不需要再连线
		 * @param  {[type]} sourceId [description]
		 * @param  {[type]} targetId [description]
		 * @return {[type]}          [description]
		 */
		function checkConnections(sourceId, targetId) {
			var connections = jsPlumb.getConnections();
			for(var i=0; i<connections.length; i++) {
				if(connections[i].sourceId == sourceId && connections[i].targetId == targetId) {
					return false;
				}
			}
			return true;
		}

		/**
		 * 从req或者是cap的properties中获取sessionType属性值，目前properties只会有这一个值
		 * sessionType用于连线
		 * @param  {[type]} reqorcapContainer [description]
		 * @return {[type]}                   [description]
		 */
		function extractValueFromProperties(reqorcapContainer) {
			var sessionType = reqorcapContainer
					.find(".propertiesContainer .KVPropertyValue").text();
			return sessionType;
		}

		/**
		 * 初始化所有的nodeTemplate连线，以及计算req和cap
		 * @param  {[type]} nodeTemplateShapeSet [description]
		 * @return {[type]}                      [description]
		 */
		function initTemplateReqAndCap(nodeTemplateShapeSet) {
			//初始化所有节点连线，相当于是把节点一个一个添加进来
			var nodeTemplates = [];
			$.each(nodeTemplateShapeSet, function(index, nodeTemplateShape){
				addReqAndCap($(nodeTemplateShape), nodeTemplates);
				nodeTemplates.push(nodeTemplateShape);
			});
		}

		/**
		 * 新增加一个nodetemplate，需要根据该node的requirements和capabilities来
		 * 计算node之间的关系，剩余没有关系的requirements和capabilities作为该servicetemplate的
		 * requirements和capabilities
		 * @param {[type]} newNodeTemplateShape [description]
		 * @param {[type]} nodeTemplateShapeSet [description]
		 */
		function addReqAndCap(newNodeTemplateShape, nodeTemplateShapeSet) {
			//新添加的nodetemplate
			var newNode = getNode(newNodeTemplateShape);
			//界面上存在的所以nodetemplate
			var allNodes = getNodes(nodeTemplateShapeSet);
			//servicetemplate的requirements
			var templateRequirements = getTableData("boundaryRequirementsTable");
			//servicetemplate的capabilities
			var templateCapabilites = getTableData("boundaryCapabilitiesTable");
			//临时变量，用于循环
			var tempRequirements = templateRequirements.concat();
			//临时变量，用于循环
			var tempCapabilites = templateCapabilites.concat();

			//循环requirements, 计算servicetemplate的requirements和capabilities
			$.each(newNode.requirements, function(reqIndex, req){
				//自动连线
				var reqSessionType = extractValueFromProperties($(req));
				$.each(allNodes, function(ndIndex, node){
					if(newNode.id != node.id) {
						var caps = node.capabilities;
						$.each(caps, function(capIndex, cap){
							var capSessionType = extractValueFromProperties($(cap));
							if(reqSessionType && capSessionType && (reqSessionType == capSessionType)) {
								if(checkConnections(newNode.id, node.id)) {
									var c = jsPlumb.connect({
										source: newNode.id,
										target: node.id,
										type:"{http://www.open-o.org/tosca/nfv/2015/12}tosca.relationships.nfv.ConnectsTo"
									});
									wct.handleConnectionCreated(c);
								}
							}
						});
					}
				});
				
				var isConnect = false;
				$.each(tempCapabilites, function(capIndex, cap){
					if(reqSessionType == cap.sessionType) {
						isConnect = true;
						//templateCapabilites内容变化了，需要重新获取index用于删除
						var index = getIndexFromObjectArray(reqSessionType, templateCapabilites);
						templateCapabilites.splice(index, 1);
					}
				});
				if(!isConnect) {
					//var index = getIndexFromObjectArray(reqSessionType, templateRequirements);
					//if(index == -1) {
						var reqId = $(req).find(".id").text();
						var type = $(req).find(".name").text();
						var serviceTemplateName = getServiceTempalteName();
						var reqObj = {
							nodeName : newNode.name,
							type : type,
							ref : reqId,
							name : serviceTemplateName + reqId,
							sessionType : reqSessionType
						}
						templateRequirements.push(reqObj);
					//}
				}
			});

			//循环capabilities, 计算servicetemplate的requirements和capabilities
			$.each(newNode.capabilities, function(capIndex, cap){
				//自动连线
				var capSessionType = extractValueFromProperties($(cap));
				$.each(allNodes, function(ndIndex, node){
					if(newNode.id != node.id) {
						var reqs = node.requirements;
						$.each(reqs, function(reqIndex, req){
							var reqSessionType = extractValueFromProperties($(req));
							if(capSessionType && reqSessionType && (capSessionType == reqSessionType)) {
								//连线是从req连到cap，故这里连线顺序与上面相反
								if(checkConnections(node.id, newNode.id)) {
									var c = jsPlumb.connect({ 
										source: node.id,
										target: newNode.id,
										type:"{http://www.open-o.org/tosca/nfv/2015/12}tosca.relationships.nfv.ConnectsTo"
									});
									wct.handleConnectionCreated(c);
								}
							}
						});
					}
				});
				
				var isConnect = false;
				$.each(tempRequirements, function(reqIndex, req){
					if(capSessionType == req.sessionType) {
						isConnect = true;
						//templateCapabilites内容变化了，需要重新获取index用于删除
						var index = getIndexFromObjectArray(capSessionType, templateRequirements);
						templateRequirements.splice(index, 1);
					}
				});	
				if(!isConnect) {
					//var index = getIndexFromObjectArray(capSessionType, templateCapabilites);
					//if(index == -1) {
						var capId = $(cap).find(".id").text();
						var type = $(cap).find(".name").text();
						var serviceTemplateName = getServiceTempalteName();
						var capObj = {
							nodeName : newNode.name,
							type : type,
							ref : capId,
							name : serviceTemplateName + capId,
							sessionType : capSessionType
						}
						templateCapabilites.push(capObj);
					//}
				}			
			});

			//把requirements数据更新到boundary的对应表格中
			updateTableData("boundaryRequirementsTable", templateRequirements);
			//把capabilities数据更新到boundary的对应表格中
			updateTableData("boundaryCapabilitiesTable", templateCapabilites);
		}

		/**
		 * 删除nodetemplate，需要重新计算servicetemplate的requirements和capabilities
		 * @param  {[type]} nodeTemplateShape [description]
		 * @return {[type]}                   [description]
		 */
		function delReqAndCap(nodeTemplateShape) {
			//删除的nodetemplate
			var node = getNode(nodeTemplateShape);
			//servicetemplate的requirements
			var templateRequirements = getTableData("boundaryRequirementsTable");
			//servicetemplate的capabilities
			var templateCapabilites = getTableData("boundaryCapabilitiesTable");

			//根据删除node的capabilities，计算servicetemplate的requirements和capabilities
			$.each(node.capabilities, function(index, cap){
				var capSessionType = extractValueFromProperties($(cap));
				var index_cap = getIndexFromObjectArray(capSessionType, templateCapabilites);
				var index_req = getIndexFromObjectArray(capSessionType, templateRequirements);
				if(index_cap > -1) {
					templateCapabilites.splice(index_cap, 1);
				} else if(index_req == -1) {
					var capId = $(cap).find(".id").text();
					var type = $(cap).find(".name").text();
					var serviceTemplateName = getServiceTempalteName();
					var capObj = {
						nodeName : node.name,
						type : type,
						ref : capId,
						name : serviceTemplateName + capId,
						sessionType : capSessionType
					}
					templateRequirements.push(capObj);
				}
			});

			//根据删除node的requirements，计算servicetemplate的requirements和capabilities
			$.each(node.requirements, function(index, req){
				var reqSessionType = extractValueFromProperties($(req));
				var index_req = getIndexFromObjectArray(reqSessionType, templateRequirements);
				var index_cap = getIndexFromObjectArray(reqSessionType, templateCapabilites);
				if(index_req > -1) {
					templateRequirements.splice(index_req, 1);
				} else if(index_cap == -1) {
					var reqId = $(req).find(".id").text();
					var type = $(req).find(".name").text();
					var serviceTemplateName = getServiceTempalteName();
					var reqObj = {
						nodeName : node.name,
						type : type,
						ref : reqId,
						name : serviceTemplateName + reqId,
						sessionType : reqSessionType
					}
					templateCapabilites.push(reqObj);
				}
			});

			//把requirements数据更新到boundary的对应表格中
			updateTableData("boundaryRequirementsTable", templateRequirements);
			//把capabilities数据更新到boundary的对应表格中
			updateTableData("boundaryCapabilitiesTable", templateCapabilites);
		}
	}
);