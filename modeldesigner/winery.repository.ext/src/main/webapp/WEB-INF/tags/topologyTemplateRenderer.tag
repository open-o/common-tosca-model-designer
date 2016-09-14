<%--
/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - skeletton for topology rendering
 *    Oliver Kopp - converted to .tag and integrated in the repository
 *******************************************************************************/
--%>
<%@tag description="Renders a toplogytemplate. This tag is used to render a topology template readonly. The topoology modeler does the rendering on itself." pageEncoding="UTF-8" %>

<%@tag import="java.lang.Math"%>
<%@tag import="java.util.ArrayList"%>
<%@tag import="java.util.Collection"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.UUID"%>
<%@tag import="javax.xml.namespace.QName"%>
<%@tag import="org.eclipse.winery.common.Util"%>
<%@tag import="org.eclipse.winery.common.ModelUtilities"%>
<%@tag import="org.eclipse.winery.common.ids.definitions.ServiceTemplateId" %>
<%@tag import="org.eclipse.winery.model.tosca.TEntityTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeType"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipTemplate.SourceElement"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipTemplate.TargetElement"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.model.tosca.TTopologyTemplate"%>
<%@tag import="org.eclipse.winery.repository.Utils"%>

<%@attribute name="topology" required="true" description="the topology template to be rendered" type="org.eclipse.winery.model.tosca.TTopologyTemplate" %>
<%@attribute name="repositoryURL" required="true" %>
<%@attribute name="client" required="true" type="org.eclipse.winery.common.interfaces.IWineryRepository" %>
<%@attribute name="fullscreen" required="false" type="java.lang.Boolean" %>
<%@attribute name="additonalCSS" required="false"%>
<%@attribute name="autoLayoutOnLoad" required="false" type="java.lang.Boolean" %>
<%@attribute name="location" required="true" type="java.lang.String" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ncnt"  tagdir="/WEB-INF/tags/templates/nodetemplates" %>
<%@taglib prefix="bdf"  tagdir="/WEB-INF/tags/common/templates" %>
<%@taglib prefix="tmpl" tagdir="/WEB-INF/tags/common/templates" %>
<%@taglib prefix="nt"   tagdir="/WEB-INF/tags/common/templates/nodetemplates" %>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions"%>

<%-- required for vShowError --%>
<script type="text/javascript" src="${w:topologyModelerURI()}/components/pnotify/jquery.pnotify.js"></script>
<script type="text/javascript" src="${w:topologyModelerURI()}/js/winery-common.js"></script>
<script type="text/javascript" src="${w:topologyModelerURI()}/js/winery-topologymodeler.js"></script>

<%-- required for vShowError --%>
<link type="text/css" href="${w:topologyModelerURI()}/components/pnotify/jquery.pnotify.default.css" media="all" rel="stylesheet" />
<link type="text/css" href="${w:topologyModelerURI()}/components/pnotify/jquery.pnotify.default.icons.css" media="all" rel="stylesheet" />

<%-- winery-common.css also contains definitions for properties --%>
<link type="text/css" href="${w:topologyModelerURI()}/css/winery-common.css" rel="stylesheet" />
<link type="text/css" href="${w:topologyModelerURI()}/css/topologytemplatecontent.css" rel="stylesheet" />
<link rel="stylesheet" href="${w:topologyModelerURI()}/css/topologymodeler.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/topologyTemplateRenderer.css" />
<c:if test="${not empty fullscreen}"><link rel="stylesheet" href="${pageContext.request.contextPath}/css/topologyTemplateRendererFullscreen.css" /></c:if>
<c:if test="${not empty additonalCSS}"><link rel="stylesheet" href="${additonalCSS}" /></c:if>

<%
	Collection<TRelationshipType> relationshipTypes = client.getAllTypes(TRelationshipType.class);

	// quick hack
	// better would be to collect all types used in the curren topoloy template
	Collection<TNodeType> nodeTypes = client.getAllTypes(TNodeType.class);
%>

<tmpl:CSSForTypes nodeTypes="<%=nodeTypes%>" relationshipTypes="<%=relationshipTypes%>"/>

<%--查看的时候也能看到属性和模板参数--%>
<%
	int index = location.indexOf("?");
	String[] params = location.substring(index + 1).split("&");

	String[] nsParam = params[1].split("=");
	String ns = Util.URLdecode(nsParam[1]);
	String[] idParam = params[2].split("=");
	String id = idParam[1];

	QName serviceTemplateQName = new QName(ns, id);
	String serviceTemplateURL = repositoryURL + "/servicetemplates/" + Util.DoubleURLencode(	serviceTemplateQName);
	String serviceTemplateName = client.getName(new ServiceTemplateId(serviceTemplateQName));
	
	boolean autoConnect = false;
%>
<ncnt:propertiesOfOneNodeTemplate repositoryURL="<%=repositoryURL%>" palette="false"/>
<bdf:boundaryDefinitions serviceTemplateURL="<%=serviceTemplateURL%>" 
	serviceTemplateName="<%=serviceTemplateName%>" ns="<%=ns%>" palette="false"/>

<script>
// required by winery-common-topologyrendering
if (typeof winery === "undefined") winery = {}
if (typeof winery.connections === "undefined") winery.connections = {}

//enable caching. This disables appending of "?_=xy" at requests
jQuery.ajaxSetup({cache:true});

//configuration for pnotify
require(["jquery", "pnotify"], function() {
	$.pnotify.defaults.styling = "bootstrap3";
});
</script>

<%
	// used for the position of the NodeTemplates
	int topCounter = 0;
%>
<script>
function doLayout() {
	var editor = $("#editorArea");
	var nodeTemplates = editor.find(".NodeTemplateShape");
	require(["winery-sugiyamaLayouter"], function(layouter) {
		layouter.layout(nodeTemplates);
	});
}
</script>
<div class="topbar">
	<div class="topbarbuttons">
		<button class="btn btn-default" onclick="doLayout();">Layout</button>
		<tmpl:toggleButtons />
	</div>
</div>
<%-- div #editorArea required for layouter --%>
<div id="editorArea">
<div id="templateDrawingArea">

<tmpl:defineCreateConnectorEndpointsFunction relationshipTypes="<%=relationshipTypes%>"/>

<%
	// can be used later to call a doLayout()
	boolean somethingWithoutPosition = false;

	Collection<TRelationshipTemplate> relationshipTemplates = new ArrayList<TRelationshipTemplate>();
	Collection<TNodeTemplate> nodeTemplates = new ArrayList<TNodeTemplate>();

	// the minimum x/y coordinates.
	// used to move the content to the top left corner
	int minTop = Integer.MAX_VALUE;
	int minLeft = Integer.MAX_VALUE;

	for (TEntityTemplate entity: topology.getNodeTemplateOrRelationshipTemplate()) {
		if (entity instanceof TNodeTemplate) {
			TNodeTemplate nodeTemplate = (TNodeTemplate) entity;
			nodeTemplates.add(nodeTemplate);

			// determine minTop and minLeft
			String top = ModelUtilities.getTop(nodeTemplate);
			if (top != null) {
				int intTop = Utils.convertStringToInt(top);
				if (intTop != 0) {
					minTop = Math.min(minTop, intTop);
				}
			}

			String left = ModelUtilities.getLeft(nodeTemplate);
			if (left != null) {
				int intLeft = Utils.convertStringToInt(left);
				if (intLeft != 0) {
					minLeft = Math.min(minLeft, intLeft);
				}
			}

		} else {
			assert(entity instanceof TRelationshipTemplate);
			
			TRelationshipTemplate tr = (TRelationshipTemplate) entity;
			if(!tr.getOwn().equals("1"))
			{
				relationshipTemplates.add(tr);
			}
		}
	}

	for (TNodeTemplate nodeTemplate: nodeTemplates) {
		// assuming the topology can be displayed as a stack, else call doLayout() afterwards
		topCounter = topCounter + 150;
		
		String left = ModelUtilities.getLeft(nodeTemplate);
		if (left == null) {
			left = "0";
			somethingWithoutPosition = true;
		} else {
			// calulate offset
			// we could hash the coordinate in the loop before
			// but that would obfuscate the code and currently, we don't have speed issues here
			left = Integer.toString(Utils.convertStringToInt(left) - minLeft);
		}
		String top = ModelUtilities.getTop(nodeTemplate);
		if (top == null) {
			top = Integer.toString(topCounter);
			somethingWithoutPosition = true;
		} else {
			// calulate offset
			top = Integer.toString(Utils.convertStringToInt(top) - minTop);
		}

		%>
			<nt:nodeTemplateRenderer client="<%=client%>" relationshipTypes="<%=relationshipTypes%>" repositoryURL="<%=repositoryURL%>" nodeTemplate="<%=nodeTemplate%>" top="<%=top%>" left="<%=left%>"/>
		<%
			
		}
	if (somethingWithoutPosition) {
		autoLayoutOnLoad = true;
	}
%>

<script>
function onDoneRendering() {
	<c:if test="${autoLayoutOnLoad}">
	doLayout();
	</c:if>

	// copied from index.jsp -> togglePrintView

	// move labels 10 px up
	// we have to do it here as jsPlumb currently paints the label on the line instead of above of it
	// See https://groups.google.com/d/msg/jsplumb/zdyAdWcRta0/K6F2MrHBH1AJ
	$(".relationshipTypeLabel").each(function(i, e) {
		var pos = $(e).offset();
		pos.top = pos.top - 10;
		$(e).offset(pos);
	});

	// The user can pass an additional script to the topologyTemplateResource via the script query parameter
	// In that script, he can define the function wineryViewExternalScriptOnLoad which is called here
	if (typeof wineryViewExternalScriptOnLoad === "function") {
		wineryViewExternalScriptOnLoad();
	}
}
</script>
<tmpl:registerConnectionTypesAndConnectNodeTemplates repositoryURL="${repositoryURL}" relationshipTypes="<%=relationshipTypes%>" relationshipTemplates="<%=relationshipTemplates%>" ondone="onDoneRendering();" readOnly="true" autoConnect="<%=autoConnect%>"/>
</div>
</div>

<script>
	
	// "mousedown" instead of "click" enables a more Visio-like behavior
	$(document).on("mousedown", "div.NodeTemplateShape", function(e) {
		var target = $(e.target);
		
		// no special handling if x-editable popover is clicked
		if (target.parents().hasClass("popover")) {
			return false;
		}

		// no special handling if connectors are clicked
		if (target.hasClass("connectorEndpoint") || target.hasClass("connectorBox") || target.hasClass("connectorLabel")) {
			return false;
		}

		if (target.is("a") && !target.hasClass("editable")) {
			// Link clicked
			// Open in new tab
			// Delay opening for 300ms to disalbe a dragstart
			window.setTimeout(function() {
				var href = target.attr("href");
				window.open(href);
			}, 300);
			return false;
		}

		if (target.is("button")) {
			// "Edit XML" or "Add deployment artifact" clicked
			return false;
		}

		// if the custom KV properties are clicked, handle them
		if (target.hasClass("KVPropertyValue")) {
			return false;
		}
		// OK or Cancel clicked at editable
		if ((target.hasClass("icon-ok")) || (target.hasClass("icon-remove"))) {
			return false;
		}

		if (target.hasClass("reqorcap")) {
			var reqOrCapId = undefined; // set to undefined to avoid compiler warnings
			// check if req or cap should be edited
			var parentReqOrCapDiv = undefined; // used to determine whether a req or cap is edited, set to undefined to avoid compiler warnings
			if (target.is("div")) {
				reqOrCapId = target.parent().attr("id");
				parentReqOrCapDiv = target.parent();
			} else {
				vShowError("Wrong branch. UI is not consistent with code");
			}
			var isReq = parentReqOrCapDiv.hasClass("requirements");
			if (isReq) {
				showAddOrUpdateDiagForReq(undefined, reqOrCapId);
			} else {
				//console.log(parentReqOrCapDiv.hasClass("capabilities"));
				showAddOrUpdateDiagForCap(undefined, reqOrCapId);
			}
			return false;
		}

		if (target.hasClass("policy")) {
			// click is always on the seen policy content (name, template, ...)
			// the complete element is the parent element
			var policy = $(target).parent();
			showUpdateDiagForPolicy(policy);
			return false;
		}

		if ( (e.shiftKey) || (e.ctrlKey) ) {
			// SHIFT or CTRL indicates multi select
			// toggle containment in the multi select
			$(this).toggleClass("selected");
		} else {
			// no explicit multi select
			var numSelected = $("div.NodeTemplateShape.selected").length;
			if ($(this).hasClass("selected")) {
				// selection if already exists

				// Below, we raise the selection change even in that case to provoke an update of properties etc.
				// When dragging and dropping a single node, the menu of the node is not shown any more
				// a click on the (still selected) node should reveal the menu entries.
			} else {
				// curent shape not selected

				if (numSelected > 0) {
					// other shapes are selected
					// the clicked shape is clicked
					// that means, all other shapes should be unselected
					$("div.NodeTemplateShape.selected").removeClass("selected");
				}

				// no multi select trigger
				// shape is unselected
				// finally, select the shape
				$(this).addClass("selected");
			}
		}

		winery.events.fire(winery.events.name.SELECTION_CHANGED);
		return false;
	});

</script>
<script>

	/**
	 * register events / event registering / eventing
	 */
	$(function() {
		winery.events.register(
			winery.events.name.SELECTION_CHANGED,
			function() {
				var numSelected = $("div.NodeTemplateShape.selected").length;
				/*
				if (numSelected == 1) {

					var selectedNodeTemplate = $("div.NodeTemplateShape.selected");
					if (isShownNodeTemplateShapeChangeBoxes(selectedNodeTemplate)) {
						// shape change boxes are already shown. Hide them
						hideNodeTemplateShapeChangeBoxes($("div.NodeTemplateShape"));
					} else {
						// fired if
						// * a single node template is selected,
						// * no menu is shown

						// bring that shape to the front
						$("div.NodeTemplateShape").css("z-index", "20");
						selectedNodeTemplate.css("z-index", "21");

						// we show the change boxes
						//showNodeTemplateShapeChangeBoxes(selectedNodeTemplate);
						//hideNodeTemplateShapeChangeBoxes($("div.NodeTemplateShape:not(.selected)"));
					}
				} else {
					// hide everywhere
					hideNodeTemplateShapeChangeBoxes($("div.NodeTemplateShape"));
				}

				updateVisibilityToggleButtons();*/
			}
		);
		winery.events.register(
			winery.events.name.command.SELECT_ALL_NODETEMPLATES,
			function () {
				$("div.NodeTemplateShape").addClass("selected");
				winery.events.fire(winery.events.name.SELECTION_CHANGED);
			}
		);
		winery.events.register(
			winery.events.name.command.UNSELECT_ALL_NODETEMPLATES,
			function () {
				$("div.NodeTemplateShape").removeClass("selected");
				winery.events.fire(winery.events.name.SELECTION_CHANGED);
			}
		);

		winery.events.register(
			winery.events.name.command.MOVE_UP,
			function () {
				wineryMoveSelectedNodeTemplateShapes(0, -1);
				return false;
			}
		);
		winery.events.register(
			winery.events.name.command.MOVE_DOWN,
			function () {
				wineryMoveSelectedNodeTemplateShapes(0, 1);
				return false;
			}
		);
		winery.events.register(
			winery.events.name.command.MOVE_LEFT,
			function () {
				wineryMoveSelectedNodeTemplateShapes(-1, 0);
				return false;
			}
		);
		winery.events.register(
			winery.events.name.command.MOVE_RIGHT,
			function () {
				wineryMoveSelectedNodeTemplateShapes(1, 0);
				return false;
			}
		);
	});

</script>
<script>
	var multiDNDmode = false;
	var multiDNDdata = {};

	$(document).on("dragstart", "div.NodeTemplateShape", function(e) {
		var nodeTemplateShape = $(this);
		hideNodeTemplateShapeChangeBoxes(nodeTemplateShape);
		if (nodeTemplateShape.hasClass("selected")) {
			var allSelectedShapes = $("div.NodeTemplateShape.selected");
			if (allSelectedShapes.length > 1) {
				// console.log("start: multiDNDmode");
				multiDNDdata.x = e.clientX;
				multiDNDdata.y = e.clientY;
				multiDNDdata.shapes = $("div.NodeTemplateShape.selected").not("#" + e.currentTarget.id);
				multiDNDmode = true;
			}
		} else {
			// "mousedown" event handling already took care about
			// deselect everything else
			// select shape
		}
	});
</script>
<script>
	$(document).on("drag", "div.NodeTemplateShape", function(e) {
		if (multiDNDmode) {
			// TODO possibly, this has to be put in a queue to avoid racing events?
			var dx = e.clientX - multiDNDdata.x;
			var dy = e.clientY - multiDNDdata.y;
			multiDNDdata.x = e.clientX;
			multiDNDdata.y = e.clientY;
			multiDNDdata.shapes.each(function(i, n) {
				n = $(n);
				var offset = n.offset();
				offset.left += dx;
				offset.top += dy;
				n.offset(offset);
			});
			jsPlumb.repaintEverything();
		}
	});

</script>
<script>
    function unselectAllConnections() {
		jsPlumb.select().each(function(connection) {
			connection.removeType("selected");
		});
	}
	// we cannot use "$("#editorArea").on("click") as this is *always* triggered before $(document).on("click", ...)
	$(document).on("mousedown", "#editorArea", function(e) {

		winery.events.fire(winery.events.name.command.UNSELECT_ALL_NODETEMPLATES);

		unselectAllConnections();

		// true because jsPlumb COULD treat this event, currently unclear
		return true;
	});

	/** marquee tool **/

	var selectionBoxMode = false;
	var selectionBox = {};

	/**
	 * This function is called when selectionBoxMode = true and the mouse gets moved
	 */
	var selectionBoxModeMouseMoveFunction = function(e) {
		selectionBox.endX = e.pageX;
		selectionBox.endY = e.pageY;

		// fix selectionbox coordinates if they are out of the window
		if (selectionBox.endX < selectionBox.minx) selectionBox.endX = selectionBox.minx;
		if (selectionBox.endX > selectionBox.maxx) selectionBox.endX = selectionBox.maxx;
		if (selectionBox.endY < selectionBox.miny) selectionBox.endY = selectionBox.miny;
		if (selectionBox.endY > selectionBox.maxy) selectionBox.endY = selectionBox.maxy;

		// we cannot show the selection box at mousedown as this conflicts somehow with jsPlumb
		// if the .offset of the selectionbox is set, jsPlumb events are not fired any more
		$("#selectionbox").show();
		// setSelectionBoxCoordinates() only works if selectionbox is shown
		setSelectionBoxCoordinates();
	}

	function setSelectionBoxCoordinates() {
		var x;
		var y;
		var height;
		var width;

		// adjust parameters for html, where top/left have to be smaller than lower right
		if (selectionBox.startX < selectionBox.endX) {
			x = selectionBox.startX;
			width = selectionBox.endX - selectionBox.startX;
		} else {
			x = selectionBox.endX;
			width = selectionBox.startX - selectionBox.endX;
		}
		if (selectionBox.startY < selectionBox.endY) {
			y = selectionBox.startY;
			height = selectionBox.endY - selectionBox.startY;
		} else {
			y = selectionBox.endY;
			height = selectionBox.startY - selectionBox.endY;
		}

		$("#selectionbox").offset({
			left : x,
			top : y
		});
		$("#selectionbox").width(width);
		$("#selectionbox").height(height);

		// console.log("realx: " + $("#selectionbox").offset().left);
		// console.log("realy: " + $("#selectionbox").offset().top);

		selectionBox.x = x;
		selectionBox.y = y;
		selectionBox.height = height;
		selectionBox.width = width;

		var area = {};
		if (lastSelectionBox == undefined) {
			// nothing selected at last, check whole new box
			area = selectionBox;
		} else {
			// TODO
			// calculate area to check
		}

		// console.log("sel: " + x + "/" + y + " --X dim: " + width + "/" + height);

		// quick hack: we just go through all node templates and check them for selection
		$("div.NodeTemplateShape:not('.hidden')").each(function(index, nodeTemplate) {
			nodeTemplate = $(nodeTemplate);
			var nx = nodeTemplate.offset().left;
			var ny = nodeTemplate.offset().top;
			var nw = nodeTemplate.width();
			var nh = nodeTemplate.height();
			/* console.log(nx + "/" + ny + " --> dim: " + nw + "/" + nh); */
			if (nx >= x &&
				ny >= y &&
				nx + nw <= x + width &&
				ny + nh <= y + height) {
				nodeTemplate.addClass("selected");
			} else {
				nodeTemplate.removeClass("selected");
			}
		});

		lastSelectionBox = selectionBox;
	}

	// register selection box handling events
	$(document).on("mousedown", "#editorArea", function(e) {
		selectionBoxMode = true;
		selectionBox.startX = e.pageX;
		selectionBox.startY = e.pageY;
		selectionBox.endX = selectionBox.startX;
		selectionBox.endY = selectionBox.startY;
		// console.log("Start: " + selectionBox.startX + "/" + selectionBox.startY)
		selectionBox.minx = document.body.scrollLeft;
		selectionBox.miny = document.body.scrollTop;
		selectionBox.maxx = selectionBox.minx + $(window).width();
		selectionBox.maxy = selectionBox.miny + $(window).height();
		lastSelectionBox = undefined;
		$(document).on("mousemove", selectionBoxModeMouseMoveFunction);
		return true;
	});
	$(document).on("mouseup", function(e) {
		// TODO: possibly, dragend could be used. With the recent libraries, it also works in Chrome
		if (selectionBoxMode) {
			$(document).off("mousemove", selectionBoxModeMouseMoveFunction);
			selectionBoxMode = false;
			
			// 创建一个DIV层
			var width = $("#selectionbox").width();
			if(width > 100)
			{
				var temp_id = "nsSelDiv";
				var count = 1;
				var id = temp_id + count;
				if ($("#" + id).length != 0) {
					var idprefix = id + "_";
					do {
						id = temp_id + count;
						count++;
					} while ($("#" + id).length != 0);
					// also adjust name
					id = temp_id + (count - 1);
				}
				
				// 创建新层并设置ID和样式
				var newDiv = $("#selectionbox").clone();
				newDiv.attr("id", id);
				newDiv.addClass("div_selectionbox");
				newDiv.addClass("ui-draggable");
				newDiv.addClass("ui-droppable");
				newDiv.addClass("layoutableComponent");
				newDiv.mouseover(function(e)
				{
					$(this).css("border", "2px solid red");
					$(this).addClass("NodeTemplateShape");
					$(this).css("z-index", "18");
					$(this).addClass("selected");
					//newDiv.css("background-image", "");
				});
				newDiv.mousemove(function(e)
				{
					//$(this).css("border", "2px solid red");
				});
				newDiv.mouseleave(function(e)
				{
					
					newDiv.removeClass("NodeTemplateShape");
					$(this).removeClass("selected");
					$(this).css("border", "2px solid black");
					//$(this).css("background-image","#655C5C!important");
				});
				
				newDiv.appendTo($("#editorArea"));
			}

			// 隐藏选择框
			$("#selectionbox").hide();
			
		} else if (multiDNDmode) {
			multiDNDmode = false;
			// console.log("end: multiDNDmode");
		}
	
	});

</script>