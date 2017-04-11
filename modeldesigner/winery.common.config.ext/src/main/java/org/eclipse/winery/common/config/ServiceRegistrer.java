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

package org.eclipse.winery.common.config;

import com.google.gson.Gson;

import org.eclipse.winery.common.config.externalservice.entity.ConfigEntity;
import org.eclipse.winery.common.config.externalservice.entity.MsbConfig;
import org.eclipse.winery.common.config.externalservice.entity.ServiceRegisterEntity;
import org.eclipse.winery.common.config.externalservice.msb.HttpClientUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Parameters. <br/>
 * 
 * @author sun qi
 * @version V1
 */
public class ServiceRegistrer implements Runnable {
  private final ArrayList<ServiceRegisterEntity> serviceEntityList =
      new ArrayList<ServiceRegisterEntity>();
  private String apiUri = "/microservices/v1/services?createOrUpdate=true";
  private String host = "127.0.0.1:80";
  private String path = "/openoapi";
  private String serviceIp = null;
  private String configFile = "winery.yml";
  private MsbConfig config;
  private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistrer.class);

  public ServiceRegistrer() {
    readConfigFile();
    if (config.getAutoRegister()) {
      initServiceEntity();
    }

  }

  @Override
  public void run() {
    LOG.info("start modeldesigner microservice register");
    ServiceRegisterEntity entity = new ServiceRegisterEntity();
    int retry = 0;
    while (retry < 1000 && serviceEntityList.size() > 0) {
      Iterator<ServiceRegisterEntity> it = serviceEntityList.iterator();
      while (it.hasNext()) {
        HttpClientUtil httpClient = new HttpClientUtil(this.host, path + apiUri);
        entity = it.next();
        LOG.info("start" + entity.getServiceName() + "  microservice register.retry:" + retry);
        httpClient.post(new Gson().toJson(entity));
        if (httpClient.getStatus() != 0) {
          LOG.warn(
              entity.getServiceName() + " microservice register failed, sleep 30S and try again.");
          threadSleep(30000);
        } else {
          LOG.info(entity.getServiceName() + " microservice register success!");
          it.remove();
        }
      }
      retry++;
    }
    LOG.info("modeldesigner microservice register end.");
  }

  private void threadSleep(int second) {
    LOG.info("start sleep ....");
    try {
      Thread.sleep(second);
    } catch (InterruptedException error) {
      LOG.error("thread sleep error.errorMsg:" + error.getMessage());
    }
    LOG.info("sleep end .");
  }

  private void initServiceEntity() {
    ServiceRegisterEntity wineryEntity = new ServiceRegisterEntity();
    wineryEntity.setServiceName("modeldesigner");
    wineryEntity.setProtocol("REST");
    wineryEntity.setUrl("/modeldesigner");
    wineryEntity.setSingleNode(serviceIp, "8202", 0);
    wineryEntity.setVisualRange("1");
    serviceEntityList.add(wineryEntity);
    ServiceRegisterEntity topoEntity = new ServiceRegisterEntity();
    topoEntity.setServiceName("modeldesigner-topologymodeler");
    topoEntity.setProtocol("REST");
    topoEntity.setUrl("/modeldesigner-topologymodeler");
    topoEntity.setSingleNode(serviceIp, "8202", 0);
    topoEntity.setVisualRange("1");
    serviceEntityList.add(topoEntity);
  }

  private void readConfigFile() {
    String configFile = getConfigFilePath();
    if (configFile == null) {
      return;
    }
    Yaml yaml = new Yaml();
    try {
      config = yaml.loadAs(new FileInputStream(new File(configFile)), MsbConfig.class);
    } catch (FileNotFoundException e1) {
      LOG.error("load config file faild errorMsg:" + e1.getMessage());
      return;
    }
    LOG.info("config file detail:" + config.toString());
    if (config == null) {
      return;
    }
    if (config.getMsbServerAddr() != null && config.getMsbServerAddr().length() > 0) {
      host = config.getMsbServerAddr();
    }
    if (config.getServiceIp() != null && config.getServiceIp().length() > 0) {
      this.serviceIp = config.getServiceIp();
    }
    if (config.getPath() != null && config.getPath().length() > 0) {
      this.path = config.getPath();
    }
  }

  private String getConfigFilePath() {
    String classPath = null;
    String configFilePath = null;
    try {
      classPath = ServiceRegistrer.class.getResource("/").toURI().getPath();
    } catch (URISyntaxException e1) {
      LOG.error("get class path faild errorMsg:" + e1.getMessage());
      return null;
    }
    LOG.info(" class path:" + classPath);
    configFilePath = classPath + "../../../../../" + "conf" + File.separator + configFile;
    LOG.info("config file path:" + configFilePath);
    return configFilePath;
  }


}
