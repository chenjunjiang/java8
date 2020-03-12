package com.chenjj.java8.lambda.methodReference;

import com.chenjj.java8.model.Apple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Administrator on 2017/3/19.
 */
public class AppleSorting {
    public static void main(String[] args) {
        List<Apple> apples = new ArrayList<>();
        apples.addAll(Arrays.asList(new Apple(80, "green"), new Apple(155,
                "green"), new Apple(120, "red")));
        apples.sort(new AppleComparator());
        System.out.println(apples);

        apples.set(1, new Apple(30, "green"));
        apples.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple apple1, Apple apple2) {
                return apple1.getWeight().compareTo(apple2.getWeight());
            }
        });
        System.out.println(apples);

        apples.set(1, new Apple(20, "red"));
        apples.sort((apple1, apple2) -> apple1.getWeight().compareTo
                (apple2.getWeight()));
        System.out.println(apples);

        apples.set(1, new Apple(10, "red"));
        apples.sort(Comparator.comparing(Apple::getWeight));
        apples.sort(Comparator.comparing((apple) -> apple.getWeight()));
        System.out.println(apples);

        // 逆序
        apples.sort(Comparator.comparing(Apple::getWeight).reversed());
        System.out.println(apples);

        Apple apple = new Apple(30, "red");
        Predicate<Apple> redApple = (a -> "red".equals(apple.getColor()));
        System.out.println(redApple.test(apple));
        //非
        Predicate<Apple> notRedApple = redApple.negate();
        System.out.println(notRedApple.test(apple));
        Predicate<Apple> redAndHeavyApple = redApple.and(a -> apple
                .getWeight() > 150);
        System.out.println(redAndHeavyApple.test(apple));
        // 要么是重（150克以上）的红苹果，要么是绿苹果
        Predicate<Apple> redAndHeavyAppleOrGreen =
                redApple.and(a -> a.getWeight() > 150)
                        .or(a -> "green".equals(a.getColor()));
        System.out.println(redAndHeavyAppleOrGreen.test(apple));
        // 请注意，and和or方法是按照在表达式链中的位置，从左向右确定优
        // 先级的。因此，a.or(b).and(c)可以看作(a || b) && c。
    }
}
