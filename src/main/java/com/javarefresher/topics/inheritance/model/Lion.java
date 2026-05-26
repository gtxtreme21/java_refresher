package com.javarefresher.topics.inheritance.model;

public final class Lion extends Feline {
    public Lion(String name) {
        super(name);
    }

    @Override
    protected String species() {
        return "lion";
    }

    @Override
    protected String felineSound() {
        return "roar";
    }
}
