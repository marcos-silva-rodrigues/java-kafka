package com.marcos.silva.rodrigues.ecommerce.dispatcher;

import com.marcos.silva.rodrigues.ecommerce.CorrelationId;
import com.marcos.silva.rodrigues.ecommerce.Message;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {

  private KafkaProducer<String, Message<T>> producer;

  public KafkaDispatcher() {
    producer = new KafkaProducer<>(properties());
  }

  private Properties properties() {
    var properties = new Properties();

    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");

    return properties;
  }

  public void send(String topic, String key, CorrelationId id, T paylod) throws ExecutionException, InterruptedException {
    Future<RecordMetadata> future = sendAsync(topic, key, id.continueWith("_" + topic), paylod);
    future.get();
  }

    public Future<RecordMetadata> sendAsync(String topic, String key, CorrelationId id, T paylod) {
    var value = new Message<>(id, paylod);
    var record = new ProducerRecord(topic, key, value);

    Callback callback = (data, ex) -> {
      if (ex != null) {
        ex.printStackTrace();
      } else {
        System.out.println(data.topic() + ":::partition" + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
      }
    };

    return producer.send(record, callback);
  }

  @Override
  public void close() {
    producer.close();
  }
}
