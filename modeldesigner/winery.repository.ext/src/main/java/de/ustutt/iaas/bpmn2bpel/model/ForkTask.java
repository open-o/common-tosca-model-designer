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
import java.util.Iterator;
import java.util.List;

public class ForkTask extends AndGatewaySplit {

    public List<GatewayBranch> getBranchList() {
        List<GatewayBranch> branchList = new ArrayList<GatewayBranch>();

        for (Iterator<GatewayBranch> iterator = branchMap.values().iterator(); iterator.hasNext();) {
            GatewayBranch branch = iterator.next();
            branchList.add(branch);
        }

        return branchList;
    }
}
