package com.javarefresher;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import com.javarefresher.core.ConsolePrinter;
import com.javarefresher.core.StudyTopic;
import com.javarefresher.topics.collections.CollectionsStudyTopic;
import com.javarefresher.topics.concurrency.ConcurrencyStudyTopic;
import com.javarefresher.topics.immutability.ImmutabilityStudyTopic;
import com.javarefresher.topics.inheritance.InheritanceStudyTopic;
import com.javarefresher.topics.springmvc.SpringMvcStudyTopic;

public final class StudyApp {
    private final ConsolePrinter printer = new ConsolePrinter();
    private final List<StudyTopic> topics = List.of(
            new InheritanceStudyTopic(),
            new ConcurrencyStudyTopic(),
            new CollectionsStudyTopic(),
            new SpringMvcStudyTopic(),
            new ImmutabilityStudyTopic()
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
        printer.section("Next step", "Core lead-level topic set is now fully implemented; optionally add docs/pages study companion as a final publishing step.");
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
        printer.section("Instruction:", "Use `.\\scripts\\study.ps1 -TopicKey <topic_key>` to run a specific topic.");
    }

    private void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java com.javarefresher.StudyApp --all");
        System.out.println("  java com.javarefresher.StudyApp --list");
        System.out.println("  java com.javarefresher.StudyApp --menu");
        System.out.println("  java com.javarefresher.StudyApp --topic <key>");
    }
}
