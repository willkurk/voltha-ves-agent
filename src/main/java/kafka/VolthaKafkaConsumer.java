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

    private final static Logger logger = LoggerFactory.getLogger("VolthaKafkaConsumer");
    private static String dataMarkerText = "DATA";
    private static Marker dataMarker = MarkerFactory.getMarker(dataMarkerText);

    private static KafkaConsumer<Long, String> createConsumer() {
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

  public static void runConsumer() throws InterruptedException {
        final KafkaConsumer<Long, String> consumer = createConsumer();

	logger.debug("Starting Consumer");

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(100000);

            consumerRecords.forEach(record -> {
                logger.info(dataMarker, "Consumer Record:({}, {}, {}, {})\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
                VesAgent.sendToVES("");
            });

            consumer.commitAsync();
        }
        //consumer.close();
        //logger.debug("DONE");
    }

}
