package com.example.util.function;

@FunctionalInterface
public interface FunctionThrowable<T, R, X extends Throwable> {
  R apply(T instance) throws X;
}
