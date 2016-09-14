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
	
	Application.Constant.Element_Assign = "Assign";
	
	Application.registerElement(Application.View.Element.extend({

		// content: function(){
		// 	return "";
		// },

		css: function(){
			return {
		        "-webkit-border-radius": "8px",
		        "-moz-border-radius": "8px",
		        "border-radius": "8px",
		        "border": "2px solid #000",
		        "font-size": "10px",
		        "padding": "10px 15px"
  			};
		},

		dialog: function(event){
			
			var dialog = new Application.View.Dialog({
				id: (this.model.get("id") + "_dialog"),
				model: this.model,
				title: "Edit Assign Event"
			});

			dialog.on("append", function(event){
				var fromParameter = new Application.View.DialogAssign({
					direction: "output",
					editable: false,
					model: this.model,
					name: "From",
					sources: ["string", "plan", "expression"],
					type : "plan"
				});
				event.dialog.$el.find("form").append(fromParameter.render().el);

			});

			dialog.on("confirm", function(event){
				var data = {
					name: event.dialog.$el.find("#name").val(),
					params: _.object(_.map(event.dialog.$el.find(".parameter"), function(parameter){
						
						var json = Backbone.$(parameter).data("parameter").toJSON();
						var obj = {"type":json.type, "value":json.from, "extension":json.extension};
						
						
						return [json.to, obj];
					}))
				};

				this.model.set(data);
			});
			dialog.on("show", function(event){
				event.dialog.$el.find("form").append(
					"<div class=\"form-group\">"
						+"<label for=\"\" class=\"col-sm-3 control-label\">Name</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"name\" name=\"name\" value=\"" + this.model.get("name") + "\"/></div>"
					+"</div>"
					+"<hr/>"
				);


				event.dialog.$el.find(".modal-footer .pull-left").append(
					"<button type=\"button\" class=\"btn btn-default\" data-event=\"append\">Append Assign</button>"
				);
				
				_.each(this.model.get("params"), function(parameter, name){
					event.dialog.$el.find("form").append(new Application.View.DialogAssign({
						model: this.model,
						to : name,
						sources : ["string", "plan", "expression"]
					}).render().el);
					
				}, this);
			});
			dialog.show();
		}

	}, {type: "Assign"}));

})(window.BPMN4TOSCAModeler);