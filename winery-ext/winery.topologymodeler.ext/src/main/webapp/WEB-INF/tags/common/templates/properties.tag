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
<%@tag description="Rendering for properties. A separate CSS has to be provided to style the content. Thus, this tag is reusable both in the topology modeler and in the management UI. Requires global javaScript function editPropertiesXML(visualElementId)" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>

<%@attribute name="propertiesDefinition" required="true" type="org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition" description="The TOSCA-conforming properties definition. May be null."%>
<%@attribute name="wpd" required="true" type="org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition" description="Winery's K/V properties definition. May be null"%>
<%@attribute name="template" required="true" type="org.eclipse.winery.model.tosca.TEntityTemplate" description="The template to display properties. Has to be null in case of the palette mode of the topology modeler"%>
<%@attribute name="pathToImages" required="true" description="The path (URI path) to the image/ url, where xml.png is available. Has to end with '/'"%>

<%@tag import="org.eclipse.winery.common.ModelUtilities"%>
<%@tag import="org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition"%>
<%@tag import="org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV"%>
<%@tag import="org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList"%>

<%
if ((propertiesDefinition != null) || (wpd != null)) {
// properties exist
%>
	<div class="propertiesContainer">
		<div class="header">Properties</div>
		<div class="content">
			<%
			if (wpd == null) {
				// no winery's special properties definition, but "normal" TOSCA properties definition

				if (propertiesDefinition.getType() != null) {
			%>
					<span class="properties_type">XSD Type: <%=propertiesDefinition.getType()%></span>
				<%
				} else {
				%>
					<span class="properties_element">XSD Element: <%=propertiesDefinition.getElement()%></span>
				<%
				}
				%>
				<textarea class="properties_xml"><%
				if (template != null) {
				%><%=org.eclipse.winery.common.Util.getXMLAsString(org.eclipse.winery.model.tosca.TEntityTemplate.Properties.class, template.getProperties())%><%
				}
				%></textarea>
				<%-- We have to do use $(this).parent().parent().parent().attr('id') instead of <%=visualElementId%> as on drag'n'drop from the palette, this binding is NOT changed, but the Id changes --> the user does NOT want to edit the properties from the palette entry, but from the node template --%>
				<button class="btn btn-default" onclick="editPropertiesXML($(this).parent().parent().parent().attr('id'));"><img src="${pathToImages}xml.png"></img>View</button>
			<%
			} else {
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
						String tag = propdef.getTag();
						String required = propdef.getRequired();
						String defaultValue = propdef.getValue();
						if(defaultValue == null) {
							defaultValue = "";
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
						</tr>
				<%
					}
				}
				%>
				</table>
			<%
			}
			%>
		</div>
	</div>
<%
}
%>
