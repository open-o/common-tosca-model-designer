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

/* no name given as RequireJS' documentatio says that makes modules less portable */
define(["datatables"], function() {
		var module = {
				initTable: initTable,
				initTableWithData: initTableWithData
			};
		return module;

		/**
		 * Initializes a table (and updates tableInfo)
		 *  * adds selection capability
		 *
		 * @param info.id: id of the table  (with #)
		 * @param paramsForDataTable: additional parameters for dataTable()
		 * @param afterInit function to be called after dataTables object initalization happened
		 *
		 * @returns initialized info object
		 *   * info.table is updated with a pointer to the data table
		 *   * info.selectedTr is set to null
		 */
		function initTable(info, afterInit) {
			var tableId = '#'+ info.id;
			var tableEleStr = '<table class="zte-table" id="'+info.id+'"><thead><tr role="row" class="heading"></tr></thead><tbody></tbody></table>';
		    $("#" + info.divId).html(tableEleStr);
		    var trEle = $(tableId  + ' > thead >tr');
		    //var dataTableColumn = [];
		    for ( var one in info.columns) {
		        var th = '<th>' + info.columns[one].name + '</th>';
		        trEle.append(th);
		    }

			var paramsForDataTable = {
				"sDom" : "",
		        "bPaginate": false,
		        "bLengthChange": true,// record number in each row
		        "bSort": false,// sort
		        "bWidth": true,
		        "bAutoWidth": false,
		        "bScrollCollapse": true,
		        "sPaginationType": "bootstrap_extended", 
		        "bDestroy": true,
		        "bSortCellsTop": true,
		        "sAjaxSource": info.url,
		        "aoColumns": info.columns,
		        // "aoColumnDefs": [
		        //     {
		        //         sDefaultContent: '',
		        //         aTargets: [ '_all' ]
		        //     }
		        // ],
		        "fnServerData": function (sSource, aoData, fnCallback, oSettings) {
		            oSettings.jqXHR = $.ajax({
		                "type": 'get',
		                "url": sSource,
		                "dataType": "json",
		                "success": function (resp) {                                       
		                    oSettings.iDraw = oSettings.iDraw + 1;
		                    
		                    resp = resp || [];
		                    var data = {};
		                    data.aaData = resp;
		                    var totalCounts = resp.length;
		                    
		                    data.iTotalRecords = totalCounts;
		                    data.iTotalDisplayRecords = totalCounts;
		                    data.sEcho = oSettings;
		                    fnCallback(data);
		                }
		            });
		        },
		        "fnInitComplete" : function() {
		        	if (afterInit) afterInit();
		        }
			};
			$(tableId).dataTable(paramsForDataTable);
		}

		/**
		 * 传入表格数据来初始化表格
		 * @param  {[type]} info      [description]
		 * @param  {[type]} tableData [description]
		 * @param  {[type]} afterInit [description]
		 * @return {[type]}           [description]
		 */
		function initTableWithData(info, tableData, afterInit) {
			var tableId = '#'+ info.id;
			var tableEleStr = '<table class="zte-table" id="'+info.id+'"><thead><tr role="row" class="heading"></tr></thead><tbody></tbody></table>';
		    $("#" + info.divId).html(tableEleStr);
		    var trEle = $(tableId  + ' > thead >tr');
		    //var dataTableColumn = [];
		    for ( var one in info.columns) {
		        var th = '<th>' + info.columns[one].name + '</th>';
		        trEle.append(th);
		    }

			var paramsForDataTable = {
				"sDom" : "",
		        "bPaginate": false,
		        "bLengthChange": true,// record number in each row
		        "bSort": false,// sort
		        "bWidth": true,
		        "bAutoWidth": false,
		        "bScrollCollapse": true,
		        "sPaginationType": "bootstrap_extended", 
		        "bDestroy": true,
		        "bSortCellsTop": true,
		        "bServerSide": false,
		        "sAjaxSource": tableData,
		        "aoColumns": info.columns,
		        "aoColumnDefs": [
		            {
		                sDefaultContent: '',
		                aTargets: [ '_all' ]
		            }
		        ],
		        "fnServerData": function (sSource, aoData, fnCallback, oSettings) {                                       
                    oSettings.iDraw = oSettings.iDraw + 1;
                    
                    var resp = tableData || [];
                    var data = {};
                    data.aaData = resp;
                    var totalCounts = resp.length;
                    
                    data.iTotalRecords = totalCounts;
                    data.iTotalDisplayRecords = totalCounts;
                    data.sEcho = oSettings;
                    fnCallback(data);
		        },
		        "fnInitComplete" : function() {
		        	if (afterInit) afterInit();
		        }
			};
			$(tableId).dataTable(paramsForDataTable);
		}
	}
);
