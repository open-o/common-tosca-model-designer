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
var SwaggerApiParser = function (url, callback) {
    var swclient = new SwaggerClient({
        url: url,
        success: function() {
            var restInfoArray = [];
            if (swclient.apisArray && swclient.apisArray.length > 0) {
                for (var i = 0; i < swclient.apisArray.length; i++) {
                    var moudle = swclient.apisArray[i];
                    var id = 0;
                    if (moudle.operationsArray && moudle.operationsArray.length > 0) {
                        for (var j = 0; j < moudle.operationsArray.length; j++) {
                            var operation = moudle.operationsArray[j];
                            
                            var inputParams = [];
                            if (operation.parameters && operation.parameters.length > 0) {
                                for (var k = 0; k < operation.parameters.length; k++) {
                                    var param = operation.parameters[k];
                                    if (param.in == "path" || param.in == "query") {
                                        inputParams.push({"name": param.name, "in": param.in, "type": param.type});
                                        continue;
                                    }
                                    
                                    if (param.in == "body") {
                                        if(param.sampleJSON != undefined) {
                                            var input = JSON.parse(param.sampleJSON);
                                            for (var attrName in input) {
                                                inputParams.push({"name": attrName, "in": param.in, "type": input[attrName]});
                                            }
                                            continue;
                                        } 
                                        
                                    }
                                }
                            }
                            
                            var outputParams = [];
                            if (operation.successResponse) {
                                var successResponse = operation.successResponse[200] || operation.successResponse[201];
                                if (successResponse && successResponse.definition && successResponse.definition.properties) {
                                    for (var attrName1 in successResponse.definition.properties) {
                                        if (successResponse.definition.properties[attrName1]) {
                                            outputParams.push({"name": attrName1, "type": successResponse.definition.properties[attrName1].type});
                                        } else {
                                            outputParams.push({"name": attrName1, "type": "string"});
                                        }
                                    }
                                }
                            }

                            var restInfo = {
                                "id": moudle.name.replace("/", ".") + "." + id,
                                "name": operation.nickname,
                                "method": operation.method.toLocaleUpperCase(),
                                "Content-Type": operation.produces[0],
                                "accept": operation.consumes[0],
                                "url": swclient.basePath + operation.path,
                                "params":{
                                    "input": inputParams,
                                    "output" : outputParams
                                }
                            }
                            id++;
                            
                            restInfoArray.push(restInfo);
                        }
                    }
                }
            }
            
            callback(restInfoArray);
        }
    });
}

