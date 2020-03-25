package com.chenjj.java8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.chenjj.java8.stream.Dish.menu;
import static java.util.stream.Collectors.toList;

/**
 * Created by Administrator on 2017/4/4.
 * 一个非常常见的数据处理套路就是从某些对象中选择信息。比如在SQL里，你可以从表中选
 * 择一列。 Stream API也通过map和flatMap方法提供了类似的工具。
 * 流支持map方法，它会接受一个函数作为参数。这个函数会被应用到每个元素上，并将其映
 * 射成一个新的元素
 */
public class TestMap {
    public static void main(String[] args) {
        // 提取流中菜肴的名称
        List<String> dishNames = menu.stream().map(Dish::getName).collect(toList());
        // 找出每道菜的名称有多长
        List<Integer> dishNameLengths = menu.stream().map(Dish::getName).map(String::length)
                .collect(toList());

        List<String> words = Arrays.asList("Java 8", "Lambdas", "In", "Action");
        // 给定一个单词列表，你想要返回另一个列表，显示每个单词中有几个字母
        List<Integer> wordLengths = words.stream().map(String::length).collect(toList());

        /**
         * 流的扁平化
         * 给 定 单 词 列 表["Hello","World"]，你想要返回列表["H","e","l", "o","W","r","d"]
         */
        words = Arrays.asList("Hello", "World");
        // 这样做返回的是List<String[]>，因为这里的map产生的是数组流
        List<String[]> result = words.stream().map(word -> word.split(""))
                .distinct().collect
                        (toList());
        result.stream().forEach((sa -> {
            System.out.println(sa);
            for (String s : sa) {
                System.out.println(s);
            }
        }));

        // 可以用flatMap来解决这个问题
        String[] arrayOfWords = {"Goodbye", "World"};
        /**
         * 尝试使用map和Arrays.stream()
         * 首先，你需要一个字符流，而不是数组流。有一个叫作Arrays.stream()的方法可以接受
         * 一个数组并产生一个流
         */
        Stream<String> streamOfWords = Arrays.stream(arrayOfWords);
        List<String> list = streamOfWords.collect(toList());
        System.out.println(list);// [Goodbye, World]
        /**
         * 当前的解决方案仍然搞不定！这是因为，你现在得到的是一个流的列表（更准确地说是
         * Stream<String>）！的确，你先是把每个单词转换成一个字母数组，然后把每个数组变成了一
         * 个独立的流。
         */
        List<Stream<String>> result1 = words.stream().map(word -> word.split(""))
                .map(Arrays::stream).distinct().collect(toList());
        for (Stream<String> stream : result1) {
            // [H, e, l, l, o]
            // [W, o, r, l, d]
            System.out.println(stream.collect(toList()));
        }

        /**
         * 使用flatMap方法的效果是，各个数组并不是分别映射成一个流，而是映射成流的内容。所
         有使用map(Arrays::stream)时生成的单个流都被合并起来，即扁平化为一个流。
         flatMap方法让你把一个流中的每个值都换成另一个流，然后把所有的流连接起来成为一个流。
         */
        /*List<String> uniqueCharacters = words.stream()
                .map(w -> w.split(""))// 将每个单词转换为由其字母构成的数组
                .flatMap(Arrays::stream) // 将各个生成流扁平化为单个流
                .distinct()
                .collect(toList());*/
        List<String> uniqueCharacters = words.stream()
                .flatMap(w -> Arrays.stream(w.split("")))
                .distinct()
                .collect(toList());
        // [H, e, l, o, W, r, d]
        System.out.println(uniqueCharacters);

        /**
         * 给定两个数字列表，如何返回所有的数对呢？例如，给定列表[1, 2, 3]和列表[3, 4]，应
         * 该返回[(1, 3), (1, 4), (2, 3), (2, 4), (3, 3), (3, 4)]。为简单起见，
         * 可以用有两个元素的数组来代表数对。
         */
        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);
        /*List<int[]> pairs = numbers1.stream().flatMap(i -> numbers2.stream().map(j -> new int[]{i, j}))
                .collect(toList());
        pairs.stream().forEach(a -> {
            System.out.println(a);
            for (int i : a) {
                System.out.print(i + " ");
            }
            System.out.println();
        });*/
        List<int[]> pairs = numbers1.stream()
                .map(i -> numbers2.stream().map(j -> new int[]{i, j}))
                .flatMap(Function.identity()).collect(toList());
        pairs.stream().forEach(a -> {
            System.out.println(a);
            for (int i : a) {
                System.out.print(i + " ");
            }
            System.out.println();
        });

        // 只返回总和能被3整除的数对呢？例如(2, 4)和(3, 3)是可以的。
        List<int[]> pairs1 = numbers1.stream()
                .map(i -> numbers2.stream()
                        .filter(j -> (i + j) % 3 == 0)
                        .map(j -> new int[]{i, j}))
                .flatMap(Function.identity()).collect(toList());
        pairs1.stream().forEach(a -> {
            System.out.println(a);
            for (int i : a) {
                System.out.print(i + " ");
            }
            System.out.println();
        });
    }
}
