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
/*
 * Modifications Copyright 2016 ZTE Corporation.
 */
--%>
<!-- basic idea by http://stackoverflow.com/a/3257426/873282 -->
<%@tag description="submenu" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@attribute name="subMenuData" required="true" type="org.eclipse.winery.repository.resources.SubMenuData"%>
<%@attribute name="selected" required="true"%>

<%-- style='<c:if test='${subMenuData.text eq "Topology Template" }'> display:none;</c:if>' --%>
<a href="${subMenuData.href}" class="styledTabMenuButton2ndlevel zte-btn zte-white<c:if test="${selected}"> selected</c:if>">
	<div class="left"></div>
	<div class="center">
		<span id="${subMenuData.text}" name_i18n="winery_i18n"></span>
	</div>
	<div class="right"></div>
</a>
