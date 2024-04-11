package com.marcos.silva.rodrigues.ecommerce;

import com.marcos.silva.rodrigues.ecommerce.consumer.ConsumerService;
import com.marcos.silva.rodrigues.ecommerce.consumer.KafkaService;
import com.marcos.silva.rodrigues.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReadingReportService implements ConsumerService<User> {

  private final Path SOURCE = new File("/src/main/resources/report.txt").toPath();


  public static void main(String[] args) throws ExecutionException, InterruptedException {
    new ServiceRunner(ReadingReportService::new).start(5);
  }

  @Override
  public String getConsumerGroup() {
    return ReadingReportService.class.getSimpleName();
  }

  @Override
  public String getTopic() {
    return "ECOMMERCE_USER_GENERATE_READING_REPORT";
  }

  @Override
  public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
    System.out.println("------------------------------");
    System.out.println("Processing report for " + record.value());
    var user = record.value().getPayload();
    var target = new File(user.getReportPath());
    IO.copyTo(SOURCE, target);
    IO.append(target, "Created for " + user.getUuid());

    System.out.println("File created: " + target.getAbsolutePath());
  }



}
