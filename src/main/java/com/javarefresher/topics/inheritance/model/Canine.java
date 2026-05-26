package com.javarefresher.topics.inheritance.model;

public abstract class Canine extends AbstractAnimal {
    protected Canine(String name) {
        super(name);
    }

    @Override
    public final String family() {
        return "Canine";
    }

    @Override
    protected final String vocalization() {
        return canineSound();
    }

    protected abstract String canineSound();
}
