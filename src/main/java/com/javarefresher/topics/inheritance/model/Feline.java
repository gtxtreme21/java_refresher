package com.javarefresher.topics.inheritance.model;

public abstract class Feline extends AbstractAnimal {
    protected Feline(String name) {
        super(name);
    }

    @Override
    public final String family() {
        return "Feline";
    }

    @Override
    protected final String vocalization() {
        return felineSound();
    }

    protected abstract String felineSound();
}
