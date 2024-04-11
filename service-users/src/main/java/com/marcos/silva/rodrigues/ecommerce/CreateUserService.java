package com.marcos.silva.rodrigues.ecommerce;

import com.marcos.silva.rodrigues.ecommerce.consumer.ConsumerService;
import com.marcos.silva.rodrigues.ecommerce.consumer.KafkaService;
import com.marcos.silva.rodrigues.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService implements ConsumerService<Order> {

  private final LocalDatabase database;

  public CreateUserService() throws SQLException {
    this.database = new LocalDatabase("users_database");
    database.createIfNotExists(
            "create table if not exists users  (" +
                    "uuid varchar(200) primary key, " +
                    "email varchar(200) )"
    );
  }

  public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
    new ServiceRunner<>(CreateUserService::new).start(1);
  }

  @Override
  public String getTopic() {
    return "ECOMMERCE_NEW_ORDER";
  }

  @Override
  public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
    System.out.println("------------------------------");
    System.out.println("Processing new order, checking for new user");
    var message = record.value();
    var order = message.getPayload();
    System.out.println(order);

    if (isNewUser(order.getEmail()))
      insertNewUser(order.getEmail());
  }

  @Override
  public String getConsumerGroup() {
    return CreateUserService.class.getSimpleName();
  }

  private void insertNewUser( String email) throws SQLException {
    String id = UUID.randomUUID().toString();
    database.update("insert into users (uuid, email) values (?, ?)", id, email);
    System.out.println("Usuario + " + id + " e " + email + " adicionado");

  }

  private boolean isNewUser(String email) throws SQLException {
    ResultSet query = database.query("select uuid from users " +
            "where email = ? limit 1", email);

    return  !query.next();

  }
}