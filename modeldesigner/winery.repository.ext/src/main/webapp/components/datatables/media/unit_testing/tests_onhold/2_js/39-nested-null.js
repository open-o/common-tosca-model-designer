/*
 * Copyright 2016 [ZTE] and others.
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
// DATA_TEMPLATE: empty_table
oTest.fnStart( "39 - nested null values" );

$(document).ready( function () {
	var test = false;

	$.fn.dataTable.ext.sErrMode = "throw";

	oTest.fnTest(
		"No default content throws an error",
		function () {
			try {
				$('#example').dataTable( {
					"aaData": [
						{ "a": "0", "b": {"c": 0} },
						{ "a": "1", "b": {"c": 3} },
						{ "a": "2", "b": null }
					],
					"aoColumns": [
						{ "mDataProp": "a" },
						{ "mDataProp": "b" },
						{ "mDataProp": "b.c" }
					]
				} );
			}
			catch(err) {
				test = true;
			}
		},
		function () { return test; }
	);

	oTest.fnTest(
		"Table renders",
		function () {
			oSession.fnRestore();
			
			$('#example').dataTable( {
				"aaData": [
					{ "a": "0", "b": {"c": 0} },
					{ "a": "1", "b": {"c": 3} },
					{ "a": "2", "b": null }
				],
				"aoColumns": [
					{ "mDataProp": "a" },
					{ "mDataProp": "b" },
					{ "mDataProp": "b.c", "sDefaultContent": "allan" }
				]
			} );
		},
		function () { return $('#example tbody td:eq(0)').html() === "0"; }
	);

	oTest.fnTest(
		"Default content applied",
		function () {
			oSession.fnRestore();
			
			$('#example').dataTable( {
				"aaData": [
					{ "a": "0", "b": {"c": 0} },
					{ "a": "1", "b": {"c": 3} },
					{ "a": "2", "b": null }
				],
				"aoColumns": [
					{ "mDataProp": "a" },
					{ "mDataProp": "b" },
					{ "mDataProp": "b.c", "sDefaultContent": "allan" }
				]
			} );
		},
		function () { return $('#example tbody td:eq(8)').html() === "allan"; }
	);
	
	oTest.fnComplete();
} );