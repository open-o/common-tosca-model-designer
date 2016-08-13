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

/**
 * This file contains supporting functions for the topoplogy modeler
 */
define(
	// although XMLWriter ist not an AMD module, requirejs does not complain when loading it
	["winery-support-common", "XMLWriter", "winery-datatable"],
	function(w) {
		// has to be consistent with {@link org.eclipse.winery.common.constants.Namespaces}
		var TOSCA_NAMESPACE = "http://docs.oasis-open.org/tosca/ns/2011/12";
		var TOSCA_WINERY_EXTENSIONS_NAMESPACE = "http://www.opentosca.org/winery/extensions/tosca/2013/02/12";

		var topologyTemplateURL;
		var repositoryURL;
		var serviceTemplateURL;
		var serviceTemplateNamespace;
		var serviceTemplateName;

		var module = {
			save: save,
			setTopologyTemplateURL: function(url) {
				topologyTemplateURL = url;
			},
			//用于设置新增nodetype的url
			setRepositoryURL: function(url) {
				repositoryURL = url;
			},
			setServiceTemplateURL: function(url) {
				serviceTemplateURL = url;
			},
			setServiceTemplateNamespace: function(ns) {
				serviceTemplateNamespace = ns;
			},
			setServiceTemplateName: function(name) {
				serviceTemplateName = name;
			},

			getTopologyTemplateAsXML: getTopologyTemplateAsXML,

			TOSCA_NAMESPACE: TOSCA_NAMESPACE,
			TOSCA_WINERY_EXTENSIONS_NAMESPACE: TOSCA_WINERY_EXTENSIONS_NAMESPACE
		};
		return module;

		function writeReqOrCaps(elements, xmlw, globalWrapperElementName, singleElementWrapperName) {
			if (elements.length != 0) {
				xmlw.writeStartElement(globalWrapperElementName);

				$.each(elements, function(i, e) {
					xmlw.writeStartElement(singleElementWrapperName);
					e = $(e);
					xmlw.writeAttributeString("id", e.children(".id").text());
					xmlw.writeAttributeString("name", e.children(".name").text());
					writeType(xmlw, e.children(".type").children("a").data("qname"));
					savePropertiesFromDivToXMLWriter(e.children("div.propertiesContainer"), xmlw);
					xmlw.writeEndElement();
				});

				xmlw.writeEndElement();
			}

		}

		/**
		 * "doSave"
		 */
		function save() {
			$("#saveBtn").button('loading');

			//校验数据合法性
			if(!validate()) {
				$("#saveBtn").button('reset');
				return;
			}

			$.ajax({
				url: topologyTemplateURL,
				type: "PUT",
				contentType: 'text/xml',
				data: getTopologyTemplateAsXML(false),
				success: function(data, textStatus, jqXHR) {
					$("#saveBtn").button('reset');

					saveBoundaryInputsParam();
					saveBoundaryMetaDataParam();
					//根据servicetemplate的substitutableNodeType创建nodetype
					//getSubstitutableNodeType();

					saveServiceTemplateParams();

					vShowSuccess("successfully saved.");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#saveBtn").button('reset');
					vShowAJAXError("Could not save", jqXHR, errorThrown);
				}
			});
		}

		/**
		 * Creates an XML String of the modelled topology template.
		 */
		function getTopologyTemplateAsXML(needsDefinitionsTag) {

			var xmlw = new XMLWriter("utf-8");
			xmlw.writeStartDocument();

			if (needsDefinitionsTag) {
				xmlw.writeStartElement("Definitions");
				xmlw.writeAttributeString("xmlns", TOSCA_NAMESPACE);
				xmlw.writeAttributeString("xmlns:winery", TOSCA_WINERY_EXTENSIONS_NAMESPACE);

				xmlw.writeStartElement("ServiceTemplate");
				xmlw.writeAttributeString("xmlns", TOSCA_NAMESPACE);
				xmlw.writeAttributeString("xmlns:winery", TOSCA_WINERY_EXTENSIONS_NAMESPACE);
			}
			xmlw.writeStartElement("TopologyTemplate");
			xmlw.writeAttributeString("xmlns", TOSCA_NAMESPACE);
			xmlw.writeAttributeString("xmlns:winery", TOSCA_WINERY_EXTENSIONS_NAMESPACE);
			$("div.NodeTemplateShape").not(".hidden").each(function() {
				xmlw.writeStartElement("NodeTemplate");

				var id = $(this).attr("id");

				var headerContainer = $(this).children("div.headerContainer");
				var name = headerContainer.children("div.name").text();
				var typeQNameStr = headerContainer.children("span.typeQName").text();
				var minmaxdiv = headerContainer.children("div.minMaxInstances");
				var min = minmaxdiv.children("span.minInstances").text();
				var max = minmaxdiv.children("span.maxInstances").text();
				if (max == "∞") {
					max = "unbounded";
				}
				var x = $(this).css("left");
				x = x.substring(0, x.indexOf("px"));
				var y = $(this).css("top");
				y = y.substring(0, y.indexOf("px"));

				xmlw.writeAttributeString("id", id);
				if (name != "") {
					xmlw.writeAttributeString("name", name);
				}
				writeType(xmlw, typeQNameStr);
				if (min != "") {
					xmlw.writeAttributeString("minInstances", min);
				}
				if (max != "") {
					xmlw.writeAttributeString("maxInstances", max);
				}
				xmlw.writeAttributeString("winery:x", x);
				xmlw.writeAttributeString("winery:y", y);

				/** documentation **/
				writeDocumentation(xmlw, headerContainer.children(".documentation"));

				/** Properties **/
				savePropertiesFromDivToXMLWriter($(this).children("div.propertiesContainer"), xmlw);

				/** Requirements **/
				writeReqOrCaps(
					$(this).children("div.requirementsContainer").children("div.content").children("div.reqorcap"),
					xmlw,
					"Requirements",
					"Requirement");

				/** Capabilities **/
				writeReqOrCaps(
					$(this).children("div.capabilitiesContainer").children("div.content").children("div.reqorcap"),
					xmlw,
					"Capabilities",
					"Capability");

				/** Policies **/
				w.writeCollectionDefinedByATextArea(xmlw,
					$(this).children("div.policiesContainer").children("div.content").children("div.policy"),
					"Policies");

				/** Deployment Artifacts **/
				var das = $(this).children("div.deploymentArtifactsContainer").children("div.content").children("div.deploymentArtifact");
				if (das.length != 0) {
					xmlw.writeStartElement("DeploymentArtifacts");
					das.each(function(i, e) {
						// the textarea contains a valid deployment artifact xml
						var xml = $(e).children("textarea").val();
						xmlw.writeXML(xml);
					});
					xmlw.writeEndElement();
				}

				// End: Nodetemplate
				xmlw.writeEndElement();
			});
			jsPlumb.select().each(function(connection) {
				xmlw.writeStartElement("RelationshipTemplate");
				var id = connection.id;
				var typeQNameStr = connection.getType()[0];

				var connData = winery.connections[id];
				if (!connData) {
					vShowError("Error in the internal data structure: Id " + id + " not found");
					return;
				}

				xmlw.writeAttributeString("id", connData.id);
				if (connData.name != "") {
					xmlw.writeAttributeString("name", connData.name);
				}
				writeType(xmlw, typeQNameStr);

				if (typeof connData.propertiesContainer !== "undefined") {
					savePropertiesFromDivToXMLWriter(connData.propertiesContainer, xmlw);
				}

				xmlw.writeStartElement("SourceElement");
				if (connData.req) {
					// conn starts at a requirement
					xmlw.writeAttributeString("ref", connData.req);
				} else {
					// conn starts at a node template
					xmlw.writeAttributeString("ref", connection.sourceId);
				}
				xmlw.writeEndElement();
				xmlw.writeStartElement("TargetElement");
				if (connData.cap) {
					// conn ends at a capability
					xmlw.writeAttributeString("ref", connData.cap);
				} else {
					// conn ends at a node template
					xmlw.writeAttributeString("ref", connection.targetId);
				}
				xmlw.writeEndElement();

				xmlw.writeEndElement();
			});

			// 遍历cp和vnfc，判断有重叠时添加连线
			$("#drawingarea .div_cp").each(function() {
				var cpID = $(this).attr("id");
				$("#drawingarea .div_vnfc").each(function() {
					var vnfcID = $(this).attr("id");
					if (isOverlap(cpID, vnfcID)) {
						xmlw.writeStartElement("RelationshipTemplate");

						var type = $("#" + cpID).children("div.endpointContainer").children().first().children("div.connectorLabel").text();
						var nameSpace = $("#" + cpID).children("div.headerContainer").children("span.typeNamespace").text();
						var id_name = "con_" + RndNum(2);
						xmlw.writeAttributeString("id", id_name);
						xmlw.writeAttributeString("name", id_name);
						xmlw.writeAttributeString("own", "1");

						xmlw.writeAttributeString("xmlns:xsd", nameSpace);
						xmlw.writeAttributeString("type", "xsd:" + type);

						xmlw.writeStartElement("SourceElement");
						xmlw.writeAttributeString("ref", cpID);
						xmlw.writeEndElement();

						xmlw.writeStartElement("TargetElement");
						xmlw.writeAttributeString("ref", vnfcID);
						xmlw.writeEndElement();

						xmlw.writeEndElement();
					}
				});
			});


			if (needsDefinitionsTag) {
				xmlw.writeEndElement();
				xmlw.writeEndElement();
			}

			xmlw.writeEndDocument();

			return xmlw.flush();
		}

		function writeQNameAttribute(w, nsPrefix, qnameStr) {
			var qname = getQName(qnameStr);
			w.writeAttributeString("xmlns:" + nsPrefix, qname.namespace);
			w.writeAttributeString("type", nsPrefix + ":" + qname.localName);
		}

		function writeType(w, typeQNameStr) {
			writeQNameAttribute(w, "ty", typeQNameStr);
		}

		/**
		 * 写入documentation
		 * @param  {[type]} w             [description]
		 * @param  {[type]} documentation [description]
		 * @return {[type]}               [description]
		 */
		function writeDocumentation(w, documentation) {
			var doc = documentation.text();
			w.writeStartElement("documentation");
			w.writeString(doc);
			w.writeEndElement();
		}

		/**
		 * 保存模板参数的输入参数
		 * @return {[type]} [description]
		 */
		function saveBoundaryInputsParam() {
			var inputsTableData = $("#boundaryInputsTable").dataTable().fnGetData();
			var inputsURL = serviceTemplateURL + "/boundarydefinitions/properties/inputs/list";
			var inputsParam = {
				inputs: inputsTableData
			}
			saveBoundaryDefinitionsData(inputsURL, inputsParam);
		}

		/**
		 * 保存模板参数的元数据
		 * @return {[type]} [description]
		 */
		function saveBoundaryMetaDataParam() {
			var metaDataTableData = $("#boundaryMetaDataTable").dataTable().fnGetData();
			var metaDataURL = serviceTemplateURL + "/boundarydefinitions/properties/metadata/list";
			var metaDataParam = {
				metadatas: metaDataTableData
			}
			saveBoundaryDefinitionsData(metaDataURL, metaDataParam);
		}

		/**
		 * 保存boundarydefinition
		 * @param  {[type]} url  [description]
		 * @param  {[type]} data [description]
		 * @return {[type]}      [description]
		 */
		function saveBoundaryDefinitionsData(url, data) {
			$.ajax({
				url: url,
				type: "POST",
				async: false,
				data: JSON.stringify(data),
				contentType: "application/json",
				success: function(resp) {

				}
			});
		}

		/**
		 * 获取servicetemplate的substitutableNodeType，如果存在值，则为该servicetemplate创建nodetype
		 * @return {[type]} [description]
		 */
		function getSubstitutableNodeType() {
			var substitutableNodeTypeURL = serviceTemplateURL + "/substitutableNodeType";
			$.ajax({
				type: "GET",
				url: substitutableNodeTypeURL,
				success: function(resp) {
					if (resp) {
						createNodeTypeDetail(resp);
					}
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log("Could not get substitutableNodeType");
				}
			});
		}

		/**
		 * 创建nodetype，保存servicetemplate的时候，要为该模板创建一个nodetype，该servicetemplate才能
		 * 作为nodetemplate被嵌套使用
		 * @return {[type]} [description]
		 */
		function createNodeTypeDetail(nodeTypeQname) {
			var namespace = nodeTypeQname.substring(nodeTypeQname.indexOf("{") + 1, nodeTypeQname.indexOf("}"));
			var name = nodeTypeQname.substring(nodeTypeQname.indexOf("}") + 1);
			var nodeType = {
				name: name,
				namespace: namespace
			}

			//查询指定nodetype是否存在URL
			var queryNodeTypeURL = repositoryURL + "/nodetypes/" + encodeID(nodeType.namespace) + "/" + nodeType.name;
			//创建指定nodetype的winerysPropertiesDefinition对象URL
			var nodeTypePropertiesDefinitionURL = queryNodeTypeURL + "/propertiesdefinition/winery";

			$.ajax({
				type: "GET",
				url: queryNodeTypeURL,
				success: function(resp) {
					//已经存在该nodetype，无需再创建
					setKVPropertiesOnServer(queryNodeTypeURL, nodeTypePropertiesDefinitionURL);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log("Could not get Node Type");
				}
			});
		}

		/**
		 * 创建指定nodetype的winerysPropertiesDefinition对象，有了这个对象才能添加属性
		 * @param  {[type]} url [description]
		 * @return {[type]}                  [description]
		 */
		function setKVPropertiesOnServer(queryNodeTypeURL, nodeTypePropertiesDefinitionURL) {
			$.ajax({
				type: "POST",
				url: nodeTypePropertiesDefinitionURL,
				contentType: "application/json",
				success: function(resp) {
					//添加模板参数定义的属性
					addNodeTypeProperty(nodeTypePropertiesDefinitionURL);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					vShowAJAXError("Could change to custom key/value pairs", jqXHR, errorThrown);
				}
			});
		}

		/**
		 * 为nodetype添加属性定义，servicetemplate中模板参数设置的输入参数和元数据作为nodetype的属性定义
		 * @param {[type]} nodeTypePropertiesDefinitionURL [description]
		 */
		function addNodeTypeProperty(nodeTypePropertiesDefinitionURL) {
			//inputs
			var inputURL = serviceTemplateURL + "/boundarydefinitions/properties/inputs";
			$.ajax({
				type: "GET",
				url: inputURL,
				async: false,
				dataType: "json",
				success: function(resp) {
					//循环创建nodetype的属性，接口只支持一个一个创建属性
					var inputParams = resp || [];

					$.each(inputParams, function(index, param) {
						var property = {
							key: param.name,
							type: "xsd:string"
						}
						sendPostToAddProperty(nodeTypePropertiesDefinitionURL, property);
					});
				},
				error: function(jqXHR, textStatus, errorThrown) {
					vShowAJAXError("Query input params error", jqXHR, errorThrown);
				}
			});

			//metadata
			var metaDataURL = serviceTemplateURL + "/boundarydefinitions/properties/metadata";
			$.ajax({
				type: "GET",
				url: metaDataURL,
				dataType: "json",
				success: function(resp) {
					//循环创建nodetype的属性，接口只支持一个一个创建属性
					var metaDataParams = resp || [];

					$.each(metaDataParams, function(index, param) {
						var property = {
							key: param.key,
							type: "xsd:string",
							tag: "attribute"
						}
						sendPostToAddProperty(nodeTypePropertiesDefinitionURL, property);
					});
				},
				error: function(jqXHR, textStatus, errorThrown) {
					vShowAJAXError("Query metadata params error", jqXHR, errorThrown);
				}
			});
		}

		function sendPostToAddProperty(nodeTypePropertiesDefinitionURL, property) {
			var addPropertyURL = nodeTypePropertiesDefinitionURL + "/list";
			$.ajax({
				type: "POST",
				url: addPropertyURL,
				async: false,
				contentType: "application/json",
				data: JSON.stringify(property),
				success: function(resp) {

				}
			});
		}

		/**
		 * 保存服务模板其他参数
		 * @return {[type]} [description]
		 */
		function saveServiceTemplateParams() {
			var desc = $("#boundaryDefinition .menuContainerHead input").val();
			var docObj = {
				documentation: desc
			}
			var documentationURL = serviceTemplateURL + "/extendProperties";
			$.ajax({
				type: "POST",
				url: documentationURL,
				contentType: "application/json",
				data: JSON.stringify(docObj),
				success: function(resp) {

				}
			});
		}

		/**
		 * 校验数据是否必填，包括nodetemplate的属性和服务模板的元数据
		 * @return {[type]} [description]
		 */
		function validate() {
			return validateNodeTemplateProperties() && validateServiceTemplateMetaData();
		}

		/**
		 * 校验节点必填属性
		 * @return {[type]} [description]
		 */
		function validateNodeTemplateProperties() {
			var nodeTemplates = $("div.NodeTemplateShape").not(".hidden");
			for(var i=0;i<nodeTemplates.length;i++) {
				var propertyTrs = $(nodeTemplates[i]).children("div.propertiesContainer").find("tr");
				for(var j=0;j<propertyTrs.length;j++) {
					var $property = $(propertyTrs[j]).children("td");
					var $propertyValue = $property.children(".KVPropertyValue");
					var propertyRequired = $property.children(".KVPropertyRequired").text();
					if(propertyRequired == "true" && $propertyValue.hasClass("editable-empty")) {
						//必填属性没有值
						var nodeTemplateName = $(nodeTemplates[i]).children(".fullName").text();
						var propertyName = $property.children(".KVPropertyKey").text();
						vShowError("保存失败，节点" + nodeTemplateName + "的必填属性" 
							+ propertyName + "属性值为空");
						return false;
					}
				}
			}
			return true;
		}

		/**
		 * 校验模板参数中元数据必填项
		 * @return {[type]} [description]
		 */
		function validateServiceTemplateMetaData() {
			var metaDataTableData = $("#boundaryMetaDataTable").dataTable().fnGetData();
			for(var i=0;i<metaDataTableData.length;i++) {
				var required = metaDataTableData[i].required;
				var value = metaDataTableData[i].value;
				if(required == "true" && !value) {
					var key = metaDataTableData[i].key;
					vShowError("保存失败，元数据必填项" + key + "值为空");
					return false;
				}
			}
			return true;
		}
	}
);