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

  public BigDecimal getAmount() {
    return amount;
  }

  public String getEmail() {
    return this.email;
  }

  @Override
  public String toString() {
    return "Order{" +
            ", orderId='" + orderId + '\'' +
            ", amount=" + amount +
            ", email='" + email + '\'' +
            '}';
  }


  public boolean isFraud() {
    return this.amount.compareTo(new BigDecimal("4500")) >= 0;
  }
}
