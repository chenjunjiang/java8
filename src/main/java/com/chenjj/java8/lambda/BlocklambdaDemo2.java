package com.chenjj.java8.lambda;

public class BlocklambdaDemo2 {

    public static void main(String[] args) {
        StringFunc reverse = (str) -> {
            String result = "";
            int i = 0;
            for (i = str.length() - 1; i >= 0; i--) {
                result += str.charAt(i);
            }

            return result;
        };

        System.out.println(reverse.func("lambda"));
    }
}
