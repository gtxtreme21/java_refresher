package com.javarefresher.topics.concurrency;

import com.javarefresher.core.ConsolePrinter;
import com.javarefresher.core.StudyTopic;
import com.javarefresher.topics.concurrency.model.AtomicCounter;
import com.javarefresher.topics.concurrency.model.Counter;
import com.javarefresher.topics.concurrency.model.LockingCounter;
import com.javarefresher.topics.concurrency.model.SynchronizedCounter;
import com.javarefresher.topics.concurrency.model.UnsafeCounter;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ConcurrencyStudyTopic implements StudyTopic {
    private static final int THREADS = 8;
    private static final int INCREMENTS_PER_THREAD = 100_000;

    @Override
    public String key() {
        return "concurrency";
    }

    @Override
    public String title() {
        return "Concurrency and Shared Resource Protection";
    }

    @Override
    public void run(ConsolePrinter printer) {
        printer.topicHeader(key(), title());
        printer.section("Functionality", "Runs the same shared counter workload with unsafe, synchronized, and lock-based implementations.");
        printer.section("Design patterns utilized", "Monitor Object (`synchronized`) + Lock Object (`ReentrantLock`) + Atomic primitive (`AtomicInteger`) + Strategy-style interchangeable counter implementations.");
        printer.interviewFrame(
                "Many threads increment one shared counter at the same time.",
                "Uncoordinated read-modify-write on mutable shared state.",
                "Protect critical sections with synchronization primitives and keep the protected code small.",
                "Lead developers are expected to prevent race conditions and explain trade-offs in synchronization choices."
        );

        int expected = THREADS * INCREMENTS_PER_THREAD;
        printer.section("Workload", "threads=%d, incrementsPerThread=%d, expectedFinalCount=%d".formatted(THREADS, INCREMENTS_PER_THREAD, expected));

        List<Counter> counters = List.of(
                new UnsafeCounter(),
                new AtomicCounter(),
                new SynchronizedCounter(),
                new LockingCounter()
        );

        System.out.println();
        System.out.println("Results:");
        for (Counter counter : counters) {
            Result result = runExperiment(counter);
            System.out.printf(
                    " - %-20s | actual=%-8d | lostUpdates=%-8d | threadSafe=%s%n",
                    counter.label(),
                    result.actual(),
                    result.lostUpdates(),
                    counter.threadSafe() ? "yes" : "no"
            );
        }

        System.out.println();
        System.out.println("Antipattern reminder:");
        System.out.println(" - Do not share mutable objects across threads without a synchronization strategy.");
        System.out.println(" - Do not hold locks longer than necessary or across slow I/O calls.");
        printer.section("Interview angle", "For simple counters, `AtomicInteger` is a lightweight first choice; move to locks when updates span multiple shared values.");
        printLeadInterviewQa();
    }

    private static Result runExperiment(Counter counter) {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch ready = new CountDownLatch(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);

        for (int thread = 0; thread < THREADS; thread++) {
            executor.submit(() -> {
                ready.countDown();
                await(start);
                for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                    counter.increment();
                }
                done.countDown();
            });
        }

        await(ready);
        start.countDown();
        await(done);
        executor.shutdown();

        int expected = THREADS * INCREMENTS_PER_THREAD;
        int actual = counter.value();
        return new Result(expected, actual);
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for worker threads.", ex);
        }
    }

    private record Result(int expected, int actual) {
        int lostUpdates() {
            return Math.max(0, expected - actual);
        }
    }

    private void printLeadInterviewQa() {
        System.out.println();
        System.out.println("Lead Interview Q&A:");
        System.out.println(" Q1: Why does the unsafe counter lose updates?");
        System.out.println("  A: Increment is a read-modify-write sequence; without coordination, concurrent threads overwrite each other's writes.");
        System.out.println(" Q2: When is `AtomicInteger` preferred over locks?");
        System.out.println("  A: For single-variable atomic operations (increment/get/compare-and-set) where low overhead and lock-free progress are valuable.");
        System.out.println(" Q3: When do you still need `ReentrantLock`?");
        System.out.println("  A: When updates must coordinate multiple fields or require lock features like tryLock, fairness, or interruptible lock acquisition.");
        System.out.println(" Q4: How do you reduce contention in production systems?");
        System.out.println("  A: Minimize shared mutable state, keep critical sections short, and partition workload to reduce lock hot spots.");
    }
}
