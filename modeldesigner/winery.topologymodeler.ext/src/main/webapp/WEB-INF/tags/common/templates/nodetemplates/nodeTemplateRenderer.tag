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
 *    Uwe Breitenbücher - skeletton for node template shapes
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
--%>
<%@tag language="java" pageEncoding="UTF-8" description="This tag is used for both real nodeTemplate node rendering and rendering of a 'template' used to create a nodeTemplateShape. The latter is called by palette.jsp. Therefore, this tag has to be more general."%>
<%-- Parameters --%>

<%-- template and palette --%>
<%@attribute name="client" required="true" description="IWineryRepository" type="org.eclipse.winery.common.interfaces.IWineryRepository"%>
<%@attribute name="repositoryURL" required="true" type="java.lang.String" description="The URL of winery's repository"%>
<%@attribute name="topologyModelerURI" required="false" type="java.lang.String" description="The URL of winery topology modeler's URI - required for images/. Has to end with '/'. Can be left blank."%>
<%@attribute name="relationshipTypes" description="the known relationship types" required="true" type="java.util.Collection"%>

<%-- only for topology modeler --%>
<%@attribute name="nodeTemplate" type="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@attribute name="top"%>
<%@attribute name="left"%>

<%-- only for palette.jsp --%>
<%@attribute name="nodeType" type="org.eclipse.winery.model.tosca.TNodeType" %>
<%@attribute name="nodeTypeQName" type="javax.xml.namespace.QName"%>

<%@tag import="org.eclipse.winery.model.tosca.TArtifactTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TArtifactType"%>
<%@tag import="org.eclipse.winery.model.tosca.TCapability"%>
<%@tag import="org.eclipse.winery.model.tosca.TDeploymentArtifact"%>
<%@tag import="org.eclipse.winery.model.tosca.TDeploymentArtifacts"%>
<%@tag import="org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate.Requirements"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate.Policies"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeType"%>
<%@tag import="org.eclipse.winery.model.tosca.TPolicy"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.model.tosca.TRequirement"%>
<%@tag import="org.eclipse.winery.model.tosca.TDocumentation"%>
<%@tag import="org.eclipse.winery.common.ModelUtilities"%>
<%@tag import="org.eclipse.winery.common.Util"%>
<%@tag import="org.eclipse.winery.common.ids.definitions.ArtifactTemplateId"%>
<%@tag import="org.eclipse.winery.common.ids.definitions.ArtifactTypeId"%>
<%@tag import="org.eclipse.winery.common.interfaces.IWineryRepository"%>
<%@tag import="org.w3c.dom.Element" %>
<%@tag import="org.apache.commons.configuration.Configuration"%>
<%@tag import="org.apache.commons.lang3.StringUtils"%>
<%@tag import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@tag import="java.io.StringWriter" %>
<%@tag import="java.util.Collections"%>
<%@tag import="java.util.Collection"%>
<%@tag import="java.util.Iterator"%>
<%@tag import="java.util.List"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.UUID"%> 
<%@tag import="java.util.Date"%>
<%@tag import="javax.xml.namespace.QName"%>
<%@tag import="javax.xml.transform.OutputKeys"%>
<%@tag import="javax.xml.transform.Transformer"%>
<%@tag import="javax.xml.transform.TransformerFactory"%>
<%@tag import="javax.xml.transform.dom.DOMSource"%>
<%@tag import="javax.xml.transform.stream.StreamResult"%>

<%@taglib prefix="nt"    tagdir="/WEB-INF/tags/common/templates/nodetemplates" %>
<%@taglib prefix="ntrq"  tagdir="/WEB-INF/tags/common/templates/nodetemplates/reqscaps" %>
<%@taglib prefix="pol"   tagdir="/WEB-INF/tags/common/policies" %>
<%@taglib prefix="props" tagdir="/WEB-INF/tags/common/templates" %>

<%
	String visualElementId;
	String documentation = "";

	boolean paletteMode;
	if (nodeTemplate == null) {
		// we are in palette mode
		// --> we render a template to be inserted in the drawing area by drag'n'drop
		paletteMode = true;
		assert(nodeType != null);
		assert(nodeTypeQName != null);

		// these values are only pseudo values, they get all overwritten in drop function of palette.jsp
		visualElementId = UUID.randomUUID().toString();
		left = "0";
		top = "0";

		//获取nodetype的documentation
		if(nodeType.getDocumentation() != null && nodeType.getDocumentation().size() > 0) {
			List<Object> content = nodeType.getDocumentation().get(0).getContent();
			if(content != null && content.size() > 0) {
				documentation = content.get(0).toString();
			}
		}
	} else {
		// we render a real node template
		paletteMode = false;
		nodeTypeQName = nodeTemplate.getType();
		nodeType = client.getType(nodeTypeQName, TNodeType.class);
		if (nodeType == null) {
%>
			<script>vShowError("Could not get node type <%=nodeTypeQName%>");</script>
<%
			return;
		}

		visualElementId = nodeTemplate.getId();

		//获取nodetemplate的documentation
		if(nodeTemplate.getDocumentation() != null && nodeTemplate.getDocumentation().size() > 0) {
			List<Object> content = nodeTemplate.getDocumentation().get(0).getContent();
			if(content != null && content.size() > 0) {
				documentation = content.get(0).toString();
			}
		}
	}

	String nodeTypeCSSName = Util.makeCSSName(nodeTypeQName);

	String name;
	if (paletteMode) {
		name = ""; // will be changed on drop
	} else {
		name = nodeTemplate.getName();
		if (StringUtils.isEmpty(name)) {
			name = visualElementId;
		}
	}

	String imageUrl = repositoryURL + "/nodetypes/" + Util.DoubleURLencode(nodeTypeQName)
		+ "/visualappearance/50x50";

	//hide tosca.nodes.nfv.FP node templates
	boolean isHidden = false;
	String namespace = nodeTypeQName.getNamespaceURI();
	if(namespace.indexOf("/vnffg/fp") > -1) {
		isHidden = true;
	}
%>

	<div class="NodeTemplateShape unselectable <%=nodeTypeCSSName%> <%if (paletteMode) {%>hidden<%} else if (isHidden) {%>hidden fpNode<%}%>" id="<%=visualElementId%>" style="left:<%=left%>px; top:<%=top%>px;">
		<span class="fullName"><%=name%></span>
		<div class="headerContainer">
			<div class="minMaxInstances hidden">
				<span class="minInstances"><%
					if (!paletteMode) {
						%><%=Util.renderMinInstances(nodeTemplate.getMinInstances())%><%
					}
				%></span>
				<span class="maxInstances"><%
					if (!paletteMode) {
						%><%=Util.renderMaxInstances(nodeTemplate.getMaxInstances())%><%
					}
				%></span>
			</div>
			<div class="id nodetemplate"><%=visualElementId%></div>
			<div class="name nodetemplate" style="display:none"><%=name%></div>
			<div class="image nodetemplate">
				<img src="<%=imageUrl%>" class="nodetemplate-icon">
			</div>
			<div class="type nodetemplate hidden"><%=Util.qname2hrefWithName(repositoryURL, TNodeType.class, nodeTypeQName, nodeType.getName())%></div>
			<span class="typeQName hidden"><%=nodeTypeQName%></span>
			<span class="typeNamespace hidden"><%=nodeTypeQName.getNamespaceURI()%></span>
			<div class="documentation hidden"><%=documentation%></div>
		</div>
		<div class="endpointContainer">
		<%
			for (TRelationshipType relationshipType: (Collection<TRelationshipType>) relationshipTypes) {
		%>
			<div class="connectorEndpoint <%=Util.makeCSSName(relationshipType.getTargetNamespace(), relationshipType.getName())%>">
				<div class="connectorBox <%=Util.makeCSSName(relationshipType.getTargetNamespace(), relationshipType.getName())%>_box"></div>
				<div class="connectorLabel" title="<%=relationshipType.getName()%>"><%=relationshipType.getName()%></div>
			</div>
		<%
			}
		%>
		</div>

		<%-- Properties --%>
		<props:complexProperties
			propertiesDefinition="<%=nodeType.getPropertiesDefinition()%>"
			wpds="<%=ModelUtilities.getWinerysPropertiesDefinitions(nodeType)%>"
			template="<%=paletteMode ? null : nodeTemplate %>"
			pathToImages="${topologyModelerURI}images/" />

	<%-- Deployment Artifacts --%>

	<%
	List<TDeploymentArtifact> deploymentArtifacts;
	if (paletteMode) {
		deploymentArtifacts = Collections.emptyList();
	} else {
		TDeploymentArtifacts tDeploymentArtifacts = nodeTemplate.getDeploymentArtifacts();		
		if (tDeploymentArtifacts == null) {
			deploymentArtifacts = Collections.emptyList();
		} else {
			deploymentArtifacts = tDeploymentArtifacts.getDeploymentArtifact();
		}
	}
	// Render even if (deploymentArtifacts.isEmpty()), because user could add some with drag'n'drop

	// following is required to render artifact specific content
	TransformerFactory transFactory = TransformerFactory.newInstance();
	Transformer transformer = transFactory.newTransformer();
	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	%>

	<div class="deploymentArtifactsContainer">

		<div class="header">Deployment Artifacts</div>
		<div class="content">
			<%
			if (!paletteMode) {
				int count = 0;
				for (TDeploymentArtifact deploymentArtifact : deploymentArtifacts) {
					Long fileIndex = (new Date()).getTime();
					String rowId = "artifactFiles_" + fileIndex + (count++) ;
					Map<QName,String> artifactData = deploymentArtifact.getOtherAttributes();
					String artifactQname = artifactData.get(new QName("artifactQname")).toString();
					%>
					<div class="deploymentArtifact row" id=<%=rowId%> >
						<div class="artifactName">
							<a data-qname=<%=artifactQname%>><%=deploymentArtifact.getName()%></a>
						</div>					
						<div class="deploy_path">
							<a data-qname=<%=artifactQname%>><%=artifactData.get(new QName("deploy_path"))%></a>
						</div>
						<div class="artifactFileName">
							<a data-qname=<%=artifactQname%>><%=artifactData.get(new QName("artifactFileName"))%></a>
						</div>
						<div class="artifactType">
							<a data-qname=<%=artifactQname%>><%=artifactData.get(new QName("artifactTypeStr"))%></a>
						</div>
						<div class="nodeDescriptionInput">
							<a data-qname=<%=artifactQname%>><%=artifactData.get(new QName("nodeDescriptionInput"))%></a>
						</div>
						<div class="nodeRepositoryInput">
							<a data-qname=<%=artifactQname%>><%=artifactData.get(new QName("nodeRepositoryInput"))%></a>
						</div>

					</div>
					<%
				}
			}
			%>

			<div class="row addDA">
				<button class="btn btn-default btn-xs center-block addDA">Add new</button>
			</div>

			<div class="row addnewartifacttemplate">
				<div class="center-block">Drop to add new deployment artifact. Not yet implemented.</div>
			</div>
		</div>
	</div>

	<%-- Requirements and Capabilities --%>
	<%
	List<TRequirement> reqList;
	if (paletteMode) {
		reqList = ModelUtilities.instantiateRequirements(nodeType);
	} else {
		Requirements reqs = nodeTemplate.getRequirements();
		if (reqs == null) {
			reqList = null;
		} else {
			reqList = reqs.getRequirement();
		}
	}
	%>
	<ntrq:reqs list="<%=reqList%>" repositoryURL="${repositoryURL}" pathToImages="${topologyModelerURI}images/" client="${client}" />

	<%
	List<TCapability> capList;
	if (paletteMode) {
		capList = ModelUtilities.instantiateCapabilities(nodeType);
	} else {
		Capabilities caps = nodeTemplate.getCapabilities();
		if (caps == null) {
			capList = null;
		} else {
			capList = caps.getCapability();
		}
	}
	%>
	<ntrq:caps list="<%=capList%>" repositoryURL="${repositoryURL}" pathToImages="${topologyModelerURI}images/" client="${client}"/>

	<%-- Policies --%>
	<%
	List<TPolicy> policyList;
	if (paletteMode) {
		policyList = null;
	} else {
		Policies policies = nodeTemplate.getPolicies();
		if (policies == null) {
			policyList = null;
		} else {
			policyList = policies.getPolicy();
		}
	}
	%>
	<pol:policies list="<%=policyList%>" repositoryURL="${repositoryURL}" />
</div>
