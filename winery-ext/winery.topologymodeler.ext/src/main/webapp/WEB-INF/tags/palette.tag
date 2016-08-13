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
<%@tag language="java" pageEncoding="UTF-8" description="Renders the palette on the left"%>

<%@attribute name="repositoryURL" required="true" type="java.lang.String"%>
<%@attribute name="client" required="true" description="IWineryRepository" type="org.eclipse.winery.common.interfaces.IWineryRepository"%>
<%@attribute name="relationshipTypes" description="the known relationship types" required="true" type="java.util.Collection"%>
<%@attribute name="ns" description="service template namespace" required="true" type="java.lang.String"%>

<%@tag import="javax.xml.namespace.QName" %>
<%@tag import="java.util.Collection"%>
<%@tag import="java.util.UUID"%>
<%@tag import="java.util.List"%>
<%@tag import="org.eclipse.winery.common.interfaces.IWineryRepository" %>
<%@tag import="org.eclipse.winery.model.tosca.TNodeType"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.common.Util" %>

<%@taglib prefix="nt" tagdir="/WEB-INF/tags/common/templates/nodetemplates" %>

<link rel="stylesheet" href="css/palette.css" />

<div id="palette">

<div style="height:50px;"></div>
<div id="paletteLabel">
NodeType
</div>

	<%
	Collection<TNodeType> allNodeTypes = client.getAllTypes(TNodeType.class);
	if (allNodeTypes.isEmpty()) {
	%>
		<script>
			vShowError("No node types exist. Please add node types in the repository.");
		</script>
	<%
	}
	for (TNodeType nodeType: allNodeTypes) {
		if (nodeType.getName() == null) {
			System.err.println("Invalid nodetype in ns " + nodeType.getTargetNamespace());
			continue;
		}

		if(nodeType.getDerivedFrom() != null) {
			//继承过来的nodetype不需要展示出来
			continue;
		} 

		String namespace = nodeType.getTargetNamespace();
		String nodeTypeName = nodeType.getName();
		//根据服务模板命名空间ns和nodetype的命名空间来过滤nodetype
		if(ns.indexOf("/nfv") > -1 && namespace.indexOf("/paas") > -1 && 
			(nodeTypeName.indexOf("Pod") > -1 || nodeTypeName.indexOf("Container") > -1)) {
			//NFV中看不到Paas的Pod和Container节点
			continue;
		} else if(ns.indexOf("/vnf") > -1 && 
			((namespace.indexOf("/vnf") > -1) || (namespace.indexOf("/ns") > -1))) {
			//VNF编排，看不到VNF和NS节点
			continue;
		} else if(ns.indexOf("/ns") > -1 && namespace.indexOf("/nfv") == -1){
			//NS编排，看不到Service节点
			continue;
		}

		String imageUrl = repositoryURL + "/nodetypes/" 
			+ Util.DoubleURLencode(nodeType.getTargetNamespace()) 
			+ "/" + Util.DoubleURLencode(nodeType.getName()) 
			+ "/visualappearance/50x50";
	%>
		<div class="paletteEntry">
			<div class="iconContainer" title="<%= nodeType.getName() %>" 
				data-placement="right">
				<img src="<%=imageUrl%>" class="nodetype-icon">
			</div>
			<div class="hidden">
				<nt:nodeTemplateRenderer
					repositoryURL="${repositoryURL}"
					client="${client}"
					relationshipTypes="${relationshipTypes}"
					nodeTypeQName="<%=new QName(nodeType.getTargetNamespace(), nodeType.getName())%>"
					nodeType="<%=nodeType%>" />
			</div>
		</div>
	<%
	}
	%>
	<div id="newNodeTemplate">
		<div id="addNodeTemplateToPalette"></div>
		<div class="ui-draggable" style="display:none">
			<div class="iconContainer" title="">
				<img src="images/vnf.png" class="nodetype-icon">
			</div>
			<div class="hidden">
				<div class="NodeTemplateShape unselectable hidden" style="left:0px; top:0px;">
					<span class="fullName"></span>
					<div class="headerContainer">
						<div class="minMaxInstances hidden">
							<span class="minInstances"></span>
							<span class="maxInstances"></span>
						</div>
						<div class="id nodetemplate"></div>
						<div class="name nodetemplate" style="display:none"></div>
						<div class="image nodetemplate">
							<img src="images/vnf.png" class="nodetemplate-icon">
						</div>
						<div class="type nodetemplate hidden">
							<a target="_blank" data-qname=""></a>
						</div>
						<span class="typeQName hidden"></span>
						<span class="typeNamespace hidden"></span>
						<div class="documentation hidden"></div>
					</div>
					
					<div class="endpointContainer">
					<%
						for (TRelationshipType relationshipType: (Collection<TRelationshipType>) relationshipTypes) {
					%>
						<div class="connectorEndpoint <%=Util.makeCSSName(relationshipType.getTargetNamespace(), relationshipType.getName())%>">
							<div class="connectorBox <%=Util.makeCSSName(relationshipType.getTargetNamespace(), relationshipType.getName())%>_box"></div>
							<div class="connectorLabel"><%=relationshipType.getName()%></div>
						</div>
					<%
						}
					%>
					</div>

					<%-- Properties --%>
					<div class="propertiesContainer">
						<div class="header">Properties</div>
						<div class="content">
							<span class="elementName"></span>
							<span class="namespace"></span>
							<table>
								<tbody></tbody>
							</table>
						</div>
					</div>

				<%-- Deployment Artifacts --%>
				<div class="deploymentArtifactsContainer">
					<div class="header">Deployment Artifacts</div>
					<div class="content">
						<div class="row addDA">
							<button class="btn btn-default btn-xs center-block addDA">Add new</button>
						</div>
						<div class="row addnewartifacttemplate">
							<div class="center-block">Drop to add new deployment artifact. Not yet implemented.</div>
						</div>
					</div>
				</div>

				<%-- Requirements and Capabilities --%>
				
				<div class="requirementsContainer">
					<div class="header">Requirements</div>
					<div class="content">
						<div class="reqorcap requirements row">
							<div class="col-xs-4 id reqorcap"></div>
							<div class="col-xs-4 name reqorcap"></div>
							<div class="col-xs-4 type reqorcap">
								<a data-qname=""></a>
							</div>
							<div class="propertiesContainer">
								<div class="header">Properties</div>
								<div class="content">
									<span class="elementName"></span>
									<span class="namespace"></span>
									<table>
										<tbody></tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="capabilitiesContainer">
					<div class="header">Capabilities</div>
					<div class="content">
						<div class="reqorcap capabilities row" id="">
							<div class="col-xs-4 id reqorcap"></div>
							<div class="col-xs-4 name reqorcap"></div>
							<div class="col-xs-4 type reqorcap">
								<a data-qname=""></a>
							</div>
							<div class="propertiesContainer">
								<div class="header">Properties</div>
								<div class="content">
									<span class="elementName"></span>
									<span class="namespace"></span>
									<table>
										<tbody></tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<%-- Policies --%>
				<%--
				<pol:policies list="<%=policyList%>" repositoryURL="${repositoryURL}" />
				--%>
			</div>
		</div>
	</div>
	</div>
</div>

<script>

	//$("#palette").css("width","20px");
	//$("div.paletteEntry").hide();

	$("#palette").click (function() {
		showPalette();
		winery.events.fire(winery.events.name.command.UNSELECT_ALL_NODETEMPLATES);
	});

	function showPalette() {
		// reset width to original CSS width
		//$("#palette").removeClass("shrunk");
		// show all palette entries
		//$("div.paletteEntry").show();
		//$("#paletteLabel").hide();

		//$("#addNodeTemplateToPalette").show();
	}

	function hidePalette() {
		//$("#palette").addClass("shrunk");
		// hide all palette entries
		//$("div.paletteEntry").hide();
		//$("#paletteLabel").show();

		//$("#addNodeTemplateToPalette").hide();
	}

	function dragHandler(newObj, event) {
		newObj.removeClass("ui-draggable");
		newObj.removeClass("ui-droppable");
		newObj.removeClass("hidden");

		// generate and set id
		var type = newObj.find("div.type.nodetemplate").text();
		var id = type;
		// we cannot use the id as the initial name, because we want to preserve special characters in the name, but not in the id.
		var name = type;

		// quick hack to make id valid
		// currently, only spaces and dots cause problems
		id = id.replace(" ", "_");
		id = id.replace(/\./g, "_");//modify by qinlihan 2015.11.17

		if ($("#" + id).length != 0) {
			var count = 2;
			var idprefix = id + "_";
			do {
				id = idprefix + count;
				count++;
			} while ($("#" + id).length != 0);
			// also adjust name
			name = name + "_" + count;
		}
		newObj.attr("id", id);
		newObj.children("div.headerContainer").children("div.id").text(id);

		// initial name has been generated based on the id
		//如果名字存在，则使用默认的软件仓储选择的nodeTemplate名字，否则使用现在生成的name
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

	$(function() {
		$( "div.paletteEntry" ).draggable({
			cursor: "move",
			cursorAt: { top: 40, left: 112 },
			helper: function( event ) {
				var newObj = $(this).find("div.NodeTemplateShape").clone();
				newObj.removeClass("hidden");
				newObj.css("z-index", "2000");
				newObj.find ("div.endpointContainer").remove();

				// Ensure that obj is appended to drawingarea and not to palette
				// Consequence: the dragged object is always under the cursor and not paintet with an offset equal to the scrollheight
				$("#drawingarea").append(newObj);

				return newObj;
			},
			start: function( event, ui ) {
				winery.events.fire(winery.events.name.command.UNSELECT_ALL_NODETEMPLATES);
				// The palette is kept visible after a drag start,
				// therefore no action
				// hidePalette();
			},
			appendTo: '#drawingarea'
		});


		$( "div#drawingarea" ).droppable({
			accept: function(d) {
				if (d.hasClass("paletteEntry")) {
					return true;
				}
			},
			drop: function( event, ui ) {

				var palEntry = ui.draggable;
				var templateCode = palEntry.find("div.NodeTemplateShape").clone().wrap("<div></div>").parent().html();

				dragHandler($(templateCode), event);
			}
		});

		$("#addNodeTemplateToPalette").click(function(){
			$("#winery").hide();
			$("#template_iframe").show();

			var namespace = "${ns}";
			var serviceTemplateName = $("#boundaryDefinition .title h4").text();
			var softwareRepositoryURL = "./jsp/softwareRepository.html?baseUrl=${repositoryURL}";
			if(namespace.indexOf("/ms") > -1) {
				softwareRepositoryURL += "&type=servicelets&filter=" + namespace + "," + serviceTemplateName;
			} else {
				softwareRepositoryURL += "&type=microservices&filter=" + namespace + "," + serviceTemplateName;
			}

			window.open(softwareRepositoryURL, "template_iframe");
		});
		
		//增加tooltip，暂时屏蔽，CSS样式显示不完整
		/*var iconContainers = $(".paletteEntry .iconContainer");
		$.each(iconContainers, function(index, icon){
			$(icon).tooltip({
				title : $(icon).attr("name")
			});
		});*/
	});

	function getDataFromChildFrame(data) {
		$("#winery").show();
		$("#template_iframe").hide();
		if(data) {
			generateNodeTemplateDiv(data);
		}
		//使焦点回到当前窗口，才能监听到键盘事件（比如按下delete键删除节点）
		$(window).trigger("focus");
	}

	//从软件仓库选择，生成nodeTemplate
	function generateNodeTemplateDiv(data) {
		var newNodeTemplate = $("#newNodeTemplate .NodeTemplateShape").clone();

		var nodeTemplate = data.template;
		var name = nodeTemplate.name;
		newNodeTemplate.find(".iconContainer").attr("name", name);
		newNodeTemplate.find(".fullName").text(name);
		var headerContainer = newNodeTemplate.children(".headerContainer");
		headerContainer.find(".name").text(name);

		//设置nodeTemplate值
		var typeQName = nodeTemplate.type;
		var namespace = typeQName.substring(typeQName.indexOf("{") + 1, typeQName.indexOf("}"));
		var localName = typeQName.substring(typeQName.indexOf("}") + 1);
		var nodeTypeCSSName = namespace + "_" + localName;
		nodeTypeCSSName = nodeTypeCSSName.replace(/[^A-Za-z0-9]/g, "_");
		newNodeTemplate.addClass(nodeTypeCSSName);
		headerContainer.find(".typeQName").text(typeQName);
		headerContainer.find(".typeNamespace").text(namespace);
		headerContainer.find(".type").text(localName);
		//设置image
		if(namespace.indexOf("/ns") > -1) {
			newNodeTemplate.find("img").attr("src", "images/ns.png");
		} else if(namespace.indexOf("/service") > -1) {
			newNodeTemplate.find("img").attr("src", "images/service.png");
		}

		//设置document
		var documentation = nodeTemplate.documentation;
		if(documentation.length && documentation[0].content.length) {
			var documentation = documentation[0].content[0];
			headerContainer.children(".documentation").text(documentation);
		}

		//设置nodeTemplate properties
		var propertyDefinitions = data.type.any[0];
		if(propertyDefinitions && propertyDefinitions.propertyDefinitionKVList) {
			var propertyDefinitionKVList = propertyDefinitions.propertyDefinitionKVList;
			var properties = nodeTemplate.properties.any;
			var propertiesHtml = "";
			$.each(propertyDefinitionKVList, function(index, property){
				var value = properties[property.key] || "";
				propertiesHtml += '<tr class="KVProperty">'
					+ '<td><span class="' + property.key + ' KVPropertyKey">' + property.key 
					+ '</span></td><td><a class="KVPropertyValue" href="#" data-type="text"' 
					+ 'data-title="Enter ' + property.key + '">' + value
					+ '</a></td><td><span class="KVPropertyType">' + property.type + '</span></td>'
					+ '<td><span class="KVPropertyTag">' + property.tag + '</span></td>'
					+ '<td><span class="KVPropertyRequired">' + property.required + '</span></td>'
					+ '</tr>';
			});
			var propertiesContainer = newNodeTemplate.children(".propertiesContainer");
			propertiesContainer.find("tbody").html(propertiesHtml);

			var elementName = propertyDefinitions.elementName;
			propertiesContainer.find(".elementName").text(elementName);
			var propertyNamespace = propertyDefinitions.namespace;
			propertiesContainer.find(".namespace").text(propertyNamespace);
		} else {
			//没有属性就移除属性节点，否则保存报错
			newNodeTemplate.children(".propertiesContainer").remove();
		}

		//设置Requirements
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
				var propertiesHtml = generateKVProperty(element.properties.any);
				requirement.find(".propertiesContainer tbody").html(propertiesHtml);

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
		
		//设置Capabilities
		var capabilitiesContent = newNodeTemplate.find(".capabilitiesContainer>.content");
		var capabilitiesContainer = capabilitiesContent.children().clone();
		capabilitiesContent.html("");
		if(nodeTemplate.capabilities) {
			var capabilities = nodeTemplate.capabilities.capability || [];
			generateReqOrCap(capabilities, capabilitiesContainer, capabilitiesContent);
		}

		//把nodeTemplate添加到页面上
		dragHandler(newNodeTemplate);	

		//自动布局
		doLayout();
	}
</script>