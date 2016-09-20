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
package org.eclipse.winery.repository.ext.yamlmodel;


public class Requirement {
    private String node = "";
    private String relationship = "";
    private String capability = "";
    private NodeFilter node_filter;
    private int[] occurrences;

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        if (node != null) {
            this.node = node;
        }
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        if (relationship != null) {
            this.relationship = relationship;
        }
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        if (capability != null) {
            this.capability = capability;
        }
    }

    public NodeFilter getNode_filter() {
        return node_filter;
    }

    public void setNode_filter(NodeFilter node_filter) {
        if (node_filter != null) {
            this.node_filter = node_filter;
        }
    }

    public int[] getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int[] occurrences) {
        if (occurrences != null && occurrences.length == 2) {
            this.occurrences = occurrences;
        }
    }



}