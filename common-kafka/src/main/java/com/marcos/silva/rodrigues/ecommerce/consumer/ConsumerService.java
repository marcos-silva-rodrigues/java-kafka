package com.marcos.silva.rodrigues.ecommerce.consumer;

import com.marcos.silva.rodrigues.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public interface ConsumerService<T> {

  String getTopic();

  void parse(ConsumerRecord<String, Message<T>> record) throws Exception;

  String getConsumerGroup();
}
