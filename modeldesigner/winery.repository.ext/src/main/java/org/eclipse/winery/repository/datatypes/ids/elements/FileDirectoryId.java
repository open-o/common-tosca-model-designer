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
package org.eclipse.winery.repository.datatypes.ids.elements;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.elements.TOSCAElementId;

public class FileDirectoryId extends TOSCAElementId {

    private final static XMLId xmlID = new XMLId("files", false);

    public FileDirectoryId(GenericId parent) {
        super(parent, xmlID);
    }

    public FileDirectoryId(GenericId parent, XMLId xmlID) {
        super(parent, xmlID);
    }

    public FileDirectoryId path(String path) {

        FileDirectoryId result = this;
        if (StringUtils.isEmpty(path)) {
            return result;
        }

        String[] content = path.replaceAll("\\\\", "/").split("/");
        for (String dir : content) {
            if (!dir.trim().isEmpty()) {
                result = new FileDirectoryId(result, new XMLId(dir, false));
            }
        }
        return result;
    }
}
