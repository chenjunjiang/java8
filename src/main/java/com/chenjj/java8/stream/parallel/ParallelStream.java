package com.chenjj.java8.stream.parallel;

import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * 请注意，在现实中，对顺序流调用parallel方法并不意味着流本身有任何实际的变化。它
 * 在内部实际上就是设了一个boolean标志，表示你想让调用parallel之后进行的所有操作都并
 * 行执行。类似地，你只需要对并行流调用sequential方法就可以把它变成顺序流。请注意，你
 * 可能以为把这两个方法结合起来，就可以更细化地控制在遍历流时哪些操作要并行执行，哪些要
 * 顺序执行。例如，你可以这样做：
 * stream.parallel()
 * .filter(...)
 * .sequential()
 * .map(...)
 * .parallel()
 * .reduce();
 * 但最后一次parallel或sequential调用会影响整个流水线。在本例中，流水线会并行执
 * 行，因为最后调用的是parallel。
 * <p>
 * 配置并行流使用的线程池
 * 看看流的parallel方法，你可能会想，并行流用的线程是从哪儿来的？有多少个？怎么
 * 自定义这个过程呢？
 * 并行流内部使用了默认的ForkJoinPool，它默认的
 * 线 程 数 量 就 是 你 的 处 理 器 数 量 ，
 * 这 个 值 是 由 Runtime.getRuntime().availableProcessors()得到的。
 * 但 是 你 可 以 通 过 系 统 属 性 java.util.concurrent.ForkJoinPool.common.
 * parallelism来改变线程池大小，如下所示：
 * System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism","12");
 * 这是一个全局设置，因此它将影响代码中所有的并行流。反过来说，目前还无法专为某个
 * 并行流指定这个值。一般而言，让ForkJoinPool的大小等于处理器数量是个不错的默认值，
 * 除非你有很好的理由，否则我们强烈建议你不要修改它。
 * <p>
 * 请记住，并行化并不是没有代价的。并行化过程本身需要对流做递归划分，把每
 * 个子流的归纳操作分配到不同的线程，然后把这些操作的结果合并成一个值。但在多个内核之间
 * 移动数据的代价也可能比你想的要大，所以很重要的一点是要保证在内核中并行执行工作的时间比在内核之间传输数据的时间长。
 * 总而言之，很多情况下不可能或不方便并行化。然而，在使用
 * 并行Stream加速代码之前，你必须确保用得对；如果结果错了，算得快就毫无意义了。
 * <p>
 * 一般而言，想给出任何关于什么时候该用并行流的定量建议都是不可能也毫无意义的，因为
 * 任何类似于“仅当至少有一千个（或一百万个或随便什么数字）元素的时候才用并行流）”的建
 * 议对于某台特定机器上的某个特定操作可能是对的，但在略有差异的另一种情况下可能就是大错
 * 特错。尽管如此，我们至少可以提出一些定性意见，帮你决定某个特定情况下是否有必要使用并
 * 行流:
 * 1、如果有疑问，测量。把顺序流转成并行流轻而易举，但却不一定是好事。前面的例子
 * 已经指出，并行流并不总是比顺序流快。此外，并行流有时候会和你的直觉不一致，所
 * 以在考虑选择顺序流还是并行流时，第一个也是最重要的建议就是用适当的基准来检查
 * 其性能。
 * 2、留意装箱。自动装箱和拆箱操作会大大降低性能。 Java 8中有原始类型流（IntStream、
 * LongStream、 DoubleStream）来避免这种操作，但凡有可能都应该用这些流。
 * 3、有些操作本身在并行流上的性能就比顺序流差。特别是limit和findFirst等依赖于元
 * 素顺序的操作，它们在并行流上执行的代价非常大。例如， findAny会比findFirst性
 * 能好，因为它不一定要按顺序来执行。你总是可以调用unordered方法来把有序流变成
 * 无序流。那么，如果你需要流中的n个元素而不是专门要前n个的话，对无序并行流调用
 * limit可能会比单个有序流（比如数据源是一个List）更高效。
 * 4、还要考虑流的操作流水线的总计算成本。设N是要处理的元素的总数， Q是一个元素通过
 * 流水线的大致处理成本，则N*Q就是这个对成本的一个粗略的定性估计。 Q值较高就意味
 * 着使用并行流时性能好的可能性比较大。
 * 5、对于较小的数据量，选择并行流几乎从来都不是一个好的决定。并行处理少数几个元素
 * 的好处还抵不上并行化造成的额外开销。
 * 6、要考虑流背后的数据结构是否易于分解。例如， ArrayList的拆分效率比LinkedList
 * 高得多，因为前者用不着遍历就可以平均拆分，而后者则必须遍历。另外，用range工厂方法创建的原始类型流也可以快速分解。
 * 7、流自身的特点，以及流水线中的中间操作修改流的方式，都可能会改变分解过程的性能。
 * 例如，一个SIZED流可以分成大小相等的两部分，这样每个部分都可以比较高效地并行处
 * 理，但筛选操作可能丢弃的元素个数却无法预测，导致流本身的大小未知。
 * 8、还要考虑终端操作中合并步骤的代价是大是小（例如Collector中的combiner方法）。
 * 如果这一步代价很大，那么组合每个子流产生的部分结果所付出的代价就可能会超出通
 * 过并行流得到的性能提升。
 * 下表按照可分解性总结了一些流数据源适不适于并行：
 * 源                可分解性
 * ArrayList         极佳
 * LinkedList        差
 * IntStream.range   极佳
 * Stream.iterate    差
 * HashSet           好
 * TreeSet           好
 */
public class ParallelStream {
    public static void main(String[] args) {
        /**
         * 求和方法的并行版本比顺序版本要慢很多，有两个问题：
         * 1、iterate生成的是装箱的对象，必须拆箱成数字才能求和；
         * 2、我们很难把iterate分成多个独立块来并行执行。iterate很难分割成能够独立执行的小块，
         * 因为每次应用这个函数都要依赖前一次应用的结果。这种情况下把流标记成并行，你其实是
         * 给顺序处理增加了开销，它还要把每次求和操作分到一个不同的线程上。
         * 这就说明了并行编程可能很复杂，有时候甚至有点违反直觉。如果用得不对（比如采用了一
         * 个不易并行化的操作，如iterate），它甚至可能让程序的整体性能更差。
         * 可以使用LongStream.rangeClosed来代替iterate，它有两个优点：
         * 1、LongStream.rangeClosed直接产生原始类型的long数字，没有装箱拆箱的开销。
         * 2、LongStream.rangeClosed会生成数字范围，很容易拆分为独立的小块。例如，范围1~20
         * 可分为1~5、 6~10、 11~15和16~20。
         */
        //System.out.println("Iterative sum done in:" + measureSumPerf(ParallelStream::iterativeSum, 10_000_000) + " msecs"); // 6
        //System.out.println("Sequential sum done in:" + measureSumPerf(ParallelStream::sequentialSum, 10_000_000) + " msecs"); // 193
        //System.out.println("Parallel sum done in:" + measureSumPerf(ParallelStream::parallelSum, 10_000_000)+" msecs"); // 393
        //System.out.println("Parallel sum with LongStream.rangeClosed done in:" + measureSumPerf(ParallelStream::parallelSum, 10_000_000) + " msecs"); // 2
        // System.out.println("SideEffect sum done in:" + measureSumPerf(ParallelStream::sideEffectSum, 10_000_000) + " msecs"); // 5
        // System.out.println("Parallel SideEffect sum done in:" + measureSumPerf(ParallelStream::sideEffectSum, 10_000_000) + " msecs"); // 5
        //这个性能看起来比用并行流的版本要差，但这只是因为必须先要把整个数字流都放进一个long[]，之后才能在ForkJoinSumCalculator任务中使用它。
        System.out.println("ForkJoin sum done in:" + measureSumPerf(ForkJoinSumCalculator::forkJoinSum, 10_000_000) + " msecs"); // 83
    }

    /**
     * 写一个方法，接受数字n作为参数，并返回从1到给定参数的所有数字的和
     */
    // Java8之前传统实现
    public static long iterativeSum(long n) {
        long result = 0;
        for (long i = 1L; i <= n; i++) {
            result += i;
        }
        return result;
    }

    // 非并行流实现
    public static long sequentialSum(long n) {
        return Stream.iterate(1L, i -> i + 1).limit(n).reduce(0L, Long::sum);
    }

    // 并行流实现
    public static long parallelSum(long n) {
        //return Stream.iterate(0L, i -> i + 1).limit(n).parallel().reduce(0L, Long::sum);
        return LongStream.rangeClosed(1L, n).parallel().reduce(0L, Long::sum);
    }

    /**
     * 测量对前n个自然数求和的函数的性能
     * 这个方法接受一个函数和一个long作为参数。它会对传给方法的long应用函数10次，记录
     * 每次执行的时间（以毫秒为单位），并返回最短的一次执行时间。
     *
     * @param adder
     * @param n
     * @return
     */
    public static long measureSumPerf(Function<Long, Long> adder, long n) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            long sum = adder.apply(n);
            long duration = (System.nanoTime() - start) / 1_000_000;
            System.out.println("Result: " + sum);
            if (duration < fastest) {
                fastest = duration;
            }
        }
        return fastest;
    }

    /**
     * 正确使用并行流
     * 错用并行流而产生错误的首要原因，就是使用的算法改变了某些共享状态。下面是另一种实
     * 现对前n个自然数求和的方法，但这会改变一个共享累加器。
     * 如果用parallel来并行执行，会得到这样的结果：
     * Result: 5959989000692
     * Result: 7425264100768
     * Result: 6827235020033
     * Result: 7192970417739
     * Result: 6714157975331
     * Result: 7497810541907
     * Result: 6435348440385
     * Result: 6999349840672
     * Result: 7435914379978
     * 这回方法的性能无关紧要了，唯一要紧的是每次执行都会返回不同的结果，都离正确值
     * 50000005000000差很远。这是由于多个线程在同时访问累加器，执行total += value，而这
     * 一句虽然看似简单，却不是一个原子操作。每次访问total都会出现数据竞争。
     * 问题的根源在于， forEach中调用的方法有副作用，
     * 它会改变多个线程共享的对象的可变状态。要是你想用并行Stream又不想引发类似的意外，就
     * 必须避免这种情况。
     * 记住要避免共享可变状态，确保并行Stream得到正确的结果。
     */
    public static long sideEffectSum(long n) {
        Accumulator accumulator = new Accumulator();
        // LongStream.rangeClosed(1, n).forEach(accumulator::add);
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }

}
