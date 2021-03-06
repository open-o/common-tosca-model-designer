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

		dialog: function(){
			var dialog = new Application.View.Dialog({
				id: (this.model.get("id") + "_dialog"),
				model: this.model,
				title: "Edit Node Management Task"
			});
			dialog.on("confirm", function(event){
				var nodeTemplateOperation = this.$el.find("#node_operation")[0].selectize.options[this.$el.find("#node_operation")[0].selectize.getValue()];
				var data = {
					name: event.dialog.$el.find("#name").val(),
					node_operation: event.dialog.$el.find("#node_operation").val(),
					node_interface: nodeTemplateOperation.interface,
					node_template: event.dialog.$el.find("#node_template").val(),					
					input: _.object(_.map(event.dialog.$el.find(".parameter.input"), function(parameter){
						var json = Backbone.$(parameter).data("parameter").toJSON();
						return [Backbone.$(parameter).data("parameter").name(), json];
					})),
					output: _.object(_.map(event.dialog.$el.find(".parameter.output"), function(parameter){
						var json = Backbone.$(parameter).data("parameter").toJSON();
						return [Backbone.$(parameter).data("parameter").name(), json];
					}))
				};
				this.model.set(data);
			});
			dialog.on("show", function(event){
				event.dialog.$el.find("form").append(""
					+"<div class=\"form-group\">"
						+"<label for=\"\" class=\"col-sm-3 control-label\">Name</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"name\" name=\"name\" value=\"" + this.model.get("name") + "\" /></div>"
					+"</div>"
					+"<div class=\"form-group\">"
						+"<label for=\"node_template\" class=\"col-sm-3 control-label\">Node Template</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"node_template\" name=\"node_template\" /></div>"
					+"</div>"
					
					+"<div class=\"form-group\">"
						+"<label for=\"node_operation\" class=\"col-sm-3 control-label\">Node Operation</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"node_operation\" name=\"node_operation\" /></div>"
					+"</div>"
					+"<hr/>"
				+"");
			});
			dialog.on("shown", function(event){
				var nodeOperationDropdown = event.dialog.$el.find("#node_operation").selectize({
					maxItems: 1,
					options: []
				})[0].selectize;

				nodeOperationDropdown.on("change", _.bind(function(value){
					if(this.$el.find("#node_operation")[0].selectize.getValue()){
						var nodeTemplate = this.$el.find("#node_template")[0].selectize.options[this.$el.find("#node_template")[0].selectize.getValue()];
						var nodeTemplateOperation = this.$el.find("#node_operation")[0].selectize.options[this.$el.find("#node_operation")[0].selectize.getValue()];
						this.$el.find(".parameter, hr").remove();

						this.model.collection.options.winery.nodeTemplateOperationParameter(nodeTemplate, nodeTemplateOperation, _.bind(function(parameters){
							_.each({"input": {editable: false, type: "string"}, "output": {editable: false}}, function(constraints, type){
								if(parameters[type].length > 0) event.dialog.$el.find("form").append("<hr/>");
								_.each(parameters[type], function(name){
									var parameter = this.model.has(type) ? this.model.get(type)[name] : false;
									event.dialog.$el.find("form").append(new Application.View.DialogParameter(_.extend(constraints, {
										direction: type,
										model: this.model,
										name: name,
										sources: (type == "input" ? ["concat", "string", "plan", "topology", "deployment_artifact", "implementation_artifact"] : ["topology"]),
										type: (type == "input" ? "string" : "topology")
									})).render().el);
								}, this);
							}, this);
						}, this));

						if(this.$el.find("#name").val().match(/^Unnamed|^$/i)) this.$el.find("#name").val(this.$el.find("#node_operation")[0].selectize.getValue() + this.$el.find("#node_template")[0].selectize.getValue());
					}
				}, event.dialog));
				

				var nodeTemplateDropdown = event.dialog.$el.find("#node_template").selectize({
					maxItems: 1,
					options: [],
				})[0].selectize;

				nodeTemplateDropdown.on("change", _.bind(function(value){
					var nodeTemplateSelectize = this.$el.find("#node_template")[0].selectize;
					var nodeTemplate = nodeTemplateSelectize.options[nodeTemplateSelectize.getValue()];
					var nodeTemplateOperationSelectize = this.$el.find("#node_operation")[0].selectize;

					this.model.collection.options.winery.nodeTemplateOperations(nodeTemplate, function(operations){
						nodeOperationDropdown.clearOptions();
						_.each(operations, function(operation){
							nodeOperationDropdown.addOption({text: operation.name, id: operation.id, interface: operation.interface, value: operation.name});					
						});
						nodeOperationDropdown.refreshOptions(false);
						nodeOperationDropdown.setValue(event.dialog.model.get("node_operation"));									
					});

				}, event.dialog));

				event.dialog.model.collection.options.winery.nodeTemplates(function(nodeTemplates){
					nodeTemplateDropdown.clearOptions();
					_.each(nodeTemplates, function(nodeTemplate){
						nodeTemplateDropdown.addOption({text: nodeTemplate.name, id: nodeTemplate.id, namespace: nodeTemplate.namespace, value: nodeTemplate.id});					
					});
					nodeTemplateDropdown.refreshOptions(false);
					nodeTemplateDropdown.setValue(event.dialog.model.get("node_template"));					
				});
				
			});
			dialog.show();
		}

	}, {type: "ToscaNodeManagementTask"}));

})(window.BPMN4TOSCAModeler);