package com.marcos.silva.rodrigues.ecommerce.consumer;

public interface ServiceFactory<T> {
  ConsumerService<T> create();
}
