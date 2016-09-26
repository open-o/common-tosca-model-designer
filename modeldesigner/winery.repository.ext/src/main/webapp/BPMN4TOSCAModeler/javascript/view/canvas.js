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

	Application.View.Canvas = Backbone.View.extend({

		events:{
			"click": "focus"
		},

		focus: function(event){
			if(event) event.preventDefault();
			this.$el.find(".element").removeClass("focus");
		},

		initialize: function(){
			this.collection.on("add", function(model, collection){
				if(Application.Elements[model.get("type")]){
					var element = new Application.Elements[model.get("type")]({
						model: model
					});
					this.$el.append(element.render().el);
					element.trigger("append");
				}
			}, this);
			this.collection.on("remove", function(model, collection, options){
				_.each(collection.filterIncomingConnection(model), function(element){
					element.removeConnection(model);
				});
			}, this);
			this.collection.on("reset", function(collection){
				this.$el.empty();
			}, this);

			Application.condition = new Application.Condition(this);

// 			jsPlumb.bind("connection", _.bind(function(info, originalEvent){
// 				info.connection.setConnector("Flowchart");
// 				info.connection.addOverlay([ "Arrow", {direction: 1, foldback:1, location: 1, width: 10, length: 10}]);
// //				info.connection.bind("click", function(connection){
// //					Backbone.$(connection.source).data("element").trigger("remove_connection", {target: Backbone.$(connection.target).data("element")});
// //					jsPlumb.detach(connection);
// //				});
// 				this.$el.find("#" + info.sourceId).data("element").trigger("add_connection", {target: this.$el.find("#" + info.targetId).data("element")});
// 			}, this));
			
		},

		render: function(){
// 			jsPlumb.importDefaults({
// 				Anchor: ["Top", "RightMiddle", "LeftMiddle", "Bottom"],
// //				Connector:["Straight", {
// //					cornerRadius: 0,
// //					stub: 0,
// //					gap: 3 
// //				}],
// 				Connector:"StateMachine",
// 		        HoverPaintStyle: {strokeStyle: "#1e8151", lineWidth: 2 },
// 		        ConnectionOverlays: [
// 		            [ "Arrow", {
// 		                location: 1,
// 		                id: "arrow",
// 		                length: 14,
// 		                foldback: 0.8
// 		            } ],
// 		            [ "Label", { label: "FOO", id: "label", cssClass: "aLabel" }]
// 		        ],
// 				PaintStyle: {lineWidth: 1}
// 			});
			this.collection.fetch({
				success: _.bind(function(collection, response, options){
					collection.each(function(model){
						_.each(model.get("connections"), function(connection){
							if((Backbone.$("#" + model.get("id")).length > 0) && (Backbone.$("#" + connection).length > 0)){
								jsPlumb.connect({endpoint: "Blank", source: Backbone.$("#" + model.get("id")), target: Backbone.$("#" + connection)});
							}
						});
					});
				}, this)
			});
			this.$el.append(new Application.View.CanvasToolbar({canvas: this}).render().el);
			return this;
		}

	});

})(window.BPMN4TOSCAModeler);