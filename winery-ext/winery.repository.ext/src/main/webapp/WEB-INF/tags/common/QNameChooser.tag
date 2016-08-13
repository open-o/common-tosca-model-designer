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
<%@tag description="Dialog parts for choosing a QName" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions"%>

<%@attribute name="allQNames" required="true" type="java.util.Collection" description="Collection&lt;QName&gt; of all available QNames" %>
<%@attribute name="includeNONE" required="false" type="java.lang.Boolean" description="Should (none) be included as option?"%>
<%@attribute name="selected" required="false" description="The initial value to select"%>
<%@attribute name="labelOfSelectField" required="true"%>
<%@attribute name="idOfSelectField" required="true"%>

<div class="form-group">
	<c:if test="${not empty labelOfSelectField}"><label for="${idOfSelectField}" class="control-label">${labelOfSelectField}:</label></c:if>
	<select id="${idOfSelectField}" name="${idOfSelectField}" class="form-control">
		<c:if test="${includeNONE}"><option value="(none)">(none)</option></c:if>
		<c:forEach var="namespaceEntry" items="${wc:convertQNameListToNamespaceToLocalNameList(allQNames)}">
			<optgroup label="${namespaceEntry.key}">
				<c:forEach var="localName" items="${namespaceEntry.value}">
					<option value="{${namespaceEntry.key}}${localName}">${localName}</option>
				</c:forEach>
			</optgroup>
		</c:forEach>
	</select>
</div>

<script>
$(function(){
	$("#${idOfSelectField}").select2();
	<c:if test="${not empty selected}">
		$("#${idOfSelectField}").select2("val", "${selected}");
	</c:if>
});
</script>
