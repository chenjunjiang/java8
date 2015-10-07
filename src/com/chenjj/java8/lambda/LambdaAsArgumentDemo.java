package com.chenjj.java8.lambda;

/**
 * Created by Administrator on 2015/9/21 0021.
 */
public class LambdaAsArgumentDemo {
    public static String stringOp(StringFunc stringFunc, String str) {
        return stringFunc.func(str);
    }

    public static void main(String[] args) {
        String inStr = "lmabda add power to Java";
        String outStr;

        System.out.println("Here is input string：" + inStr);

        outStr = stringOp((str) -> {
            String result = "";
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) != ' ') {
                    result += str.charAt(i);
                }
            }

            return result;
        }, inStr);

        System.out.println("The string switch spaces removed：" + outStr);

        StringFunc reverse = (str) -> {
            String result = "";
            for (int i = str.length() - 1; i > 0; i--) {
                result += str.charAt(i);
            }

            return result;
        };

        System.out.println("The string reversed：" + stringOp(reverse, inStr));
    }
}
