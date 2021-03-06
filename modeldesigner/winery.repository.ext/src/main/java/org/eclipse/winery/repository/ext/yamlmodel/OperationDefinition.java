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
package org.eclipse.winery.repository.ext.yamlmodel;

/**
 * @author Sebi
 */
@Deprecated
public class OperationDefinition {

    private String implementation = "";

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        if (implementation != null) {
            this.implementation = implementation;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationDefinition that = (OperationDefinition) o;

        if (!implementation.equals(that.implementation)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return implementation.hashCode();
    }
}
