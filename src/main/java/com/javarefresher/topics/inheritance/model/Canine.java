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

    /* Take advantage of the fact that all canines bark, 
    but allow subclasses to modify the sound if needed. */
    protected String canineSound() {
        return "bark";
    }
}
