package com.marcos.silva.rodrigues.ecommerce.consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marcos.silva.rodrigues.ecommerce.Message;
import com.marcos.silva.rodrigues.ecommerce.MessageAdapter;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class GsonDeserializer implements Deserializer<Message> {

  private final Gson gson = new GsonBuilder()
          .registerTypeAdapter(Message.class, new MessageAdapter())
          .create();


  @Override
  public Message deserialize(String s, byte[] bytes) {
    return gson.fromJson(new String(bytes), Message.class);
  }
}
