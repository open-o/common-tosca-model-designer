/**
 * Copyright 2017 ZTE Corporation.
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
package org.eclipse.winery.repository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.junit.After;
import org.junit.BeforeClass;

/**
 * @author 10186401
 *
 */
public class AbstractRepositoryTest {

  protected List<GenericId> ids = new ArrayList<GenericId>();

  @BeforeClass
  public static void setUp() throws IOException, URISyntaxException {
    Prefs.INSTANCE = new Prefs(false);
    String location = Prefs.INSTANCE.properties.getProperty("repositoryPath");
    //String location = "winery-repository";
    URL rootPath = Prefs.INSTANCE.getClass().getClassLoader().getResource("");
    String repositoryLocation = Paths.get(rootPath.toURI()).resolve(location).toString();
    Prefs.INSTANCE.repository = new FilebasedRepository(repositoryLocation);
  }

  protected void addClearUpIds(GenericId... genericIds) {
    for (GenericId genericId : genericIds) {
      ids.add(genericId);
    }
  }

  @After
  public void clearUp() {
    if (!ids.isEmpty()) {
      for (GenericId id : ids) {
        BackendUtils.delete(id);
      }
    }
  }
}
