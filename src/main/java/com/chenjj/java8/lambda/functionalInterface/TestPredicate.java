package com.chenjj.java8.lambda.functionalInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class TestPredicate {
    public static void main(String[] args) {
        Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
        List<String> nonEmpty = filter(Arrays.asList("ac", "xx", "", "pp"), nonEmptyStringPredicate);
        System.out.println(nonEmpty);
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> results = new ArrayList<>();
        for (T s : list) {
            if (predicate.test(s)) {
                results.add(s);
            }
        }
        return results;
    }
}
