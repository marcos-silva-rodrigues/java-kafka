package com.marcos.silva.rodrigues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

  final Logger logger = LoggerFactory.getLogger(NewOrderMain.class);
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    try (var dispatcher = new KafkaDispatcher() ) {
      for (int i = 0; i < 10; i++) {
        var key = UUID.randomUUID().toString();
        var value = key + ",1412,2345";
        dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);;

        var email = "Thanks you for uor order! We are processing your order";
        dispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
      }
    }
  }


}