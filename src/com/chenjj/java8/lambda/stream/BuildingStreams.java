package com.chenjj.java8.lambda.stream;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.*;
import java.nio.charset.Charset;
import java.nio.file.*;

public class BuildingStreams {

    public static void main(String... args) throws Exception {

        // Stream.of
        Stream<String> stream = Stream.of("Java 8", "Lambdas", "In", "Action");
        stream.map(String::toUpperCase).forEach(System.out::println);

        // Stream.empty
        Stream<String> emptyStream = Stream.empty();

        // Arrays.stream
        int[] numbers = {2, 3, 5, 7, 11, 13};
        System.out.println(Arrays.stream(numbers).sum());

        /**
         * Stream.iterate和Stream.generate
         * 可以创建所谓的无限流：不像从固定集合创建的流那样有固定大小的流。由iterate
         和generate产生的流会用给定的函数按需创建值，因此可以无穷无尽地计算下去！一般来说，
         应该使用limit(n)来对这种流加以限制，以避免打印无穷多个值。
         */
        // Stream.iterate
        // 返回的是前一个元素加上2
        // 一般来说，在需要依次生成一系列值的时候应该使用iterate
        Stream.iterate(0, n -> n + 2)
                .limit(10)
                .forEach(System.out::println);

        // fibonnaci with iterate
        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .limit(10)
                .forEach(t -> System.out.println("(" + t[0] + ", " + t[1] +
                        ")"));

        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .limit(10)
                .map(t -> t[0])
                .forEach(System.out::println);

        // random stream of doubles with Stream.generate
        Stream.generate(Math::random)
                .limit(10)
                .forEach(System.out::println);

        // stream of 1s with Stream.generate
        IntStream.generate(() -> 1)
                .limit(5)
                .forEach(System.out::println);

        /**
         * 这里使用的匿名类和Lambda的区别在于，匿名类可以通过字段定义状态，而状态又可以用
         getAsInt方法来修改。这是一个副作用的例子。你迄今见过的所有Lambda都是没有副作用的；
         它们没有改变任何状态。
         */
        IntStream.generate(new IntSupplier() {
            // private boolean flag;
            public int getAsInt() {
                // flag = true;
                return 2;
            }
        }).limit(5)
                .forEach(System.out::println);


        /**
         * 创建了一个IntSupplier的实例。此对象有可变的状态：它在两个实例变量中
         记录了前一个斐波纳契项和当前的斐波纳契项。getAsInt在调用时会改变对象的状态，由此在
         每次调用时产生新的值。相比之下，使用iterate的方法则是纯粹不变的：它没有修改现有状态，
         但在每次迭代时会创建新的元组。你将在第7章了解到，你应该始终采用不变的方法，以便并行
         处理流，并保持结果正确。请注意，因为你处理的是一个无限流，所以必须使用limit操作来显
         式限制它的大小；否则，终端操作（这里是forEach）将永远计算下去。同样，你不能对无限流
         做排序或归约，因为所有元素都需要处理，而这永远也完不成！
         */
        IntSupplier fib = new IntSupplier() {
            private int previous = 0;
            private int current = 1;

            public int getAsInt() {
                int nextValue = this.previous + this.current;
                this.previous = this.current;
                this.current = nextValue;
                return this.previous;
            }
        };
        IntStream.generate(fib).limit(10).forEach(System.out::println);

        // 使用Files.lines得到一个流，其中的每个元素都是给定文件中的一行。
        long uniqueWords = Files.lines(Paths.get("lambdasinaction/chap5/data" +
                ".txt"), Charset.defaultCharset())
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .distinct()
                .count();

        System.out.println("There are " + uniqueWords + " unique words in " +
                "data.txt");


    }
}
