package com.javarefresher.topics.inheritance.model;

public abstract class Bird extends AbstractAnimal {
    protected Bird(String name) {
        super(name);
    }

    @Override
    public final String family() {
        return "Bird";
    }

    @Override
    protected final String vocalization() {
        return birdSound();
    }

    protected abstract String birdSound();
}
