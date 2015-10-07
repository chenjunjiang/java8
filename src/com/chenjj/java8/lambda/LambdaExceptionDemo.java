package com.chenjj.java8.lambda;

/**
 * Created by Administrator on 2015/9/22 0022.
 */
public class LambdaExceptionDemo {
    public static void main(String[] args) throws EmptyArrayException {
        double[] values = {1.0, 2.0, 3.0, 4.0, 5.0};
        DoubleNumbericArrayFunc doubleNumbericArrayFunc = n -> {// 这里也可以写成double[] n
            double sum = 0;
            if (n.length == 0) {
                throw new EmptyArrayException();
            }
            for (int i = 0; i < n.length; i++) {
                sum += n[i];
            }

            return sum;
        };

        System.out.println("The average is " + doubleNumbericArrayFunc.func(values));

        System.out.println("The average is " + doubleNumbericArrayFunc.func(new double[0]));
    }
}
