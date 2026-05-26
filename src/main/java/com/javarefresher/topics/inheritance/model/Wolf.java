package com.javarefresher.topics.inheritance.model;

public final class Wolf extends Canine {
    public Wolf(String name) {
        super(name);
    }

    @Override
    protected String species() {
        return "wolf";
    }

    @Override
    protected String canineSound() {
        return super.canineSound() + " (but louder)";
        // Inheritance and polymorphism: Wolves also "bark" 
        // but we can modify the sound if needed by calling the parent class's canineSound method and adding a note about it being louder.
    }
}
