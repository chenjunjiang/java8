package com.chenjj.java8.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2017/1/24.
 */
public class Test {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("xx");
        list.add("yy");
        list.add("zz");
        // 永远不需要为一个lambda表达式执行返回类型,它总是会从上下文中被推导出来
        // s+"1"
        /*list.stream().map(s -> {
            return s+"1";
        });*/
        // list.stream().map(s -> s + "1");
        /*list.stream().map(s->{
            return new String(s);
        });*/
        // list.stream().map(s-> new String(s));
        // 和list.stream().map(s-> new String(s));等价
        /*list.stream().map(String::new);
        list.stream().map(String::length);*/
        // 以下3种写法等价
        /*list.stream().toArray(String[]::new);
        list.stream().toArray(s-> new String[s]);*/
        String[] arrStr = list.stream().toArray(s -> {
            // 这里的s的值就是list的size
            System.out.println(s);
            return new String[s];
        });
        System.out.println(arrStr[0]);
        System.out.println(arrStr[1]);
        System.out.println(arrStr[2]);

        // 流只能消费一次
        List<String> title = Arrays.asList("Java8", "In", "Action");
        Stream<String> s = title.stream();
        s.forEach(System.out::println);
        // java.lang.IllegalStateException: stream has already been operated upon or closed
        // s.forEach(System.out::println);
    }
}
