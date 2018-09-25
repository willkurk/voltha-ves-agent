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
import kafka.KafkaConsumerType;

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
        KafkaAlarmsThread kafkaAlarms = new KafkaAlarmsThread();
        kafkaAlarms.start();
        KafkaKpisThread kafkaKpis = new KafkaKpisThread();
        kafkaKpis.start();
        SpringApplication.run(Application.class, args);
    }

}
class KafkaAlarmsThread extends Thread {

    private final static Logger logger = LoggerFactory.getLogger("KafkaAlarmsThread");

    public void run() {
        logger.debug("Start Kafka Alarms Consumer Thread");
        try {
            VolthaKafkaConsumer consumer = new VolthaKafkaConsumer(KafkaConsumerType.ALARMS);
            consumer.runConsumer();
        } catch (Exception e) {
            logger.error("Error in Kafka Alarm thread", e);
            logger.error(e.toString());
        }

    }
}
class KafkaKpisThread extends Thread {

    private final static Logger logger = LoggerFactory.getLogger("KafkaKpisThread");

    public void run() {
        logger.debug("Start Kafka KPIs Consumer Thread");
        try {
            VolthaKafkaConsumer consumer = new VolthaKafkaConsumer(KafkaConsumerType.KPIS);
            consumer.runConsumer();
        } catch (Exception e) {
            logger.error("Error in Kafka KPI thread", e);
            logger.error(e.toString());
        }

    }
}
