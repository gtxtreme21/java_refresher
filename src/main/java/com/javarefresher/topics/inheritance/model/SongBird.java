package com.javarefresher.topics.inheritance.model;

public abstract class SongBird extends Bird {
    protected SongBird(String name) {
        super(name);
    }

    @Override
    protected final String birdSound() {
        return "chirp";
    }
}
