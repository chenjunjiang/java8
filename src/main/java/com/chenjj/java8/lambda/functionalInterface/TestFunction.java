package com.chenjj.java8.lambda.functionalInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TestFunction {
    public static void main(String[] args) {
        List<Integer> results = map(Arrays.asList("lambdas", "in", "action"), (String s) -> s.length());
        System.out.println(results);
    }

    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        List<R> results = new ArrayList<>();
        for (T t : list) {
            results.add(function.apply(t));
        }
        return results;
    }
}
