package com.example.util.function;

@FunctionalInterface
public interface SupplierThrowable<T, X extends Throwable> {
  T get() throws X;
}
