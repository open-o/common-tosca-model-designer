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
package org.eclipse.winery.repository.ext.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5Util {
  private final static Logger logger = LoggerFactory.getLogger(MD5Util.class);
  private static MessageDigest md = null;

  static {
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException ne) {
      logger.error("NoSuchAlgorithmException: md5", ne);
    }
  }

  public MessageDigest getInstance() {
    return md;
  }

  public static String md5(InputStream is, OutputStream os) {
    try {
      // 100KB each time
      byte[] buffer = new byte[102400];
      int length;
      long loopCount = 0;

      md.reset();
      while ((length = is.read(buffer)) != -1) {
        md.update(buffer, 0, length);
        if (os != null) {
          os.write(buffer, 0, length);
        }
        loopCount++;
      }
      logger.debug("read file to buffer loopCount:" + loopCount);
      return new String(Hex.encodeHex(md.digest()));
    } catch (FileNotFoundException e) {
      logger.error("md5 file failed.", e);
    } catch (IOException e) {
      logger.error("md5 file failed.", e);
    }
    return null;
  }

  public static String md5(String source) {
    return DigestUtils.md5Hex(source);
  }
}
