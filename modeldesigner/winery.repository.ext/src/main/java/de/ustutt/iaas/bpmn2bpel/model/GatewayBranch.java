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
/**
 * 
 */
package de.ustutt.iaas.bpmn2bpel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 10186401
 *
 */
public class GatewayBranch {
    private String condition;

    private List<Task> branch = new ArrayList<Task>();

    public GatewayBranch() {
        super();
    }

    public GatewayBranch(List<Task> tasks) {
        setBranch(tasks);
    }

    public List<Task> getBranch() {
        return branch;
    }

    public void setBranch(List<Task> branch) {
        this.branch = branch;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
