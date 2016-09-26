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

import java.util.List;

import de.ustutt.iaas.bpmn2bpel.model.param.Parameter;

public class AssignTask extends Task {
    private List<Parameter> params;

    public List<Parameter> getParams() {
        return params;
    }

    public void setParams(List<Parameter> params) {
        this.params = params;
    }

    public AssignTask(List<Parameter> params) {
        super();
        this.params = params;

        convertParams();
    }

    private void convertParams() {
        if (null == this.params) {
            return;
        }

        for (Parameter param : params) {
            String paramName = param.getName();
            if (null != paramName && paramName.contains(".")) {
                String[] tmp = paramName.split("\\.");
                param.setName(tmp[1]);
            }
        }
    }

    public AssignTask() {
        super();
    }

}
