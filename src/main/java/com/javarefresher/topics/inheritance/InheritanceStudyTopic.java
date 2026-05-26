package com.javarefresher.topics.inheritance;

import com.javarefresher.core.ConsolePrinter;
import com.javarefresher.core.StudyTopic;
import com.javarefresher.topics.inheritance.model.Animal;
import com.javarefresher.topics.inheritance.model.Dog;
import com.javarefresher.topics.inheritance.model.Goose;
import com.javarefresher.topics.inheritance.model.Lion;
import com.javarefresher.topics.inheritance.model.MallardDuck;
import com.javarefresher.topics.inheritance.model.Sparrow;

import java.util.List;

public final class InheritanceStudyTopic implements StudyTopic {
    @Override
    public String key() {
        return "inheritance";
    }

    @Override
    public String title() {
        return "Inheritance and Polymorphism (Animal hierarchy)";
    }

    @Override
    public void run(ConsolePrinter printer) {
        printer.topicHeader(key(), title());
        printer.section("Functionality", "Demonstrates one Animal interface shared by Feline, Canine, and Bird hierarchies with polymorphic talk behavior.");
        printer.section("Design patterns utilized", "Interface-based polymorphism + Template Method + hierarchical specialization for shared bird behavior.");
        printer.interviewFrame(
                "Call `talk()` on a mixed list of Animal objects without type checks.",
                "Using long `instanceof` chains to decide each animal sound.",
                "Use an abstract template in the base class and let each family or sub-family own the sound hook.",
                "This shows extensible object modeling and clean dispatch decisions expected from lead-level developers."
        );

        System.out.println();
        System.out.println("Polymorphic talk() output:");
        List<Animal> animals = List.of(
                new Lion("Simba"),
                new Dog("Rex"),
                new MallardDuck("Daisy"),
                new Goose("Gus"),
                new Sparrow("Pip")
        );

        for (Animal animal : animals) {
            System.out.printf(" - %-8s | family=%-7s | %s%n", animal.name(), animal.family(), animal.talk());
        }

        System.out.println();
        System.out.println("Antipattern reminder:");
        System.out.println(" - Avoid if/else trees like: if (animal instanceof Lion) { ... }");
        System.out.println(" - Prefer: animal.talk() and let polymorphism choose the behavior.");
        printer.section("Interview angle", "Waterfowl subclasses inherit quack behavior, while songbirds inherit chirp behavior from a different bird sub-family.");
    }
}
