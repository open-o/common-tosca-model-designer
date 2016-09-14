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
				title: "Edit Public Rest Task"
			});
			dialog.on("confirm", function(event){
				var publicInterface = this.$el.find("#publicInterface")[0].selectize.options[this.$el.find("#publicInterface")[0].selectize.getValue()];
				
				var data = {
					name: event.dialog.$el.find("#name").val(),
					microservice: event.dialog.$el.find("#microservice").val(),
					publicInterface: event.dialog.$el.find("#publicInterface").val(),
					method: publicInterface.method,
					accept : publicInterface.accept,
					contentType:publicInterface.contentType,
					url : publicInterface.url,
						
					path: _.object(_.map(event.dialog.$el.find(".parameter.path"), function(parameter){
						var dialogParameter = Backbone.$(parameter).data("parameter");
						return [dialogParameter.name(), dialogParameter.toJSON()];
					})),
					query: _.object(_.map(event.dialog.$el.find(".parameter.query"), function(parameter){
						var dialogParameter = Backbone.$(parameter).data("parameter");
						return [dialogParameter.name(), dialogParameter.toJSON()];
					})),
					body: _.object(_.map(event.dialog.$el.find(".parameter.body"), function(parameter){
						var dialogParameter = Backbone.$(parameter).data("parameter");
						return [dialogParameter.name(), dialogParameter.toJSON()];
					})),
					output: _.object(_.map(event.dialog.$el.find(".parameter.output"), function(parameter){
						var dialogParameter = Backbone.$(parameter).data("parameter");
						return [dialogParameter.name(), dialogParameter.toJSON()];
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
						+"<label for=\"microservice\" class=\"col-sm-3 control-label\">Micro Service</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"microservice\" name=\"microservice\" /></div>"
					+"</div>"
					
					+"<div class=\"form-group\">"
						+"<label for=\"publicInterface\" class=\"col-sm-3 control-label\">Public Interface</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"publicInterface\" name=\"publicInterface\" /></div>"
					+"</div>"

					+"<hr/>"
				+"");
			});
			dialog.on("shown", function(event){
				var interfaceDropdown = event.dialog.$el.find("#publicInterface").selectize({
					maxItems: 1,
					options: []
				})[0].selectize;

				interfaceDropdown.on("change", _.bind(function(value){
					if(this.$el.find("#publicInterface")[0].selectize.getValue()){
						var microservice = this.$el.find("#microservice")[0].selectize.options[this.$el.find("#microservice")[0].selectize.getValue()];
						var publicInterface = this.$el.find("#publicInterface")[0].selectize.options[this.$el.find("#publicInterface")[0].selectize.getValue()];
						this.$el.find(".parameter, hr").remove();

						this.model.collection.options.msb.microServiceInterfaceParameter(microservice.value, publicInterface.value, _.bind(function(parameters){
							_.each({"input": {editable: false, type: "string"}, "output": {editable: false}}, function(constraints, type){
								if(parameters[type].length > 0) event.dialog.$el.find("form").append("<hr/>");
								_.each(parameters[type], function(parameter){
									// var parameter = this.model.has(type) ? this.model.get(type)[name] : false;
									var direction = (type == "input"? parameter.in:"output");
									event.dialog.$el.find("form").append(new Application.View.DialogParameter(_.extend(constraints, {
										direction: direction,
										model: this.model,
										name: parameter.name,
										sources: (type == "output" ? ["topology", "plan"] : ["concat", "string", "plan", "topology", "deployment_artifact", "implementation_artifact"]),
										type: (type == "output" ? "topology" : "string" )
									})).render().el);
								}, this);
							}, this);
							
						}, this));

						if(this.$el.find("#name").val().match(/^Unnamed|^$/i)) this.$el.find("#name").val(publicInterface.text + " " + microservice.text);
					}
				}, event.dialog));
				

				var microserviceDropdown = event.dialog.$el.find("#microservice").selectize({
					maxItems: 1,
					options: [],
				})[0].selectize;

				microserviceDropdown.on("change", _.bind(function(value){
					var microserviceSelectize = this.$el.find("#microservice")[0].selectize;
					var microservice = microserviceSelectize.options[microserviceSelectize.getValue()];
					var publicInterfaceSelectize = this.$el.find("#publicInterface")[0].selectize;

					this.model.collection.options.msb.microserviceInterfaces(microservice.value, function(publicInterfaces){
						interfaceDropdown.clearOptions();
						_.each(publicInterfaces, function(publicInterface){
							interfaceDropdown.addOption({text: publicInterface.name, id: publicInterface.id, value: publicInterface.id, method: publicInterface.method, accept : publicInterface.accept, contentType : publicInterface['Content-Type'], url : publicInterface.url});					
						});
						interfaceDropdown.refreshOptions(false);
						interfaceDropdown.setValue(event.dialog.model.get("publicInterface"));									
					});

				}, event.dialog));

				event.dialog.model.collection.options.msb.microservices(function(microservices){
					microserviceDropdown.clearOptions();
					_.each(microservices, function(microservice){
						microserviceDropdown.addOption({text: microservice.name, id: microservice.id, namespace: microservice.namespace, value: microservice.id});					
					});
					microserviceDropdown.refreshOptions(false);
					microserviceDropdown.setValue(event.dialog.model.get("microservice"));					
				});
				
			});
			dialog.show();
		}

	}, {type: "RestTask"}));

})(window.BPMN4TOSCAModeler);