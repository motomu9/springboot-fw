package com.example.util.function;

@FunctionalInterface
public interface ConsumerThrowable<T, X extends Throwable> {
  void accept(T instance) throws X;
}
