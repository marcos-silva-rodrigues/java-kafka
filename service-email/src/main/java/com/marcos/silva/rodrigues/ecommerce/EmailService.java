package com.marcos.silva.rodrigues.ecommerce;

import com.marcos.silva.rodrigues.ecommerce.consumer.ConsumerService;
import com.marcos.silva.rodrigues.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import java.util.concurrent.ExecutionException;

public class EmailService implements ConsumerService<String> {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    new ServiceRunner(EmailService::new).start(5);
  }

  public String getConsumerGroup() {
    return EmailService.class.getSimpleName();
  }

  @Override
  public String getTopic() {
    return "ECOMMERCE_SEND_EMAIL";
  }

  @Override
  public void parse(ConsumerRecord<String, Message<String>> record){
    System.out.println("--------------------------------------------");
    System.out.println("Sending email");
    System.out.println(record.key());
    System.out.println(record.value());
    System.out.println(record.partition());
    System.out.println(record.offset());
    System.out.println("Email sent");

  }


}
