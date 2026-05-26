package com.javarefresher.topics.inheritance.model;

public final class MallardDuck extends Waterfowl {
    public MallardDuck(String name) {
        super(name);
    }

    @Override
    protected String species() {
        return "mallard duck";
    }
}
