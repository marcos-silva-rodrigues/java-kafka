package com.marcos.silva.rodrigues;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class EmailService {

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    var emailService = new EmailService();
    try (
            var service = new KafkaService(
                    EmailService.class.getSimpleName(),
                    "ECOMMERCE_SEND_EMAIL",
                    emailService::parse)
            ) {
      service.run();
    }


  }

  private void parse(ConsumerRecord<String, String> record){
    System.out.println("--------------------------------------------");
    System.out.println("Sending email");
    System.out.println(record.key());
    System.out.println(record.value());
    System.out.println(record.partition());
    System.out.println(record.offset());
    System.out.println("Email sent");

  }


}
