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
(function(Application){
	Application.Constant.Element_Fork = "Fork";
	
	Application.registerElement(Application.View.Element.extend({

		/*content: function(){
			return "";
		},*/

		css: function(){
			return {
				"-webkit-border-radius": "5px",
				"-moz-border-radius": "5px",
				"background-size": "cover",
				"border": "2px solid #000",
				"border-radius": "5px",
				"height": "25px",
				"width": "115px",
				"text-align": "center"
			};
		},

		dialog: function(){
			return false;
		}

	}, {type: "Fork"}));

})(window.BPMN4TOSCAModeler);