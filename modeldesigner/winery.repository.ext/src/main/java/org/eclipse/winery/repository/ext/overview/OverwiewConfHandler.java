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
package org.eclipse.winery.repository.ext.overview;

import java.util.List;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.JsonUtil;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplatesResource;

import com.google.gson.Gson;

/**
 * @author 10186401
 *
 */
public class OverwiewConfHandler {
    public static ServiceTemplatesOverviewConfigration INSTANCE;

    private static String path = getFilePath();

    static {
        init();
    }

    public static void reload() {
        clear();
        init();
    }

    public static void clear() {
        OverwiewConfHandler.INSTANCE = null;
    }

    public static void init() {
        if (null != INSTANCE) {
            return;
        }
        String jsonStr = JsonUtil.readJson(path);
        OverwiewConfHandler.INSTANCE =
                new Gson().fromJson(jsonStr, ServiceTemplatesOverviewConfigration.class);
        if (null == OverwiewConfHandler.INSTANCE) {
            OverwiewConfHandler.INSTANCE = new ServiceTemplatesOverviewConfigration();
        }
        addRepositoryTemplates();
    }

    private static void addRepositoryTemplates() {
        List<TServiceTemplate> allServiceTemplates =
                new ServiceTemplatesResource().getAllServiceTemplates();

        for (TServiceTemplate tServiceTemplate : allServiceTemplates) {
            OverwiewConfHandler.INSTANCE.addInfo(new ServiceTemplateOverviewInfo(tServiceTemplate));
        }
    }

    private static String getFilePath() {
        String clsPath = ServiceTemplatesOverviewResource.class.getResource("/").getPath();
        String path =
                clsPath.replace("/build/classes", "").replace("%20", " ").replace("classes/", "")
                        + "overview.json";
        return path;
    }

    public static void save() {
        JsonUtil.write(INSTANCE, path);
    }
}
