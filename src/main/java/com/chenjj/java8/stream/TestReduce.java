package com.chenjj.java8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Administrator on 2017/4/4.
 */
public class TestReduce {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(4, 5, 6, 9);
        // 有初始值0
        int total = numbers.stream().reduce(0, (a, b) -> a + b);
        // numbers.stream().reduce(0, Integer::sum);
        System.out.println(total);
        // 无初始值
        Optional<Integer> result = numbers.stream().reduce((a, b) -> a + b);
        System.out.println(result.get());

        // 最大值
        numbers.stream().reduce((a, b) -> a > b ? a : b);
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        Optional<Integer> min = numbers.stream().reduce(Integer::min);
        System.out.println(max.get());
        System.out.println(min.get());
    }
}
