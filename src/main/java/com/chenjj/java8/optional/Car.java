package com.chenjj.java8.optional;

import java.util.Optional;

public class Car {
    /**
     * 车可能进行了保险，也可能没有保险，所以将这个字段声明为Optional
     */
    private Optional<Insurance> insurance;

    public Optional<Insurance> getInsurance() {
        return insurance;
    }
}
