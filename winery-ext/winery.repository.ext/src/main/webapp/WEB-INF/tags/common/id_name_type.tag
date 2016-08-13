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
<%@tag description="Dialog parts for name and type choosing" pageEncoding="UTF-8"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions"%>
<%@taglib prefix="w"  tagdir="/WEB-INF/tags/common"%>


<%@attribute name="allTypes" required="true" type="java.util.Collection" description="Collection&lt;QName&gt; of all available types" %>
<%@attribute name="idPrefix" required="true" description="prefix used for name and type field. E.g., 'Req' becomes 'ReqType'."%>
<%@attribute name="hideIdField" required="false" description="if given, id field is not displayed. Quick hack to have this dialog reusable. Future versions might always show the id dialog and provide sync between name and id"%>

	<c:if test="${not hideIdField}">
		<div class="form-group">
			<label for="${idPrefix}Id" class="control-label">Id:</label>
			<input id="${idPrefix}Id" class="form-control" name="${shortName}Name" type="text" required="required" disabled="disabled"/>
		</div>
	</c:if>
	<div class="form-group">
		<label for="${idPrefix}Name" class="control-label">Name:</label>
		<input id="${idPrefix}Name" class="form-control" name="${shortName}Name" type="text" required="required" />
	</div>

<w:QNameChooser allQNames="${allTypes}" idOfSelectField="${idPrefix}Type" labelOfSelectField="Type" />
