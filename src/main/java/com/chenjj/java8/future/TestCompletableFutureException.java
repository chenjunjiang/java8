package com.chenjj.java8.future;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 测试CompletableFuture并发抛出异常的行为
 */
public class TestCompletableFutureException {
    private static final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 0, 10);
    private static final ExecutorService executor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100), r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public static void main(String[] args) throws InterruptedException {
        /**
         * 异常直接抛出，不处理
         * 不管用哪种方式，只要在处理过程中有一个元素抛出异常，整个处理过程就结束
         */
        // List<Integer> result = numbers.stream().map(n -> div(n)).collect(Collectors.toList());
        // List<Integer> result = numbers.parallelStream().map(n -> div(n)).collect(Collectors.toList());
        /*List<CompletableFuture<Integer>> futures = numbers.stream().map(n -> CompletableFuture.supplyAsync(() -> div(n), executor)).collect(Collectors.toList());
        List<Integer> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());*/

        /**
         * 捕获异常
         * 不管用哪种方式，只要在处理过程中捕获了异常，整个处理过程就正常就行
         */
        /*List<Integer> result = numbers.stream().map(n -> {
            int r = -1;
            // 这里捕获了异常,所有不会中断整个处理流程
            try {
                r = div(n);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return r;
        }).collect(Collectors.toList());*/
        /*List<Integer> result = numbers.parallelStream().map(n -> {
            int r = -1;
            // 这里捕获了异常,所有不会中断整个处理流程
            try {
                r = div(n);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return r;
        }).collect(Collectors.toList());*/
        numbers.stream().map(n -> CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "*************" + n);
            int r = -1;
            // 这里捕获了异常,所有不会中断整个处理流程
            try {
                r = div(n);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return r;
        }, executor)).collect(Collectors.toList());
        /*List<Integer> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println(result);*/
        System.out.println("xxxxxxxxxxxxxxxx");
        // Thread.sleep(10000);
        /**
         * 关于这里使用线程池实现异步，有以下说明：
         * 1、如果线程池中使用的是守护线程，且主线程没有等待就结束了，此时JVM就退出了，
         * 那么守护线程也会退出，现象就是线程池中的线程只执行了部分任务。有些任务还没来得及执行，
         * 所有为了让线程池中所有任务执行完之后再退出，建议使用下面的关闭线程池逻辑来等待线程池中的所有任务完成。
         * 2、如果线程池中使用的是不是守护线程，且主线程没有等待就结束了，此时JVM还不会退出，
         * 等线程池中的任务全部完成了，JVM也不会退出，线程池中的线程都是空闲的，在等待新任务到来，如果JVM想要在
         * 线程池所有任务完成的时候退出，就需要执行下面的线程池关闭逻辑。
         */
        try {
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (Throwable e) {
            executor.shutdownNow();
        }
    }

    private static int div(int n) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "----------------" + n);
        return 100 / n;
    }
}
