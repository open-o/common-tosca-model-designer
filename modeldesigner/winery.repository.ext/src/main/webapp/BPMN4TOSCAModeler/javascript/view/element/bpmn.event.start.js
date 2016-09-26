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
(function(Application){

	Application.registerElement(Application.View.Element.extend({

		content: function(){
			return "";
		},

		css: function(){
			return {
				"-webkit-border-radius": "30px",
				"-moz-border-radius": "30px",
				"background-image": "url('data:image/png;base64,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')",
				"background-size": "cover",
				"border": "2px solid #000",
				"border-radius": "30px",
				"height": "30px",
				"width": "30px"
			};
		},

		dialog: function(event){
			if(event) event.stopPropagation();
			var dialog = new Application.View.Dialog({
				id: (this.model.get("id") + "_dialog"),
				model: this.model,
				title: "Edit Start Event"
			});
			dialog.on("appendInput", function(event){
				var parameter = new Application.View.DialogParameter({
					direction: "output",
					editable: true,
					model: this.model,
					name: _.uniqueId("Parameter"),
					sources: ["string"],
					type : "string"
				});
				event.dialog.$el.find("form .inputParameter").append(parameter.render().el);
				parameter.trigger("rename");
			});
			dialog.on("appendOutput", function(event){
				var parameter = new Application.View.DialogParameter({
					direction: "variable",
					editable: true,
					model: this.model,
					name: _.uniqueId("Parameter"),
					sources: ["string"],
					type : "string"
				});
				event.dialog.$el.find("form .outputParameter").append(parameter.render().el);
				parameter.trigger("rename");
			});
			dialog.on("confirm", function(event){
				this.model.set({
					name: event.dialog.$el.find("#name").val(),
					output: _.object(_.map(event.dialog.$el.find(".parameter.output"), function(parameter){
						var json = Backbone.$(parameter).data("parameter").toJSON();
						return [Backbone.$(parameter).data("parameter").name(), json];
					})),
					variable: _.object(_.map(event.dialog.$el.find(".parameter.variable"), function(parameter){
						var json = Backbone.$(parameter).data("parameter").toJSON();
						return [Backbone.$(parameter).data("parameter").name(), json];
					}))
				});
			});
			dialog.on("show", function(event){
				event.dialog.$el.find("form").append(
					"<div class=\"form-group\">"
						+"<label for=\"\" class=\"col-sm-3 control-label\">Name</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"name\" name=\"name\" value=\"" + this.model.get("name") + "\"/></div>"
					+"</div>"
					+"<hr/>"
					+ "<div class=\"inputParameter\"></div>"
					+"<hr/>"
					+ "<div class=\"outputParameter\"></div>"

				);
				event.dialog.$el.find(".modal-footer .pull-left").append(
					"<button type=\"button\" class=\"btn btn-default\" data-event=\"appendInput\">Append Input Parameter</button>"
					+ "<button type=\"button\" class=\"btn btn-default\" data-event=\"appendOutput\">Append Variable</button>"
				);
				
				// begin init display
				var initPara = ['serviceTemplateId','containerapiUrl','resourceUrl','statusUrl','instanceId', 'callbackId','roUrl','flavor','flavorParams','vnfmId','iaUrl'];
				var count = initPara.length;
				var setInitPara = function(name)
				{
					var parameters = new Application.View.DialogParameter({
						direction: "output",
						editable: false,
						model: dialog.model,
						name: name,
						sources: ["string"],
						hide : "hide",
						type : "string"
					});
					
					event.dialog.$el.find("form").append(parameters.render().el);
				};
				
				var isInitPara = function(name)
				{
					for ( var int = 0; int < count; int++)
					{
						if(name == initPara[int])
						{
							return true;
						}
					}
					
					return false;
				};
				
				// new created Start Event
				if(undefined == this.model.get("output"))
				{
					for ( var int = 0; int < count; int++)
					{
						setInitPara(initPara[int]);
					}
				}
				// end init display
				
				// input params
				_.each(this.model.get("output"), function(parameter, name){
					var options = {
						direction: "output",
						editable: isInitPara(name)?false:true,
						model: this.model,
						hide: isInitPara(name)?"hide":"",
						name: name,
						sources: ["string"]
					};
					if(isInitPara(name)) {
						event.dialog.$el.find("form").append(new Application.View.DialogParameter(options).render().el);
					} else {
						event.dialog.$el.find("form .inputParameter").append(new Application.View.DialogParameter(options).render().el);
					}
					
					
					
				}, this);
				
				// variables
				_.each(this.model.get("variable"), function(parameter, name){
					event.dialog.$el.find("form .outputParameter").append(new Application.View.DialogParameter({
						direction: "variable",
						editable: true,
						model: this.model,
						hide: "",
						name: name,
						sources: ["string"]
					}).render().el);
					
				}, this);
				
			});
			dialog.show();
		}

	}, {type: "StartEvent"}));

})(window.BPMN4TOSCAModeler);