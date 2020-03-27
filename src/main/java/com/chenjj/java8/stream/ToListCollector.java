package com.chenjj.java8.stream;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

/**
 * Collector<T, A, R>接口三个泛型参数说明:
 * 1、T是流中要收集的项目的泛型
 * 2、A是累加器的类型，累加器是在收集过程中用于累积部分结果的对象。
 * 3、R是收集操作得到的对象（通常但并不一定是集合）的类型。
 *
 * @param <T>
 */
public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {
    /**
     * 在调用它的时候会创建一个空的累加器实例，供数据收集过程使用。
     * 在这里我们返回的是一个空的List
     *
     * @return
     */
    @Override
    public Supplier<List<T>> supplier() {
        // return ()->new ArrayList<>();
        return ArrayList::new;
    }

    /**
     * accumulator方法会返回执行归约操作的函数。当遍历到流中第n个元素时，这个函数执行
     * 时会有两个参数：保存归约结果的累加器（已收集了流中的前 n-1 个项目）， 还有第n个元素本身。
     * 该函数将返回void，因为累加器是原位更新，即函数的执行改变了它的内部状态以体现遍历的
     * 元素的效果。对于ToListCollector，这个函数仅仅会把当前项目添加至已经遍历过的项目的列表
     *
     * @return
     */
    @Override
    public BiConsumer<List<T>, T> accumulator() {
        // return (list, t) -> list.add(t);
        return List::add;
    }

    /**
     * 在遍历完流后， finisher方法是在累积过程返回后必须要调用的一个函数，以便将累加
     * 器对象转换为整个集合操作的最终结果。通常，就像ToListCollector的情况一样，累加器对
     * 象恰好符合预期的最终结果，因此无需进行转换。所以finisher方法只需返回identity函数
     *
     * @return
     */
    @Override
    public Function<List<T>, List<T>> finisher() {
        return Function.identity();
    }

    /**
     * combiner方法会返回一个供归约操作使用的函数，它定义了对
     * 流的各个子部分进行并行处理时，各个子部分归约所得的累加器要如何合并。对于toList而言，
     * 这个方法的实现非常简单，只要把从流的第二个部分收集到的项目列表加到遍历第一部分时得到
     * 的列表后面就行了
     *
     * @return
     */
    @Override
    public BinaryOperator<List<T>> combiner() {
        return ((list1, list2) -> {
            list1.addAll(list2);
            return list1;
        });
    }

    /**
     * characteristics会返回一个不可变的Characteristics集合，它定义
     * 了收集器的行为——尤其是关于流是否可以并行归约，以及可以使用哪些优化的提示。
     * Characteristics是一个包含三个项目的枚举。
     * 1、UNORDERED——归约结果不受流中项目的遍历和累积顺序的影响。
     * 2、CONCURRENT——accumulator函数可以被多个线程同时调用，且该收集器可以并行归
     * 约流。如果收集器没有标为UNORDERED，那它仅在用于无序数据源时才可以并行归约。
     * 3、IDENTITY_FINISH——这表明finisher方法返回的函数是一个恒等函数，可以跳过。这种
     * 情况下，累加器对象将会直接用作归约过程的最终结果。这也意味着，将累加器A不加检
     * 查地转换为结果R是安全的。
     * ToListCollector是IDENTITY_FINISH的，因为用来累积流中元素的List已经是我们要的最终结果，
     * 用不着进一步转换了，但它并不是UNORDERED的，因为用在有序流上的时候，我们还是希望顺序能够保留在得到的List中。
     * 最后，它是CONCURRENT的，但我们刚才说过了，仅仅在背后的数据源无序时才会并行处理。
     *
     * @return
     */
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT));
    }
}
