package kafka;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import ves.*;
import config.Config;

public class VolthaKafkaConsumer {

//    private final static String TOPIC = "voltha.alarms";
//    private final static String BOOTSTRAP_SERVERS =
//            "kafka.voltha.svc.cluster.local:9092";

    private final Logger logger = LoggerFactory.getLogger("VolthaKafkaConsumer");
    private final String dataMarkerText = "DATA";
    private final Marker dataMarker = MarkerFactory.getMarker(dataMarkerText);

    private KafkaConsumer<Long, String> consumer;

    public VolthaKafkaConsumer() {
      logger.debug("VolthaKafkaConsumer constructor called");
      initVesAgent();
      consumer = createConsumer();
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
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(100000);

            consumerRecords.forEach(record -> {
                logger.info(dataMarker, "Consumer Record:({}, {}, {}, {})\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
                logger.info("Attempting to send data to VES");
                boolean success = false;
                while (!success) {
                  success = VesAgent.sendToVES(record.value());
                  if (!success) {
                    logger.info("Ves message failed. Sleeping for 15 seconds.");
                    try {
                      Thread.sleep(15000);
                    } catch (InterruptedException e) {
                      logger.info(e.getMessage());
                    }
                  } else {
                    logger.info("Sent Ves Message");
                  }
                }

            });

            consumer.commitAsync();
        }
        //consumer.close();
        //logger.debug("DONE");
    }

}
