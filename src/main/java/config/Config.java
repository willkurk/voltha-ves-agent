/*
 * Copyright 2018- Cisco
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
package config;

import java.util.Properties;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

  private static Properties properties;

  private final static Logger logger = LoggerFactory.getLogger("VolthaKafkaConsumer");

  public static void loadProperties(String file) {
    // create application properties with default
    try {
      properties = new Properties();

      // now load properties
      // from last invocation
      FileInputStream in = new FileInputStream(file);
      properties.load(in);
      in.close();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public static String get(String key) {
    return (String)properties.get(key);
  }

  public static String getVesAddress() {
    return get("onap_ves_address");
  }

  public static String getVesPort() {
    return get("onap_ves_port");
  }

  public static String getKafkaAddress() {
    return get("kafka_address");
  }

  public static String getKafkaPort() {
    return get("kafka_port");
  }

  public static String getKafkaTopic() {
    return get("kafka_topic");
  }
}
