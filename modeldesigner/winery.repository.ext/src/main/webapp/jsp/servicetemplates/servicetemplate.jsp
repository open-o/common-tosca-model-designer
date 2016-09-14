<%--

    Copyright 2016 [ZTE] and others.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="org.eclipse.winery.repository.resources.SubMenuData"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%-- add submenus after the submenus defined for the type --%>
<%
java.util.List<SubMenuData> subMenus = new java.util.ArrayList<SubMenuData>(5);

SubMenuData data;

//data = new SubMenuData("#topologytemplate", "拓扑模板");
//subMenus.add(data);

data = new SubMenuData("#plans", "winery-submenu-plan");
subMenus.add(data);

data = new SubMenuData("#selfserviceportal", "winery-submenu-plan-option");
subMenus.add(data);

data = new SubMenuData("/winery/servicetemplates.html", "winery-submenu-return");
subMenus.add(data);

//data = new SubMenuData("#boundarydefinitions", "Boundary Definitions");
//subMenus.add(data);

//Tags are currently not implemented -> Don't confuse users by showing the tab
//has to be enabled again, when tags are implemented
//data = new SubMenuData("#tags", "Tags");
//subMenus.add(data);
%>

<t:componentinstance cssClass="serviceTemplate" selected="ServiceTemplate" subMenus="<%=subMenus%>">
</t:componentinstance>
