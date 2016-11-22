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

package org.eclipse.winery.common.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * WineryConfig. <br/>
 * 
 * @author sun qi
 * @version  V1
 */
public class WineryConfig implements ServletContextListener {
  private static final Logger LOG = LoggerFactory.getLogger(ServletContextListener.class);

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    LOG.info("******* context destroy***********");
  }

  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    LOG.info("******* context init***********");
    initService();
  }

  private void initService() {
    Thread registerInventoryService = new Thread(new ServiceRegistrer());
    registerInventoryService.setName("register modelDesigner service to Microservice Bus");
    registerInventoryService.start();
  }

}
