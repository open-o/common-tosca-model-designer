/**
 * Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Map;
import java.util.TreeMap;

/**
 * @author 10186401
 *
 */
public class DecisionTask extends AndGatewaySplit {

    private Map<Integer, ConditionElement> indexMap = new TreeMap<Integer, ConditionElement>();

    public List<GatewayBranch> getBranchList() {
        List<GatewayBranch> branchList = new ArrayList<GatewayBranch>();

        for (Iterator<ConditionElement> iterator = indexMap.values().iterator(); iterator.hasNext();) {
            ConditionElement ele = iterator.next();
            GatewayBranch branch = getBranch(ele.getId());
            if (null != branch) {
                if (null != ele.getCondition() && !ele.getCondition().isEmpty()) {
                    branch.setCondition(ele.getCondition());
                }
                branchList.add(branch);
            }
        }

        return branchList;
    }

    public void addIndex(Integer index, String id, String condition) {
        ConditionElement conditionElement = new ConditionElement(id, index, condition);
        indexMap.put(index, conditionElement);
    }

    public class ConditionElement {
        private String id;
        private Integer index;
        private String condition;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public ConditionElement() {
            super();
        }

        public ConditionElement(String id, Integer index, String condition) {
            super();
            this.id = id;
            this.index = index;
            this.condition = condition;
        }
    }
}
