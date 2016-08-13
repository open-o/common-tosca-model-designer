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
