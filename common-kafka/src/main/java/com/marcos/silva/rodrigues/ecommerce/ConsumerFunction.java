package com.marcos.silva.rodrigues.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public interface ConsumerFunction<T> {

  void consume(ConsumerRecord<String, Message<T>> record) throws Exception;
}
