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

package org.eclipse.winery.common.config.externalservice.entity;

public class MsbConfig {

  private String msbServerAddr;


  private String serviceIp;


  private boolean autoRegister = true;

  private String path = "/openoapi";

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getMsbServerAddr() {
    return msbServerAddr;
  }


  public void setMsbServerAddr(String msbServerAddr) {
    this.msbServerAddr = msbServerAddr;
  }

  public void setServiceIp(String serviceIp) {
    this.serviceIp = serviceIp;
  }

  public String getServiceIp() {
    return serviceIp;
  }

  public void setAutoRegister(boolean autoRegister) {
    this.autoRegister = autoRegister;
  }

  public boolean getAutoRegister() {
    return this.autoRegister;
  }

}
