package com.chenjj.java8.stream;

import com.chenjj.java8.model.Dish;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.chenjj.java8.model.Dish.menu;

/**
 * 数值流
 * Java 8引入了三个原始类型特化流接口，IntStream、 DoubleStream和
 * LongStream，分别将流中的元素特化为int、 long和double，从而避免了暗含的装箱成本。每
 * 个接口都带来了进行常用数值归约的新方法，比如对数值流求和的sum，找到最大元素的max。
 * 此外还有在必要时再把它们转换回对象流的方法。要记住的是，这些特化的原因并不在于流的复
 * 杂性，而是装箱造成的复杂性——即类似int和Integer之间的效率差异。
 */
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


        /**
         * 对于三种原始流特化，也分别有一个Optional原始类
         * 型特化版本： OptionalInt、 OptionalDouble和OptionalLong。
         *找到IntStream中的最大元素，可以调用max方法，它会返回一个OptionalInt
         */
        OptionalInt maxCalories = menu.stream()
                .mapToInt(Dish::getCalories)
                .max();

        // 如果没有最大值的话,显示提供一个默认最大值
        int max = maxCalories.orElse(1);
        if (maxCalories.isPresent()) {
            max = maxCalories.getAsInt();
        } else {
            // we can choose a default value
            max = 1;
        }
        System.out.println(max);

        // java 8引入了两个可以用于IntStream和LongStream的静态方法，帮助生成这种范围：
        // range和rangeClosed。这两个方法都是第一个参数接受起始值，第二个参数接受结束值。但
        //range是不包含结束值的，而rangeClosed则包含结束值。
        // numeric ranges
        IntStream evenNumbers = IntStream.rangeClosed(1, 100)
                .filter(n -> n % 2 == 0);
        System.out.println(evenNumbers.count());

        // 要把原始流转换成一般流（每个int都会装箱成一个Integer），可以使用boxed方法
        IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
        Stream<Integer> stream = intStream.boxed();

        /**
         * 创建一个勾股数流
         */
        Stream<int[]> pythagoreanTriples =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a -> IntStream.rangeClosed(a, 100)
                                .filter(
                                        // 平方根的结果是不是整数
                                        b -> Math.sqrt(a * a + b * b) % 1 == 0)
                                .boxed()
                                .map(b -> new int[]{a, b, (int) Math.sqrt(a *
                                        a + b * b)}));

        pythagoreanTriples.forEach(t -> System.out.println(t[0] + ", " + t[1]
                + ", " + t[2]));

        // 上面那种方法要求两次平方根,还可以优化
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
