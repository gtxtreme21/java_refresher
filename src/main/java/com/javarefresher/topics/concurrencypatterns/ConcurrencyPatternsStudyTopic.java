package com.javarefresher.topics.concurrencypatterns;

import com.javarefresher.core.ConsolePrinter;
import com.javarefresher.core.StudyTopic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public final class ConcurrencyPatternsStudyTopic implements StudyTopic {
    private static final int POOL_SIZE = 3;
    private static final int WORKERS = 8;
    private static final int OPERATIONS_PER_WORKER = 200;

    @Override
    public String key() {
        return "concurrency-patterns";
    }

    @Override
    public String title() {
        return "Java Concurrency Patterns and Thread-Safe Resource Design";
    }

    @Override
    public void run(ConsolePrinter printer) {
        printer.topicHeader(key(), title());
        printer.section("Functionality", "Demonstrates common Java concurrency patterns and validates a thread-safe pooled resource under concurrent load.");
        printer.section("Design patterns utilized", "Producer-Consumer queue semantics + Bulkhead isolation (`Semaphore`) + Guarded resource access with a bounded blocking queue.");
        printer.interviewFrame(
                "Many threads must borrow and return a limited shared resource safely.",
                "Unsynchronized borrow/return from a shared collection, causing duplicate checkout, leaks, or pool corruption.",
                "Bound concurrency with permits and use a thread-safe queue for resource ownership transfer.",
                "Lead developers are expected to choose primitives that enforce invariants even under heavy contention."
        );

        printPatternCheatSheet();
        runThreadSafeResourceDemo();
        printLeadInterviewQa();
    }

    private void printPatternCheatSheet() {
        System.out.println();
        System.out.println("Concurrency pattern cheat sheet:");
        System.out.println(" - Producer-Consumer: hand off work or resources through a blocking queue.");
        System.out.println(" - Bulkhead pattern: cap concurrent access to fragile dependencies with semaphores.");
        System.out.println(" - Immutable message handoff: reduce shared mutable state between threads.");
    }

    private void runThreadSafeResourceDemo() {
        System.out.println();
        System.out.println("Thread-safe resource example: pooled API clients");
        System.out.println(" workload: poolSize=%d, workers=%d, operationsPerWorker=%d".formatted(POOL_SIZE, WORKERS, OPERATIONS_PER_WORKER));

        ThreadSafeClientPool pool = new ThreadSafeClientPool(POOL_SIZE);
        ExecutorService executor = Executors.newFixedThreadPool(WORKERS);
        CountDownLatch ready = new CountDownLatch(WORKERS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(WORKERS);

        for (int worker = 0; worker < WORKERS; worker++) {
            int workerId = worker;
            executor.submit(() -> {
                ready.countDown();
                await(start);
                for (int i = 0; i < OPERATIONS_PER_WORKER; i++) {
                    PooledClient client = pool.borrow();
                    try {
                        client.execute(workerId, i);
                    } finally {
                        pool.release(client);
                    }
                }
                done.countDown();
            });
        }

        await(ready);
        start.countDown();
        await(done);
        executor.shutdown();

        int totalOperations = WORKERS * OPERATIONS_PER_WORKER;
        System.out.println(" results:");
        System.out.println(" - total operations completed      = " + totalOperations);
        System.out.println(" - max concurrent leases observed  = " + pool.maxConcurrentLeases());
        System.out.println(" - resources available at end      = " + pool.availableResources());

        boolean invariantHolds = pool.maxConcurrentLeases() <= POOL_SIZE && pool.availableResources() == POOL_SIZE;
        System.out.println(" - invariant check (no over-lease) = " + (invariantHolds ? "PASS" : "FAIL"));
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while coordinating worker threads.", ex);
        }
    }

    private void printLeadInterviewQa() {
        System.out.println();
        System.out.println("Lead Interview Q&A:");
        System.out.println(" Q1: Why combine `Semaphore` with `BlockingQueue` for a pool?");
        System.out.println("  A: The semaphore enforces max concurrency and the queue guarantees safe ownership transfer of concrete resources.");
        System.out.println(" Q2: What issue does this avoid compared to a synchronized list?");
        System.out.println("  A: It avoids ad hoc lock logic and accidental resource duplication or leak scenarios under race conditions.");
        System.out.println(" Q3: When would you choose `ReentrantLock` instead?");
        System.out.println("  A: When coordination needs conditions, timed lock attempts, or more complex state transitions than queue/permit semantics.");
        System.out.println(" Q4: How do you detect pool correctness in production?");
        System.out.println("  A: Track max concurrent leases, queue depth, and timeout/error rates; alert when invariants drift.");
    }

    private static final class ThreadSafeClientPool {
        private final Semaphore permits;
        private final BlockingQueue<PooledClient> clients;
        private final AtomicInteger activeLeases = new AtomicInteger(0);
        private final AtomicInteger maxConcurrentLeases = new AtomicInteger(0);

        private ThreadSafeClientPool(int poolSize) {
            this.permits = new Semaphore(poolSize);
            this.clients = new ArrayBlockingQueue<>(poolSize);
            for (int i = 1; i <= poolSize; i++) {
                clients.add(new PooledClient("client-" + i));
            }
        }

        private PooledClient borrow() {
            try {
                permits.acquire();
                PooledClient client = clients.take();
                int nowLeased = activeLeases.incrementAndGet();
                maxConcurrentLeases.accumulateAndGet(nowLeased, Math::max);
                return client;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while borrowing pooled client.", ex);
            }
        }

        private void release(PooledClient client) {
            try {
                clients.put(client);
                activeLeases.decrementAndGet();
                permits.release();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while releasing pooled client.", ex);
            }
        }

        private int availableResources() {
            return clients.size();
        }

        private int maxConcurrentLeases() {
            return maxConcurrentLeases.get();
        }
    }

    private record PooledClient(String id) {
        private void execute(int workerId, int operation) {
            int marker = (id.hashCode() ^ workerId ^ operation) & 0x0F;
            if (marker == -1) {
                throw new IllegalStateException("unreachable guard");
            }
        }
    }
}
