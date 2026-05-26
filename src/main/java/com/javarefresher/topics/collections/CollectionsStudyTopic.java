package com.javarefresher.topics.collections;

import com.javarefresher.core.ConsolePrinter;
import com.javarefresher.core.StudyTopic;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class CollectionsStudyTopic implements StudyTopic {
    @Override
    public String key() {
        return "collections";
    }

    @Override
    public String title() {
        return "Looping, CRUD, and Collection Pitfalls";
    }

    @Override
    public void run(ConsolePrinter printer) {
        printer.topicHeader(key(), title());
        printer.section("Functionality", "Demonstrates common collection-mutation bugs and safer CRUD/looping approaches.");
        printer.section("Design patterns utilized", "Iterator pattern + defensive copying + immutable transformation.");
        printer.interviewFrame(
                "Apply updates/removals while iterating a collection of records.",
                "Mutating collections unsafely during traversal or mutating shared object references across list boundaries.",
                "Use iterator-aware removal, collect-then-apply, and immutable views/snapshots for safer updates.",
                "Lead developers are expected to prevent subtle data bugs in high-traffic services and batch jobs."
        );

        demonstrateIndexShiftPitfall();
        demonstrateConcurrentModificationPitfall();
        demonstrateSharedReferenceCrudPitfall();

        System.out.println();
        System.out.println("Antipattern reminders:");
        System.out.println(" - Forward-index removal can skip elements after index shifts.");
        System.out.println(" - Enhanced for-loop + direct remove causes runtime failure.");
        System.out.println(" - Shallow list copies do not protect against object-level mutation side effects.");
        printer.section("Interview angle", "Prefer explicit update rules, immutable boundaries, and collection operations that make side effects obvious.");
        printLeadInterviewQa();
    }

    private void demonstrateIndexShiftPitfall() {
        System.out.println();
        System.out.println("[1] Forward-loop index shift pitfall");
        List<Integer> values = new ArrayList<>(List.of(2, 4, 6, 7));
        System.out.println(" start values = " + values);

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) % 2 == 0) {
                values.remove(i);
            }
        }
        System.out.println(" unsafe forward remove result = " + values + " (even value 4 was skipped)");

        List<Integer> safeValues = new ArrayList<>(List.of(2, 4, 6, 7));
        safeValues.removeIf(v -> v % 2 == 0);
        System.out.println(" safe removeIf result        = " + safeValues);
    }

    private void demonstrateConcurrentModificationPitfall() {
        System.out.println();
        System.out.println("[2] Enhanced for-loop mutation pitfall");
        List<String> tags = new ArrayList<>(List.of("todo", "legacy", "cleanup", "legacy-api"));
        System.out.println(" start tags = " + tags);

        try {
            for (String tag : tags) {
                if (tag.startsWith("legacy")) {
                    tags.remove(tag);
                }
            }
        } catch (ConcurrentModificationException ex) {
            System.out.println(" unsafe enhanced for remove -> ConcurrentModificationException");
        }

        List<String> safeTags = new ArrayList<>(List.of("todo", "legacy", "cleanup", "legacy-api"));
        Iterator<String> iterator = safeTags.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().startsWith("legacy")) {
                iterator.remove();
            }
        }
        System.out.println(" safe iterator remove result = " + safeTags);
    }

    private void demonstrateSharedReferenceCrudPitfall() {
        System.out.println();
        System.out.println("[3] CRUD on shared object references");
        List<MutableCandidate> pipeline = new ArrayList<>(List.of(
                new MutableCandidate("Ava", "Screen"),
                new MutableCandidate("Noah", "Onsite")
        ));

        List<MutableCandidate> shortlistAlias = new ArrayList<>(pipeline);
        shortlistAlias.get(0).setStage("Offer");

        System.out.println(" pipeline after alias update   = " + describeMutable(pipeline));
        System.out.println(" shortlist alias               = " + describeMutable(shortlistAlias));
        System.out.println(" note: both changed because lists reference the same mutable objects");

        List<CandidateSnapshot> safeSnapshot = pipeline.stream()
                .map(c -> new CandidateSnapshot(c.name(), c.stage()))
                .collect(Collectors.toCollection(ArrayList::new));
        safeSnapshot.set(0, safeSnapshot.get(0).withStage("Rejected"));

        System.out.println(" immutable snapshot update     = " + safeSnapshot);
        System.out.println(" original pipeline still same  = " + describeMutable(pipeline));
    }

    private String describeMutable(List<MutableCandidate> candidates) {
        return candidates.stream()
                .map(c -> c.name() + ":" + c.stage())
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static final class MutableCandidate {
        private final String name;
        private String stage;

        private MutableCandidate(String name, String stage) {
            this.name = name;
            this.stage = stage;
        }

        private String name() {
            return name;
        }

        private String stage() {
            return stage;
        }

        private void setStage(String stage) {
            this.stage = stage;
        }
    }

    private record CandidateSnapshot(String name, String stage) {
        CandidateSnapshot withStage(String updatedStage) {
            return new CandidateSnapshot(name, updatedStage);
        }
    }

    private void printLeadInterviewQa() {
        System.out.println();
        System.out.println("Lead Interview Q&A:");
        System.out.println(" Q1: Why can forward-index deletes produce logic bugs?");
        System.out.println("  A: Removing at index i shifts later elements left, so incrementing i can skip elements that still need evaluation.");
        System.out.println(" Q2: What is the safest way to remove while iterating?");
        System.out.println("  A: Use iterator-aware removal (`Iterator.remove`) or `removeIf` when rule-based filtering fits.");
        System.out.println(" Q3: Why are shallow list copies risky in CRUD flows?");
        System.out.println("  A: They duplicate container references, not objects, so mutating an item in one list mutates it everywhere.");
        System.out.println(" Q4: How do you make collection updates more maintainable?");
        System.out.println("  A: Prefer immutable snapshots/transformations so side effects are explicit and easier to reason about.");
    }
}
