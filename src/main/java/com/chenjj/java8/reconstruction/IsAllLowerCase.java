package com.chenjj.java8.reconstruction;

public class IsAllLowerCase implements ValidationStrategy {
    @Override
    public boolean execute(String str) {
        return str.matches("[a-z]+");
    }
}
