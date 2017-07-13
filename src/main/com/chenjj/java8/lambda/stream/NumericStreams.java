package com.chenjj.java8.lambda.stream;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.chenjj.java8.lambda.stream.Dish.menu;

public class NumericStreams {

    public static void main(String... args) {

        List<Integer> numbers = Arrays.asList(3, 4, 5, 1, 2);

        Arrays.stream(numbers.toArray()).forEach(System.out::println);

        // 这段代码的问题是，它有一个暗含的装箱成本。每个Integer都必须拆箱成一个原始类型，再进行求和。
        /*int calories = menu.stream()
                .map(Dish::getCalories)
                .reduce(0, Integer::sum);*/

        int calories = menu.stream()
                .mapToInt(Dish::getCalories)
                .sum();
        System.out.println("Number of calories:" + calories);


        // max and OptionalInt
        OptionalInt maxCalories = menu.stream()
                .mapToInt(Dish::getCalories)
                .max();

        int max;
        if (maxCalories.isPresent()) {
            max = maxCalories.getAsInt();
        } else {
            // we can choose a default value
            max = 1;
        }
        System.out.println(max);

        // ava 8引入了两个可以用于IntStream和LongStream的静态方法，帮助生成这种范围：
        // range和rangeClosed。这两个方法都是第一个参数接受起始值，第二个参数接受结束值。但
        //range是不包含结束值的，而rangeClosed则包含结束值。
        // numeric ranges
        IntStream evenNumbers = IntStream.rangeClosed(1, 100)
                .filter(n -> n % 2 == 0);

        System.out.println(evenNumbers.count());

        // 要把原始流转换成一般流（每个int都会装箱成一个Integer），可以使用boxed方法
        Stream<int[]> pythagoreanTriples =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a -> IntStream.rangeClosed(a, 100)
                                .filter(b -> Math.sqrt(a * a + b * b) % 1 ==
                                        0).boxed()
                                .map(b -> new int[]{a, b, (int) Math.sqrt(a *
                                        a + b * b)}));

        pythagoreanTriples.forEach(t -> System.out.println(t[0] + ", " + t[1]
                + ", " + t[2]));

        Stream<double[]> pythagoreanTriples2 =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a ->
                                IntStream.rangeClosed(a, 100)
                                        .mapToObj(
                                                b -> new double[]{a, b, Math
                                                        .sqrt(a * a + b * b)})
                                        .filter(t -> t[2] % 1 == 0));

    }

    public static boolean isPerfectSquare(int n) {
        return Math.sqrt(n) % 1 == 0;
    }

}
