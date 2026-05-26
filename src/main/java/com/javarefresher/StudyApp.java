package com.javarefresher;

import com.javarefresher.core.ConsolePrinter;
import com.javarefresher.core.StudyTopic;
import com.javarefresher.topics.PlaceholderTopic;
import com.javarefresher.topics.collections.CollectionsStudyTopic;
import com.javarefresher.topics.concurrency.ConcurrencyStudyTopic;
import com.javarefresher.topics.inheritance.InheritanceStudyTopic;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public final class StudyApp {
    private final ConsolePrinter printer = new ConsolePrinter();
    private final List<StudyTopic> topics = List.of(
            new InheritanceStudyTopic(),
            new ConcurrencyStudyTopic(),
            new CollectionsStudyTopic(),
            new PlaceholderTopic(
                    "spring-mvc",
                    "Spring Boot MVC Patterns and Antipatterns",
                    "Refresh controller/service/repository responsibilities and boundary-focused design.",
                    "Layered Architecture + Dependency Injection",
                    "Placing business logic in controllers and leaking persistence objects to API boundaries.",
                    "Keep thin controllers, cohesive services, and explicit DTO mapping between layers.",
                    "Interviewers often assess architecture judgment and maintainability trade-offs."
            ),
            new PlaceholderTopic(
                    "immutability",
                    "Immutability and Thread-Safety Design",
                    "Use immutable domain data and controlled state transitions to reduce concurrency risk.",
                    "Immutable Value Object pattern",
                    "Mutable shared objects passed broadly across threads with unclear ownership.",
                    "Prefer immutable objects and narrowly scoped mutation points.",
                    "This demonstrates senior-level decision-making around correctness and operability."
            )
    );

    public static void main(String[] args) {
        new StudyApp().run(args);
    }

    private void run(String[] args) {
        List<String> argList = Arrays.asList(args);

        if (argList.contains("--list")) {
            listTopics();
            return;
        }

        int topicIndex = argList.indexOf("--topic");
        if (topicIndex >= 0) {
            if (topicIndex + 1 >= argList.size()) {
                printer.banner("Invalid arguments");
                System.out.println("`--topic` requires a topic key.");
                printUsage();
                return;
            }
            runSingleTopic(argList.get(topicIndex + 1));
            return;
        }

        if (argList.contains("--all")) {
            runAllTopics();
            return;
        }

        if (argList.contains("--menu") || argList.isEmpty()) {
            runInteractiveMenu();
            return;
        }

        printer.banner("Unknown argument(s)");
        printUsage();
    }

    private void runAllTopics() {
        printer.banner("Java Lead Developer Refresher");
        for (StudyTopic topic : topics) {
            topic.run(printer);
        }
        printer.section("Next step", "Upcoming phases will replace remaining placeholder topics with full runnable code walkthroughs.");
    }

    private void runSingleTopic(String key) {
        String normalizedKey = key.toLowerCase(Locale.ROOT);
        for (StudyTopic topic : topics) {
            if (topic.key().equals(normalizedKey)) {
                printer.banner("Single Topic Study");
                topic.run(printer);
                return;
            }
        }
        printer.banner("Topic not found");
        System.out.println("Unknown topic key: " + key);
        listTopics();
    }

    private void runInteractiveMenu() {
        printer.banner("Java Refresher Menu");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Choose an option:");
                System.out.println("  0) Run all topics");
                for (int i = 0; i < topics.size(); i++) {
                    System.out.printf("  %d) %s (%s)%n", i + 1, topics.get(i).title(), topics.get(i).key());
                }
                System.out.println("  q) Quit");
                System.out.print("> ");

                String input = scanner.nextLine().trim();
                if ("q".equalsIgnoreCase(input)) {
                    System.out.println("Exiting study menu.");
                    return;
                }

                if ("0".equals(input)) {
                    runAllTopics();
                    continue;
                }

                try {
                    int selected = Integer.parseInt(input);
                    if (selected < 1 || selected > topics.size()) {
                        System.out.println("Invalid selection.");
                        continue;
                    }
                    printer.banner("Menu Topic Selection");
                    topics.get(selected - 1).run(printer);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a number or 'q'.");
                }
            }
        }
    }

    private void listTopics() {
        printer.banner("Available Topic Keys");
        for (StudyTopic topic : topics) {
            System.out.printf("- %s : %s%n", topic.key(), topic.title());
        }
    }

    private void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java com.javarefresher.StudyApp --all");
        System.out.println("  java com.javarefresher.StudyApp --list");
        System.out.println("  java com.javarefresher.StudyApp --menu");
        System.out.println("  java com.javarefresher.StudyApp --topic <key>");
    }
}
