<%--
/*******************************************************************************
 * Copyright (c) 2012-2013,2015 University of Stuttgart.
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
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<div style="margin-top:10px;display:none;">
	<a class="zte-btn zte-primary" href="../.." >返回</a>
	<a class="zte-btn zte-primary" id="newtab" href="${it.location}" >编辑</a>
	<a class="btn btn-info mybtn" href="topologytemplate/?view" target="_blank" style="display: none;">Open View</a>
	<br>
	<br>
	<div id="loading" class="topologyTemplatePreviewSizing" style="position:absolute; background-color: white; z-index:5;">Loading preview...</div>
	<iframe id="topologyTemplatePreview" class="topologyTemplatePreviewSizing" src="topologytemplate/?view=small" onload="$('#loading').hide(1000);"></iframe>
</div>
<script>
	$(function(){
		//直接跳转到编辑界面，不需要在iframe中显示视图
		window.location = "topologytemplate/?edit";
	});
</script>
