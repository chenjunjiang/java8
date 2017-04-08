package com.chenjj.java8.lambda;

public class BlocklambdaDemo {

    public static void main(String[] args) {
        NumericFunc numericFunc = (n) -> {
            int result = 1;
            for (int i = 1; i < n; i++) {
                result = i * result;
            }
            return result;
        };

        System.out.println(numericFunc.func(10));
    }
}
