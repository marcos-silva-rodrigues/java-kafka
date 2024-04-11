package com.marcos.silva.rodrigues.ecommerce;

import com.marcos.silva.rodrigues.ecommerce.consumer.ConsumerService;
import com.marcos.silva.rodrigues.ecommerce.consumer.KafkaService;
import com.marcos.silva.rodrigues.ecommerce.consumer.ServiceRunner;
import com.marcos.silva.rodrigues.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;


import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerService<Order> {

  private final LocalDatabase database;

  public FraudDetectorService() throws SQLException {
    this.database = new LocalDatabase("frauds_database");
    database.createIfNotExists(
            "create table if not exists orders  (" +
                    "uuid varchar(200) primary key, " +
                    "is_fraud boolean )"
    );
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
   new ServiceRunner<>(FraudDetectorService::new).start(1);
  }

  private final KafkaDispatcher<Order> dispatcher = new KafkaDispatcher<>();

  @Override
  public String getTopic() {
    return "ECOMMERCE_NEW_ORDER";
  }

  @Override
  public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
    System.out.println("------------------------------");
    System.out.println("Processing new order, checking for fraud");
    System.out.println(record.key());
    var message = record.value();
    var order = message.getPayload();
    System.out.println(order);
    System.out.println(record.partition());
    System.out.println(record.offset());

    if (wasProcessed(order)) {
      System.out.println("Order " + order.getOrderId() + " was already processed");
      return;
    }
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      // ignoring because its a simulation
      e.printStackTrace();
    }
    if (order.isFraud()) {
      database.update("insert into orders (uuid, is_fraud) values (?, true)", order.getOrderId());
      System.out.println("Order is a fraud");
      dispatcher.send(
              "ECOMMERCE_ORDER_REJECTED",
              order.getEmail(),
              message.getId().continueWith(FraudDetectorService.class.getSimpleName()),
              order
      );
    } else {
      database.update("insert into orders (uuid, is_fraud) values (?, false)", order.getOrderId());
      System.out.println("Order was accepted");
      dispatcher.send(
              "ECOMMERCE_ORDER_APPROVED",
              order.getEmail(),
              message.getId().continueWith(FraudDetectorService.class.getSimpleName()),
              order);
    }
  }

  private boolean wasProcessed(Order order) throws SQLException {
    var result = database.query("select uuid from orders where uuid = ? limit 1", order.getOrderId());
    return !result.next();
  }

  @Override
  public String getConsumerGroup() {
    return FraudDetectorService.class.getSimpleName();
  }

}
