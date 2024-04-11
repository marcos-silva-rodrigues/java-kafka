package com.marcos.silva.rodrigues.ecommerce;

import com.marcos.silva.rodrigues.ecommerce.consumer.KafkaService;
import com.marcos.silva.rodrigues.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    var myService = new EmailNewOrderService();
    try (var service = new KafkaService<>(EmailNewOrderService.class.getSimpleName(),
            "ECOMMERCE_NEW_ORDER",
            myService::parse,
            Map.of())) {
      service.run();
    }
  }

  private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

  private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
    System.out.println("------------------------------");
    System.out.println("Processing new email, preparing email");

    var message = record.value();
    System.out.println(message);
    var order = message.getPayload();
    var emailCode = "Thanks you for uor order! We are processing your order";

    emailDispatcher.send(
            "ECOMMERCE_SEND_EMAIL",
            order.getEmail(),
            new CorrelationId(EmailNewOrderService.class.getSimpleName()),
            emailCode
    );
  }
}