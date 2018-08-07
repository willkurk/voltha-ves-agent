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
package kafka;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.KafkaException;
import java.util.Collections;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.xml.ws.http.HTTPException;

import java.util.concurrent.TimeUnit;

import ves.*;
import config.Config;
import com.google.gson.JsonSyntaxException;

public class VolthaKafkaConsumer {

    private final Logger logger = LoggerFactory.getLogger("VolthaKafkaConsumer");
    private final String dataMarkerText = "DATA";
    private final Marker dataMarker = MarkerFactory.getMarker(dataMarkerText);

    private KafkaConsumer<Long, String> consumer;

    public VolthaKafkaConsumer() {
        logger.debug("VolthaKafkaConsumer constructor called");
        initVesAgent();
        try {
            consumer = createConsumer();
        } catch (KafkaException e) {
            logger.error("Error with Kafka connection. Retrying in 15 seconds.");
            //Don't try to resolve it here. Try again in the thread loo, in case this is a temporal issue
        }
    }

    private void initVesAgent() {
        VesAgent.initVes();
    }

    private KafkaConsumer<Long, String> createConsumer() {
        logger.debug("Creating Kafka Consumer");

        String kafkaAddress = Config.getKafkaAddress() + ":" + Config.getKafkaPort();
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
        "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
        false);

        // Create the consumer using props.
        final KafkaConsumer<Long, String> consumer =
        new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(Config.getKafkaTopic()));
        return consumer;
    }

    public void runConsumer() throws InterruptedException {

        logger.debug("Starting Consumer");

        while (true) {
            ConsumerRecords<Long, String> consumerRecords;
            try {
                if (consumer == null) {
                    this.consumer = createConsumer();
                }
                consumerRecords = consumer.poll(1000);
            } catch (KafkaException e) {
                logger.error("Error with Kafka connection. Retrying in 15 seconds.");
                consumer = null;
                TimeUnit.SECONDS.sleep(15);
                continue;
            }
            logger.info("{} Records retrieved from poll.", consumerRecords.count());

            boolean commit = true;
            try {
                consumerRecords.forEach(record -> {
                    logger.info(dataMarker, "Consumer Record:({}, {}, {}, {})\n",
                    record.key(), record.value(),
                    record.partition(), record.offset());
                    logger.info("Attempting to send data to VES");
                    boolean success = VesAgent.sendToVES(record.value());
                    if (!success) {
                        throw new HTTPException(0);
                    } else {
                        logger.info("Sent Ves Message");
                    }
                });
            } catch (HTTPException e) {
                logger.info("Ves message failed. Going back to polling.");
                commit = false;
            } catch (JsonSyntaxException e) {
                logger.error("Json Syntax Exception: ", e);
            }
            if (commit) {
                consumer.commitAsync();
            }
        }
        //consumer.close();
        //logger.debug("DONE");
    }

}
