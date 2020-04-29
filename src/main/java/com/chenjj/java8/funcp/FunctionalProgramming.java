package com.chenjj.java8.funcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;

/**
 * 被称为“函数式”的函数或方法都只能修改本地变量。除此之外，它引用的
 * 对象都应该是不可修改的对象。通过这种规定，我们期望所有的字段都为final类型，所有的引
 * 用类型字段都指向不可变对象。实际也允许对方法中全新创建的对
 * 象中的字段进行更新，不过这些字段对于其他对象都是不可见的，也不会因为保存对后续调用结
 * 果造成影响。要成为真正的函数式程序还有一个附加条件，不过它在最初
 * 时不太为大家所重视。要被称为函数式， 函数或者方法不应该抛出任何异常。
 * 从实际操作的角度出发，你可以
 * 选择在本地局部地使用异常，避免通过接口将结果暴露给其他方法，这种方式既取得了函数式的
 * 优点，又不会过度膨胀代码。
 * 最后，作为函数式的程序，你的函数或方法调用的库函数如果有副作用，你必须设法隐藏它
 * 们的非函数式行为，否则就不能调用这些方法（换句话说，你需要确保它们对数据结构的任何修
 * 改对于调用者都是不可见的，你可以通过首次复制，或者捕获任何可能抛出的异常实现这一目
 * 的）。
 * 如果一个函数只要传递同样的参数值，总是返回同样的结果，那这个
 * 函数就是引用透明的。 String.replace方法就是引用透明的， 因为像"raoul".replace('r',
 * 'R')这样的调用总是返回同样的结果（replace方法返回一个新的字符串，用小写的r替换掉
 * 所有大写的R），而不是更新它的this对象，所以它可以被看成函数式的。
 * 换句话说，函数无论在何处、何时调用，如果使用同样的输入总能持续地得到相同的结果，
 * 就具备了函数式的特征。
 * Java语言中，关于引用透明性还有一个比较复杂的问题。假设你对一个返回列表的方法调用
 * 了两次。这两次调用会返回内存中的两个不同列表，不过它们包含了相同的元素。如果这些列表
 * 被当作可变的对象值（因此是不相同的），那么该方法就不是引用透明的。如果你计划将这些列
 * 表作为单纯的值（不可修改），那么把这些值看成相同的是合理的，这种情况下该方法是引用透
 * 明的。通常情况下， 在函数式编程中，你应该选择使用引用透明的函数。
 * <p>
 * 面向对象的编程和函数式编程的对比
 * 一种支持极端的面向对象：任何事物都是对象，程序要么通过更新字段完成操作，要么调用对与它相关的对象进行更新的方法。
 * 另一种观点支持引用透明的函数式编程，认为方法不应该有（对外部可见的）对象修改。
 */
public class FunctionalProgramming {
    public static void main(String[] args) {
        List<List<Integer>> subs = subsets(Arrays.asList(1, 4, 9));
        System.out.println(subs); // [[], [9], [4], [4, 9], [1], [1, 9], [1, 4], [1, 4, 9]]
        subs.forEach(System.out::println);
    }

    /**
     * 给定一个列表List<value>，比如{1, 4,9}，构造一个List<List<Integer>>，它的成员都是类表{1, 4, 9}的子集
     * ——我们暂时不考虑元素的顺序。 {1, 4, 9}的子集是{1, 4, 9}、 {1, 4}、 {1, 9}、 {4, 9}、 {1}、 {4}、 {9}以及{}。
     */
    public static List<List<Integer>> subsets(List<Integer> list) {
        if (list.isEmpty()) {
            List<List<Integer>> ans = new ArrayList<>();
            ans.add(Collections.emptyList());
            return ans;
        }
        Integer first = list.get(0);
        List<Integer> rest = list.subList(1, list.size());
        List<List<Integer>> subans = subsets(rest);
        List<List<Integer>> subans2 = insertAll(first, subans);
        return concat(subans, subans2);
    }

    /*private static List<List<Integer>> concat(List<List<Integer>> subans, List<List<Integer>> subans2) {
        subans.addAll(subans2);
        return subans;
    }*/

    /**
     * 这个版本的concat是纯粹的函数式。虽然它在内部会对对象进行修改（向列
     * 表result添加元素），但是它返回的结果基于参数却没有修改任何一个传入的参数。
     *
     * @param subans
     * @param subans2
     * @return
     */
    private static List<List<Integer>> concat(List<List<Integer>> subans, List<List<Integer>> subans2) {
        List<List<Integer>> result = new ArrayList<>(subans);
        result.addAll(subans2);
        return result;
    }

    private static List<List<Integer>> insertAll(Integer first, List<List<Integer>> lists) {
        List<List<Integer>> result = new ArrayList<>();
        for (List<Integer> list : lists) {
            List<Integer> copyList = new ArrayList<>();
            copyList.add(first);
            copyList.addAll(list);
            result.add(copyList);
        }
        return result;
    }

    // 迭代式的阶乘计算
    public static int factorialIterative(int n) {
        int r = 1;
        for (int i = 1; i <= n; i++) {
            r *= i;
        }
        return r;
    }

    /**
     * 递归式的阶乘计算
     * 每次执行factorialRecursive
     * 方法调用都会在调用栈上创建一个新的栈帧，用于保存每个方法调用的状态（即它需要进行的乘
     * 法运算），这个操作会一直指导程序运行直到结束。这意味着你的递归迭代方法会依据它接收的
     * 输入成比例地消耗内存。
     *
     * @param n
     * @return
     */
    public static long factorialRecursive(long n) {
        return n == 1 ? 1 : n * factorialRecursive(n - 1);
    }

    // 基于Stream的阶乘
    public static long factorialStreams(long n) {
        return LongStream.rangeClosed(1, n).reduce(1, (a, b) -> a * b);
    }

    /**
     * 尾调用指的是一个方法或者函数的调用在另一个方法或者函数的最后一条指令中进行。
     * 比如：factorialHelper方法的调用就是在factorialTailRecursive方法的最后
     * https://www.cnblogs.com/jiangyaxiong1990/articles/9241495.html
     * 这种形式的递归是非常有意义的，现在我们不需要在不同的栈帧上保存每次递归计算的中间
     * 值，编译器能够自行决定复用某个栈帧进行计算，只使用了一个栈帧。
     * 坏消息是，目前Java还不支持这种优化。很多的现代JVM语言，比如Scala和Groovy都已经支持对这种形式的递归的优化，
     * 最终实现的效果和迭代不相上下。
     *
     * @param n
     * @return
     */
    public static long factorialTailRecursive(long n) {
        return factorialHelper(1, n);
    }

    public static long factorialHelper(long acc, long n) {
        return n == 1 ? acc : factorialHelper(acc * n, n - 1);
    }
}
