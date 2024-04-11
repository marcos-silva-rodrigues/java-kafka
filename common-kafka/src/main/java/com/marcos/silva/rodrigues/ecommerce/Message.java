package com.marcos.silva.rodrigues.ecommerce;

import com.google.gson.JsonElement;

public class Message<T> {

  private final CorrelationId id;
  private final T paylod;

  public Message(CorrelationId id, T paylod) {
    this.id = id;
    this.paylod = paylod;
  }

  @Override
  public String toString() {
    return "Message{" +
            "id=" + id +
            ", paylod=" + paylod +
            '}';
  }

  public CorrelationId getId() {
    return id;
  }

  public T getPayload() {
    return paylod;
  }
}
