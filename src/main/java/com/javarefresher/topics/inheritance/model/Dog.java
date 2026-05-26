package com.javarefresher.topics.inheritance.model;

public final class Dog extends Canine {
    public Dog(String name) {
        super(name);
    }

    @Override
    protected String species() {
        return "dog";
    }

    // Inheritance and polymorphism: 
    // All Canines "bark" - Call the parent class's canineSound method
    // to get the default "bark" behavior without needing to duplicate it here.
}
