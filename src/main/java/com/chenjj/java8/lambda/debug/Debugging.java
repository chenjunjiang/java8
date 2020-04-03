package com.chenjj.java8.lambda.debug;

import com.chenjj.java8.lambda.test.Point;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lambda表达式和栈跟踪
 * 由于Lambda表达式没有名字，它的栈跟踪可能很难分析。
 */
public class Debugging {
    public static void main(String[] args) {
        List<Point> points = Arrays.asList(new Point(12, 2), null);
        //points.stream().map(p -> p.getX()).forEach(System.out::println);
        /**
         * 异常信息如下：
         * Exception in thread "main" java.lang.NullPointerException
         * 	at com.chenjj.java8.lambda.debug.Debugging.lambda$main$0(Debugging.java:15)
         * 	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
         * 	......
         * 	这些表示错误发生在Lambda表达式内部。由于Lambda表达式没有名字，所以编译器只能为
         * 它们指定一个名字。这个例子中，它的名字是lambda$main$0，看起来非常不直观。如果你使
         * 用了大量的类，其中又包含多个Lambda表达式，这就成了一个非常头痛的问题。
         * 即使你使用了方法引用，还是有可能出现栈无法显示你使用的方法名的情况。将之前的
         * Lambda表达式p-> p.getX()替换为方法引用reference Point::getX也会产生难于分析的栈
         * 跟踪
         */
        // points.stream().map(Point::getX).forEach(System.out::println);

        /**
         * 注意，如果方法引用指向的是同一个类中声明的方法，那么它的名称是可以在栈跟踪中显示的。
         */
        /*List<Integer> numbers = Arrays.asList(1, 2, 3);
        numbers.stream().map(Debugging::divideByZero).forEach(System.out::println);*/

        /**
         * 对流操作中的流水线进行调试，可以使用peek方法，peek的设计初衷就是在流的每个元素恢复运行之
         * 前，插入执行一个动作。但是它不像forEach那样恢复整个流的运行，而是在一个元素上完成操
         * 作之后，它只会将操作顺承到流水线中的下一个操作。
         */
        List<Integer> numbers = Arrays.asList(2, 3, 4, 5);
        numbers.stream()
                .map(x -> x + 17)
                .filter(x -> x % 2 == 0)
                .limit(3)
                .forEach(System.out::println);

        numbers.stream()
                .peek(x -> System.out.println("from stream: " + x))
                .map(x -> x + 17)
                .peek(x -> System.out.println("after map: " + x))
                .filter(x -> x % 2 == 0)
                .peek(x -> System.out.println("after filter: " + x))
                .limit(3)
                .peek(x -> System.out.println("after limit: " + x))
                .collect(Collectors.toList());// 必须要有collect这样的终端操作才能启动整个流水线，流水线操作才会执行
    }

    public static int divideByZero(int n) {
        return n / 0;
    }
}
