package com.marcos.silva.rodrigues.ecommerce;

import java.math.BigDecimal;

public class Order {

  private final String orderId;
  private final BigDecimal amount;

  private final String email;

  public Order(String orderId, BigDecimal amount, String email) {
    this.amount = amount;
    this.orderId = orderId;
    this.email = email;
  }

  public String getOrderId() {
    return orderId;
  }
}
