package com.javarefresher.topics.concurrency.model;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicCounter implements Counter {
    private final AtomicInteger value = new AtomicInteger(0);

    @Override
    public String label() {
        return "AtomicInteger";
    }

    @Override
    public boolean threadSafe() {
        return true;
    }

    @Override
    public void increment() {
        value.incrementAndGet();
    }

    @Override
    public int value() {
        return value.get();
    }
}
