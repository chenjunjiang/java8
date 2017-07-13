package com.chenjj.java8.lambda;

public class GenericFunctionalInterfaceDemo {

    public static void main(String[] args) {
        SomeFunc<Integer> factorial = (n) -> {
            int result = 1;
            for (int i = 1; i < n; i++) {
                result *= i;
            }
            return result;
        };

        System.out.println(factorial.func(5));
    }

}
