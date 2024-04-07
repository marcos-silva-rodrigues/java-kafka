package com.marcos.silva.rodrigues.ecommerce;

import java.math.BigDecimal;

public class Order {

  private final String userId, orderId;
  private final BigDecimal amount;

  public Order(String orderId, String userId, BigDecimal amount ) {
    this.userId = userId;
    this.amount = amount;
    this.orderId = orderId;
  }
}
