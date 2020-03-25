package com.chenjj.java8.stream;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * 构建流
 */
public class CreateStream {
    public static void main(String[] args) {
        /**
         * 由值创建流
         * 你可以使用静态方法Stream.of，通过显式值创建一个流。它可以接受任意数量的参数。例
         * 如，以下代码直接使用Stream.of创建了一个字符串流。然后，你可以将字符串转换为大写，再
         * 一个个打印出来
         */
        Stream<String> stream = Stream.of("Java 8 ", "Lambdas ", "In ", "Action");
        stream.map(String::toUpperCase).forEach(System.out::println);
        // 你可以使用empty得到一个空流
        Stream<String> emptyStream = Stream.empty();

        /**
         * 由数组创建流
         * 你可以使用静态方法Arrays.stream从数组创建一个流。它接受一个数组作为参数。例如，
         * 你可以将一个原始类型int的数组转换成一个IntStream
         */
        int[] numbers = {2, 3, 5, 7, 11, 13};
        int sum = Arrays.stream(numbers).sum();

        /**
         * 由文件生成流
         * Files.lines，它会返回一个由指定文件中的各行构成的字符串流
         * 你可以用这个方法看看一个文件中有多少各不相同的词
         */
        long uniqueWords = 0;
        try (Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" "))).distinct().count();
            System.out.println(uniqueWords);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 由函数生成流：创建无限流
         * Stream API提供了两个静态方法来从函数生成流： Stream.iterate和Stream.generate。
         * 这两个操作可以创建所谓的无限流：不像从固定集合创建的流那样有固定大小的流。由iterate
         * 和generate产生的流会用给定的函数按需创建值，因此可以无穷无尽地计算下去！一般来说，
         * 应该使用limit(n)来对这种流加以限制，以避免打印无穷多个值。
         */
        // 前10个正偶数
        Stream.iterate(0, n -> n + 2).limit(10).forEach(System.out::println);

        /**
         * 斐波纳契元组序列
         * 这个数列就是斐波纳契数列的一部分： 0, 1, 1,
         * 2, 3, 5, 8, 13, 21, 34, 55…数列中开始的两个数字是0和1，后续的每个数字都是前两个数字之和。
         */
        System.out.println("-------------");
        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]}).limit(10)
                .map(t -> t[0]).forEach(System.out::println);

        /**
         * 与iterate方法类似， generate方法也可让你按需生成一个无限流。但generate不是依次
         * 对每个新生成的值应用函数的。它接受一个Supplier<T>类型的Lambda提供新的值。
         * generate方法将使用给定的供应源，并反复调用
         */
        // 0到1之间的随机双精度数
        List<Double> list = Stream.generate(Math::random).limit(5).sorted().collect(toList());
        System.out.println(list);

        /**
         * 斐波纳契元组序列
         * 代码创建了一个IntSupplier的实例。此对象有可变的状态：它在两个实例变量中
         * 记录了前一个斐波纳契项和当前的斐波纳契项。 getAsInt在调用时会改变对象的状态，由此在
         * 每次调用时产生新的值。相比之下， 使用iterate的方法则是纯粹不变的：它没有修改现有状态，
         * 但在每次迭代时会创建新的元组。在学完并行数据处理后会了解到，你应该始终采用不变的方法，以便并行
         * 处理流，并保持结果正确。请注意，因为你处理的是一个无限流，所以必须使用limit操作来显
         * 式限制它的大小；否则，终端操作（这里是forEach）将永远计算下去。同样，你不能对无限流
         * 做排序或归约，因为所有元素都需要处理，而这永远也完不成！如果加了limit的话可以排序
         */
        IntSupplier intSupplier = new IntSupplier() {
            private int previous = 0;
            private int current = 1;

            @Override
            public int getAsInt() {
                int oldPrevious = this.previous;
                int nextValue = this.previous + this.current;
                this.previous = this.current;
                this.current = nextValue;
                return oldPrevious;
            }
        };
        // 反复调用getAsInt方法
        IntStream.generate(intSupplier).limit(10).forEach(System.out::println);
    }
}
