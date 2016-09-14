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
define([], function(){
	var DEFAULT = 0;
	var READ_ONLY = 1;
	var ATTRIBUTE = 2;
	var PROPERTY = 4;
	var INPUT = 8;
	var METATDATA = 16;
	var PASSWORD = 32;

	return {
		hasTag: hasTag,
		addTag: addTag,

		DEFAULT: DEFAULT,
		READ_ONLY: READ_ONLY,
		ATTRIBUTE: ATTRIBUTE,
		PROPERTY: PROPERTY,
		INPUT: INPUT,
		METATDATA: METATDATA,
		PASSWORD: PASSWORD
	};

	function hasTag(value, tag) {
		if(value == "" || value == null) {
			value = DEFAULT;
		}
		return (parseInt(value, 2) & tag) == tag;
	}

	function addTag(value, tag) {
		if(value == "" || value == null) {
			value = DEFAULT;
		}
		var result = parseInt(value, 2) | tag;
		return result.toString(2);
	}
});