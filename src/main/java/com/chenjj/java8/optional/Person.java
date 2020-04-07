package com.chenjj.java8.optional;

import java.util.Optional;

public class Person {
    /**
     * 人可能有车，也可能没有车，因此将这个字段声明为Optional
     */
    private Optional<Car> car;

    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Optional<Car> getCar() {
        return car;
    }
}
