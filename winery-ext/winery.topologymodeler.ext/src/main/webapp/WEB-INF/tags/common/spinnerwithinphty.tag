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
<%@tag description="A spinner with the possibility to set to inphty via button" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Code copied between repository and topology-modeler --%>

<%--
Could also be realized as
 * HTML5 Web Component (http://www.ibm.com/developerworks/library/wa-html5components1/) or
 * x-tags (http://www.x-tags.org/)
We decided to use JSP tags to avoid an additional JavaScript library
--%>

<%@attribute name="label" required="true"%>
<%@attribute name="id" required="true"%>
<%@attribute name="min"%>
<%@attribute name="max" required="false" description="Maximum value. Default is 1000. The underlying library does not allow arbitrary high values."%>
<%@attribute name="name" required="false" description="The name of the input field. Defaults to the id"%>
<%@attribute name="withinphty" required="false" description="If set, then an inphty button is provded"%>
<%@attribute name="value"%>
<%@attribute name="width" required="false" description="The Column with according to bootstrap rules. Default is 3 (should not be smaller)."%>
<%@attribute name="changedfunction" required="false" description="Called if value changed"%>
<%@attribute name="palette" required="false" type="java.lang.Boolean" description="palette mode or view mode"%>

<%-- Set default name value if required --%>
<c:if test="${empty name}">
	<c:set var="name" value="${id}"></c:set>
</c:if>

<c:if test="${empty width}">
	<c:set var="width" value="3"></c:set>
</c:if>

<%--<div class="form-group">
	<label for="${id}">${label}</label>
	<div class="row">
		<div class="col-lg-${width}">
			<c:choose>
			<c:when test="${palette}">
			    <div class="input-group">
					<input id="${id}" class="spinner form-control" name="${name}" type="text" <c:if test="${not empty changedfunction}">onblur="${changedfunction}();"</c:if>/>
					<c:if test="${not empty withinphty}">
						<span class="input-group-addon" style="cursor: pointer; border-left:0" onclick="setToInfin('${id}'<c:if test="${not empty changedfunction}">, ${changedfunction}</c:if>);">&infin;</span>
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<input id="${id}" class="form-control" name="${name}" type="text" disabled/>
			</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>--%>

<div class="form-group">
	<label for="${id}" class="col-sm-3 control-label">${label}</label>
	<div class="col-sm-8">
	    <div class="input-group">
			<input id="${id}" class="spinner form-control" name="${name}" type="text" <c:if test="${not empty changedfunction}">onblur="${changedfunction}();"</c:if>/>
			<c:if test="${not empty withinphty}">
				<span class="input-group-addon" style="cursor: pointer; border-left:0" onclick="setToInfin('${id}'<c:if test="${not empty changedfunction}">, ${changedfunction}</c:if>);">&infin;</span>
			</c:if>
		</div>
	</div>
</div>

<script>
<%--
included multiple times.
Drawback when not using HTML5 components and keeping the JavaScript functions closed to the HTML code
--%>
function setToInfin(id, changedFunction) {
	var spinner = $("#" + id);
	spinner.val('∞'); // &inphty; - jQuery does not decode that, but places the plain text. Therefore, we directly pass the char we want
	if (changedFunction !== undefined) {
		changedFunction();
	}
}

$(function() {
	var param = {}
	<c:if test="${not empty min}">
	param.minimum = "${min}";
	</c:if>
	<c:if test="${empty max}">
	param.maximum = 1000;
	</c:if>

	// use bootstrap-spinedit plugin
	$("#${id}").spinedit(param);

	<c:if test="${not empty changedfunction}">
	$("#${id}").on('valueChanged', ${changedfunction});
	</c:if>

});

</script>

