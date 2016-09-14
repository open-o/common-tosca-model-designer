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
package de.ustutt.iaas.bpmn2bpel.model;

import java.util.HashMap;
import java.util.Map;

public class AndGatewaySplit extends Gateway {
    protected Map<String, GatewayBranch> branchMap = new HashMap<String, GatewayBranch>();

    public Map<String, GatewayBranch> getBranchMap() {
        return branchMap;
    }

    public void setBranchMap(Map<String, GatewayBranch> branchMap) {
        this.branchMap = branchMap;
    }

    public void addBranch(String id, GatewayBranch branch) {
        branchMap.put(id, branch);
    }

    public GatewayBranch getBranch(String id) {
        return branchMap.get(id);
    }
}
