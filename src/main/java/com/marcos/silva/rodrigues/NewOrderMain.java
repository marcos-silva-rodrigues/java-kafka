package com.marcos.silva.rodrigues;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

  final Logger logger = LoggerFactory.getLogger(NewOrderMain.class);
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    var producer = new KafkaProducer<String, String>(properties());

    var email = "Thanks you for uor order! We are processing your order";

    Callback callback = (data, ex) -> {
      if (ex != null) {
        ex.printStackTrace();
      } else {
        System.out.println(data.topic() + ":::partition" + data.partition() + "/ offset" + data.offset() + "/ timestamp" + data.timestamp());
      }
    };

    for (int i = 0; i < 100; i++) {
      var key = UUID.randomUUID().toString();
      var value = key + ",1412,2345";
      var record = new ProducerRecord("ECOMMERCE_NEW_ORDER", value, value);

      producer.send(record, callback).get();
      ProducerRecord<String, String> emailRecord = new ProducerRecord<>("ECOMMERCE_SEND_EMAIL", email, email);
      producer.send(emailRecord,callback).get();
    }

  }

  private static Properties properties() {
    var properties = new Properties();

    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());


    return properties;
  }
}