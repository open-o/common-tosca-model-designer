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

package org.eclipse.winery.common.config.externalservice.msb;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpClientUtil {
  private String apiUrl;
  private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
  private HttpClient httpClient = null;
  private HttpPost method = null;
  private long startTime = 0L;
  private long endTime = 0L;
  private int status = 0;

  /**
   * init http client .
   * 
   * @param host http service address
   * @param path service path
   */
  public HttpClientUtil(String host, String path) {
    this.apiUrl = host + path;
    httpClient = HttpClients.createDefault();
    method = new HttpPost(apiUrl);
  }

  /**
   * send http post request.
   * 
   * @param parameters post request parameters
   * @return response context
   */
  public String post(String parameters) {
    String body = null;
    logger.info("apiURl" + this.apiUrl + " parameters:" + parameters);
    if (method != null & parameters != null && !"".equals(parameters.trim())) {
      try {
        method.addHeader("Content-type", "application/json; charset=utf-8");
        method.setHeader("Accept", "application/json");
        method.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));
        startTime = System.currentTimeMillis();
        HttpResponse response = httpClient.execute(method);
        endTime = System.currentTimeMillis();
        int statusCode = response.getStatusLine().getStatusCode();
        logger.info("statusCode:" + statusCode);
        logger.info("Call API to spend time(Unit:ms):" + (endTime - startTime));
        if (statusCode != HttpStatus.SC_CREATED) {
          logger.error("post Method failed:" + response.getStatusLine());
          status = 1;
        }
        body = EntityUtils.toString(response.getEntity());
      } catch (IOException e1) {
        logger.error("post Method failed,errormsg:" + e1.getMessage());
        status = 3;
      } finally {
        logger.info("Call API status:" + status);
      }
    }
    return body;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }
}
