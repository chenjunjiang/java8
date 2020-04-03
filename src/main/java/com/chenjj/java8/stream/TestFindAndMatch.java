package com.chenjj.java8.stream;

import com.chenjj.java8.model.Dish;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.chenjj.java8.model.Dish.menu;

/**
 * 另一个常见的数据处理套路是看看数据集中的某些元素是否匹配一个给定的属性。 Stream
 * API通过allMatch、 anyMatch、 noneMatch、 findFirst和findAny方法提供了这样的工具。
 */
public class TestFindAndMatch {
    public static void main(String[] args) {
        /**
         * anyMatch方法可以回答“流中是否有一个元素能匹配给定的谓词”。比如，你可以用它来看
         * 看菜单里面是否有素食可选择.
         * anyMatch方法返回一个boolean，因此是一个终端操作。
         */
        if (menu.stream().anyMatch(Dish::isVegetarian)) {
            System.out.println("The menu is (somewhat) vegetarian friendly!!");
        }

        /**
         * allMatch方法的工作原理和anyMatch类似，但它会看看流中的元素是否都能匹配给定的谓
         * 词。比如，你可以用它来看看菜品是否有利健康（即所有菜的热量都低于1000卡路里）
         */
        if (menu.stream().allMatch(d -> d.getCalories() < 1000)) {
            System.out.println("所有菜的热量都低于1000卡路里");
        } else {
            System.out.println("不利于健康");
        }

        /**
         * 和allMatch相对的是noneMatch。它可以确保流中没有任何元素与给定的谓词匹配。比如，
         * 你可以用noneMatch重写前面的例子
         */
        if (menu.stream().noneMatch(d -> d.getCalories() >= 1000)) {
            System.out.println("所有菜的热量都低于1000卡路里");
        } else {
            System.out.println("不利于健康");
        }

        /**
         * findAny方法将返回当前流中的任意元素。它可以与其他流操作结合使用。比如，你可能想
         * 找到一道素食菜肴。你可以结合使用filter和findAny方法来实现这个查询
         */
        Optional<Dish> dish = menu.stream().filter(Dish::isVegetarian).findAny();
        dish.ifPresent(d -> System.out.println(d.getName()));

        /**
         * 有些流有一个出现顺序（encounter order）来指定流中项目出现的逻辑顺序（比如由List或
         * 排序好的数据列生成的流）。对于这种流，你可能想要找到第一个元素。为此有一个findFirst
         * 方法，它的工作方式类似于findany。例如，给定一个数字列表，下面的代码能找出第一个平方
         * 能被3整除的数
         */
        List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> firstSquareDivisibleByThree =
                someNumbers.stream().map(x -> x * x).filter(x -> x % 3 == 0).findFirst();
        System.out.println(firstSquareDivisibleByThree.get());// 9
    }
}
