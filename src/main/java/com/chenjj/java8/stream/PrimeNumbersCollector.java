package com.chenjj.java8.stream;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * Collector接口的定义是:public interface Collector<T, A, R>
 * 其中T、 A和R分别是流中元素的类型、用于累积部分结果的对象类型，以及collect操作最
 * 终 结 果 的 类 型 。 这 里 应 该 收 集Integer 流 ， 而 累 加 器 和 结 果 类 型 则 都 是 Map<Boolean,
 * List<Integer>>，键是true和false，值则分别是质数和非质数的List
 */
public class PrimeNumbersCollector implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {
    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        // https://blog.csdn.net/luman1991/article/details/53034602
        // 第一层括弧实际是定义了一个匿名内部类，第二层括弧实际上是一个实例初始化块，这个块在内部匿名类构造时被执行
        return () -> new HashMap<Boolean, List<Integer>>() {
            {
                put(true, new ArrayList<>());
                put(false, new ArrayList<>());
            }
        };
        // 一般的写法
        /*return () -> {
            HashMap<Boolean, List<Integer>> map = new HashMap<>();
            map.put(true, new ArrayList<>());
            map.put(false, new ArrayList<>());
            return map;
        };*/
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
            acc.get(isPrime(acc.get(true), candidate)).add(candidate);
        };
    }

    /**
     * 让收集器并行工作（如果可能）
     * 请注意，实际上这个收集器是不能并行使用的，因为该算法本身是顺序的。这意味着永远都
     * 不会调用combiner方法，你可以把它的实现留空（更好的做法是抛出一个UnsupportedOperationException异常）。
     * 为了让这个例子完整，我们还是决定实现它。
     *
     * @return
     */
    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return ((booleanListMap1, booleanListMap2) -> {
            booleanListMap1.get(true).addAll(booleanListMap2.get(true));
            booleanListMap1.get(false).addAll(booleanListMap2.get(false));
            return booleanListMap1;
        });
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    /**
     * 它既不是CONCURRENT也不是UNORDERED，但却是IDENTITY_FINISH的
     *
     * @return
     */
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }

    public static boolean isPrime(List<Integer> primes, int candidate) {
        int candidateRoot = (int) Math.sqrt(candidate);
        return takeWhile(primes, i -> i <= candidateRoot)
                .stream()
                .noneMatch(p -> candidate % p == 0);
    }

    public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
        int i = 0;
        for (A item : list) {
            if (!p.test(item)) {
                return list.subList(0, i);
            }
            i++;
        }
        return list;
    }
}
