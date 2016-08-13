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

	Application.Collection.Managementplan = Backbone.Collection.extend({

		defaults: {
			inputparameters: [],
			outputparameters: []
		},

		model: Application.Model.Element,

		filterIncomingConnection: function(to){
			return this.models.filter(function(c){
				return _.contains(c.get("connections"), to.get("id"));
			});
		},
		
		initialize: function(models, options){
			this.options = _.extend(this.defaults, options);
		},

		save: function(callback){
			$.ajax({
				crossDomain: true,
			    contentType: "multipart/form-data; boundary=---------------------------7da24f2e50046",
				data: '-----------------------------7da24f2e50046\r\nContent-Disposition: form-data; name="file"; filename="file.json"\r\nContent-type: plain/text\r\n\r\n' + JSON.stringify(this.toJSON()) + '\r\n-----------------------------7da24f2e50046--\r\n',
				url: this.options.winery.planFile(), 
				type: "PUT",
				success: function(){
					callback();
				}
			});
		},

		toJSON: function(){
			var json = [];
			this.each(function(model){
				json.push(model.toJSON());
			});
			return json;
		},

		traverseBackwards: function(to){
			var path = [to];
			_.each(this.filterIncomingConnection(to), function(t2){
				_.each(this.traverseBackwards(t2), function(t3){
					path.push(t3);
				});
			}, this);
			return path;
		},

		traverseForward: function(from){
			var path = [from];
			_.each(from.get("connections"), function(connection){
				_.each(this.traverseForward(this.findWhere({id: connection})), function(element){
					path.push(element);
				});
			}, this);
			return path;			
		},

		url: function(){
			return this.options.winery.planFile();
		}

	});

})(window.BPMN4TOSCAModeler);