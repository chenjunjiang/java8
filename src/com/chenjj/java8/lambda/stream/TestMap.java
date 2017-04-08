package com.chenjj.java8.lambda.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2017/4/4.
 */
public class TestMap {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("Hello", "World");
        List<String[]> result = words.stream().map(word -> word.split(""))
                .distinct().collect
                        (Collectors.toList());

        String[] arrayOfWords = {"Goodbye", "World"};
        /**
         * Arrays.stream()方法可以接受一个数组并产生一个流
         */
        Stream<String> streamOfwords = Arrays.stream(arrayOfWords);
        List<String> list = streamOfwords.collect(Collectors.toList());
        System.out.println(list);// [Goodbye, World]

        List<Stream<String>> result1 = words.stream().map(word -> word.split
                (""))
                .map(Arrays::stream).distinct().collect(Collectors.toList());
        for (Stream<String> stream : result1) {
            // [H, e, l, l, o]
            // [W, o, r, l, d]
            System.out.println(stream.collect(Collectors.toList()));
        }

        /**
         * 使用flatMap方法的效果是，各个数组并不是分别映射成一个流，而是映射成流的内容。所
         有使用map(Arrays::stream)时生成的单个流都被合并起来，即扁平化为一个流。
         */
        List<String> uniqueCharacters = words.stream().map(w -> w.split(""))
                .flatMap(Arrays::stream).distinct().collect(Collectors.toList
                        ());
        // [H, e, l, o, W, r, d]
        System.out.println(uniqueCharacters);

    }
}
