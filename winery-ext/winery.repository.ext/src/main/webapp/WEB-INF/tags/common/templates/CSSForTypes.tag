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
<%@tag description="Generates style element for node types and relationship types" pageEncoding="UTF-8" %>

<%@attribute name="nodeTypes" required="true" type="java.util.Collection" %>
<%@attribute name="relationshipTypes" required="true" type="java.util.Collection" %>

<%@tag import="java.util.Collection"%>
<%@tag import="javax.xml.namespace.QName"%>
<%@tag import="org.eclipse.winery.common.ModelUtilities"%>
<%@tag import="org.eclipse.winery.common.Util"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeType"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>

<style>
<%
	for (TNodeType nt: (Collection<TNodeType>) nodeTypes) {
		String borderColor = ModelUtilities.getBorderColor(nt);
		String cssName = Util.makeCSSName(nt.getTargetNamespace(), nt.getName());
%>
		div.NodeTemplateShape.<%=cssName%> {
			border-color: <%=borderColor%>;
		}
<%
	}

	// relationship types CSS
	for (TRelationshipType rt: (Collection<TRelationshipType>) relationshipTypes) {
		String color = ModelUtilities.getColor(rt);
		QName qname = new QName(rt.getTargetNamespace(), rt.getName());
		String cssName = Util.makeCSSName(qname) + "_box";
%>
		div.<%=cssName%> {
			background: <%=color%>;
		}
<%
	}
%>
</style>
