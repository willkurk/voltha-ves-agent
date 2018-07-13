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
package controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kafka.VolthaKafkaConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.InterruptedException;

import config.Config;


@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    public static void main(String[] args) {
        Config.loadProperties("/opt/ves-agent/config.properties");
	      KafkaThread kafka = new KafkaThread();
        kafka.start();
        SpringApplication.run(Application.class, args);
    }

}
class KafkaThread extends Thread {

    private final static Logger logger = LoggerFactory.getLogger("KafkaThread");

    public void run() {
      logger.debug("Start Kafka Consumer Thread");
       try {
         VolthaKafkaConsumer consumer = new VolthaKafkaConsumer();
       	 consumer.runConsumer();
       } catch (InterruptedException e) {
          logger.error(e.getMessage());
       }

    }
 }
