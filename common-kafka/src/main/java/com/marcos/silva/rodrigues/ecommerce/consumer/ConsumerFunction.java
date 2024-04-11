package com.marcos.silva.rodrigues.ecommerce.consumer;

import com.marcos.silva.rodrigues.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {

  void consume(ConsumerRecord<String, Message<T>> record) throws Exception;
}
