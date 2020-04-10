package com.chenjj.java8.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws Exception {
        Shop shop = new Shop("BestShop");
        long start = System.nanoTime();
        Future<Double> future = shop.getPriceAsync("my favorite product");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " msecs");
        // 执行更多任务，比如查询其他商店
        doSomethingElse();
        try {
            /**
             * 调用Future的get方法，执行了这个操作后，客户要么获得Future中封装的值（如果异步任务已经完成），
             * 要么发生阻塞，直到该异步任务完成。如果getPriceAsync方法里面的calculatePrice抛出异常
             * 而没被处理， 这个异常就会被限制那个异步线程内，最终会杀死该线程，
             * 而这就会导致这里调用get方法永久阻塞。
             */
            double price = future.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " msecs");

        BestPriceFinder bestPriceFinder = new BestPriceFinder();
        /*start = System.nanoTime();
        System.out.println(bestPriceFinder.findPrices4("myPhone27S"));
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " msecs");*/
        long start1 = System.nanoTime();
        Stream<CompletableFuture<String>> stream = bestPriceFinder.findPricesStream("myPhone27S");
        CompletableFuture[] futures = stream
                .map(f -> f.thenAccept(s -> System.out.println(s + " (done in " + ((System.nanoTime() - start1) / 1_000_000) + " msecs)")))
                .toArray(size -> new CompletableFuture[size]);
        System.out.println("输出最终价格......");
        // 如果需要等待CompletableFuture对象数组中所有对象执行完毕可以使用allOf方法
        CompletableFuture.allOf(futures).join();
        // 如果只要CompletableFuture对象数组中有任何一个执行完毕就不再等待可以使用anyOf方法
        //CompletableFuture.anyOf(futures).join();
    }

    /**
     * 模拟执行其它任务
     */
    private static void doSomethingElse() {
        try {
            System.out.println("执行其它任务......");
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
