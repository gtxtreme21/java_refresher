package com.javarefresher.topics.concurrency.model;

public final class SynchronizedCounter implements Counter {
    private int value;

    @Override
    public String label() {
        return "Synchronized method";
    }

    @Override
    public boolean threadSafe() {
        return true;
    }

    @Override
    public synchronized void increment() {
        value++;
    }

    @Override
    public synchronized int value() {
        return value;
    }
}
