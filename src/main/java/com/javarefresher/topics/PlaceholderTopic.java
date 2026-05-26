package com.javarefresher.topics;

import com.javarefresher.core.ConsolePrinter;
import com.javarefresher.core.StudyTopic;

public final class PlaceholderTopic implements StudyTopic {
    private final String key;
    private final String title;
    private final String functionality;
    private final String patternFocus;
    private final String antipattern;
    private final String preferredPattern;
    private final String whyInterviewersCare;

    public PlaceholderTopic(
            String key,
            String title,
            String functionality,
            String patternFocus,
            String antipattern,
            String preferredPattern,
            String whyInterviewersCare
    ) {
        this.key = key;
        this.title = title;
        this.functionality = functionality;
        this.patternFocus = patternFocus;
        this.antipattern = antipattern;
        this.preferredPattern = preferredPattern;
        this.whyInterviewersCare = whyInterviewersCare;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public void run(ConsolePrinter printer) {
        printer.topicHeader(key, title);
        printer.section("Functionality", functionality);
        printer.section("Pattern focus", patternFocus);
        printer.interviewFrame(
                "Use this topic to explain practical design choices and trade-offs in production code.",
                antipattern,
                preferredPattern,
                whyInterviewersCare
        );
        printer.section("Phase status", "Phase 1 scaffold complete; detailed runnable code lands in upcoming phases.");
        printLeadInterviewQa();
    }

    private void printLeadInterviewQa() {
        System.out.println();
        System.out.println("Lead Interview Q&A:");
        System.out.println(" Q1: What antipattern should you call out first?");
        System.out.println("  A: " + antipattern + " Describe the production risk and why it scales poorly.");
        System.out.println(" Q2: What is your preferred pattern and why?");
        System.out.println("  A: " + preferredPattern + " Explain how it improves correctness, readability, and maintainability.");
        System.out.println(" Q3: How do you frame this at lead level?");
        System.out.println("  A: " + whyInterviewersCare);
    }
}
