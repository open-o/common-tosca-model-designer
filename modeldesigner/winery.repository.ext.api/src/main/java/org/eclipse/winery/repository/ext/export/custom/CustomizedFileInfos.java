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
package org.eclipse.winery.repository.ext.export.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.servicetemplate.FileInfo;

/**
 * @author 10186401
 *
 */
public class CustomizedFileInfos {

  private Map<String, RepositoryFileReference> planInfos =
      new HashMap<String, RepositoryFileReference>();
  private List<CustomFileResultInfo> customizedFileResults = new ArrayList<CustomFileResultInfo>();

  public Map<String, RepositoryFileReference> getPlanInfos() {
    return planInfos;
  }

  public void setPlanInfos(Map<String, RepositoryFileReference> planInfos) {
    this.planInfos = planInfos;
  }

  public List<CustomFileResultInfo> getCustomizedFileResults() {
    return customizedFileResults;
  }

  public void setCustomizedFileResults(List<CustomFileResultInfo> customizedFileResults) {
    this.customizedFileResults = customizedFileResults;
  }

  public void addPlanInfo(FileInfo planInfo) {
    this.planInfos.put(planInfo.getFileName(), planInfo.getRef());
  }

  public void addCustFileInfo(CustomFileResultInfo custInfo) {
    this.customizedFileResults.add(custInfo);
  }
}
