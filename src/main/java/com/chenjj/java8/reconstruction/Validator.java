package com.chenjj.java8.reconstruction;

public class Validator {
    private final ValidationStrategy strategy;

    public Validator(ValidationStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean validate(String str) {
        return strategy.execute(str);
    }
}
