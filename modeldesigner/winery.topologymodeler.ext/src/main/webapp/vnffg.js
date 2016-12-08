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
 	function goBack(url) {
		url = url + "?edit";	
 		if(vnffgConstants.PARAM_TYPE) {
 			url = url + "&type=" + vnffgConstants.PARAM_TYPE
 		}

 		window.location.href=url;
 	}

	var vnffgConstants = {

		RELATIONSHIP_FORWARDTO_TYPE : "tosca.relationships.nfv.ForwardsTo",
		RELATIONSHIP_FORWARDTO_NAMESPACE : "http://www.zte.com.cn/tosca/nfv/vnffg",
		RELATIONSHIP_VIRTUAL_LINKSTO : "tosca.relationships.nfv.VirtualLinksTo",
		NODETPYE_FORWARD_PATH_NAMESPACE : "http://www.zte.com.cn/tosca/nfv/vnffg/fp",
		NODETPYE_VNF_NAMESPACE : "http://www.zte.com.cn/tosca/nfv/vnf",

		_URL_BASE : "/modeldesigner/servicetemplates/http%253A%252F%252Fwww.zte.com.cn%252Ftosca%252Fnfv%252Fns/{nsId}",
		URL_BASE : "",
		_URL_NODETEMPLATE_REQUIREMENT : "/topologytemplate/nodetemplates/{nodeTmepateId}/extrequirements",
		URL_NODETEMPLATE_REQUIREMENT : "",
		_URL_NODETEMPLATE_PROPERTIES : "/topologytemplate/nodetemplates/{nodeTmepateId}/properties/propertiesmap",
		URL_NODETEMPLATE_PROPERTIES : "",
		URL_NODETEMPLATE_CREATE : "/topologytemplate/nodetemplates/create",
		URL_NODETEMPLATE : "/topologytemplate/nodetemplates",


		URL_VNFFG : "/grouptemplates/",
		_URL_VNFFG_TARGET:"/grouptemplates/{groupId}/target",
		URL_VNFFG_TARGET : "",
		_URL_VNFFG_PROPERTIES:"/grouptemplates/{groupId}/properties/propertiesmap",
		URL_VNFFG_PROPERTIES:"",

		NODETYPES : [],
		

		NS_ID : "",
		CURRENT_GROUP_ID : "",
		CURRENT_PATH_ID : "",
		PARAM_TYPE : "",

		CURRENT_NODE_TYPE: {},

		VNFFG_INFO:{},
		NODETEMPLATES : [],
		NODETEMPLATE_VL : [],
		NFPS : []
	};


	var REQ = {
		_success : function(resData, textStatus, jqXHR) {
					var resp = resData;
					vShowSuccess("success", "success");
		},
		_error : function(message) {
			message = message || "call backend service fail";
			return function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError(message, jqXHR, errorThrown);
			};
		},
		_request : function(type, url, data, successFunc, errorFunc, accept, contentType) {
			var param = {
				type: type,
				url: url,
				contentType: contentType || "application/json",
				data : data,
				success: successFunc || REQ._success,
				error: errorFunc || REQ._error()
			};

			if(accept) {
				param.dataType = accept;
			}

			$.ajax(param);
		},

		createPath : function(nodeTemplate, successFunc) {
			var url = vnffgConstants.URL_BASE + vnffgConstants.URL_NODETEMPLATE_CREATE;
			REQ._request("POST", url, JSON.stringify(nodeTemplate), successFunc, REQ._error("Create path error"));
		}, 

		deletePath : function(name, successFunc) {
			var url = vnffgConstants.URL_BASE + vnffgConstants.URL_NODETEMPLATE + "/"+ name;
			REQ._request("DELETE", url, null, successFunc, REQ._error("Could not get count of artifact template usage"));
		},

		getNodeType : function(nodeType) {
			var url = "/modeldesigner/nodetypes/http%253A%252F%252Fwww.zte.com.cn%252Ftosca%252Fnfv%252Fvnffg%252Ffp/" + nodeType + "/detail/";

			var successFunc = function(resData, textStatus, jqXHR) {
				vnffgConstants.CURRENT_NODE_TYPE = resData;
			};

			var errorMsg = "Get nodeType info error:[" + nodeType + "]";

			REQ._request("GET", url, null, successFunc, REQ._error(errorMsg), "json");
		},

		getNodeTypes : function (successFunc) {
			var url = "/modeldesigner/nodetypes";
			REQ._request("GET", url, null, successFunc, REQ._error("getNodeTypes error"), "json");
		},

		saveRequirements : function (requirements, successFunc) {
			var url = vnffgConstants.URL_BASE + vnffgConstants.URL_NODETEMPLATE_REQUIREMENT;
			REQ._request("POST", url, JSON.stringify(requirements), successFunc, REQ._error("Save requirements error"));
		},

		saveNodeTemplateProperties : function(properties, successFunc) {
			var url = vnffgConstants.URL_BASE + vnffgConstants.URL_NODETEMPLATE_PROPERTIES;
			REQ._request("POST", url, JSON.stringify(properties), successFunc, REQ._error("save NodeTemplate Properties error"));
		}, 



		getNodeTemplateInfo : function(pathName, successFunc) {
			var url = vnffgConstants.URL_BASE + vnffgConstants.URL_NODETEMPLATE + "/" + pathName;
			REQ._request("GET", url, null, successFunc, REQ._error("getNodeTemplateInfo error"), "json");
		},

		getNodeTemplates : function(successFunc) {
	    	var url = vnffgConstants.URL_BASE + vnffgConstants.URL_NODETEMPLATE;
	    	REQ._request("GET", url, null, successFunc, REQ._error("getNodeTemplates error"), "json");
		},

		addNfp2Vnffg : function (pathName, successFunc) {
			var url = vnffgConstants.URL_BASE + vnffgConstants.URL_VNFFG_TARGET;
			
			REQ._request("POST", url, pathName, successFunc, REQ._error("Save requirements error"));
		},

		deleteNfpFromVnffg : function (pathName, successFunc) {
			var url = vnffgConstants.URL_BASE + vnffgConstants.URL_VNFFG_TARGET;
			
			REQ._request("DELETE", url, pathName, successFunc, REQ._error("delete Nfp From Vnffg error"));
		},

		getVnffg : function (successFunc) {
	    	var url = vnffgConstants.URL_BASE + vnffgConstants.URL_VNFFG + "/" + vnffgConstants.CURRENT_GROUP_ID;
	    	REQ._request("GET", url, null, successFunc, REQ._error("getVnffg error"), "json");
	    },

	    getVnffgProperties : function(successFunc) {
	    	var url = vnffgConstants.URL_BASE + vnffgConstants.URL_VNFFG_PROPERTIES;
	    	REQ._request("GET", url, null, successFunc, REQ._error("getVnffgProperties error"), "json");
	    },

	    saveVnffgProperties : function(properties) {
	    	var url = vnffgConstants.URL_BASE + vnffgConstants.URL_VNFFG_PROPERTIES;

	    	REQ._request("POST", url, JSON.stringify(properties), null, REQ._error("save Vnffg Properties error"));
	    }

	};


	function showAddNfpDialog() {
		$("#addNFPDialog").modal("show");
	}

	// add new vnf 
	function addNfp() {
		var name = $('#nfpName').val();
		var nodeType = $('#nodeTypeSelect').val();

		var pathExist = false;
		if(vnffgConstants.NFPS) {
			$.each(vnffgConstants.NODETEMPLATES, function(index, nodeTemplate) {
				if(nodeTemplate.name == name) {
					pathExist = true;
				}
			});
		}

		if(pathExist) {
			vShowError("node name already exists, please change path name", "error");
			return ;
		}

		var nodeTemplate = {
			"id": name,
			"name":name,
			"type":nodeType,
			"namespace" :  vnffgConstants.NODETPYE_FORWARD_PATH_NAMESPACE
		};

		$("#addNFPDialog").modal("hide");


		vnffgConstants.CURRENT_PATH_ID = name;
		REQ.getNodeType(nodeType);
		vnffgConstants.URL_NODETEMPLATE_REQUIREMENT = vnffgConstants._URL_NODETEMPLATE_REQUIREMENT.replace("{nodeTmepateId}", vnffgConstants.CURRENT_PATH_ID);
		vnffgConstants.URL_NODETEMPLATE_PROPERTIES = vnffgConstants._URL_NODETEMPLATE_PROPERTIES.replace("{nodeTmepateId}", vnffgConstants.CURRENT_PATH_ID);
		$('#selectedPath').text(name);
		
		var successFunc = function(resData, textStatus, jqXHR) {
				REQ.addNfp2Vnffg(vnffgConstants.CURRENT_PATH_ID, loadVnffgInfo);
				clearPath();	// hide the exitsted path
				
				vShowSuccess("add Path success[" + name + "]", "success");

				createPathDiv();
			};

		REQ.createPath(nodeTemplate, successFunc);
	}


	function createPathDiv() {
		var successFunc = function(resData, textStatus, jqXHR) {
			var data = {};
			data.template = resData;
			data.template.properties = {any:[]};
			data.type = vnffgConstants.CURRENT_NODE_TYPE;
			generateNodeTemplateDiv(data);

			editNfpAttri();
		};

		REQ.getNodeTemplateInfo(vnffgConstants.CURRENT_PATH_ID, successFunc);

	}


	function dragHandler(newObj, event) {
		newObj.removeClass("ui-draggable");
		newObj.removeClass("ui-droppable");
		// newObj.removeClass("hidden");

		function generateReqOrCapId(nodeTemplate, className, count) {
			var reqOrCaps = nodeTemplate.find(className + "Container")
					.children(".content").children(className);
			$.each(reqOrCaps, function(index, reqOrCap){
				var newId = $(reqOrCap).attr("id") + count;
				$(reqOrCap).attr("id", newId).children(".id").text(newId);
			});
		}

		// generate and set id
		var type = newObj.find("div.type.nodetemplate").text();
		var id = vnffgConstants.CURRENT_PATH_ID;
		// we cannot use the id as the initial name, because we want to preserve special characters in the name, but not in the id.
		var name = vnffgConstants.CURRENT_PATH_ID;
		
		newObj.attr("id", id);
		newObj.children("div.headerContainer").children("div.id").text(id);

		// initial name has been generated based on the id
		//newObj.children("div.headerContainer").children("div.name").text(name);
		var $nodeName = newObj.children("div.headerContainer").children("div.name");
		if(!$nodeName.text()) {
			$nodeName.text(name);
			newObj.children(".fullName").text(name);
		}

		// fix main.css -> #editorArea -> margin-top: 45px;
		var top = 100;
		var left =100;
		if(event) {
			top = Math.max(event.pageY-45, 0);
			// drag cursor is at 112/40
			// fix that
			top = Math.max(top-40, 0);
			left = Math.max(event.pageX-112, 0);
		}
		newObj.css("top", top);
		newObj.css("left", left);

		newObj.addClass("selected");

		// insert into sheet
		newObj.appendTo( $( "div#drawingarea" ) );

		// initialization works only for displayed objects
		require(["winery-common-topologyrendering"], function(wct) {
			wct.initNodeTemplate(newObj, true);

			// handle menus
			winery.events.fire(winery.events.name.SELECTION_CHANGED);
		});
	}


	// generate a new node template div while create nfp
	function generateNodeTemplateDiv(data) {
		var newNodeTemplate = $("#newNodeTemplate .NodeTemplateShape").clone();

		var nodeTemplate = data.template;
		var name = nodeTemplate.name;
		newNodeTemplate.find(".iconContainer").attr("name", name);
		newNodeTemplate.find(".fullName").text(name);
		var headerContainer = newNodeTemplate.children(".headerContainer");
		headerContainer.find(".name").text(name);

		//set nodeTemplate value
		var typeQName = nodeTemplate.type;
		var namespace = typeQName.substring(typeQName.indexOf("{") + 1, typeQName.indexOf("}"));
		var localName = typeQName.substring(typeQName.indexOf("}") + 1);
		var nodeTypeCSSName = namespace + "_" + localName;
		nodeTypeCSSName = nodeTypeCSSName.replace(/[^A-Za-z0-9]/g, "_");
		newNodeTemplate.addClass(nodeTypeCSSName);
		headerContainer.find(".typeQName").text(typeQName);
		headerContainer.find(".typeNamespace").text(namespace);
		headerContainer.find(".type").text(localName);
		//set image
		if(namespace.indexOf("/ns") > -1) {
			newNodeTemplate.find("img").attr("src", "images/ns.png");
		} else if(namespace.indexOf("/service") > -1) {
			newNodeTemplate.find("img").attr("src", "images/service.png");
		}

		//set document
		var documentation = nodeTemplate.documentation;
		if(documentation.length && documentation[0].content.length) {
			var documentation = documentation[0].content[0];
			headerContainer.children(".documentation").text(documentation);
		}

		//set nodeTemplate properties
		var propertyDefinitions = data.type.any[0];
		if(propertyDefinitions && propertyDefinitions.propertyDefinitionKVList) {
			var propertyDefinitionKVList = propertyDefinitions.propertyDefinitionKVList;
			var properties = nodeTemplate.properties.any;
			var propertiesHtml = "";
			$.each(propertyDefinitionKVList, function(index, property){
				//set value of properties
				var value = properties[property.key] || property.value;
				if(!value) {
					value = "";
				}
				var validValue = "";
				if(property.constraint) {
					validValue = property.constraint.validValue || "";
				}
				propertiesHtml += '<tr class="KVProperty">'
					+ '<td><span class="' + property.key + ' KVPropertyKey">' + property.key 
					+ '</span></td><td><a class="KVPropertyValue" href="#" data-type="text"' 
					+ 'data-title="Enter ' + property.key + '">' + value
					+ '</a></td><td><span class="KVPropertyType">' + property.type + '</span></td>'
					+ '<td><span class="KVPropertyTag">' + property.tag + '</span></td>'
					+ '<td><span class="KVPropertyRequired">' + property.required + '</span></td>'
					+ '<td><span class="KVPropertyValidValue">' + validValue + '</span></td>'
					+ '</tr>';
			});
			var propertiesContainer = newNodeTemplate.children(".propertiesContainer");
			propertiesContainer.find("tbody").html(propertiesHtml);

			var elementName = propertyDefinitions.elementName;
			var content = propertiesContainer.children(".content").attr("name", "Properties");			
			content.children(".elementName").text(elementName);
			var propertyNamespace = propertyDefinitions.namespace;
			content.children(".namespace").text(propertyNamespace);
		} else {
			//remove property node while there is no property, or there will an error
			newNodeTemplate.children(".propertiesContainer").remove();
		}

		//set Requirements
		var generateKVProperty = function(properties) {
			var propertiesHtml = "";
			$.each(properties, function(key, value){
				propertiesHtml += '<tr class="KVProperty">'
					+ '<td><span class="' + key + ' KVPropertyKey">' + key 
					+ '</span></td><td><a class="KVPropertyValue" href="#" data-type="text"' 
					+ 'data-title="Enter ' + key + '">' + value
					+ '</a></td></tr>';
			});
			return propertiesHtml;
		}
		var generateReqOrCap = function(elements, container, content) {
			$.each(elements, function(index, element) {
				var requirement = container.clone();
				requirement.find(".id").text(element.id);
				requirement.find(".name").text(element.name);
				requirement.find(".type a").attr("data-qname", element.type);

				requirement.find(".elementName").text(elementName);
				requirement.find(".namespace").text(propertyNamespace);
				if(element.properties && element.properties.any) {
					var propertiesHtml = generateKVProperty(element.properties.any);
					requirement.find(".propertiesContainer tbody").html(propertiesHtml);
				}

				content.append(requirement);
			});
		}

		var requirementsContent = newNodeTemplate.find(".requirementsContainer>.content");
		var requirementsContainer = requirementsContent.children().clone();
		requirementsContent.html("");
		if(nodeTemplate.requirements) {
			var requirements = nodeTemplate.requirements.requirement || [];
			generateReqOrCap(requirements, requirementsContainer, requirementsContent);
		}
		
		//set Capabilities
		var capabilitiesContent = newNodeTemplate.find(".capabilitiesContainer>.content");
		var capabilitiesContainer = capabilitiesContent.children().clone();
		capabilitiesContent.html("");
		if(nodeTemplate.capabilities) {
			var capabilities = nodeTemplate.capabilities.capability || [];
			generateReqOrCap(capabilities, capabilitiesContainer, capabilitiesContent);
		}

		//add nodeTemplate to page
		dragHandler(newNodeTemplate);	
	}




	function addPath2Table(name, nodeType, desc) {
		// var operationHtml = 
		// 		'<i class="fa fa-edit icon-del" style="margin-right:3px;" onclick="editNfp(\'' + name + '\', \'' + nodeType + '\')"></i>'
		// 		// '<i class="fa fa-check icon-del" name_i18n="winery_i18n" style="margin-right:3px;"  onclick="editNfp(\'' + name + '\', \'' + nodeType + '\')"></i>'
		// 	  	+ '<i class="fa fa-trash icon-del" onclick="deleteNfp(\'' + name + '\')"></i>';


		// var tr = 
		// '<tr>'  
	 //      +'<td>' + name + '</td>'
	 //      +'<td>' + nodeType + '</td>'
	 //      +'<td>' + desc + '</td>'
	 //      +'<td>'
	 //      	// + '<a href="javascript:void(0)" onclick="editNfp(\'' + name + '\', \'' + nodeType + '\')">Edit</a>'
	 //      	// + '<a href="javascript:void(0)" onclick="deleteNfp(\'' + name + '\')">Delete</a>'
	 //      	+ operationHtml
	 //      +'</td>'
	 //    +'</tr>';
		// $('#nfpTable tbody').append(tr);
	}

	function editNfpAttri() {
		if(vnffgConstants.CURRENT_PATH_ID) {
			winery.events.fire(winery.events.name.command.UNSELECT_ALL_NODETEMPLATES);
			$("#" + vnffgConstants.CURRENT_PATH_ID).addClass("selected");
			winery.events.fire(winery.events.name.SELECTION_CHANGED);
		} else {
			vShowError("please select nfp first", "error");
		}
	}


	/**
	 * editNfp
	 * @param  {[type]} nfpId [description]
	 * @return {[type]}       [description]
	 */
	function editNfp(nfpId) {

		vnffgConstants.CURRENT_PATH_ID = nfpId;
		vnffgConstants.URL_NODETEMPLATE_REQUIREMENT = vnffgConstants._URL_NODETEMPLATE_REQUIREMENT.replace("{nodeTmepateId}", vnffgConstants.CURRENT_PATH_ID);
		vnffgConstants.URL_NODETEMPLATE_PROPERTIES = vnffgConstants._URL_NODETEMPLATE_PROPERTIES.replace("{nodeTmepateId}", vnffgConstants.CURRENT_PATH_ID);

		clearPath();	// hide path collection

		$('#selectedPath').text(nfpId);

		var successFunc = function(resData, textStatus, jqXHR) {


			connectPath(resData);	// repain path

			var type = resData.type;
			typeName = type.substring(1).split("}")[1];

			editNfpAttri();

			REQ.getNodeType(typeName);
		};

		REQ.getNodeTemplateInfo(nfpId, successFunc);
	}

	/**
	 * delete path
	 * @param  {[type]} name [description]
	 * @return {[type]}      [description]
	 */
	function deleteNfp(name) {
		var successFunc = function() {
			// update vnffg info
			REQ.deleteNfpFromVnffg(name, updateVnffgProperties);
			$('#' + name).remvoe();
			$('#' + name + "Path").remvoe();
			vShowSuccess("delete Path success[" + name + "]", "success");
		};

		REQ.deletePath(name, successFunc);
	}


	function savePath() {
		if(vnffgConstants.CURRENT_PATH_ID == "") {
			vShowError("please choose path first", "error");
			return;
		}

		if(validatePathTemplate() && validateNodeTemplateProperties()) {
			savePathProperties(savePathRequirement);
		}
	}

	function savePathRequirement() {
		var forwardConnections = [];
		jsPlumb.select().each(function(connection) {
			var connData = winery.connections[connection.id];
			if(connData.nsAndLocalName.localname == vnffgConstants.RELATIONSHIP_FORWARDTO_TYPE) {
				forwardConnections[forwardConnections.length] = connection;
			}
		});
		
		var sortedConnections = computePathSequence(forwardConnections);
		var requirements = createRequirements(sortedConnections);

		REQ.saveRequirements(requirements, updateVnffgProperties);
	}


	function savePathProperties(successFunc) {
		var properties = {};

		$("#" + vnffgConstants.CURRENT_PATH_ID + " .propertiesContainer div[name='Properties']  tr").each(function(index, element) {
			var key = $(element).find(".KVPropertyKey").text();
			var value = $(element).find(".KVPropertyValue").text();
			properties[key] = value;
		});


		REQ.saveNodeTemplateProperties(properties, successFunc);
	}

	function validatePathTemplate() {
		var flag = true;

		var connectionMap = {};
		var connections = [];
		jsPlumb.select().each(function(connection) {
			var id = connection.id;
			var connData = winery.connections[id];
			if(connData.nsAndLocalName.localname == vnffgConstants.RELATIONSHIP_FORWARDTO_TYPE) {
				connections[connections.length] = connection;
				// check whether the index repeat
				var pathIndex = $(connection.getOverlay("label").getElement()).text();
				if(!pathIndex) {
					flag = false;
					vShowError("please set path index first");
				} else {
					if(connectionMap[pathIndex]) {
						vShowError("path index duplicat:" + pathIndex);
						flag = false;
					} else {
						connectionMap[pathIndex] = connection;
					}
				}
				

				// check source and target 
				var tmp = validateConnectionSourceTarget(connection, connData);
				if(!tmp) {
					flag = false;
				}
			}
		});

		if(!flag) {
			return flag;
		}

		for(var i=0,len=connections.length; i<len; i++) {
			var index = i + 1;
			if(!connectionMap["" + index]) {
				vShowError("path index " + index + " not set");
				return false;
			}
		}

		return flag;
	}

	function validateConnectionSourceTarget(connection, connData) {

		var sourceNamespace = $(connection.source).children('.headerContainer').children('span.typeNamespace').text();
		if(sourceNamespace == vnffgConstants.NODETPYE_VNF_NAMESPACE) { 
			if (!connData.req) {
				vShowError("please set the source of the path");
				return false;
			}
		}

		var targetNamespace = $(connection.target).children('.headerContainer').children('span.typeNamespace').text();;
		if(targetNamespace == vnffgConstants.NODETPYE_VNF_NAMESPACE) { 
			if (!connData.cap) {
				vShowError("please set the source of the path");
				return false;
			}
		}

		return true;
	}


	/**
	 * node property validate
	 * @return {[type]} [description]
	 */
	function validateNodeTemplateProperties() {
		var nodeTemplate = $("#" + vnffgConstants.CURRENT_PATH_ID);
		var propertyTrs = $(nodeTemplate).children("div.propertiesContainer")
					.children("[name='Properties']").find("tr");
		for(var j=0;j<propertyTrs.length;j++) {
			var $property = $(propertyTrs[j]).children("td");
			var $propertyValue = $property.children(".KVPropertyValue");
			var propertyRequired = $property.children(".KVPropertyRequired").text();
			if(propertyRequired == "true" && $propertyValue.hasClass("editable-empty")) {
				var nodeTemplateName = $(nodeTemplate).children(".fullName").text();
				var propertyName = $property.children(".KVPropertyKey").text();
				vShowError($.i18n.prop("winery-template-validate-nodeproperties-required", nodeTemplateName, propertyName));
				return false;
			}
		}
	return true;
	}



	
	/**
	 * get connect points of current path
	 * @param  {[type]} connections [description]
	 * @return {[type]}             [description]
	 */
	function createRequirements(connections) {
		var tmp = vnffgConstants.CURRENT_NODE_TYPE.requirementDefinitions.requirementDefinition[0].requirementType.substring(1).split("}");
		var requirementType = {"namespace" :tmp[0], "type" : tmp[1]};
		var requirements = [];

		$.each(connections, function(index, connection){
			var connData = winery.connections[connection.id];
			
			// source
			var sourceId = vnffgConstants.CURRENT_PATH_ID + index + "s";
			var sourceRequirement = createRequirement(sourceId, requirementType, connection.sourceId, connData.req);
			requirements[requirements.length] = sourceRequirement;
			

			// target
			var targetId = vnffgConstants.CURRENT_PATH_ID + index + "t";
			var sourceRequirement = createRequirement(targetId, requirementType, connection.targetId, connData.cap);
			requirements[requirements.length] = sourceRequirement;
		});

		// remove duplicate cps
		var result = [];
		$.each(requirements, function(index, requirement) {
			if(index == 0) {
				result[0] = requirement;
			} else {
				var preRequirement = result[result.length-1];
				if(preRequirement.node == requirement.node && preRequirement.capability == requirement.capability) {
					// duplicate cp in and out from the same cp skip
				} else {
					result[result.length] = requirement;
				}
			}
		});


		return result;
	}

	
	/**
	 * create requirement param
	 * @param  {[type]} id                [requirement id]
	 * @param  {[type]} relationType      [forward type]
	 * @param  {[type]} relationNameSpace [forward namespace]
	 * @param  {[type]} nodeId            [nodeId]
	 * @param  {[type]} capabilityId      [capabilityId]
	 * @return {[type]}                   [requirement]
	 */
	function createRequirement(id, requirementType, nodeId, capabilityId) {
		var nodeName = $('#' + nodeId + ' .headerContainer .name').text();
		// source
		var requirement = {"id":id, "name":"forwarder", "type":requirementType.type, "namespace":requirementType.namespace, "node":nodeName};
		
		var capabilityName = null;
		var nodeNamespace = $('#' + nodeId + ' span.typeNamespace').text();
		if(vnffgConstants.NODETPYE_VNF_NAMESPACE == nodeNamespace) {
			capabilityName = $('#' + capabilityId + " div.name").text();
			requirement.capability = capabilityName;
		}

		return requirement;
	}




	
	function computePathSequence(connections) {
		var connectionMap = {};
		$.each(connections, function(index, connection) {
			var pathIndex = $(connection.getOverlay("label").getElement()).text();
			connectionMap[pathIndex] = connection;
		});

		var sortedConnections = [];
		for(var i=0, len=connections.length; i<len; i++) {
			var index = i + 1;
			sortedConnections[i] = connectionMap["" + index];
		}
		return sortedConnections;
	}



	// get the path's node sequence
	/**
	 * getPathSequence
	 * @param  {[type]} connectionMap     [description]
	 * @param  {[type]} startPoint        [description]
	 * @param  {[type]} sortedConnections [description]
	 * @return {[type]}                   [description]
	 */
	function getPathSequence(connectionMap, startPoint, sortedConnections) {
		var connection = connectionMap[startPoint];
		if(connection) { // 
			sortedConnections[sortedConnections.length] = connection;
			getPathSequence(connectionMap, connection.targetId, sortedConnections);
		}
	}
	
	/**
	 * repaint path on page
	 * @param  {[type]} nodeTemplate [description]
	 * @return {[type]}              [description]
	 */
	function connectPath(nodeTemplate) {
		if(nodeTemplate.requirements && nodeTemplate.requirements.requirement) {
			var requirements = nodeTemplate.requirements.requirement;
			var label = 1;
			for(var index=0, len=requirements.length; index<len; index++) {
				if(index == 0) {

				} else {
					if(requirements[index-1].otherAttributes.node != requirements[index].otherAttributes.node) {
						connectNode(requirements[index-1], requirements[index], "" + label);
						label = label + 1;
					}
				}
				
			}
		}
	}

	function getIdByNodeName(nodeName) {
		var node = null;
		$(".NodeTemplateShape").each(function(index, nodeTemplate) {
			var tmp = $(nodeTemplate).find(".name.nodetemplate").text();
			if(tmp == nodeName) {
				node = nodeTemplate;
			}
		});

		return node;
	}

	function connectNode(source, target, label) {
		var sourceId = $(getIdByNodeName(source.otherAttributes.node)).find(".id.nodetemplate").text();
		var targetId = $(getIdByNodeName(target.otherAttributes.node)).find(".id.nodetemplate").text();

		require(["winery-common-topologyrendering"], function(wct) {
			var c = jsPlumb.connect({
				source: sourceId,
				target: targetId,
				type:"{"+ vnffgConstants.RELATIONSHIP_FORWARDTO_NAMESPACE + "}" + vnffgConstants.RELATIONSHIP_FORWARDTO_TYPE
			});
			wct.handleConnectionCreated(c);
			c.getOverlay("label").setLabel(label);

			var sourceReq = source.otherAttributes.capability;
			if(sourceReq) {
				var elements = $('#' + sourceId + ' .capabilities .name:contains("' + sourceReq + '")');
				$.each(elements, function(index, element) {
					if(sourceReq == $(element).text()) {
						var sourceReqId = $(element).siblings(".id").text();
						winery.connections[c.id].req = sourceReqId;
					}
				});
				
			}

			var targetCap = target.otherAttributes.capability;
			if(targetCap) {
				var elements = $('#' + targetId + ' .capabilities .name:contains("' + targetCap + '")');
				$.each(elements, function(index, element) {
					if(targetCap == $(element).text()) {
						var targetCapId = $(element).siblings(".id").text();
						winery.connections[c.id].cap = targetCapId;
					}
				});
			}
		});
	}



	/**
	 * Erase path
	 * @return {[type]} [description]
	 */
	function clearPath() {
		$.each(jsPlumb.getConnections(), function(index, connection){
			var id = connection.id;

			var connData = winery.connections[id];
			if (!connData) {
				vShowError("Error in the internal data structure: Id " + id + " not found");
				return;
			}

			if(connData.nsAndLocalName.localname == vnffgConstants.RELATIONSHIP_FORWARDTO_TYPE) {
				jsPlumb.detach(connection);
			}
		});
	}


	function updateVnffgProperties() {

		var getVnffgPropertiesSuccessFunc = function(resData, textStatus, jqXHR) {
			if(!resData) { // property is empty
				resData = {
					"number_of_endpoints" : 0,
					"dependent_virtual_link" : [],
					"connection_point" : [],
					"constituent_vnfs" : []
				};
			}
			var vlNameArray = [];
			$.each(vnffgConstants.NODETEMPLATE_VL, function(index, nodeTemplate) {
				vlNameArray[index] = nodeTemplate.name;
			});

			var vnfMap = {};
			var vnfArray = [];
			var cpMap = {};
			var cpArray = [];
			$.each(vnffgConstants.NFPS, function(index, nodeTemplate) {
				if(nodeTemplate.requirements && nodeTemplate.requirements.requirement) {
					$.each(nodeTemplate.requirements.requirement, function(index, requirement) {
						if(requirement.otherAttributes.capability) { // VNF
							if(!vnfMap[requirement.otherAttributes.node]) {
								vnfMap[requirement.otherAttributes.node] = true;
								vnfArray[vnfArray.length] = requirement.otherAttributes.node;
							}
						} else { // CP
							if(!cpMap[requirement.otherAttributes.node]) {
								cpMap[requirement.otherAttributes.node] = true;
								cpArray[cpArray.length] = requirement.otherAttributes.node;
							}
						}
					});
				}
			});

			resData.dependent_virtual_link = JSON.stringify(vlNameArray);
			resData.constituent_vnfs = JSON.stringify(vnfArray);
			resData.connection_point = JSON.stringify(cpArray);
			resData.number_of_endpoints = cpArray.length;

			REQ.saveVnffgProperties(resData);
		};


		var getNodeTemplatesSuccessFunc = function(resData, textStatus, jqXHR) {
			getNfpOfGroup(resData, textStatus, jqXHR);

			REQ.getVnffgProperties(getVnffgPropertiesSuccessFunc);
		};

		REQ.getNodeTemplates(getNodeTemplatesSuccessFunc);
	}


	function getAllVnfOfVnffg() {
		var vnfs = {};
		var vnfArray = [];

		$.each(vnffgConstants.NFPS, function(nfpIndex, nfp) {
			$.each(nfp.requirements, function(requirementIndex, requirement) {
				if(!requirement.capability) {
					if(!vnfs[requirement.node]) {
						vnfArray[vnfArray.length] = requirement.node;
					}
					vnfs[requirement.node] = true;
				}
			});
		});

		return vnfArray;
	}

	/**
	 * getForwardNodeTypes
	 * @return {[type]} [description]
	 */
	function getForwardNodeTypes() {
		if (vnffgConstants.NODETYPES == null || vnffgConstants.NODETYPES.length == 0) {
			// 
		} else {
			$.each(vnffgConstants.NODETYPES, function(index, nodeType) {
				if(nodeType.namespace == vnffgConstants.NODETPYE_FORWARD_PATH_NAMESPACE) {
					var option = '<option value="' + nodeType.id + '">' + nodeType.id + '</option>';
					$("#nodeTypeSelect").append(option);
				}
			});
		}
	}


  	/**
  	 * [getUrlParam description]
  	 * @param  {[type]} name [description]
  	 * @return {[type]}      [description]
  	 */
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); 
        var r = window.location.search.substr(1).match(reg);  
        
        if (r != null) {
        	return unescape(r[2]); 
        }

        return null; 
    }


    function loadVnffgInfo() {
    	var successFunc = function(resData, textStatus, jqXHR) {
				vnffgConstants.VNFFG_INFO = resData;
				REQ.getNodeTemplates(getNfpOfGroup);
			};
    	REQ.getVnffg(successFunc);
    }


    /**
     * get all path in group
     * @return {[type]} [description]
     */
    function getNfpOfGroup(resData, textStatus, jqXHR)  {
    	vnffgConstants.NODETEMPLATES = resData;
		getAllVL(resData);
		var paths = {};
		if(vnffgConstants.VNFFG_INFO.targets) {
			$.each(vnffgConstants.VNFFG_INFO.targets, function(index, target) {
				if(target.length > 0) {
					$.each(target, function(targetIndex, aTarget) {
						paths[aTarget] = true;
					});
					
				}
			});
		}

		vnffgConstants.NFPS = [];
		$('#nfpTable tbody').empty();
		$('#pathList').empty();
		$.each(resData, function(index, nodeTemplate) {
			if(paths[nodeTemplate.name]) {
				vnffgConstants.NFPS[vnffgConstants.NFPS.length] = nodeTemplate;
				var type = nodeTemplate.type;
				addPath2Table(nodeTemplate.name, type.substring(type.indexOf("}") + 1), getPathDesc(nodeTemplate));

				var pathHtml = 
					'<li id="' + nodeTemplate.name + 'Path">'
					+ '<a  href="javascript:void(0)">' 
						+ '<span onclick="editNfp(\'' + nodeTemplate.name + '\')">' + nodeTemplate.name + '</span>'
						+ '<i onclick="showDialogConfirmYesNo(\'delete path ' + nodeTemplate.name + ' ?\', deleteNfp, \'delete\', \'' + nodeTemplate.name + '\')"  class="fa fa-trash icon-del" style="margin-left:20px;"></i>'
					+ '</a>'
					+'</li>';

				$('#pathList').append(pathHtml);
			}

		});
    }


    function showDialogConfirmYesNo(msg, fnOnYes, title, params) {
		title = title || "Please confirm";
		$("#diagyesnotitle").text(title);
		$("#diagyesnomsg").text(msg);
		$("#diagyesnoyesbtn").off("click");
		$("#diagyesnoyesbtn").on("click", function() {
			var diag = $("#diagyesno");
			// quick hack to get fnOnYes() working -> use the hidden.bs.modal event
			diag.on("hidden.bs.modal", function() {
				fnOnYes(params);
				diag.off("hidden.bs.modal");
			});
			diag.modal("hide");
		});
		$("#diagyesno").modal("show");
	}


    function getPathDesc(nodeTemplate) {
		var desc = "";	
    	if(nodeTemplate.requirements && nodeTemplate.requirements.requirement) {
    		var requirements = nodeTemplate.requirements.requirement;
    		for(var i=0, len=requirements.length; i<len; i=i+2) {
    			if(i != 0) {
    				desc = desc + " --> ";
    			}
    			desc = desc + requirements[i].otherAttributes.node;
    		}

    		desc = desc + " --> " + requirements[requirements.length - 1].otherAttributes.node;
    	}
    	
    	return desc;
    }


    



    function getAllVL(nodeTemplates) {
    	vnffgConstants.NODETEMPLATE_VL = [];
    	$.each(nodeTemplates, function(index, nodeTemplate) {
    		// TODO need to change the way of check a node is VL
    		if(nodeTemplate.type.indexOf(".VL") != -1) {
    			vnffgConstants.NODETEMPLATE_VL[vnffgConstants.NODETEMPLATE_VL.length] = nodeTemplate;
    		}
    	});
    }


    function pathIndexHandler() {
    	// add index to path 
		var indexHtml = ''
    		+ '<div class="form-group">'
				+ '<label for="pathIndex">Index</label>'
				+ '<input id="pathIndex" class="form-control"></input>'
			+ '</div>';

    	$('#relationshipTemplateInformationSection fieldset').append(indexHtml);
    	$('#pathIndex').change(function() {
    		var index = $('#pathIndex').val();
    		var connectId = $('#relationshiptemplateid').val();
    		jsPlumb.select().each(function(connection) {
    			// var label = "<span name='sequence'>" +index + "</span><span name='condition'>" + condition + "</span>";
		// connection.getOverlay("label").setLabel(label);
		// var labelId = connection.getOverlay("label").getElement().id;
		// condition.index = $("#" + labelId + " span[name='sequence']").text();
				var tmp = winery.connections[connection.id].id
				if(tmp == connectId) {
					connection.getOverlay("label").setLabel(index);
				}
		
    		});
    	});
    }

	function vnffgInit() {
		$('#palette').hide();
		// hide unrelated relations
		$(".connectorEndpoint").hide();
		$('.http___www_zte_com_cn_tosca_nfv_vnffg_tosca_relationships_nfv_ForwardsTo').show();

		vnffgConstants.NS_ID = getUrlParam("id");
		vnffgConstants.CURRENT_GROUP_ID = getUrlParam("groupId");
		
		vnffgConstants.URL_BASE = vnffgConstants._URL_BASE.replace("{nsId}", vnffgConstants.NS_ID);
		vnffgConstants.URL_VNFFG_TARGET = vnffgConstants._URL_VNFFG_TARGET.replace("{groupId}", vnffgConstants.CURRENT_GROUP_ID);
		vnffgConstants.URL_VNFFG_PROPERTIES = vnffgConstants._URL_VNFFG_PROPERTIES.replace("{groupId}", vnffgConstants.CURRENT_GROUP_ID);

		vnffgConstants.PARAM_TYPE = getUrlParam("type");
		var successFunc = function(resData, textStatus, jqXHR) {
			vnffgConstants.NODETYPES = resData;
			getForwardNodeTypes();
		};
		REQ.getNodeTypes(successFunc);
		

		// get group info
		loadVnffgInfo();

		pathIndexHandler();

		winery.events.register(winery.events.name.command.SAVE, savePath);



		$(".NodeTemplateShape").each(function(index, element) {
    		var typeQName = $(element).find("span.typeQName").text();
    		// var type = "{http://www.zte.com.cn/tosca/nfv}tosca.nodes.nfv.ext.zte.VL";
    		if(typeQName.indexOf(".VL") != -1) {
    			$(element).hide();
    		}
    	});
	}


	vnffgInit();
