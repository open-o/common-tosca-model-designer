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
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
--%>
<%@tag description="Rendering for properties. A separate CSS has to be provided to style the content. Thus, this tag is reusable both in the topology modeler and in the management UI. Requires global javaScript function editPropertiesXML(visualElementId)" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>

<%@attribute name="propertiesDefinition" required="true" type="org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition" description="The TOSCA-conforming properties definition. May be null."%>
<%@attribute name="wpds" required="true" type="java.util.List" description="Winery's K/V properties definition. May be null"%>
<%@attribute name="template" required="true" type="org.eclipse.winery.model.tosca.TEntityTemplate" description="The template to display properties. Has to be null in case of the palette mode of the topology modeler"%>
<%@attribute name="pathToImages" required="true" description="The path (URI path) to the image/ url, where xml.png is available. Has to end with '/'"%>

<%@tag import="org.eclipse.winery.common.ModelUtilities"%>
<%@tag import="org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition"%>
<%@tag import="org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV"%>
<%@tag import="org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList"%>
<%@tag import="org.eclipse.winery.common.propertydefinitionkv.Constraint"%>
<%@tag import="java.util.List"%>

<%
if (wpds != null) {
// properties exist
%>
	<div class="propertiesContainer">
		<div class="header">Properties</div>
		<%
		for(Object obj : wpds) {
			WinerysPropertiesDefinition wpd = (WinerysPropertiesDefinition) obj;
		%>
		<div class="content" name="<%=wpd.getElementName()%>">
		<%
			// Winery special mode
			java.util.Properties props;
			if (template == null) {
				// setting null only because of dump compiler.
				// We never read props if in paletteMode
				props = null;
			} else {
				props = ModelUtilities.getPropertiesKV(template);
			}
		%>
			<%-- stores wrapper element name and namespace to ease serialization--%>
			<span class="elementName"><%=wpd.getElementName()%></span>
			<span class="namespace"><%=wpd.getNamespace()%></span>
			<table>
			<%
			PropertyDefinitionKVList list = wpd.getPropertyDefinitionKVList();
			if (list != null) {
				// iterate on all defined properties
				for (PropertyDefinitionKV propdef: list) {
					String key = propdef.getKey();
					String type = propdef.getType();
					String tag = propdef.getTag() == null ? "" : propdef.getTag();
					String required = propdef.getRequired();
					String defaultValue = propdef.getValue() == null ? "" : propdef.getValue();

					String validValue = "";
					Constraint constraint = propdef.getConstraint();
					if(constraint != null) {//获取枚举值
						validValue = constraint.getValidValue();
					}
					
					String value;
					if (template == null) {
						value = defaultValue;
					} else {
						// assign value, but change "null" to "" if no property is defined
						value = props.getProperty(key) == null ?  defaultValue : props.getProperty(key);
					}
			%>
					<tr class="KVProperty">
						<td><span class="<%= key %> KVPropertyKey"><%= key %></span></td>
						<td><a class="KVPropertyValue" href="#" data-type="text" data-title="Enter <%= key %>"><%=value %></a></td>
						<td><span class="KVPropertyType"><%=type %></span></td>
						<td><span class="KVPropertyTag"><%=tag %></span></td>
						<td><span class="KVPropertyRequired"><%=required %></span></td>
						<td><span class="KVPropertyValidValue"><%=validValue %></span></td>
					</tr>
			<%
				}
			}
			%>
			</table>
		</div>
		<%
		}
		%>
	</div>
<%
}
%>
