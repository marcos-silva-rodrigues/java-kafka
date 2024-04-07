package com.marcos.silva.rodrigues.service;

import com.marcos.silva.rodrigues.kafka.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class LogService {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    var logService = new LogService();
    try (
            var service = new KafkaService(
                    LogService.class.getSimpleName(),
                    Pattern.compile("ECOMMERCE.*"),
                    logService::parse,
                    String.class,
                    Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getSimpleName()))
    ) {
      service.run();
    }

  }

  private void parse(ConsumerRecord<String, String> record) {
    System.out.println("--------------------------------------------");
    System.out.println("Log " + record.topic());
    System.out.println(record.key());
    System.out.println(record.value());
    System.out.println(record.partition());
    System.out.println(record.offset());
  }
}
