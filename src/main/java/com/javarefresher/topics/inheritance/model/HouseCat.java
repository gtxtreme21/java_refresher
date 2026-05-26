package com.javarefresher.topics.inheritance.model;

public final class HouseCat extends Feline {
    public HouseCat(String name) {
        super(name);
    }

    @Override
    protected String species() {
        return "house cat";
    }

    @Override
    protected String felineSound() {
        return "meow";
    }
}
