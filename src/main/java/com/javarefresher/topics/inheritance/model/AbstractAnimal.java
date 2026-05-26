package com.javarefresher.topics.inheritance.model;

public abstract class AbstractAnimal implements Animal {
    private final String name;

    protected AbstractAnimal(String name) {
        this.name = name;
    }

    @Override
    public final String name() {
        return name;
    }

    protected abstract String species();
    protected abstract String vocalization();

    @Override
    public final String talk() {
        return "%s the %s says \"%s\".".formatted(name, species(), vocalization());
    }
}
