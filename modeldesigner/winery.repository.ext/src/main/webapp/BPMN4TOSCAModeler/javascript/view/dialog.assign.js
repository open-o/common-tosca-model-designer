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

	Application.View.DialogAssign = Backbone.View.extend({

		defaults: {
			name: "",
			sources: ["string"],
			type: "string",
			value: ""
		},

		events: {
			"click .dropdown-menu a": "dropdown"
		},

		types: {
			"string": "String",
			"deployment_artifact": "DA",
			"implementation_artifact": "IA",
			"plan": "Plan",
			"concat": "Concat",
			"topology": "Topology",
			"expression":"Expression"
		},

		dropdown: function(event){
			if(event) event.preventDefault();
			this.trigger(Backbone.$(event.currentTarget).attr("data-event"), event);
		},

		changeSourceType: function(type, value, direction, queryExtension){
			this.$el.find(".query").hide();
			if(type == "string" || type == "expression"){
				this.unselectize(this.$el.find("." + direction + " input"));
				this.$el.find("." + direction + " input").val(value);
			}else{
				var instance = this.selectize(this.$el.find("." + direction + " input"));
				instance.clearOptions();

				var options = [];
				if(type == "concat" || type == "plan"){
					this.$el.find(".query").show();
					_.each(this.model.collection.traverseBackwards(this.model), function(element){
						if(direction == "to") {
							if(element.get("type") == "StartEvent" && element.cid != this.model.cid) {
								_.each(element.get("variable"), function(output, name){
									options.push({text: (element.get("name") + "." + name), type: "reference", value: (element.get("name") + "." + name)});
								});
							}
						} else {
							if(element.cid != this.model.cid){
								_.each(element.get("output"), function(output, name){
									options.push({text: (element.get("name") + "." + name), type: "reference", value: (element.get("name") + "." + name)});
								});
								_.each(element.get("variable"), function(output, name){
									options.push({text: (element.get("name") + "." + name), type: "reference", value: (element.get("name") + "." + name)});
								});
							}
							
							this.$el.find(".query input").val(queryExtension);
						}

					}, this);
				}
				if(type == "concat" || type == "topology"){
					this.model.collection.options.winery.topologyProperties(function(properties){
						_.each(properties, function(property){
							instance.addOption({text: property, value: property});
						});
						instance.setValue(value);
					});
				}
				if(type == "concat" || type == "deployment_artifact"){
					this.model.collection.options.winery.getAllDeploymentArtifacts(function(deploymentArtifacts){
						_.each(deploymentArtifacts, function(deploymentArtifact){
							instance.addOption({text: deploymentArtifact.text, value: deploymentArtifact.id});
						});
						instance.setValue(value);
					});
				}
				if(type == "concat" || type == "implementation_artifact"){
					this.model.collection.options.winery.getAllImplementationArtifacts(function(implementationArtifacts){
						_.each(implementationArtifacts, function(implementationArtifact){
							instance.addOption({text: implementationArtifact.text, value: implementationArtifact.id});
						});
						instance.setValue(value);
					});
				}

				_.each(options, function(option){
					instance.addOption(option);
				}, this);
				
				if((type == "concat")){
					instance.settings.create = function(input){
						return {
				            value: input,
				            text: input,
				            type: "string"
				        }
					};
					instance.settings.maxItems = 1000;

				}else{
					instance.settings.create = false;
					instance.settings.maxItems = 1;
				}
				instance.refreshOptions(false);
				instance.setValue(value);
			}
			this.$el.find("." + direction + " .type").html(this.types[type]);
			this.$el.find("." + direction + " input").attr("data-type", type);
		},

		fromModel: function(){
			if(this.model.has("params")){
				if(this.model.get("params")[this.options.to]){
					var obj = this.model.get("params")[this.options.to];
					return {"type":obj.type, "from":obj.value, "to":this.options.to, "extension":obj.extension};
				} 
				
			}
			return {type: this.options.type, from: "", to:"", extension:""};
		},


		mergeOptions : function(options) {
			var mergedOptions = {
								name: "",
								sources: ["string"],
								type: "string",
								value: ""
							};
			

			for(var key in options) {
				mergedOptions[key] = options[key];
			}

			return mergedOptions;
		},

		initialize: function(options){
			// this.options = _.extend(this.defaults, options);
			this.options = this.mergeOptions(options);
			this.on("change", function(event){
				this.changeSourceType(Backbone.$(event.currentTarget).attr("data-type"), "", "from");
			});
			this.on("remove", function(event){
				this.remove();
			});
			this.on("rename", function(event){
				var dialog = new Application.View.Dialog({
					id: "rename",
					parameter: this,
					title: "Rename Parameter",
					name: this.options.name
				});
				dialog.on("confirm", function(event){
					var data = event.dialog.$el.find("form").serializeObject();
					this.options.parameter.$el.find("label").html(data["name"]);
					this.options.parameter.$el.find("input").attr("data-name", data["name"]);
				});
				dialog.on("show", function(event){
					event.dialog.$el.find("form").append(
						"<div class=\"form-group\">"
							+"<label for=\"\" class=\"col-sm-3 control-label\">Name</label>"
							+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"pull right form-control\" id=\"name\" name=\"name\" value=\"" + this.options.name + "\" /></div>"
						+"</div>"
					);
				});
				dialog.on("shown", function(event){
					event.dialog.$el.find("input[name=\"name\"]").focus().select();
				});
				dialog.show();
			});
		},

		render: function(){
			var assignEl  = 
				"<div class=\"form-group parameter\">"
					+"<div class=\"col-sm-7 from\">"
						+"<div class=\"input-group\">"
							+"<input type=\"text\" class=\"form-control\" data-type=\"string\" data-name=\"name\" value=\"\" />"
							+"<div class=\"input-group-btn\">"
								+"<button class=\"btn btn-default dropdown-toggle\" data-toggle=\"dropdown\" type=\"button\"><span class=\"type\">Action</span>&nbsp;<span class=\"caret\"></span></button>"
								+"<ul class=\"dropdown-menu\"></ul>"
							+"</div>"
						+"</div>"
					+"</div>"
					+"<div class=\"col-sm-5 to\">"
						+"<div class=\"input-group\">"
							+"<input type=\"text\" class=\"form-control\" data-type=\"plan\" data-name=\"to\" value=\"\" />"
							+"<div class=\"input-group-btn\">"
								+"<button class=\"btn btn-default dropdown-toggle\" data-toggle=\"dropdown\" type=\"button\"><span class=\"type\">Plan</span>&nbsp;<span class=\"caret\"></span></button>"
								+"<ul class=\"dropdown-menu\">"
									+"<li><a href=\"#\" data-type=\"plan\">Plan</a></li>"
								+"</ul>"
							+"</div>"
						+"</div>"
					+"</div>"
				+"</div>"
				
				+"<div class=\"form-group query\">"
					+"<label for=\"\" class=\"col-sm-1 control-label\">Query</label>"
					+"<div class=\"col-sm-11\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"name\" name=\"name\" value=\"\"/></div>"
				+"</div>"
				
				;

			this.$el.append(assignEl).find(".parameter").data("parameter", this);
			

			_.each(this.options.sources, function(source){
				this.$el.find(".from .dropdown-menu").append(
					"<li><a href=\"#\" data-event=\"change\" data-type=\"" + source + "\">" + this.types[source] + "</a></li>"
				);
			}, this);

			
			this.$el.find(".to .dropdown-menu").append("<li><a href=\"#\" data-event=\"remove\">Remove</a></li>");


			var parameter = this.fromModel();
			this.changeSourceType(parameter.type, parameter.from, "from", parameter.extension);
			this.changeSourceType("plan", parameter.to, "to");


			return this;
		},

		unselectize: function(el){
			if(el.hasClass("selectized")) el.addClass("form-control")[0].selectize.destroy();
		},

		selectize: function(el){
			if(!el[0].selectize){
				var select = el.removeClass("form-control").selectize({

				    create: function(input){
				        return {
				            value: input,
				            text: input,
				            type: "string"
				        }
				    },

				    delimiter: ",",

				    inputClass: "form-control selectize-input",

					onItemAdd: function(dropdown){
					    this.blur();
					},

					onOptionAdd: function(value, data){
					    this.blur();
					},
				    
				    options: [{text: this.options.value, value: this.options.value}],

				    persist: false,

					render: {

						option: function(item, escape){
							return "<div>" + item.text + "</div>";
						}

					}			    
				
				})[0].selectize;
				select.setValue(this.options.value);
				return select;
			}else{
				return el[0].selectize;
			}
		},

		name: function(){
			return this.$el.find("input, .selectized").attr("data-name");
		},

		toJSON: function(){

			var type = this.$el.find(".from input, .selectized").attr("data-type");
			var from = ((this.$el.find(".from .selectized").length == 0) ? this.$el.find(".from input").val() : this.$el.find(".from input")[0].selectize.getValue());
			var to = this.$el.find(".to input")[0].selectize.getValue();
			var extension = this.$el.find(".query input").val();

			return {type: type, from: from, to:to, extension:extension};
		}

	});

})(window.BPMN4TOSCAModeler);