package com.javarefresher.topics.concurrency.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class LockingCounter implements Counter {
    private final Lock lock = new ReentrantLock();
    private int value;

    @Override
    public String label() {
        return "ReentrantLock";
    }

    @Override
    public boolean threadSafe() {
        return true;
    }

    @Override
    public void increment() {
        lock.lock();
        try {
            value++;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int value() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }
}
