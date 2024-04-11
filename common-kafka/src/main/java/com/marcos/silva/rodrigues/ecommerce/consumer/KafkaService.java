package com.marcos.silva.rodrigues.ecommerce.consumer;

import com.marcos.silva.rodrigues.ecommerce.Message;
import com.marcos.silva.rodrigues.ecommerce.dispatcher.GsonSerializer;
import com.marcos.silva.rodrigues.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
  private final ConsumerFunction parse;
  private KafkaConsumer<String, Message<T>> consumer;

  public KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
    this(groupId, parse, type, properties);
    this.consumer.subscribe(Collections.singletonList(topic));
  }

  public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
    this(groupId, parse, type, properties);
    this.consumer.subscribe(topic);
  }

  private KafkaService(String groupId, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
    this.parse = parse;
    this.consumer = new KafkaConsumer<>(getProperties(type, groupId, properties));
  }


  public void run() throws ExecutionException, InterruptedException {
    try (var deadLetter = new KafkaDispatcher<>()) {
      System.out.println("runnn");
      while (true) {
        var records = consumer.poll(Duration.ofMillis(100));
        if (!records.isEmpty()) {
          System.out.println("Encontrei " + records.count() + " registros");
          for (var record : records) {
            try {
              parse.consume(record);
            } catch (Exception e) {
              var message = record.value();
              deadLetter.send(
                      "ECOMMERCE_DEADLETTER",
                      message.getId().toString(),
                      message.getId()
                              .continueWith("DeadLetter"),
                      new GsonSerializer<>()
                              .serialize("", message));
            }
          }
        }
      }
    }

  }

  private Properties getProperties(Class<T> type, String groupId, Map<String, String> overrideProperties) {
    var properties = new Properties();

    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, groupId + UUID.randomUUID().toString());

    properties.putAll(overrideProperties);

    return properties;
  }

  @Override
  public void close() {
    consumer.close();
  }
}
