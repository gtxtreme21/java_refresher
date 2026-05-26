package com.javarefresher.topics.inheritance.model;

public final class Sparrow extends SongBird {
    public Sparrow(String name) {
        super(name);
    }

    @Override
    protected String species() {
        return "sparrow";
    }
}
