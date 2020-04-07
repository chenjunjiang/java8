package com.chenjj.java8.default1;

public interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}
