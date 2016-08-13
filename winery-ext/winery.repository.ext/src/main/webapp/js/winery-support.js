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
		/*
		* Valid chars: See
		* <ul>
		* <li>http://www.w3.org/TR/REC-xml-names/#NT-NCName</li>
		* <li>http://www.w3.org/TR/REC-xml/#NT-Name</li>
		* </ul>
		*/
		// NameCharRange \u10000-\ueffff is not supported by Java
		var NCNameStartChar_RegExp = "[A-Z_a-z\u00c0-\u00d6\u00d8\u00f6\u00f8\u02ff\u0370\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf\ufdf0-\ufffd]";
		var NCNameChar_RegExp = NCNameStartChar_RegExp + "|[-\\.0-9\u00B7\u0300-\u036F\u203F-\u2040]";
		var NCName_RegExp = NCNameStartChar_RegExp + "(" + NCNameChar_RegExp + ")*";
		var QName_RegExp = "(" + NCName_RegExp + ":)?(" + NCName_RegExp + ")";

		var NCNameStartChar_Pattern = new RegExp(NCNameStartChar_RegExp);
		var NCNameChar_Pattern = new RegExp(NCNameChar_RegExp);

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
		function initTable(info, paramsForDataTable, afterInit) {
			paramsForDataTable = paramsForDataTable || {};

			$(info.id).click(function(event) {
				if (info.selectedTr != null) {
					info.selectedTr.removeClass('row_selected');
					
					// begin add for plans.jsp
					if('#embeddedPlansTable' == info.id)
					{
						$("#planEdit").addClass('hidden');
					}
					// end add for plans.jsp
				}
				var row = $(event.target.parentNode);
				if ((info.selectedTr != null) && (info.selectedTr[0] == row[0])) {
					// row is deselected if selected again
					info.selectedRow = null;
					info.selectedTr = null;
				} else {
					info.selectedTr = row;
					info.selectedTr.addClass('row_selected');
					info.selectedRow = event.target.parentNode;
					
					// begin add for plans.jsp
					if('#embeddedPlansTable' == info.id)
					{
						console.log(row.context.children[3].innerHTML);
						if('BPMN4TOSCA 2.0' == row.context.children[3].innerHTML)
						{
							$("#planEdit").removeClass('hidden');
						}
					}
					// end add for plans.jsp
				}
			});
			info.selectedTr = null;
			info.table = $(info.id).dataTable(paramsForDataTable);
			if (afterInit) afterInit();
		}

		/**
		 * Function to determine whether a data table described with the tableInfo object is empty
		 *
		 * @param tableInfo the table info object describing the table
		 * @returns true if the table is empty, false otherwise
		 */
		function isEmptyTable(tableInfo) {
			return tableInfo.table.children("tbody").children("tr").first().children("td").hasClass("dataTables_empty");
		}

		/**
		 * JavaScript implementation of org.eclipse.winery.common.Util.makeNCName(String)
		 */
		function makeNCName(text) {
			if (!text || text == "") {
				return text;
			}

			var res = "";

			var start = text.substr(0, 1);
			if (NCNameStartChar_Pattern.test(start)) {
				res += start;
			} else {
				res += "_";
			}

			for (var i=1; i<text.length; i++) {
				var c = (text.substr(i, 1));
				if (NCNameChar_Pattern.test(c)) {
					res += c;
				} else {
					res += "_";
				}
			}
			return res;
		}

		var module = {
			initTable: initTable,
			isEmptyTable: isEmptyTable,
			makeNCName: makeNCName,
			QName_RegExp: QName_RegExp
		};
		return module;
	}
);
