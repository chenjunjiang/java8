package com.chenjj.java8.lambda.methodReference;

import com.chenjj.java8.model.Apple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Administrator on 2017/3/19.
 * 方法引用
 * 有些情况下，我们用Lambda表达式仅仅是调用一些已经存在的方法，除了调用动作外，
 * 没有其他任何多余的动作，在这种情况下，我们倾向于通过方法名来调用它。
 * 方法引用可以理解为Lambda表达式的另外一种表现形式。
 * <p>
 * 方法引用主要有4类：
 * 1、静态方法引用
 * 2、实例方法引用
 * 3、对象方法引用
 * 4、构建方法引用
 * 类型	           语法	                对应的Lambda表达式
 * 静态方法引用	  类名::staticMethod	   (args) -> 类名.staticMethod(args)
 * 实例方法引用	  inst::instMethod	   (args) -> inst.instMethod(args)
 * 对象方法引用	 类名::instMethod	   (inst,args) -> inst.instMethod(args)
 * 构造方法引用	 类名::new	           (args) -> new 类名(args)
 * 这里的实例方法引用和对象方法引用可能有点绕，那么可以根据下边的规则判断：
 * 实例方法引用，顾名思义就是调用已经存在的实例的方法，与静态方法引用不同的是类要先实例化，静态方法引用类无需实例化，直接用类名去调用。
 * 若Lambda参数列表中的第一个参数是实例方法的调用者，而第二个参数是实例方法的参数时，可以使用对象方法引用，当然，第二个参数可有可无。
 */
public class AppleSorting {
    public static void main(String[] args) {
        //Supplier<Apple> supplier = ()->new Apple();
        // 构造方法引用
        Supplier<Apple> supplier = Apple::new;
        Apple supplierApple = supplier.get();
        supplierApple.setColor("red");
        supplierApple.setWeight(120);
        System.out.println(supplierApple);

        //Function<Integer,Apple> function = (weight)->new Apple(weight);
        // 构造方法引用
        Function<Integer, Apple> function = Apple::new;
        Apple functionApple = function.apply(120);

        // BiFunction<Integer,String,Apple> biFunction = (weight,color)->new Apple(weight,color);
        // 构造方法引用
        BiFunction<Integer, String, Apple> biFunction = Apple::new;
        Apple biFunctionApple = biFunction.apply(120, "red");

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
        apples.sort(Comparator.comparing((apple) -> apple.getWeight()));
        apples.sort(Comparator.comparing(Apple::getWeight));
        System.out.println(apples);

        // 按重量逆序
        apples.sort(Comparator.comparing(Apple::getWeight).reversed());
        System.out.println(apples);

        // 按重量逆序，如果重量一致那就按颜色排序
        apples.sort(Comparator.comparing(Apple::getWeight).reversed().thenComparing(Apple::getColor));

        Apple apple = new Apple(30, "red");
        Predicate<Apple> redApple = (a -> "red".equals(apple.getColor()));
        System.out.println(redApple.test(apple));
        //非
        Predicate<Apple> notRedApple = redApple.negate();
        System.out.println(notRedApple.test(apple));

        // 谓词复合
        // 一个苹果既是红色
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

        // 函数复合
        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 2;
        // 先执行加1操作，然后把结果*2
        // Function<Integer, Integer> h = f.andThen(g);
        // 先执行*2操作，然后把结果加1
        Function<Integer, Integer> h = f.compose(g);
        System.out.println(h.apply(1));

        // 返回结果就是输入参数
        Function function1 = Function.identity();
        System.out.println(function1.apply(1));
    }
}
