package com.chenjj.java8.parametric;

import com.chenjj.java8.model.Apple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppleFilter {
    public static void main(String[] args) {
        List<Apple> inventory = Arrays.asList(new Apple(80, "green"),
                new Apple(155, "green"), new Apple(120, "red"));
        // 使用匿名类来进一步改善代码
        List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
            @Override
            public boolean test(Apple apple) {
                return "red".equals(apple.getColor());
            }
        });
        System.out.println(redApples);
        /**
         * 但匿名类还是不够好。它往往很笨重，因为它占用了很多空间。即使匿名类处理在某种程度上改
         * 善了为一个接口声明好几个实体类的啰嗦问题，但它仍不能令人满意。在只需要传递一段简单的
         * 代码时（例如表示选择标准的boolean表达式），你还是要创建一个对象，明确地实现一个方法
         * 来定义一个新的行为。
         */
        // 使用Lambda表达式
        List<Apple> result = filterApples(inventory, (Apple apple) -> "red".equals(apple.getColor()));
        System.out.println(result);

        // 定义成泛型之后就可以处理其它类型了
        result = filter(inventory, (Apple apple) -> "red".equals(apple.getColor()));
        System.out.println(result);
    }

    /**
     * 筛选绿苹果
     *
     * @param inventory
     * @return
     */
    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> results = new ArrayList<>();
        for (Apple apple : inventory) {
            if ("green".equals(apple.getColor())) {
                results.add(apple);
            }
        }
        return results;
    }

    /**
     * 如果想要筛选更多的颜色, 那么上面这种方法就应付不了了,所以下面把颜色作为参数
     *
     * @param inventory
     * @param color
     * @return
     */
    public static List<Apple> filterApplesByColor(List<Apple> inventory, String color) {
        List<Apple> results = new ArrayList<>();
        for (Apple apple : inventory) {
            if (color.equals(apple.getColor())) {
                results.add(apple);
            }
        }
        return results;
    }

    /**
     * 现在需求又发生了变化,要求能区分苹果的轻重,比如大于150g的是重苹果,我们又把weight作为参数。
     * 虽然这样能符合需求，但是这和过滤颜色的方法相比代码显得有些重复。
     *
     * @param inventory
     * @param weight
     * @return
     */
    public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
        List<Apple> results = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getWeight() > weight) {
                results.add(apple);
            }
        }
        return results;
    }

    /**
     * 为了消除重复代码,可以将颜色和重量结合为一个方法,然后用一个标志来区分对颜色和重量的查询。
     * 这种解决方案太差了,首先，代码看上去很糟糕。true和false是什么意思呢？此外如果需求继续变化，要求用大小、
     * 形状、产地等属性来过滤又怎么办？我们需要一种更好的方式，来把苹果的选择标准告诉filterApples方法。
     * 就需要用行为参数化来实现这种灵活性。
     *
     * @param inventory
     * @param color
     * @param weight
     * @param flag
     * @return
     */
    public static List<Apple> filterApples(List<Apple> inventory, String color, int weight, boolean flag) {
        List<Apple> results = new ArrayList<>();
        for (Apple apple : inventory) {
            if ((flag && apple.getColor().equals(color)) ||
                    (!flag && apple.getWeight() > weight)) {
                results.add(apple);
            }
        }
        return results;
    }

    /**
     * 这就是行为参数化,让方法接受多种行为作为参数，并在内部使用，来完成不同的行为。
     * 这段代码比之前灵活多了，读起来，用起来也更容易。现在你可以创建不同的ApplePredicate对象，并将它们传递给
     * filterApples方法。但这个过程显得有点啰嗦，因为你需要声明很多只需要实例化一次的类。
     *
     * @param inventory
     * @param applePredicate
     * @return
     */
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate applePredicate) {
        List<Apple> results = new ArrayList<>();
        for (Apple apple : inventory) {
            // 谓词对象封装了测试苹果的条件
            if (applePredicate.test(apple)) {
                results.add(apple);
            }
        }
        return results;
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T e : list) {
            if (predicate.test(e)) {
                result.add(e);
            }
        }
        return result;
    }
}
