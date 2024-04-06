package com.marcos.silva.rodrigues;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction {

  void consume(ConsumerRecord<String, String> record);
}
