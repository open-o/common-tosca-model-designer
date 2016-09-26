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

	Application.Condition = function(canvas){ //
		this.canvas = canvas;

		// getSource endPoint
		this.getSource = function(sourceId) {
			var sourceEndpoint = null;
			_.each(Application.condition.canvas.collection.models, function(model){
					if(model.id == sourceId) {
						sourceEndpoint = model;
					}
					
				}, this);
			
			return sourceEndpoint;
		};

		this.getCondition = function(model, targetId) {
			var condition = null;
			_.each(model.attributes.conditions, function(tmp){
					if(tmp.id == targetId) {
						condition = tmp;
					}
					
				}, this);
			
			return condition;
		};
		
		
		this.removeCondition = function(model, targetId) {
			var index = 0;

			if(model.attributes.conditions) {
				var length = model.attributes.conditions.length;
				for(var i=0; i<length; i++) {
					if(model.attributes.conditions[i].id == targetId) {
						index = i;
						break;
					}
				}
				
				model.attributes.conditions.splice(index,1);
			}
			
		};
		
		
		// ´ÓÁ¬ÏßÖÐ»ñÈ¡Ìõ¼þÖµ
		this.getConditionFromLabel = function(connection) {
			var condition = {};
			
			var labelId = connection.getOverlay("label").getElement().id;
			condition.index = $("#" + labelId + " span[name='sequence']").text();
			condition.condition = $("#" + labelId + " span[name='condition']").text();
			
			return condition;
		};
		
		
		// ÉèÖÃÁ¬ÏßÉÏÏÔÊ¾µÄLabel
		this.setConditionToLabel = function(connection, index, condition) {
			if(index == null || "" == index) {
				index = "";
			} 
			
			if(condition == null || "" == condition) {
				condition = "";
			} 
			
			var label = "<span name='sequence'>" +index + "</span><span name='condition'>" + condition + "</span>";
			
			connection.getOverlay("label").setLabel(label);
		};
		
		
		// �޸��߼��ж�����
		this.modifyCondition = function(connection) {
			
			var sourceId = connection.sourceId;
			var targetId = connection.targetId;
			
			var model = Application.condition.getSource(sourceId);
			if(model.attributes.type != Application.Constant.Element_Gateway) { // ֻ�е�Դ�ڵ���Gateway ������������
				return;
			}
			
			var conditionFromLabel = this.getConditionFromLabel(connection);
			var oldIndex = conditionFromLabel.index;
			var oldCondition = conditionFromLabel.condition;
			
			var dialog = new Application.View.Dialog({
				id: ("condition_dialog"),
				model: {},
				title: "Edit Condition"
			});
			
			
			dialog.on("show", function(event){
				event.dialog.$el.find("form").append(""
					+"<div class=\"form-group\">"
						+"<label for=\"\" class=\"col-sm-3 control-label\">sequence</label>"
						+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"sequence\" name=\"sequence\" value=\"\" /></div>"
					+"</div>"
					+"<div class=\"form-group\">"
					+"<label for=\"\" class=\"col-sm-3 control-label\">condition</label>"
					+"<div class=\"col-sm-9\"><input type=\"text\" autocomplete=\"off\" class=\"form-control\" id=\"condition\" name=\"condition\" value=\"\" /></div>"
					+"</div>"
				+"");
				
				$(event.dialog.$el.find("input")[0]).val(oldIndex);
				$(event.dialog.$el.find("input")[1]).val(oldCondition);
			});
			
			dialog.on("confirm", function(event){
				var newIndex = this.$el.find("#sequence")[0].value;
				var newCondition = this.$el.find("#condition")[0].value;
				Application.condition.setConditionToLabel(connection, newIndex, newCondition);
				
				var model = Application.condition.getSource(sourceId);
				
				var condition = Application.condition.getCondition(model, targetId);

				if(condition == null) {
					condition = {"id":targetId, "condition" :newCondition, "index" : newIndex};
					model.attributes.conditions.push(condition);
				} else {
					condition.condition = newCondition;
					condition.index = newIndex;
				}
				
			});
			
			dialog.show();
		};
		

		jsPlumb.importDefaults({
			Anchor: ["Top", "RightMiddle", "LeftMiddle", "Bottom"],
//			Connector:["Straight", {
//				cornerRadius: 0,
//				stub: 0,
//				gap: 3 
//			}],
			Connector:"StateMachine",
	        HoverPaintStyle: {strokeStyle: "#1e8151", lineWidth: 2 },
	        ConnectionOverlays: [
	            [ "Arrow", {
	                location: 1,
	                id: "arrow",
	                length: 14,
	                foldback: 0.8
	            } ],
	            [ "Label", { label: "", id: "label", cssClass: "aLabel" }]
	        ],
			PaintStyle: {lineWidth: 1}
		});
		
		
		jsPlumb.bind("connection", _.bind(function(info, originalEvent){
			info.connection.setConnector("Flowchart");
			info.connection.addOverlay([ "Arrow", {direction: 1, foldback:1, location: 1, width: 10, length: 10}]);
			
			var timer = null; 	// ��������ͬʱע���˵�����˫��ʱ�䣬 �����ʱ�������ڴ�������֮��ĳ�ͻ
			info.connection.bind("click", function(connection){
				clearTimeout(timer); 
				timer = setTimeout(function () { // �ڵ����¼������һ��setTimeout()�������õ����¼�������ʱ���� 
					 Backbone.$(connection.source).data("element").trigger("remove_connection", {target: Backbone.$(connection.target).data("element")});
					 jsPlumb.detach(connection);

					 var sourceModel = Application.condition.getSource(info.connection.sourceId); 
					 Application.condition.removeCondition(sourceModel, info.connection.targetId);
					
				}, 300); 

			});
			info.connection.bind("dblclick", function(connection){
				clearTimeout(timer);
				Application.condition.modifyCondition(connection);
			});
//			this.$el.find("#" + info.sourceId).data("element").trigger("add_connection", {target: this.$el.find("#" + info.targetId).data("element")});
			$("#" + info.sourceId).data("element").trigger("add_connection", {target: $("#" + info.targetId).data("element")});
			


			var sourceModel = this.getSource(info.connection.sourceId); 

			if(sourceModel.attributes.type == Application.Constant.Element_Gateway) {  // �Է�֧�����������⴦��
				if(sourceModel.attributes.conditions == null) {
					sourceModel.attributes.conditions = [];
				}

				var condition = this.getCondition(sourceModel, info.connection.targetId);
				if(condition == null) {
					var index = sourceModel.attributes.conditions.length + 1 + "";
					condition = {"id" : info.connection.targetId, "index":index, "condition":""};
					sourceModel.attributes.conditions.push(condition);
					this.setConditionToLabel(info.connection, index, "");
				} else {
					this.setConditionToLabel(info.connection, condition.index, condition.condition);
				}
			}
			
		}, this));

	};

})(window.BPMN4TOSCAModeler);