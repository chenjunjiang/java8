package com.chenjj.java8.lambda.functionalInterface;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TestConsumer {
    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName());
        // 没有类型推断，自己指定类型
        forEach(Arrays.asList(1, 2, 3, 4, 5), (Integer i) -> {
            System.out.println(i);
            System.out.println(Thread.currentThread().getName());
        });
        // 类型推断
        //forEach(Arrays.asList(1, 2, 3, 4, 5), (i) -> System.out.println(i));
        // 当Lambda仅有一个类型需要推断的参数时，参数名称两边的括号也可以省略
        //forEach(Arrays.asList(1, 2, 3, 4, 5), i -> System.out.println(i));

        int portNumber = 1337;
        Runnable r = () -> System.out.println(portNumber);
        // Variable used in lambda expression should be final or effectively final
        // portNumber = 31337;
        /**
         * 为什么 Lambda 表达式(匿名类) 不能访问非 final 的局部变量呢？
         * 因为实例变量存在堆中，而局部变量是在栈上分配，
         * 假如Lambda 表达(匿名类) 是在另一个线程中执行的。如果在线程中要访问这个局部变量的时候，
         * 可能分配该局部变量的线程已经被销毁了，而 final 类型的局部变量在 Lambda 表达式(匿名类) 中其实是局部变量的一个拷贝。
         * 而不是访问的原始变量，所以才不会有问题。
         * 闭包：
         * 你可能已经听说过闭包（closure，不要和Clojure编程语言混淆）这个词，你可能会想
         * Lambda是否满足闭包的定义。用科学的说法来说，闭包就是一个函数的实例，且它可以无限
         * 制地访问那个函数的非本地变量。例如，闭包可以作为参数传递给另一个函数。它也可以访
         * 问和修改其作用域之外的变量。现在， Java 8的Lambda和匿名类可以做类似于闭包的事情：
         * 它们可以作为参数传递给方法，并且可以访问其作用域之外的变量。但有一个限制：它们不
         * 能修改定义Lambda的方法的局部变量的内容。这些变量必须是隐式最终的。可以认为Lambda
         * 是对值封闭，而不是对变量封闭。如前所述，这种限制存在的原因在于局部变量保存在栈上，
         * 并且隐式表示它们仅限于其所在线程。如果允许捕获可改变的局部变量，就会引发造成线程
         * 不安全的新的可能性，而这是我们不想看到的（实例变量可以，因为它们保存在堆中，而堆
         * 是在线程之间共享的）。
         */
    }

    public static <T> void forEach(List<T> list, Consumer<T> consumer) {
        for (T t : list) {
            consumer.accept(t);
        }
    }
}
