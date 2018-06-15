package controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kafka.VolthaKafkaConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.InterruptedException;

@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    public static void main(String[] args) {
	KafkaThread kafka = new KafkaThread();
        kafka.start();
        SpringApplication.run(Application.class, args);
    }

}
class KafkaThread extends Thread {
   
    private final static Logger logger = LoggerFactory.getLogger("KafkaThread");

    public void run() {
       try {
       	  VolthaKafkaConsumer.runConsumer();
       } catch (InterruptedException e) {
          logger.error(e.getMessage());
       }

    }
 }
