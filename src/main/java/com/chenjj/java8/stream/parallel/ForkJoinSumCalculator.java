package com.chenjj.java8.stream.parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * 使用Fork/Join框架对long[]数组求和
 *
 * 使用分支/合并框架的最佳做法
 * 1、对一个任务调用join方法会阻塞调用方，直到该任务做出结果。因此，有必要在两个子
 * 任务的计算都开始之后再调用它。否则，你得到的版本会比原始的顺序算法更慢更复杂，
 * 因为每个子任务都必须等待另一个子任务完成才能启动。
 * 2、不应该在RecursiveTask内部使用ForkJoinPool的invoke方法。相反，你应该始终直
 * 接调用compute或fork方法，只有顺序代码才应该用invoke来启动并行计算。
 * 3、对子任务调用fork方法可以把它排进ForkJoinPool。同时对左边和右边的子任务调用
 * 它似乎很自然，但这样做的效率要比直接对其中一个调用compute低。这样做你可以为
 * 其中一个子任务重用同一线程，从而避免在线程池中多分配一个任务造成的开销。
 * 4、调试使用分支/合并框架的并行计算可能有点棘手。特别是你平常都在你喜欢的IDE里面
 * 看栈跟踪（stack trace）来找问题，但放在分支合并计算上就不行了，因为调用compute
 * 的线程并不是概念上的调用方，后者是调用fork的那个。
 * 5、和并行流一样，你不应理所当然地认为在多核处理器上使用分支/合并框架就比顺序计
 * 算快。我们已经说过，一个任务可以分解成多个独立的子任务，才能让性能在并行化时
 * 有所提升。所有这些子任务的运行时间都应该比分出新任务所花的时间长；一个惯用方
 * 法是把输入/输出放在一个子任务里，计算放在另一个里，这样计算就可以和输入/输出
 * 同时进行。此外，在比较同一算法的顺序和并行版本的性能时还有别的因素要考虑。就
 * 像任何其他Java代码一样，分支/合并框架需要“预热”或者说要执行几遍才会被JIT编
 * 译器优化。这就是为什么在测量性能之前跑几遍程序很重要，我们的测试框架就是这么
 * 做的。同时还要知道，编译器内置的优化可能会为顺序版本带来一些优势（例如执行死
 * 码分析——删去从未被使用的计算）。
 */
public class ForkJoinSumCalculator extends RecursiveTask<Long> {
    // 要求和的数组
    private final long[] numbers;
    // 子任务处理的数组的起始和终止位置
    private final int start;
    private final int end;
    // 不再将任务分解为子任务的数组大小
    public static final long THRESHOLD = 10_000;

    // 公共构造方法用于创建主任务
    public ForkJoinSumCalculator(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    // 私有构造方法用于以递归方式为主任务创建子任务
    private ForkJoinSumCalculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        // 该任务负责求和部分的大小
        int length = end - start;
        // 如果小于等于阈值，顺序计算结果
        if (length <= THRESHOLD) {
            return computeSequentially();
        }
        // 创建一个子任务来为数组的前一半求和
        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length / 2);
        // 利用另一个ForkJoinPool线程异步执行新创建的子任务
        leftTask.fork();
        // 创建一个子任务来为数组的后一半求和
        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length / 2, end);
        // 同步执行第二个子任务，有可能允许进一步递归划分
        long rightResult = rightTask.compute();
        // 读取第一个子任务的结果，如果尚未完成就等待
        long leftResult = leftTask.join();
        return leftResult + rightResult;
    }

    /**
     * 请注意在实际应用时，使用多个ForkJoinPool是没有什么意义的。正是出于这个原因，一
     * 般来说把它实例化一次，然后把实例保存在静态字段中，使之成为单例，这样就可以在软件中任
     * 何部分方便地重用了。这里创建时用了其默认的无参数构造函数，这意味着想让线程池使用JVM
     * 能够使用的所有处理器。更确切地说，该构造函数将使用Runtime.availableProcessors的
     * 返回值来决定线程池使用的线程数。请注意availableProcessors方法虽然看起来是处理器，
     * 但它实际上返回的是可用内核的数量，包括超线程生成的虚拟内核。
     *
     * @param n
     * @return
     */
    public static long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        return new ForkJoinPool().invoke(task);
    }

    /**
     * 在子任务不再可分时计算结果的简单方法
     *
     * @return
     */
    private long computeSequentially() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }
}
