package com.javarefresher.core;

public final class ConsolePrinter {
    private static final String RULE = "========================================================================";

    public void banner(String title) {
        System.out.println();
        System.out.println(RULE);
        System.out.println(title);
        System.out.println(RULE);
    }

    public void section(String label, String value) {
        System.out.printf("%s: %s%n", label, value);
    }

    public void topicHeader(String key, String title) {
        System.out.println();
        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("Topic: %s (%s)%n", title, key);
        System.out.println("-----------------------------------------------------------------------");
    }

    public void interviewFrame(String scenario, String antipattern, String preferredPattern, String whyInterviewersCare) {
        section("Scenario", scenario);
        section("Antipattern", antipattern);
        section("Preferred pattern", preferredPattern);
        section("Why interviewers care", whyInterviewersCare);
    }
}
