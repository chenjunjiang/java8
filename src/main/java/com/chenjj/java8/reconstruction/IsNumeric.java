package com.chenjj.java8.reconstruction;

public class IsNumeric implements ValidationStrategy {
    @Override
    public boolean execute(String str) {
        return str.matches("\\d+");
    }
}
