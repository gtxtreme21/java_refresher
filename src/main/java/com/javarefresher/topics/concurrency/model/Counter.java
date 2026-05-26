package com.javarefresher.topics.concurrency.model;

public interface Counter {
    String label();
    boolean threadSafe();
    void increment();
    int value();
}
