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
package org.eclipse.winery.repository.ext.yamlmodel;

/**
 * Currently not used, but should be used in the future for type definitions!
 * @author Sebi
 */
public class ArtifactDefinition extends YAMLElement {

    private String type = "";
    private String mime_type = "";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type != null) {
            this.type = type;
        }
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        if (mime_type != null) {
            this.mime_type = mime_type;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ArtifactDefinition that = (ArtifactDefinition) o;

        if (!mime_type.equals(that.mime_type)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + mime_type.hashCode();
        return result;
    }
}
