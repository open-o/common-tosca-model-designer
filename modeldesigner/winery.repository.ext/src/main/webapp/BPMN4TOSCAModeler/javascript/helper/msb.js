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

	Application.Helper.Msb = function(url){ // TODO
		this._predefineServiceLength = 0;	// Ô¤ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½í¦Ží´¶ï¿½
		this.datas = new Array();			//	ï¿½ï¿½ï¿½ï¿½ï¿½Ð±ï¿½ ï¿½ï¿½Ô¤ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½msbï¿½ï¿½×¢ï¿½ï¿½Ä·ï¿½ï¿½ï¿½
		this.url = url;						// ï¿½ï¿½È¡msbï¿½ï¿½ï¿½ï¿½ï¿½Ð±ï¿½ï¿½Ö·


		// ï¿½ï¿½ï¿½Ô¤ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
		this._predefineService = function() {
			var statusUrl = "http://127.0.0.1:80/api/nsoc/v1/appinstances/{instanceId}/operations/{operation}/callbackstatus";
			var resourceUrl = "http://127.0.0.1:80/api/nsoc/v1/appinstances/{instanceId}/operations/{operation}/callbackresource";

			var predefineServiceData = {
				"id":"predefine", "name":"predefine services", "url":"",
				"interfaces" : [
		                        {
		                        	"id":"resourceUrl", "name":"Resource", "method":"POST", "Content-Type":"application/json", "accept":"application/json", "url":resourceUrl,
		                        	"params":{
		                        		"input":[
		                        			{"in":"path", "name":"instanceId", "type":"string"},
											{"in":"path", "name":"operation", "type":"string"},
		                        			{"in":"query", "name":"callbackId", "type":"string"},
		                        			{"in":"body", "name":"body", "type":"string"}
		                        		],
		                        		"output" : [
		                        			{"name":"result", "type":"string"},
		                        		]
		                        	}
		                    	},
		                    	{
		                        	"id":"statusUrl", "name":"Status", "method":"POST", "Content-Type":"application/json", "accept":"application/json", "url":statusUrl,
		                        	"params":{
		                        		"input":[
		                        			{"in":"path", "name":"instanceId", "type":"string"},
											{"in":"path", "name":"operation", "type":"string"},
		                        			{"in":"query", "name":"callbackId", "type":"string"},
		                        			{"in":"body", "name":"body", "type":"string"}
		                        		],
		                        		"output" : [
		                        			{"name":"result", "type":"string"},
		                        		]
		                        	}
		                    	}
				]
			};

			this.datas.push(predefineServiceData);
			this._predefineServiceLength = this.datas.length;
		};

		this._predefineService();



		this.microservices = function(callback) {
			 if(this.datas.length == this._predefineServiceLength) {
			 	this.getServices(this.datas, callback);
			 } else {
				callback(this.datas);
			 }
			
		};

		this.microserviceInterfaces = function(microserviceId, callback) {
			_.each(this.datas, function(microservice){
					if(microservice.id == microserviceId) {
						if(microservice.interfaces == null) {
							this.getPublicInterfaces(microservice, callback);
						} else {
							callback(microservice.interfaces);
						}
					}
				}, this);
		};


		this.microServiceInterfaceParameter = function(microservice, publicInterface, callback) {
			_.each(this.datas, function(data){
					if(data.id == microservice) {
						_.each(data.interfaces, function(pi){
							if(pi.id == publicInterface) {
								callback(pi.params);
								// return;
							}
						}, this);
					}
				}, this);

		};



		// this.datas = [
		// 				{
		// 					"id":"serviceId1", "name":"serviceName1", "url":"/api/ssh/swagger.json"
							// "interfaces":[
		     //                    {
		     //                    	"id":"serviceId1pi1", "name":"lvboRestPOST", "method":"POST", "Content-Type":"application/json", "accept":"application/json", "url":"http://10.74.44.37:8080/jerseytest2/rest/MOC",
		     //                    	"params":{
		     //                    		"pathParams" : [],
		     //                    		"input":["POSTinputParam1", "POSTinputParam2", "POSTinputParam3"],
		     //                    		"output" : ["outputParam1", "outputParam2"]
		     //                    	}
		     //                	},
		     //                	{
		     //                    	"id":"serviceId1pi2", "name":"RestGET", "method":"GET", "Content-Type":"application/json", "accept":"application/json", "url":"http://10.74.44.37:8080/jerseytest2/rest/MOC/{pathParam1}/{pathParam2}",
		     //                    	"params":{
		     //                    		"pathParams" : ["pathParam1", "pathParam2"],
		     //                    		"input":["GETinputParam1", "GETinputParam2", "GETinputParam3"],
		     //                    		"output" : ["outputParam1", "outputParam2"]
		     //                    	}
		     //                	},
		     //                	{
		     //                    	"id":"serviceId1pi3", "name":"postPathParam", "method":"POST", "Content-Type":"application/json", "accept":"application/json", "url":"http://10.74.44.37:8080/jerseytest2/rest/MOC/{pathParam1}/{pathParam2}",
		     //                    	"params":{
		     //                    		"pathParams" : ["pathParam1", "pathParam2"],
		     //                    		"input":["inputParam1", "inputParam2", "inputParam3"],
		     //                    		"output" : ["outputParam1", "outputParam2"]
		     //                    	}
		     //                	}
		     //                ]
						// },
						// {
						// 	"id":"serviceId2", "name":"serviceName2", "url":"/api/ssh/swagger.json"
							// "interfaces":[
		     //                    {
		     //                    	"id":"serviceId2pi1", "name":"serviceName2rest1", "method":"POST", "Content-Type":"application/json", "accept":"application/json", "url":"http://127.0.0.1:8080",
		     //                    	"params":{
		     //                    		"pathParams" : ["pathParam1", "pathParam2"],
		     //                    		"input":["inputParam1", "inputParam2", "inputParam3"],
		     //                    		"output" : ["outputParam1", "outputParam2"]
		     //                    	}
		     //                	},
		     //                	{
		     //                    	"id":"serviceId2pi2", "name":"serviceName2rest2", "method":"POST", "Content-Type":"application/json", "accept":"application/json", "url":"http://127.0.0.1:8080",
		     //                    	"params":{
		     //                    		"pathParams" : ["pathParam1", "pathParam2"],
		     //                    		"input":["inputParam1", "inputParam2", "inputParam3"],
		     //                    		"output" : ["outputParam1", "outputParam2"]
		     //                    	}
		     //                	},
		     //                	{
		     //                    	"id":"serviceId2pi3", "name":"serviceName2rest3", "method":"POST", "Content-Type":"application/json", "accept":"application/json", "url":"http://127.0.0.1:8080",
		     //                    	"params":{
		     //                    		"pathParams" : ["pathParam1", "pathParam2"],
		     //                    		"input":["inputParam1", "inputParam2", "inputParam3"],
		     //                    		"output" : ["outputParam1", "outputParam2"]
		     //                    	}
		     //                	}
		     //                ]
					// 	}
					// ];

		// get rest interfaces of micro service
		this.getPublicInterfaces = function (service, callback) {
			new SwaggerApiParser(service.url, function (restArray) {
				// console.log(restArray);
				// var restArray = this.restInterfaces;
				_.each(restArray, function(rest){
						rest.url = "http://127.0.0.1:80" + rest.url;

						var patt=/\{(.*?)\}/g;
						var pathParams = rest.url.match(patt);

						rest.params.pathParams = new Array();

						_.each(pathParams, function(pathParam){
							var tmp=pathParam.substring(1, pathParam.length-1);
							rest.params.pathParams.push(tmp);
						}, this);

				}, this);

				service.interfaces = restArray;

				callback(service.interfaces);


			});
			
		};


		this.getServices = function(datas, callback){
			$.ajax({
				// crossDomain: true,
				type: 'GET',
				dataType: "json",
				success: function(response){

					_.each(response, function(service){
							var serviceData = {"id":service.serviceName, "name":service.serviceName, "url":service.apiJson};
							datas.push(serviceData);
						}, this);
					
					callback(datas);
				},
				url: this.url
			});
		};




		this.restInterfaces = [
		                     {
		                    	 "Content-Type" : "application/json",
		                    	 "accept":"application/json",
		                    	 "id":"12",
		                    	 "method":"post",
		                    	 "name":"uploadFiles1",
		                    	 "url":"/api/ssh/v1/sftp/fileuploads",
		                    	 "params":{
		                    		 "input":["ip","port", "username"],
		                    		 "output":["code", "message"]
		                    	 }
		                    	 
		                     },
		                     {
		                    	 "Content-Type" : "application/json",
		                    	 "accept":"application/json",
		                    	 "id":"11",
		                    	 "method":"post",
		                    	 "name":"uploadFiles2",
		                    	 "url":"/api/ssh/v1/sftp/fileuploads/{pathParam1}",
		                    	 "params":{
		                    		 "input":["ip","port", "username"],
		                    		 "output":["code", "message"]
		                    	 }		
		                    	 
		                     }
		                     
		                     ]; 

	};

})(window.BPMN4TOSCAModeler);