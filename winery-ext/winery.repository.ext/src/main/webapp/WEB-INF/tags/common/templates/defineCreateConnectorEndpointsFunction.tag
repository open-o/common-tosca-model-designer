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
<%@tag description="Defines the javascript function createConnectorEndpoints globally. Quick hack to avoid huge hacking at the repository" pageEncoding="UTF-8"%>

<%@tag import="java.util.Collection"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.common.Util"%>

<%@attribute name="relationshipTypes" type="java.util.Collection" required="true" %>

<script>
function createConnectorEndpoints(nodeTemplateShapeSet) {
<%
	for (TRelationshipType relationshipType: (Collection<TRelationshipType>) relationshipTypes) {
%>
		nodeTemplateShapeSet.find(".<%=Util.makeCSSName(relationshipType.getTargetNamespace(), relationshipType.getName()) %>").each(function(i,e) {
			var p = $(e).parent();
			var grandparent = $(p).parent();

			jsPlumb.makeSource($(e), {
				parent:grandparent,
				anchor:"Continuous",
				connectionType: "{<%=relationshipType.getTargetNamespace()%>}<%=relationshipType.getName()%>",
				endpoint:"Blank"
			});
		});
<%
	}
%>
}
</script>
