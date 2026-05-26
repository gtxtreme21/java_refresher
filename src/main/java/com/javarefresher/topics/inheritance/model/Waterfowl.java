package com.javarefresher.topics.inheritance.model;

public abstract class Waterfowl extends Bird {
    protected Waterfowl(String name) {
        super(name);
    }

    @Override
    protected final String birdSound() {
        return "quack";
    }
}
