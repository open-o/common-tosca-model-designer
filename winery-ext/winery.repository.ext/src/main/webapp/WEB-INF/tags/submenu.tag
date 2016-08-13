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
<!-- basic idea by http://stackoverflow.com/a/3257426/873282 -->
<%@tag description="submenu" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@attribute name="subMenuData" required="true" type="org.eclipse.winery.repository.resources.SubMenuData"%>
<%@attribute name="selected" required="true"%>

<%-- style='<c:if test='${subMenuData.text eq "Topology Template" }'> display:none;</c:if>' --%>
<a href="${subMenuData.href}" class="styledTabMenuButton2ndlevel btn btn-primary<c:if test="${selected}"> selected</c:if>">
	<div class="left"></div>
	<div class="center">${subMenuData.text}</div>
	<div class="right"></div>
</a>
