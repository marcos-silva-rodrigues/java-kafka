package com.marcos.silva.rodrigues;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class FraudDetectorService{

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    var fraudDetectorService = new FraudDetectorService();

    try(var service = new KafkaService(
            FraudDetectorService.class.getSimpleName(),
            "ECOMMERCE_NEW_ORDER",
            fraudDetectorService::parse)) {
      service.run();

    }
  }

  private void parse(ConsumerRecord<String, String> record){
    System.out.println("--------------------------------------------");
    System.out.println("Processing new order, checking for fraud");
    System.out.println(record.key());
    System.out.println(record.value());
    System.out.println(record.partition());
    System.out.println(record.offset());
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      System.out.println("Error");
    }

    System.out.println("Order processed");
  }

}
