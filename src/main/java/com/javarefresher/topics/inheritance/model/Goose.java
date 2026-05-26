package com.javarefresher.topics.inheritance.model;

public final class Goose extends Waterfowl {
    public Goose(String name) {
        super(name);
    }

    @Override
    protected String species() {
        return "goose";
    }
}
