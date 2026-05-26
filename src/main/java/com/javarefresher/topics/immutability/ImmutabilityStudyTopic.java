package com.javarefresher.topics.immutability;

import com.javarefresher.core.ConsolePrinter;
import com.javarefresher.core.StudyTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public final class ImmutabilityStudyTopic implements StudyTopic {
    private static final int THREADS = 8;
    private static final int TRANSITIONS_PER_THREAD = 60_000;

    @Override
    public String key() {
        return "immutability";
    }

    @Override
    public String title() {
        return "Immutability and Thread-Safety Design";
    }

    @Override
    public void run(ConsolePrinter printer) {
        printer.topicHeader(key(), title());
        printer.section("Functionality", "Contrasts mutable aliasing pitfalls with immutable domain design and compares unsafe versus atomic service-state transitions.");
        printer.section("Design patterns utilized", "Immutable Value Object + copy-with transitions + AtomicReference snapshot updates for thread-safe service state.");
        printer.interviewFrame(
                "Multiple requests update domain state while other components read the same object graph.",
                "Mutable domain objects leaked across boundaries and mutated in-place across threads.",
                "Use immutable domain objects and atomic replacement of snapshots in service classes.",
                "Lead developers are expected to preserve correctness under concurrency without overusing heavyweight locking."
        );

        demonstrateDomainImmutabilityBoundaries();
        demonstrateThreadSafetyDesignPatterns();
        printLeadInterviewQa();
    }

    private void demonstrateDomainImmutabilityBoundaries() {
        System.out.println();
        System.out.println("[1] Domain object boundary: mutable aliasing vs immutable copy-on-write");

        MutableCandidateProfile mutable = new MutableCandidateProfile(
                "1001",
                "Ava Thompson",
                "SCREEN",
                new ArrayList<>(List.of("java"))
        );

        List<String> leakedSkills = mutable.skills();
        leakedSkills.add("kotlin");
        mutable.setStage("ONSITE");
        System.out.println(" mutable profile after external mutation = " + mutable);

        CandidateProfile immutable = CandidateProfile.of(
                "1001",
                "Ava Thompson",
                "SCREEN",
                List.of("java"),
                0
        );
        CandidateProfile promoted = immutable.withAddedSkill("kotlin").transitionTo("ONSITE");

        System.out.println(" immutable original profile             = " + immutable);
        System.out.println(" immutable promoted profile             = " + promoted);

        try {
            immutable.skills().add("rust");
        } catch (UnsupportedOperationException ex) {
            System.out.println(" immutable skills list is protected from external mutation");
        }
    }

    private void demonstrateThreadSafetyDesignPatterns() {
        System.out.println();
        System.out.println("[2] Service-state transitions under concurrency");

        int expectedRevision = THREADS * TRANSITIONS_PER_THREAD;
        System.out.println(" workload: threads=%d, transitionsPerThread=%d, expectedRevision=%d"
                .formatted(THREADS, TRANSITIONS_PER_THREAD, expectedRevision));

        List<ProfileWorkflowService> services = List.of(
                new UnsafeMutableWorkflowService(),
                new AtomicImmutableWorkflowService()
        );

        System.out.println(" results:");
        for (ProfileWorkflowService service : services) {
            int actualRevision = runTransitionExperiment(service);
            int lostTransitions = Math.max(0, expectedRevision - actualRevision);
            System.out.printf(
                    " - %-34s | revision=%-8d | lostTransitions=%-8d | threadSafe=%s%n",
                    service.label(),
                    actualRevision,
                    lostTransitions,
                    service.threadSafe() ? "yes" : "no"
            );
        }

        System.out.println(" note: immutable snapshots with atomic replacement keep updates linearizable without explicit locks.");
    }

    private int runTransitionExperiment(ProfileWorkflowService service) {
        service.seed(CandidateProfile.of("1001", "Ava Thompson", "SCREEN", List.of("java"), 0));

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch ready = new CountDownLatch(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);

        for (int thread = 0; thread < THREADS; thread++) {
            executor.submit(() -> {
                ready.countDown();
                await(ready);
                await(start);
                for (int i = 0; i < TRANSITIONS_PER_THREAD; i++) {
                    service.transition("ONSITE");
                }
                done.countDown();
            });
        }

        start.countDown();
        await(done);
        executor.shutdown();
        return service.revision();
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting on worker coordination.", ex);
        }
    }

    private void printLeadInterviewQa() {
        System.out.println();
        System.out.println("Lead Interview Q&A:");
        System.out.println(" Q1: Why are immutable domain objects valuable in concurrent applications?");
        System.out.println("  A: They eliminate in-place mutation races, simplify reasoning, and make shared reads safe without defensive locking.");
        System.out.println(" Q2: Why pair immutability with `AtomicReference` in service classes?");
        System.out.println("  A: Atomic replacement gives thread-safe state transitions while keeping domain objects immutable and side-effect boundaries explicit.");
        System.out.println(" Q3: What trade-off comes with immutable snapshots?");
        System.out.println("  A: More object allocation and copy cost, which is usually acceptable for clarity/correctness unless profiling says otherwise.");
        System.out.println(" Q4: How does this map to Spring service design?");
        System.out.println("  A: Keep request-scoped computations immutable and isolate mutable shared caches/state behind thread-safe coordination primitives.");
    }

    private interface ProfileWorkflowService {
        String label();
        boolean threadSafe();
        void seed(CandidateProfile profile);
        void transition(String newStage);
        int revision();
    }

    private static final class UnsafeMutableWorkflowService implements ProfileWorkflowService {
        private MutableCandidateProfile profile;

        @Override
        public String label() {
            return "Unsafe mutable service state";
        }

        @Override
        public boolean threadSafe() {
            return false;
        }

        @Override
        public void seed(CandidateProfile profile) {
            this.profile = new MutableCandidateProfile(
                    profile.id(),
                    profile.fullName(),
                    profile.stage(),
                    new ArrayList<>(profile.skills())
            );
            this.profile.setRevision(profile.revision());
        }

        @Override
        public void transition(String newStage) {
            int nextRevision = profile.revision() + 1;
            Thread.yield();
            profile.setRevision(nextRevision);
            profile.setStage(newStage);
        }

        @Override
        public int revision() {
            return profile.revision();
        }
    }

    private static final class AtomicImmutableWorkflowService implements ProfileWorkflowService {
        private final AtomicReference<CandidateProfile> profileRef = new AtomicReference<>();

        @Override
        public String label() {
            return "AtomicReference + immutable profile";
        }

        @Override
        public boolean threadSafe() {
            return true;
        }

        @Override
        public void seed(CandidateProfile profile) {
            profileRef.set(profile);
        }

        @Override
        public void transition(String newStage) {
            profileRef.updateAndGet(current -> current.transitionTo(newStage));
        }

        @Override
        public int revision() {
            return profileRef.get().revision();
        }
    }

    private static final class MutableCandidateProfile {
        private final String id;
        private final String fullName;
        private final List<String> skills;
        private String stage;
        private int revision;

        private MutableCandidateProfile(String id, String fullName, String stage, List<String> skills) {
            this.id = id;
            this.fullName = fullName;
            this.stage = stage;
            this.skills = skills;
        }

        private List<String> skills() {
            return skills;
        }

        private int revision() {
            return revision;
        }

        private void setRevision(int revision) {
            this.revision = revision;
        }

        private void setStage(String stage) {
            this.stage = stage;
        }

        @Override
        public String toString() {
            return "MutableCandidateProfile[id=%s, fullName=%s, stage=%s, skills=%s, revision=%d]"
                    .formatted(id, fullName, stage, skills, revision);
        }
    }

    private record CandidateProfile(
            String id,
            String fullName,
            String stage,
            List<String> skills,
            int revision
    ) {
        private CandidateProfile {
            skills = List.copyOf(skills);
        }

        private static CandidateProfile of(
                String id,
                String fullName,
                String stage,
                List<String> skills,
                int revision
        ) {
            return new CandidateProfile(id, fullName, stage, skills, revision);
        }

        private CandidateProfile withAddedSkill(String skill) {
            List<String> nextSkills = new ArrayList<>(skills);
            nextSkills.add(skill);
            return new CandidateProfile(id, fullName, stage, nextSkills, revision);
        }

        private CandidateProfile transitionTo(String newStage) {
            return new CandidateProfile(id, fullName, newStage, skills, revision + 1);
        }
    }
}
