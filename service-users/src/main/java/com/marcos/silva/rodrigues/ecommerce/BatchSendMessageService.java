package com.marcos.silva.rodrigues.ecommerce;

import com.marcos.silva.rodrigues.ecommerce.consumer.KafkaService;
import com.marcos.silva.rodrigues.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {

  private final Connection connection;
  private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

  public BatchSendMessageService() throws SQLException {
    String url = "jdbc:sqlite:target/user_database.db";
    connection = DriverManager.getConnection(url);

    try {
      connection.createStatement().execute(
              "create table if not exists users  (" +
                      "uuid varchar(200) primary key, " +
                      "email varchar(200) )"
      );
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
    var myService = new BatchSendMessageService();
    try (
            var service = new KafkaService(
                    BatchSendMessageService.class.getSimpleName(),
                    "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",
                    myService::parse,
                    String.class,
                    Map.of());
    ) {
      service.run();
    }
  }

  private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException, ExecutionException, InterruptedException {
    System.out.println("------------------------------");
    System.out.println("Processing new batch");
    var message =  record.value();
    String topic = message.getPayload();

    for (User user: getAllUsers()) {
      userDispatcher.sendAsync(
              topic,
              user.getUuid(),
              message.getId().continueWith(BatchSendMessageService.class.getSimpleName()),
              user
      );
    }
  }

  private List<User> getAllUsers() throws SQLException {
    var results = connection.prepareStatement("select uuid from users").executeQuery();

    List<User> users = new ArrayList<>();
    while (results.next()) {
      users.add(new User(results.getString(1)));
    }

    return users;
  }

}
