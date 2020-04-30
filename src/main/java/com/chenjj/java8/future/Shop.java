package com.chenjj.java8.future;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.chenjj.java8.future.Util.randomDelay;

/**
 * 假设获取商品价格是一个远程服务，我们用delay()模拟远程调用耗时
 */
public class Shop {
    private final String name;
    private final Random random;

    public Shop(String name) {
        this.name = name;
        random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
    }

    public String getName() {
        return name;
    }

    public double getPrice(String product) {
        return calculatePrice(product);
    }

    public String getPrice1(String product) {
        double price = calculatePrice(product);
        System.out.println("getPrice1:" + Thread.currentThread().getName() + ":" + price);
        Discount.Code code = Discount.Code.values()[random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }

    /**
     * 没有处理calculatePrice里面抛出的异常，这会导致future.get()永久阻塞
     *
     * @param product
     * @return
     */
    /*public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> future = new CompletableFuture<>();
        new Thread(() -> {
            double price = calculatePrice(product);
            // 需长时间计算的任务结束并得出结果时，设置Future的返回值
            future.complete(price);
        }).start();
        return future;
    }*/

    /**
     *处理了calculatePrice抛出的异常,future.get()不会阻塞
     * @param product
     * @return
     */
    public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> future = new CompletableFuture<>();
        new Thread(() -> {
            try {
                double price = calculatePrice(product);
                // 需长时间计算的任务结束并得出结果时，设置Future的返回值
                future.complete(price);
            } catch (Exception e) {

                /**
                 * 为了让调用方清楚线程内到底发生了什么问题，
                 * 使用completeExceptionally方法将CompletableFuture内发生问题的异常抛出
                 */
                future.completeExceptionally(e);
            }
        }).start();
        return future;
    }

    /*public Future<Double> getPriceAsync(String product) {
        // 这种方式连异常都已经处理好了和上面我们自己处理是等价的
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }*/

    private double calculatePrice(String product) {
        // 故意模拟耗时操作
        // delay();
        randomDelay();
        // 故意抛出异常
        if (true) throw new RuntimeException("product not available");
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }
}
