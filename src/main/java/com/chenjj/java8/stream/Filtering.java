package com.chenjj.java8.stream;

import com.chenjj.java8.model.Dish;

import java.util.*;
import java.util.stream.Collectors;

import static com.chenjj.java8.model.Dish.menu;
import static java.util.stream.Collectors.toList;

/**
 * Java 8中的Stream API可以让你写出这样的代码：
 * 1、声明性——更简洁，更易读
 * 2、可复合——更灵活
 * 3、可并行——性能更好
 * <p>
 * 流到底是什么呢？简短的定义就是“从支持数据处理操作的源生成的元素序列”。让我们一步步剖析这个定义。
 * 1、元素序列
 * 就像集合一样，流也提供了一个接口，可以访问特定元素类型的一组有序
 * 值。因为集合是数据结构，所以它的主要目的是以特定的时间/空间复杂度存储和访问元
 * 素（如ArrayList 与 LinkedList）。但流的目的在于表达计算，比如你下面代码见到的
 * filter、 sorted和map。集合讲的是数据，流讲的是计算。
 * 2、源
 * 流会使用一个提供数据的源，如集合、数组或输入/输出资源。 请注意，从有序集
 * 合生成流时会保留原有的顺序。由列表生成的流，其元素顺序与列表一致。
 * 3、数据处理操作
 * 流的数据处理功能支持类似于数据库的操作，以及函数式编程语言中
 * 的常用操作，如filter、 map、 reduce、 find、 match、 sort等。流操作可以顺序执行，也可并行执行。
 * 4、流水线
 * 很多流操作本身会返回一个流，这样多个操作就可以链接起来，形成一个大
 * 的流水线。这让我们一些优化操作成为可能，如延迟和短路。流水线的操作可以看作对数据源进行数据库式查询。
 * 5、内部迭代
 * 与使用迭代器显式迭代的集合不同，流的迭代操作是在背后进行的。
 * <p>
 * 粗略地说，集合与流之间的差异就在于什么时候进行计算。
 * 集合是一个内存中的数据结构，它包含数据结构中目前所有的值——集合中的每个元素都得先算出来才能添加到集合中。
 * （你可以往集合里加东西或者删东西，但是不管什么时候，集合中的每个元素都是放在内存里的，元素
 * 都得先算出来才能成为集合的一部分。）
 * 相比之下，流则是在概念上固定的数据结构（你不能添加或删除元素），其元素则是按需计
 * 算的。 这对编程有很大的好处。这个思想就是用户仅仅从流中提取需要的值，而这些值——在用
 * 户看不见的地方——只会按需生成。这是一种生产者－消费者的关系。从另一个角度来说，流就
 * 像是一个延迟创建的集合：只有在消费者要求的时候才会计算值。
 */
public class Filtering {

    public static void main(String... args) {
        // 返回低热量的菜肴名词并按卡路里排序
        // Java8之前的写法
        List<Dish> lowCaloricDishes = new ArrayList<>();
        for (Dish dish : menu) {
            // 低热量
            if (dish.getCalories() < 400) {
                lowCaloricDishes.add(dish);
            }
        }
        // 按卡路里排序
        Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
            @Override
            public int compare(Dish dish1, Dish dish2) {
                return Integer.compare(dish1.getCalories(), dish2.getCalories());
            }
        });
        // 取出菜名
        List<String> lowCaloricDishesName = new ArrayList<>();
        for (Dish dish : lowCaloricDishes) {
            lowCaloricDishesName.add(dish.getName());
        }

        // Java8的写法
        lowCaloricDishesName = menu.stream() // 从菜单获取流
                .filter(dish -> dish.getCalories() < 400) // 中间操作
                .sorted(Comparator.comparing(Dish::getCalories)) // 中间操作
                .map(Dish::getName) // 中间操作
                .collect(Collectors.toList()); //将Stream转换为List
        /**
         * 你可以看到两类操作：
         * filter、 sorted和map可以连成一条流水线；
         * collect触发流水线执行并关闭它。
         * 可以连接起来的流操作称为中间操作，关闭流的操作称为终端操作。
         */

        // Filtering with predicate
        List<Dish> vegetarianMenu =
                menu.stream()
                        .filter(Dish::isVegetarian)
                        .collect(toList());

        vegetarianMenu.forEach(System.out::println);

        /**
         * 去重
         * 流还支持一个叫作distinct的方法，它会返回一个元素各异（根据流所生成元素的
         * hashCode和equals方法实现）的流。
         */
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct()
                .forEach(System.out::println);
        System.out.println("numbers:" + numbers);

        /**
         * 截断流
         * 流支持limit(n)方法，该方法会返回一个不超过给定长度的流。所需的长度作为参数传递
         * 给limit。
         */
        List<Dish> dishesLimit3 =
                menu.stream()
                        .filter(d -> d.getCalories() > 300)
                        .limit(3)
                        .collect(toList());

        dishesLimit3.forEach(System.out::println);

        /**
         * 跳过元素
         * 流还支持skip(n)方法，返回一个扔掉了前n个元素的流。如果流中元素不足n个，则返回一个空流。
         */
        List<Dish> dishesSkip2 =
                menu.stream()
                        .filter(d -> d.getCalories() > 300)
                        .skip(2)
                        .collect(toList());

        dishesSkip2.forEach(System.out::println);
    }
}
