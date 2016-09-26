/*
 * Copyright 2016 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
softwareRepositoryUtil.loadi18n('winery-topologymodeler-i18n', '/winery-topologymodeler/i18n/');

var vm = avalon
	.define({
		$id: "softwareRepositoryController",
		$dataTableLanguage: {
			"sProcessing": "<img src='../img/loading-spinner-grey.gif'/><span>&nbsp;&nbsp;"+ $.i18n.prop("winery-table-sProcess") +"...</span>",
			"sInfoEmpty": $.i18n.prop("winery-table-sInfoEmpty"),
			"sGroupActions": $.i18n.prop("winery-table-sGroupActions"),
			"sAjaxRequestGeneralError": $.i18n.prop("winery-table-sAjaxRequestGeneralError"),
			"sEmptyTable": $.i18n.prop("winery-table-sEmptyTable"),
			"oPaginate": {
				"sPrevious": $.i18n.prop("winery-table-sPrevious"),
				"sNext": $.i18n.prop("winery-table-sNext"),
				"sPage": $.i18n.prop("winery-table-sPage"),
				"sPageOf": $.i18n.prop("winery-table-sPageOf")
			},
			"sLengthMenu": $.i18n.prop("winery-table-sLengthMenu"),
			"sZeroRecords": $.i18n.prop("winery-table-sZeroRecords"),
			"sInfoFiltered": $.i18n.prop("winery-table-sInfoFiltered"),
			"sInfo": $.i18n.prop("winery-table-sInfo"),
			"sSearch": $.i18n.prop("winery-table-sSearch"),
		},
		baseUrl: "",
		addBtnName: $.i18n.prop("winery-softwareRepository-btn-add"),
		nsInfoArray: [],
		vnfInfoArray: [],
		servicesInfoArray: [],
		serviceletsInfoArray: [],
		microservicesInfoArray: [],
		tab: {
			ifVNF: false,
			ifNS: false,
			ifServices: false,
			ifMicroservices: false,
			ifServicelets: false
		},
		initData: function() {
			var url = window.location.search.substr(1);
			vm.baseUrl = softwareRepositoryUtil.getQueryString(url, "baseUrl");
			var type = softwareRepositoryUtil.getQueryString(url, "type");
			var filter = softwareRepositoryUtil.getQueryString(url, "filter");

			var queryTemplatesUrl = vm.baseUrl + "/API/templates/summaries?type=" + type + "&filter=" + filter;
			$.ajax({
				"type": 'get',
				"url": queryTemplatesUrl,
				"dataType": "json",
				success: function(resp) {
					if (resp["http://www.zte.com.cn/tosca/nfv/ns"] != null) {
						vm.tab.ifNS = true;
						vm.nsInfoArray = resp["http://www.zte.com.cn/tosca/nfv/ns"]
					}

					if (resp["http://www.zte.com.cn/tosca/nfv/vnf"] != null) {
						vm.tab.ifVNF = true;
						vm.vnfInfoArray = resp["http://www.zte.com.cn/tosca/nfv/vnf"]
					}

					if (resp["services"] != null) {
						vm.tab.ifServices = true;
						vm.servicesInfoArray = resp["services"];
						vm.addBtnName = $.i18n.prop("winery-softwareRepository-btn-addblueprint");
					}

					if (resp["microservices"] != null) {
						vm.tab.ifMicroservices = true;
						vm.microservicesInfoArray = resp["microservices"];
						vm.addBtnName = $.i18n.prop("winery-softwareRepository-btn-addblueprint");
					}

					if (resp["servicelets"] != null) {
						vm.tab.ifServicelets = true;
						vm.serviceletsInfoArray = resp["servicelets"];
						vm.addBtnName = $.i18n.prop("winery-softwareRepository-btn-addblueprint");
					}

					$("#blueprintTab li").each(function() {
						if ($(this).is(":visible") == true) {
							$(this).children().tab('show')
							return false;
						}

					});
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					alert($.i18n.prop("winery-softwareRepository-message-error-query") + XMLHttpRequest.responseText);
					return;
				},
				complete: function() {
					table = $('.zte-table').DataTable({
						"oLanguage": vm.$dataTableLanguage,
						//"dom": '<"top">rt<"bottom"lip><"clear">',
						"sPaginationType": "bootstrap_extended",
						"columnDefs": [{
							"targets": [2, 3],
							"searchable": false,
							"bSortable": false,
						}],
						"order": [
							[0, 'asc']
						]
					});
				}
			});
		},
		queryTemplate: function(tableId, type) {
			var checkedDatas = softwareRepositoryUtil.getTableCheckedData(tableId);
			var idStr = checkedDatas.toString();

			if(!idStr) {
				alert($.i18n.prop("winery-softwareRepository-message-select"));
				return;
			}

			var queryTemplateByIdUrl = vm.baseUrl + "/API/templates/" + idStr + "?type=" + type;
			$.ajax({
				"type": 'GET',
				"url": queryTemplateByIdUrl,
				"dataType": "json",
				success: function(resp) {
					window.parent.getDataFromChildFrame(resp);
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					alert($.i18n.prop("winery-softwareRepository-message-error-add") + XMLHttpRequest.responseText);
					return;
				}
			});
		},
		returnParent: function() {
			window.parent.getDataFromChildFrame();
		}
	});
avalon.scan();
vm.initData();