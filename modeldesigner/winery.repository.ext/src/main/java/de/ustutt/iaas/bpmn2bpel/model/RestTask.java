/**
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
package de.ustutt.iaas.bpmn2bpel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.ustutt.iaas.bpmn2bpel.model.param.Parameter;

public class RestTask extends ManagementTask {
    private String methodType;

    private String accept;

    private String url;

    private String request;

    private String contentType;

    private String requestBody;
    
    public String getRequestBody() {
      return requestBody;
    }

    public void setRequestBody(String requestBody) {
      this.requestBody = requestBody;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    private Map<String, Parameter> pathParams = new HashMap<String, Parameter>();

    public void addPathParameter(Parameter param) {
        pathParams.put(param.getName(), param);
    }

    public void setPathParameters(List<Parameter> inputParams) {
        Iterator<Parameter> iter = inputParams.iterator();
        while (iter.hasNext()) {
            Parameter param = (Parameter) iter.next();
            this.pathParams.put(param.getName(), param);

        }
    }

    public Parameter getPathParameter(String name) {
        return pathParams.get(name);
    }

    public List<Parameter> getPathParameters() {
        return new ArrayList<Parameter>(pathParams.values());
    }

    private Map<String, Parameter> queryParams = new HashMap<String, Parameter>();

    public void addQueryParameter(Parameter param) {
        queryParams.put(param.getName(), param);
    }

    public void setQueryParameters(List<Parameter> inputParams) {
        Iterator<Parameter> iter = inputParams.iterator();
        while (iter.hasNext()) {
            Parameter param = (Parameter) iter.next();
            this.queryParams.put(param.getName(), param);

        }
    }

    public Parameter getQueryParameter(String name) {
        return queryParams.get(name);
    }

    public List<Parameter> getQueryParameters() {
        return new ArrayList<Parameter>(queryParams.values());
    }

    private Map<String, Parameter> bodyParams = new HashMap<String, Parameter>();

    public void addBodyParameter(Parameter param) {
        bodyParams.put(param.getName(), param);
    }

    public void setBodyParameters(List<Parameter> inputParams) {
        Iterator<Parameter> iter = inputParams.iterator();
        while (iter.hasNext()) {
            Parameter param = (Parameter) iter.next();
            this.bodyParams.put(param.getName(), param);

        }
    }

    public Parameter getBodyParameter(String name) {
        return bodyParams.get(name);
    }

    public List<Parameter> getBodyParameters() {
        return new ArrayList<Parameter>(bodyParams.values());
    }
}
