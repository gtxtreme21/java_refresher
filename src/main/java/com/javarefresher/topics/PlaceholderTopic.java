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
    }
}
