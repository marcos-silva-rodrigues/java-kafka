package com.marcos.silva.rodrigues.ecommerce;

import com.marcos.silva.rodrigues.ecommerce.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class EmailService {

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    var emailService = new EmailService();
    try (
            var service = new KafkaService(
                    EmailService.class.getSimpleName(),
                    "ECOMMERCE_SEND_EMAIL",
                    emailService::parse,
                    new HashMap())
            ) {
      service.run();
    }


  }

  private void parse(ConsumerRecord<String, Message<String>> record){
    System.out.println("--------------------------------------------");
    System.out.println("Sending email");
    System.out.println(record.key());
    System.out.println(record.value());
    System.out.println(record.partition());
    System.out.println(record.offset());
    System.out.println("Email sent");

  }


}
