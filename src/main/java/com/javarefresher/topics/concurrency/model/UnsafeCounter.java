package com.javarefresher.topics.concurrency.model;

public final class UnsafeCounter implements Counter {
    private int value;

    @Override
    public String label() {
        return "Unsafe (no lock)";
    }

    @Override
    public boolean threadSafe() {
        return false;
    }

    @Override
    public void increment() {
        int next = value + 1;
        Thread.yield();
        value = next;
    }

    @Override
    public int value() {
        return value;
    }
}
