/**
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
package de.ustutt.iaas.bpmn2bpel.model.param;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Sebastian Wagner
 *
 */
public class PlanParameter extends Parameter {

    private String startTaskName;

    private String parameterName;

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getStartTaskName() {
        return startTaskName;
    }

    public void setStartTaskName(String startTaskName) {
        this.startTaskName = startTaskName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        String[] tmp = value.split("\\.");
        this.startTaskName = tmp[0].trim().replaceAll(" ", "_");
        this.parameterName = tmp[1];
    }

    @Override
    public void setExtension(String extension) {
        super.setExtension(extension);
        this.query = extension;
    }

    @Override
    public ParamType getType() {
        return ParamType.PLAN;
    }

}
