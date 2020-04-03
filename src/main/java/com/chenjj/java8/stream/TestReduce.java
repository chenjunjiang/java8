package com.chenjj.java8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.chenjj.java8.model.Dish.menu;

/**
 * Created by Administrator on 2017/4/4.
 * 使用reduce操作来表达更复杂的查询，比如“计算菜单中的总卡路里”或“菜单中卡路里最高的菜是哪一个”。
 * 此类查询需要将流中所有元素反复结合起来，得到一个值，比如一个Integer。这样的查询可以被归类为归约操作
 * （将流归约成一个值）。用函数式编程语言的术语来说，这称为折叠（fold），因为你可以将这个操
 * 作看成把一张长长的纸（你的流）反复折叠成一个小方块，而这就是折叠操作的结果。
 */
public class TestReduce {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(4, 5, 6, 9);
        //使用for-each循环来对数字列表中的元素求和
        int sum = 0;
        for (int x : numbers) {
            sum += x;
        }
        System.out.println(sum);
        /**
         * numbers中的每个元素都用加法运算符反复迭代来得到结果。通过反复使用加法，你把一个
         * 数字列表归约成了一个数字。这段代码中有两个参数：
         * 1、总和变量的初始值，在这里是0；
         * 2、将列表中所有元素结合在一起的操作，在这里是+。
         */

        // 有初始值0
        int total = numbers.stream().reduce(0, (a, b) -> a + b);
        // numbers.stream().reduce(0, Integer::sum);
        System.out.println(total);
        // 无初始值
        /**
         * 为什么它返回一个Optional<Integer>呢？考虑流中没有任何元素的情况。reduce操作无
         * 法返回其和，因为它没有初始值。这就是为什么结果被包裹在一个Optional对象里，以表明和
         * 可能不存在。
         */
        Optional<Integer> result = numbers.stream().reduce((a, b) -> a + b);
        System.out.println(result.get());

        // 最大值
        numbers.stream().reduce((a, b) -> a > b ? a : b);
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        Optional<Integer> min = numbers.stream().reduce(Integer::min);
        System.out.println(max.get());
        System.out.println(min.get());

        // 怎样用map和reduce方法数一数流中有多少个菜呢？
        int count = menu.stream().map(d -> 1).reduce(0, Integer::sum);
        System.out.println(count);
        // 计算流中的元素个数
        System.out.println(menu.stream().count());

        /**
         * reduce 操作可以实现从Stream中生成一个值，其生成的值不是随意的，而是根据指定的计算模型。比如，之前提到count、min和max方
         * 法，因为常用而被纳入标准库中。事实上，这些方法都是reduce操作。
         *
         * reduce方法有三个override的方法：
         *
         * Optional<T> reduce(BinaryOperator<T> accumulator);
         * T reduce(T identity, BinaryOperator<T> accumulator);
         * <U> U reduce(U identity,BiFunction<U, ? super T, U> accumulator,BinaryOperator<U> combiner);
         *
         */
    }
}
