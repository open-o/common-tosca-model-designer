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
var vm = avalon
		.define({
			$id : "softwareRepositoryController",	
        	$dataTableLanguage: {
			"sProcessing": "<img src='../img/loading-spinner-grey.gif'/><span>&nbsp;&nbsp;处理中...</span>",
            "sInfoEmpty": "共 0 项",
            "sGroupActions": "_TOTAL_ 条结果被选择:  ",
            "sAjaxRequestGeneralError": "不能完成请求，请检查您的网络连接情况。",
            "sEmptyTable": "表中数据为空",
            "oPaginate": {
                "sPrevious": "前页",
                "sNext": "下页",
                "sPage": "第",
                "sPageOf": "页，总页数："
            },
			"sLengthMenu": "每页 _MENU_ 行 ",
            "sZeroRecords": "没有匹配结果",
             "sInfoFiltered": "(从 _MAX_ 条记录过滤)",
            "sInfo": "<span class='seperator'>  </span>共 _TOTAL_ 条记录",
            "sSearch": "搜索：",
        	},	
        	baseUrl:"",
        	addBtnName: "添加",
        	nsInfoArray :  [],
			vnfInfoArray :  [],	
			servicesInfoArray :  [],
			serviceletsInfoArray:[],
			microservicesInfoArray:[],
			tab:{
				ifVNF:false,
				ifNS:false,
				ifServices:false,
				ifMicroservices:false,
				ifServicelets:false
			},
			initData:function(){

				var url= window.location.search.substr(1);
				vm.baseUrl=softwareRepositoryUtil.getQueryString(url,"baseUrl");
		 		var type=softwareRepositoryUtil.getQueryString(url,"type");
				var filter=softwareRepositoryUtil.getQueryString(url,"filter");

				var queryTemplatesUrl = vm.baseUrl + "/API/templates/summaries?type=" + type + "&filter=" + filter;
		

				$.ajax({
	                "type": 'get',
	                "url": queryTemplatesUrl,
	                "dataType": "json",
	                success: function (resp) { 

	                	if(resp["http://www.zte.com.cn/tosca/nfv/ns"]!=null){
	                		vm.tab.ifNS=true;
	                		vm.nsInfoArray=resp["http://www.zte.com.cn/tosca/nfv/ns"]
	                	}

	                	if(resp["http://www.zte.com.cn/tosca/nfv/vnf"]!=null){
	                		vm.tab.ifVNF=true;
	                		vm.vnfInfoArray=resp["http://www.zte.com.cn/tosca/nfv/vnf"]
	                	}

	                	if(resp["services"]!=null){
	                		vm.tab.ifServices=true;
	                		vm.servicesInfoArray=resp["services"];
	                		vm.addBtnName="添加到蓝图";
	                	}

	                	if(resp["microservices"]!=null){
	                		vm.tab.ifMicroservices=true;
	                		vm.microservicesInfoArray=resp["microservices"];
	                		vm.addBtnName="添加到蓝图";
	                	}

	                	if(resp["servicelets"]!=null){
	                		vm.tab.ifServicelets=true;
	                		vm.serviceletsInfoArray=resp["servicelets"];
	                		vm.addBtnName="添加到蓝图";
	                	}

	                	$("#blueprintTab li").each(function(){
	                		if ($(this).is(":visible")==true) {
	                			$(this).children().tab('show')
	                			return false;
	                		}

	                	});

	                	

	                   
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						   alert("获取数据查询失败："+XMLHttpRequest.responseText);                       
	                       return;
	                 },
	                 complete:function(){

	                 	table=$('.zte-table').DataTable({
						     
							  "oLanguage": vm.$dataTableLanguage,
							   //"dom": '<"top">rt<"bottom"lip><"clear">',
					  			"sPaginationType": "bootstrap_extended",
					  			 "columnDefs": [ {
								      "targets": [2,3],
								      "searchable": false,
								      "bSortable": false,
								    }],
								   "order": [[0, 'asc']]
							});
	                 	
	            		}
				});
			},
			queryTemplate:function(id,type){


				var queryTemplateByIdUrl = vm.baseUrl + "/API/templates/" + id + "?type=" + type;

				$.ajax({
	                "type": 'get',
	                "url": queryTemplateByIdUrl,
	                "dataType": "json",
	                success: function (resp) {  
	                     window.parent.getDataFromChildFrame(resp); 
	                             	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						   alert("添加到蓝图失败："+XMLHttpRequest.responseText);                       
	                       return;
	                 }
	             });

		  
			},
			returnParent:function(){
				window.parent.getDataFromChildFrame();
			}




		});

avalon.scan();
vm.initData();