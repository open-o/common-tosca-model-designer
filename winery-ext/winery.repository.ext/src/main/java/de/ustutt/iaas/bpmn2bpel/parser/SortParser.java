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
package de.ustutt.iaas.bpmn2bpel.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ustutt.iaas.bpmn2bpel.model.AndGatewayMerge;
import de.ustutt.iaas.bpmn2bpel.model.AndGatewaySplit;
import de.ustutt.iaas.bpmn2bpel.model.GatewayBranch;
import de.ustutt.iaas.bpmn2bpel.model.ManagementFlow;
import de.ustutt.iaas.bpmn2bpel.model.Task;

/**
 * @author 10186401
 *
 */
public class SortParser {

    private Map<String, Task> taskMap;

    private Map<String, Set<String>> nodeWithTargetsMap;

    public SortParser(Map<String, Task> taskMap, Map<String, Set<String>> nodeWithTargetsMap) {
        super();
        this.taskMap = taskMap;
        this.nodeWithTargetsMap = nodeWithTargetsMap;
    }

    public SortParser() {
        super();
    }

    public ManagementFlow buildManagementFlow(String currentId) {
        List<Task> taskList = buildTaskList(currentId);
        ManagementFlow managementFlow = convert2ManagementFlow(taskList);

        return managementFlow;
    }

    private ManagementFlow convert2ManagementFlow(List<Task> taskList) {
        ManagementFlow managementFlow = new ManagementFlow();

        for (Task task : taskList) {
            managementFlow.addVertex(task);
        }

        Task src = null;
        Task target = null;
        for (int i = 0; i < taskList.size() - 1; i++) {
            src = taskList.get(i);
            target = taskList.get(i + 1);
            managementFlow.addEdge(src, target);
        }

        return managementFlow;
    }

    private List<Task> buildTaskList(String currentId) {
        List<Task> taskList = new ArrayList<Task>();
        boolean hasNext = true;
        do {
            Task task = taskMap.get(currentId);

            Set<String> followSet = nodeWithTargetsMap.get(currentId);

            if (task instanceof AndGatewaySplit) {
                String endGatewayId = decisionNodeHandler((AndGatewaySplit) task);
                taskList.add(task);

                Task endTask = taskMap.get(endGatewayId);
                taskList.add(endTask);

                Set<String> endFollowSet = nodeWithTargetsMap.get(endGatewayId);
                currentId = endFollowSet.iterator().next();
            } else if (task instanceof AndGatewayMerge) {
                hasNext = false;
            } else {
                taskList.add(task);
                if (followSet == null || followSet.size() == 0) {
                    // final node
                    hasNext = false;
                } else {
                    currentId = followSet.iterator().next();
                }
            }
        } while (hasNext);
        return taskList;
    }

    private String decisionNodeHandler(AndGatewaySplit gateway) {
        String endGatewayId = null;

        Iterator<String> iterator = nodeWithTargetsMap.get(gateway.getId()).iterator();

        while (iterator.hasNext()) {
            String currentId = iterator.next();

            List<Task> subList = buildTaskList(currentId);
            gateway.addBranch(currentId, new GatewayBranch(subList));

            if (null == endGatewayId) {
                endGatewayId = getEndDecisionNodeId(subList);
            }
        }

        return endGatewayId;
    }

    private String getEndDecisionNodeId(List<Task> subList) {

        Task lastTask = subList.get(subList.size() - 1);

        Set<String> followSet = nodeWithTargetsMap.get(lastTask.getId());
        return followSet.iterator().next();
    }
}
