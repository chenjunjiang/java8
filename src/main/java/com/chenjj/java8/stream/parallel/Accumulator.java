package com.chenjj.java8.stream.parallel;

public class Accumulator {
    public long total = 0;

    public void add(long value) {
        total += value;
    }
}
