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
(function(window, bootstrap){

	window.BPMN4TOSCAModeler = bootstrap(Backbone, {
		Collection: {},
		Elements: {},
		Helper: {},
		Model: {},
		View: {}
	});

}(this, function(Backbone, Modeler){

	Modeler.getQueryParameter = function(name){
	    var expression = new RegExp("[\\?&]" + (name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]")) + "=([^&#]*)");
	    var matches = expression.exec(window.location.search);
	    return matches === null ? "" : decodeURIComponent(matches[1].replace(/\+/g, " "));
	};

	Modeler.initialize = function(){

		return new Modeler.View.Canvas({
			el: ".canvas",
			collection: new Modeler.Collection.Managementplan([], {
				winery: new Modeler.Helper.Winery(this.getQueryParameter("repositoryURL"), this.getQueryParameter("namespace"), this.getQueryParameter("id"), this.getQueryParameter("plan")),
				msb : new Modeler.Helper.Msb("/api/microservices/v1/apiRoute")
			})
		}).render();

	};
	
	
	Modeler.Constant = {};

	Modeler.registerElement = function(element){
		this.Elements[element.type] = element;
	};
	
	return Modeler;

}));