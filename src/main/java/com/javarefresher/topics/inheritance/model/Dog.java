package com.javarefresher.topics.inheritance.model;

public final class Dog extends Canine {
    public Dog(String name) {
        super(name);
    }

    @Override
    protected String species() {
        return "dog";
    }

    @Override
    protected String canineSound() {
        return "bark";
    }
}
