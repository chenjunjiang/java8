package com.chenjj.java8.future;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 《Java并发编程实战》（http://mng.bz/979c）一书中， Brian Goetz和合著者们为线程池大小
 * 的优化提供了不少中肯的建议。这非常重要，如果线程池中线程的数量过多，最终它们会竞争
 * 稀缺的处理器和内存资源，浪费大量的时间在上下文切换上。反之，如果线程的数目过少，正
 * 如你的应用所面临的情况，处理器的一些核可能就无法充分利用。 Brian Goetz建议，线程池大
 * 小与处理器的利用率之比可以使用下面的公式进行估算：
 * Nthreads = NCPU * UCPU * (1 + W/C)
 * 其中：
 * NCPU是处理器的核的数目，可以通过Runtime.getRuntime().availableProcessors()得到
 * UCPU是期望的CPU利用率（该值应该介于0和1之间）
 * W/C是等待时间与计算时间的比率
 * 你的应用99%的时间都在等待商店的响应(在代码中故意用delay()方法等待1s)，所以估算出的W/C比率为100。这意味着如果你
 * 期望的CPU利用率是100%，你需要创建一个拥有400个线程的线程池。实际操作中，如果你创建
 * 的线程数比商店的数目更多，反而是一种浪费，因为这样做之后，你线程池中的有些线程根本没
 * 有机会被使用。出于这种考虑，我们建议你将执行器使用的线程数，与你需要查询的商店数目设
 * 定为同一个值，这样每个商店都应该对应一个服务线程。不过，为了避免发生由于商店的数目过
 * 多导致服务器超负荷而崩溃，你还是需要设置一个上限，比如100个线程。
 */
public class BestPriceFinder {
    private final List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"),
            new Shop("ShopEasy"));
    private final int size = Math.min(shops.size(), 100);
    private final ExecutorService executor = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100), r -> {
        /**
         * 创建的是一个由守护线程构成的线程池。Java程序无法终止或者退出一个正
         * 在运行中的线程，所以最后剩下的那个线程可能会由于一直等待无法发生的事件而引发问题。与此相
         * 反，如果将线程标记为守护进程，意味着程序退出时它也会被回收。这二者之间没有性能上的差
         * 异。
         */
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    /**
     * 每次调用getPrice都要花费大约1秒，那findPrices至少需要4秒，因为是顺序执行的
     *
     * @param product
     * @return
     */
    public List<String> findPrices2(String product) {
        return shops.stream().map(shop -> String.format("%s price is %.2f",
                shop.getName(), shop.getPrice(product))).collect(Collectors.toList());
    }

    /**
     * 使用并行流改进
     *
     * @param product
     * @return
     */
    public List<String> findPrices1(String product) {
        return shops.parallelStream().map(shop -> String.format("%s price is %.2f",
                shop.getName(), shop.getPrice(product))).collect(Collectors.toList());
    }

    /**
     * 使用CompletableFuture，将findPrices方法中对不同商店的同步调用替换为异步调用
     * CompletableFuture和parallelStream内部采用的是同样的通用线程池，默认都使用固定数目的线程，
     * 具体线程数取决于Runtime.getRuntime().availableProcessors()的返回值。
     * 我的windows cpu是双核，每核2线程，一共就是4线程，那么线程池默认的线程数就是4
     * 当我们把shops的数量增加到5个，那么可以并行运行（通用线程池中处于可用状态的）的四个线程现在都处于繁忙状态，
     * 都在对前4个商店进行查询，第五个查询只能等到前面某一个操作完成释放出空闲线程才能继续，所以这个就比
     * shops数量为4的执行时间多了将近1s。
     * 然而， CompletableFuture比起parallelStream具有一定的
     * 优势，因为它允许你对执行器（Executor）进行配置，尤其是线程池的大小，
     * 让它以更适合应用需求的方式进行配置，满足程序的要求，而这是并行流API无法提供的。
     * CompletableFuture使用线程池之后5个shop测试花费1016ms，比不用线程池快了将近1s，
     * 处理9个shop花费1022ms，这种状态会一直持续，直到shop的数目达到我们之前通过公式
     * Nthreads = NCPU * UCPU * (1 + W/C)计算的阈值400。
     * <p>
     * 使用parallelStream还是CompletableFutures？
     * 如果你进行的是计算密集型的操作，并且没有I/O，那么推荐使用Stream接口，因为实
     * 现简单，同时效率也可能是最高的（如果所有的线程都是计算密集型的，那就没有必要
     * 创建比处理器核数更多的线程）。
     * 反之，如果你并行的工作单元还涉及等待I/O的操作（包括网络连接等待），那么使用
     * CompletableFuture灵活性更好，你可以像前文讨论的那样，依据等待/计算，或者
     * W/C的比率设定需要使用的线程数。这种情况不使用并行流的另一个原因是，处理流的
     * 流水线中如果发生I/O等待，流的延迟特性会让我们很难判断到底什么时候触发了等待。
     */
    public List<String> findPrices(String product) {
        /**
         * Stream的延迟特性会引起顺序执行，如果使用单一流水线来处理，
         * 那么新的CompletableFuture对象只有在前一个操作完全结束之后(执行join方法获取到结果)，才能创建。
         * 这样在性能上和顺序处理没任何区别
         */
        /*return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() ->
                        String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))))
                .map(CompletableFuture::join).collect(Collectors.toList());*/

        /**
         * 先将CompletableFutures对象聚集到一个列表中，然后再通过第二个流来获取结果
         * 这种方式新的CompletableFutures对象就不用等待前一个对象完成操作之后再创建。
         */
       /* List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))))
                .collect(Collectors.toList());*/

        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(),
                        shop.getPrice(product)), executor))
                .collect(Collectors.toList());
        /**
         * join方法和Future接口中的get有相同的含义，并且也声明在
         * Future接口中，它们唯一的不同是join不会抛出任何检测到的异常。使用它你不再需要使用
         * try/catch语句块让你传递给map方法的Lambda表达式变得过于臃肿。
         */
        return priceFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    /**
     * 顺序且同步的方式获取打折后的价格
     * 从shop对象中获取价格，接着把价格转换为Quote
     * 拿到返回的Quote对象，将其作为参数传递给Discount服务，取得最终的折扣价格。
     *
     * @param product
     * @return
     */
    public List<String> findPrices3(String product) {
        return shops.stream()
                .map(shop -> shop.getPrice1(product))
                .map(Quote::parse)
                .map(Discount::applyDiscount).collect(Collectors.toList());
    }

    /**
     * 输出结果：
     * getPrice1:Thread-0:123.25651664705744
     * parse:Thread-0:123.26
     * getPrice1:Thread-3:184.74384995303313
     * applyDiscount:Thread-0:123.26
     * parse:Thread-3:184.74
     * applyDiscount:Thread-3:184.74
     * getPrice1:Thread-2:214.12914480588853
     * getPrice1:Thread-1:169.4653393606115
     * parse:Thread-1:169.47
     * applyDiscount:Thread-1:169.47
     * getPrice1:Thread-4:176.08324622661846
     * parse:Thread-2:214.13
     * applyDiscount:Thread-2:214.13
     * parse:Thread-4:176.08
     * applyDiscount:Thread-4:176.08
     * [BestPrice price is 110.93, LetsSaveBig price is 135.58, MyFavoriteShop price is 192.72, BuyItAll price is 184.74, ShopEasy price is 167.28]
     * Done in 2031 msecs
     * <p>
     * 从结果可以看出：并发5个线程执行了 【从shop对象中获取价格，接着把价格转换为Quote，
     * 拿到返回的Quote对象，将其作为参数传递给Discount服务，取得最终的折扣价格。】 这个过程，
     * 单看每一个过程是同步的，因为后一个操作要依赖前一个操作的结果，整体来看是并发的。
     * 代码中即使使用thenApplyAsync和thenComposeAsync在性能上也不会有提升，因为后一个操作依赖前一个操作的结果，
     * 要等前一个操作结束后才开始下一个操作
     *
     * @param product
     * @return
     */
    public List<String> findPrices4(String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                // 异步执行
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice1(product), executor))
                // 这里不涉及耗时操作,同步调用不会带来太多延迟,thenApply方法不会阻塞代码的执行(这里就是不会阻塞main线程继续执行)。
                .map(future -> future.thenApply(Quote::parse))
                // 异步执行
                /**
                 * thenCompose方法允许你对两个异步操作进行流水线，第一个操作完成时，将其
                 * 结果作为参数传递给第二个操作。换句话说，你可以创建两个CompletableFutures对象，对
                 * 第 一 个 CompletableFuture 对 象 调 用 thenCompose ， 并 向其 传 递一 个函 数 。当 第一 个
                 * CompletableFuture执行完毕后，它的结果将作为该函数的参数，这个函数的返回值是以第一
                 * 个CompletableFuture的返回做输入计算出的第二个CompletableFuture对象。
                 */
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)))
                .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * 合并两个独立的CompletableFuture对象
     * <p>
     * 你需要将两个完全不相干的CompletableFuture对象的结果整合起来，而且你也不希望等到第一个任务完全结
     * 束才开始第二项任务。这种情况，你应该使用thenCombine方法，它接收名为BiFunction的第二参数，这个参数
     * 定义了当两个CompletableFuture对象完成计算后，结果如何合并。同thenCompose方法一样，
     * thenCombine方法也提供有一个Async的版本。这里，如果使用thenCombineAsync会导致
     * BiFunction中定义的合并操作被提交到线程池中，由另一个任务以异步的方式执行。
     * 有一家商店提供的价格是以欧元（EUR）计价的，
     * 但是你希望以美元的方式提供给你的客户。你可以用异步的方式向商店查询指定商品的价格，同
     * 时从远程的汇率服务那里查到欧元和美元之间的汇率。当二者都结束时，再将这两个结果结合起
     * 来，用返回的商品价格乘以当时的汇率，得到以美元计价的商品价格。用这种方式，你需要使用
     * 第三个CompletableFuture对象， 当前两个CompletableFuture计算出结果，并由
     * BiFunction方法完成合并后，由它来最终结束这一任务
     *
     * @param product
     * @return
     */
    public double findPrice5(String product) {
        Shop shop = shops.get(0);
        CompletableFuture<Double> futurePriceInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                .thenCombine(CompletableFuture.supplyAsync(() -> ExchangeService.getRate(ExchangeService.Money.EUR, ExchangeService.Money.USD)),
                        (price, rate) -> price * rate);
        return futurePriceInUSD.join();
    }

    /**
     * 利用Java 7的方法合并两个Future对象
     *
     * @param product
     * @return
     */
    public double findPrice6(String product) throws Exception {
        Shop shop = shops.get(0);
        Future<Double> futureRate = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return ExchangeService.getRate(ExchangeService.Money.EUR, ExchangeService.Money.USD);
            }
        });
        Future<Double> futurePriceInUSD = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                double priceInEUR = shop.getPrice(product);
                return priceInEUR * futureRate.get();
            }
        });
        return futurePriceInUSD.get();
    }

    public Stream<CompletableFuture<String>> findPricesStream(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice1(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));

    }
}
