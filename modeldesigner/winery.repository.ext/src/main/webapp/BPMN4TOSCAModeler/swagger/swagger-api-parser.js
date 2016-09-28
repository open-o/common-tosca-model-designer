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
                                    if ('sampleJSON' in param && param.sampleJSON != undefined) {
                                        inputParams.push({"name": param.name, "in": param.in, "type": "Object", "sample": param.sampleJSON});
                                        continue;
                                    }
                                    
                                    if ('type' in param) {
                                        sample = '{"' + param.name + '":"' + param.type + '"}';
                                        inputParams.push({"name": param.name, "in": param.in, "type": param.type, "sample": sample});
                                        continue;
                                    }
                                    
                                    inputParams.push({"name": param.name, "in": param.in, "type": "", "sample": ""});
                                }
                            }
                            
                            var outputParams = [];
                            if (operation.successResponse) {
                                var response = operation.successResponse[200] || operation.successResponse[201];
                                if (response && response.definition) {
                                    sample = SwConvert2JsonSample(response.definition, swclient.definitions);
                                    outputParams.push({"name": "response", "type": response.definition.type, "sample": sample});
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
            
            console.debug(restInfoArray);
            callback(restInfoArray);
        }
    });
}

var SwConvert2JsonSample = function (definition, definitions) {
    if (!('type' in definition)) {
        return '""';
    }

    if (definition.type == 'array' && 'items' in definition) {
        ref = definition.items.$ref;
        if (ref) {
            tmps = ref.split('/');
            return '[' + SwConvert2JsonSample(definitions[tmps[tmps.length-1]], definitions) + ']';
        }

        return '[' + definition.items.type + ']';
    }

    if (definition.type == 'object' && 'properties' in definition) {
        sample = '{';
        for (var attrName in definition.properties) {
            sample += '"' + attrName + '": ' + SwConvert2JsonSample(definition.properties[attrName], definitions) + ', ';
        }
        if (sample.length > 2) {
            sample = sample.substring(0, sample.length-2);
        }
        sample += '}';
        return sample;
    }

    return '"' + definition.type + '"';
}