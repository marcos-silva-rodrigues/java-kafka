package com.marcos.silva.rodrigues;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaDispatcher implements Closeable {

  private KafkaProducer<String, String> producer;

  public KafkaDispatcher() {
    producer = new KafkaProducer<>(properties());

  }

  private Properties properties() {
    var properties = new Properties();

    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    return properties;
  }

  public void send(String topic, String key, String value) throws ExecutionException, InterruptedException {
    var record = new ProducerRecord(topic, value, value);

    Callback callback = (data, ex) -> {
      if (ex != null) {
        ex.printStackTrace();
      } else {
        System.out.println(data.topic() + ":::partition" + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
      }
    };

    producer.send(record, callback).get();

  }

  @Override
  public void close() {
    producer.close();
  }
}
