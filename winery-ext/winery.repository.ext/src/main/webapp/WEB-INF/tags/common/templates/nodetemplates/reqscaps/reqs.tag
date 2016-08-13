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
<%@tag description="Renders the list of requirements or capabilties" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="nt" tagdir="/WEB-INF/tags/common/templates/nodetemplates/reqscaps" %>

<%@attribute name="client" required="true" description="IWineryRepository" type="org.eclipse.winery.common.interfaces.IWineryRepository"%>
<%@attribute name="list" required="false" type="java.util.List"%>
<%@attribute name="repositoryURL" required="true" %>
<%@attribute name="pathToImages" required="true" description="The path (URI path) to the image/ url, where xml.png is available. Has to end with '/'"%>

<nt:reqsorcaps
	headerLabel="Requirements"
	cssClassPrefix="requirements"
	list="${list}"
	shortName="Req"
	TReqOrCapTypeClass="<%=org.eclipse.winery.model.tosca.TRequirementType.class%>"
	repositoryURL="${repositoryURL}"
	typeURLFragment="requirementtypes"
	pathToImages="${pathToImages}"
	client="${client}"
/>
