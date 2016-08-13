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
(function(Application){

	Application.View.CanvasToolbar = Backbone.View.extend({

		defaults: {

		},

		events: {
			"click .save": "save"
		},

		initialize: function(options){
			this.options = _.extend(this.defaults, options);
			this.options.canvas.$el.droppable({

				accept: ".item",

				drop: _.bind(function(event, ui){
					if((ui.offset.top <= 44 && ui.offset.left > 465) || (ui.offset.top > 44)){
					 	this.options.canvas.collection.add(_.extend({
					 		name: _.uniqueId("Element "),
					 		position: {
								left: ui.offset.left,
								top: ui.offset.top
					 		},
					 	}, _.pick(Backbone.$(ui.helper).data(), ["name", "type", "node_template", "microservice"])))
					 	this.$el.find(".dropdown").trigger("click");
					}
				}, this)

			});
		},

		render: function(){
			this.$el.addClass("toolbar").html(
				"<div class=\"form-group\">"
					+"<div class=\"btn-group\">"
						+"<button type=\"button\" class=\"btn btn-default item\" data-name=\"StartEvent\" data-type=\"StartEvent\" title=\"Add new Start Event\"><span>Start Event</button>"
						+"<button type=\"button\" class=\"btn btn-default item\" data-name=\"EndEvent\"data-type=\"EndEvent\" title=\"Add new End Event\"><span>End Event</span></button>"
						
						+"<button type=\"button\" class=\"btn btn-default item\" data-name=\"Assign\"data-type=\"Assign\" title=\"Add Assign\"><span>Assign</span></button>"
						
						// +"<button type=\"button\" class=\"btn btn-default item\" data-type=\"Gateway\" title=\"Add new Parallel Gateway\"><span>Gateway</span></button>"
						+"<div name='gateway' class=\"btn-group\">"
							+"<div class=\"dropdown\" style=\"display: inline-block\">"
								+"<button type=\"button\" class=\"btn btn-default item\" data-name=\"Unnamed NodeManagementTask\" data-type=\"ToscaNodeManagementTask\" data-toggle=\"dropdown\" title=\"Add new Node Management Task\">GATEWAY&nbsp;<span class=\"caret\"></span></button>"
								+"<ul class=\"dropdown-menu\">"
									+"<li><a href=\"#\" class=\"item\" data-type=\"Decision\" data-name=\"Decision\" title=\"Add new Parallel Gateway \">Decision</a></li>"
									+"<li><a href=\"#\" class=\"item\" data-type=\"Merge\" data-name=\"Merge\" title=\"Add new Merge \">Merge</a></li>"
									+"<li><a href=\"#\" class=\"item\" data-type=\"Fork\" data-name=\"Fork\" title=\"Add new Fork \">Fork</a></li>"
									+"<li><a href=\"#\" class=\"item\" data-type=\"Join\" data-name=\"Join\" title=\"Add new Join \">Join</a></li>"
									+"<li><a href=\"#\" class=\"item\" data-type=\"While\" data-name=\"While\" title=\"Add new WhileStart \">WhileStart</a></li>"									
									+"<li><a href=\"#\" class=\"item\" data-type=\"RepeatUntil\" data-name=\"RepeatUntil\" title=\"Add new RepeatStart \">RepeatStart</a></li>"
									+"<li><a href=\"#\" class=\"item\" data-type=\"Loop\" data-name=\"End\" title=\"Add new LoopEnd \">LoopEnd</a></li>"
								+"</ul>"
							+"</div>"
						+"</div>"

						+"<div name='ia' class=\"btn-group\">"
							+"<div class=\"dropdown\" style=\"display: inline-block\">"
								+"<button type=\"button\" class=\"btn btn-default item\" data-name=\"Unnamed NodeManagementTask\" data-type=\"ToscaNodeManagementTask\" data-toggle=\"dropdown\" title=\"Add new Node Management Task\">Node Management Task&nbsp;<span class=\"caret\"></span></button>"
								+"<ul class=\"dropdown-menu\"></ul>"
							+"</div>"
						+"</div>"
						+"<div name='pi' class=\"btn-group\">"
							+"<div class=\"dropdown\" style=\"display: inline-block\">"
								+"<button type=\"button\" class=\"btn btn-default item\" data-name=\"Unnamed NodeManagementTask\" data-type=\"ToscaNodeManagementTask\" data-toggle=\"dropdown\" title=\"Add Public Rest Interface\">Public service&nbsp;<span class=\"caret\"></span></button>"
								+"<ul class=\"dropdown-menu\"></ul>"
							+"</div>"
						+"</div>"
					+"</div>"
					+"<div class=\"pull-right btn-group\">"
					        +"<button type=\"button\" class=\"btn btn-success back\" onclick=\"history.go(-1)\"><span>Back</span></button>"
						+"<button type=\"button\" class=\"btn btn-success save\"><span>Save</span></button>"
					+"</div>"					
				+"</div>"
			);

			// add nodeTemplate to select
			this.options.canvas.collection.options.winery.nodeTemplates(_.bind(function(nodeTemplates){
				_.each(nodeTemplates, function(nodeTemplate){
					this.$el.find("div[name='ia']>.dropdown ul").append(
						"<li><a href=\"#\" class=\"item\" data-type=\"ToscaNodeManagementTask\" data-node_template=\"" + nodeTemplate.name + "\" data-name=\"" + nodeTemplate.name + " NodeManagementTask\" title=\"Add new " + nodeTemplate.name + " Node Management Task\">" + nodeTemplate.name + "</a></li>"
					);
				}, this);
				this.$el.find(".item").draggable({cancel: false, helper: "clone"});
			}, this), this);
			
			
			// add by lvbo start 
			// add rest interfaces to public interfaces
			this.options.canvas.collection.options.msb.microservices(_.bind(function(services){
				_.each(services, function(service){
					this.$el.find("div[name='pi']>.dropdown ul").append(
						"<li><a href=\"#\" class=\"item\" data-type=\"RestTask\" data-microservice=\"" + service.id + "\"  data-name=\"Unnamed " + service.name + " RestTask\" title=\"Add new " + service.name + " Rest Task\">" + service.name + "</a></li>"
					);
				}, this);
				this.$el.find(".item").draggable({cancel: false, helper: "clone"});
			}, this), this);
			// add by lvbo start 
			
			
			
			
			return this;
		},

		save: function(event){
			if(!this.decisionVerify(this.options.canvas.collection.toJSON()))
			{
				return;
			}
			if(event) event.preventDefault();
			this.$el.find(".btn.save span").html("Saving ..");
			this.options.canvas.collection.save(_.bind(function(){
				this.$el.find(".btn.save span").html("Save");
			}, this));
		},
		
		isNull: function(val){
			if(val == undefined || val == "" || val == null || 0 == val.length){
				return true;
			}
			return false;
		},
		
		isArrSame: function(val){
			 return /(\x0f[^\x0f]+)\x0f[\s\S]*\1/.test("\x0f"+val.join("\x0f\x0f") +"\x0f");
		},
		
		decisionVerify: function(jsonData){
			console.log(jsonData);
			var dataLen = jsonData.length;
			for ( var i = 0; i < dataLen; i++)
			{
				if("Decision" == jsonData[i].type)
				{
					var cData = jsonData[i].conditions;
					var cLen = cData.length;
					var isNull = 0;
					var notNull = 0;
					var arrIndex = [];
					
					for ( var j = 0; j < cLen; j++)
					{
						if(this.isNull(cData[j].condition))
						{
							isNull++;
						}
						
						if(!this.isNull(cData[j].condition))
						{
							notNull++;
						}
						
						arrIndex.push(cData[j].index);
					}
					
					if(0 < cLen)
					{
						if(1 < isNull)
						{
							this.errorDialog("There is only one condition is empty!");
							return false;
						}
						
						if(1 > notNull)
						{
							this.errorDialog("At least one of the condition is not empty!");
							return false;
						}
						
						if(this.isArrSame(arrIndex))
						{
							this.errorDialog("The index is not the same!");
							return false;
						}
					}
				}
			}
			return true;
		},
		
		errorDialog: function(msg){
			var dialog = new Application.View.Dialog({
				id: ("error_dialog"),
				title: "Warning"
			});
			dialog.on("show", function(event){
				event.dialog.$el.find("form").append(
					"<div>"
					    + msg
					+"</div>"
				);
				event.dialog.$el.find(".modal-footer .btn-danger").text("OK");
				event.dialog.$el.find(".modal-footer .btn-success").css("display","none");
			});
			dialog.show();
		}
	});

})(window.BPMN4TOSCAModeler);













