package com.marcos.silva.rodrigues.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class CreateUserService {

  private final Connection connection;

  CreateUserService() throws SQLException {
    String url = "jdbc:sqlite:target/user_database.db";
    connection = DriverManager.getConnection(url);
    try {
      connection.createStatement().execute(
              "create table users (" +
                      "uuid varchar(200) primary_key, " +
                      "email varchar(200) )"
      );
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

  }

  public static void main(String[] args) throws SQLException {
    var myService = new CreateUserService();
    try (
            var service = new KafkaService(
                    CreateUserService.class.getSimpleName(),
                    "ECCOMERCE_NEW_ORDER",
                    myService::parse,
                    Order.class,
                    Map.of());
            ) {
      service.run();
    }
    System.out.println("Hello world!");
  }

  private void parse(ConsumerRecord<String, Order> record) throws SQLException {
    System.out.println("------------------------------");
    System.out.println("Processing new order, checking for new user");
    Order order = record.value();
    System.out.println(order);

    if (isNewUser(order.getEmail()))
      insertNewUser(order.getEmail());
  }

  private void insertNewUser( String email) throws SQLException {
    PreparedStatement insert = connection.prepareStatement("insert into users (uuid, email) values (?, ?)");
    insert.setString(1, UUID.randomUUID().toString());
    insert.setString(2, email);
    insert.execute();

    System.out.println("Usuario uuid e " + email + " adicionado");

  }

  private boolean isNewUser(String email) throws SQLException {
    var exists = connection.prepareStatement("select uuid from users " +
            "where email = ? limit 1");
    exists.setString(1, email);
    var resultSet = exists.executeQuery();

    return !resultSet.next();
  }
}