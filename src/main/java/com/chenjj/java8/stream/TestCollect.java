package com.chenjj.java8.stream;

import java.util.*;
import java.util.stream.Stream;

import static com.chenjj.java8.stream.Dish.menu;
import static java.util.stream.Collectors.*;

/**
 * 收集器
 * 收集器非常有用，因为用它可以简洁而灵活地定义collect用来生成结果集合的标准。更具体地说，对流调用
 * collect方法将对流中的元素触发一个归约操作（由Collector来参数化）。
 * 一般来说，Collector会对元素应用一个转换函数（很多时候是不体现任何效果的恒等转换，
 * 例如toList），并将结果累积在一个数据结构中，从而产生这一过程的最终输出。例如，下面对交易按照货币分组例子中
 * ，转换函数提取了每笔交易的货币，随后使用货币作为键，将交易本身累积在生成的Map中。
 * Collector接口中方法的实现决定了如何对流执行归约操作。
 */
public class TestCollect {
    public static List<Transaction> transactions = Arrays.asList(new Transaction(Currency.EUR, 1500.0),
            new Transaction(Currency.USD, 2300.0),
            new Transaction(Currency.GBP, 9900.0),
            new Transaction(Currency.EUR, 1100.0),
            new Transaction(Currency.JPY, 7800.0),
            new Transaction(Currency.CHF, 6700.0),
            new Transaction(Currency.EUR, 5600.0),
            new Transaction(Currency.USD, 4500.0),
            new Transaction(Currency.CHF, 3400.0),
            new Transaction(Currency.GBP, 3200.0),
            new Transaction(Currency.USD, 4600.0),
            new Transaction(Currency.JPY, 5700.0),
            new Transaction(Currency.EUR, 6800.0));

    public static void main(String[] args) {
        // java8前对交易按照货币分组
        Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>();
        for (Transaction transaction : transactions) {
            Currency currency = transaction.getCurrency();
            List<Transaction> transactionsForCurrency = transactionsByCurrencies.get(currency);
            if (transactionsForCurrency == null) {
                transactionsForCurrency = new ArrayList<>();
                transactionsByCurrencies.put(currency, transactionsForCurrency);
            }
            transactionsForCurrency.add(transaction);
        }

        // java8对交易按照货币分组
        transactionsByCurrencies = transactions.stream().collect(groupingBy(Transaction::getCurrency));

        /**
         * 归约和汇总
         */
        // 数一数菜单里有多少种菜
        long howManyDishes = menu.stream().collect(counting());
        howManyDishes = menu.stream().count();
        System.out.println(howManyDishes);

        // 找出菜单中热量最高的菜,你可以使用两个收集器， Collectors.maxBy和Collectors.minBy，来计算流中的最大或最小值。
        Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> dish = menu.stream().collect(maxBy(dishCaloriesComparator));
        System.out.println(dish.isPresent());

        // 求出菜单列表的总热量
        int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
        // 计算热量的平均数
        double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));
        // 通过一次summarizing操作你可以就数出菜单中元素的个数，并得到菜肴热量总和、平均值、最大值和最小值
        IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
        // IntSummaryStatistics{count=9, sum=4300, min=120, average=477.777778, max=800}
        System.out.println(menuStatistics);

        // 连接字符串
        /**
         * joining工厂方法返回的收集器会把对流中每一个对象应用toString方法得到的所有字符
         * 串连接成一个字符串。这意味着你把菜单中所有菜肴的名称连接起来。
         * 请注意， joining在内部使用了StringBuilder来把生成的字符串逐个追加起来。
         */
        String shortMenu = menu.stream().map(Dish::getName).collect(joining());
        shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
        System.out.println(shortMenu);

        /**
         * 广义的归约汇总
         * 事实上，我们之前讨论的所有收集器，都是一个可以用reducing工厂方法定义的归约过程
         * 的特殊情况而已。 Collectors.reducing工厂方法是所有这些特殊情况的一般化。可以说，先
         * 前讨论的案例仅仅是为了方便程序员而已。
         */
        // 可以用reducing方法创建的收集器来计算你菜单的总热量
        int totalCalories1 = menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
        /**
         * 它需要三个参数:
         * 1、第一个参数是归约操作的起始值，也是流中没有元素时的返回值，所以很显然对于数值
         * 和而言0是一个合适的值。
         * 2、第二个参数是一个函数，它将菜肴转换成一个表示其所含热量的int。
         * 3、第三个参数是一个BinaryOperator，将两个项目累积成一个同类型的值。这里它就是对两个int求和。
         */
        System.out.println(totalCalories1);

        // 同样，你可以使用下面这样单参数形式的reducing来找到热量最高的菜
        Optional<Dish> mostCalorieDish = menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
        System.out.println(mostCalorieDish.isPresent());

        /**
         * 你可能想知道， Stream接口的collect和reduce方法有何不同，因为两种方法通常会获得相同的结果。
         * 例如，你可以像下面这样使用reduce方法来实现toListCollector所做的工作。
         * 这个解决方案有两个问题：一个语义问题和一个实际问题。语义问题在于， reduce方法
         * 旨在把两个值结合起来生成一个新值，它是一个不可变的归约。与此相反， collect方法的设
         * 计就是要改变容器，从而累积要输出的结果。这意味着，下面的代码片段是在滥用reduce方
         * 法，因为它在原地改变了作为累加器的List。你在并行处理中会更详细地看到，以错误的语义
         * 使用reduce方法还会造成一个实际问题：这个归约过程不能并行工作，因为由多个线程并发
         * 修改同一个数据结构可能会破坏List本身。在这种情况下，如果你想要线程安全，就需要每
         * 次分配一个新的List，而对象分配又会影响性能。这就是collect方法特别适合表达可变容
         * 器上的归约的原因，更关键的是它适合并行操作。
         */
        Stream<Integer> stream = Arrays.asList(1, 2, 3, 4, 5, 6).stream();
        // System.out.println(stream.collect(toList()));
        List<Integer> result = stream.reduce(new ArrayList<Integer>(), (List<Integer> l, Integer e) -> {
            System.out.println("......");
            l.add(e);
            return l;
        }, (List<Integer> l1, List<Integer> l2) -> {
            /**
             * 这个函数并不会执行，因为这reduce的这个参数是用来处理并发操作的
             * 如果你使用了parallelStream.reduce来进行并发操作，
             * 为了避免竞争 每个reduce线程都会有独立的result，combiner
             * 的作用在于合并每个线程的result得到最终结果
             */
            l1.addAll(l2);
            return l1;
        });
        System.out.println(result);

        // 从逻辑上说，归约操作的工作原理就是利用累积函数，把一个初始化为起始值的累加器，和把转换函数应用到流中每个元素上得到的结果不断迭代合并起来。
        totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, Integer::sum));
        // 还有另一种方法不使用收集器也能执行相同操作——将菜肴流映射为每一道菜的热量，然后用前一个版本中使用的方法引用来归约得到的流
        totalCalories = menu.stream().map(Dish::getCalories).reduce(Integer::sum).get();
        // 更简洁的方法是把流映射到一个IntStream，然后调用sum方法，你也可以得到相同的结果
        totalCalories = menu.stream().mapToInt(Dish::getCalories).sum();
        /**
         * 更倾向于使用最后一个解决方案（使用IntStream），因为它最简明，也很可能最易读。
         * 同时，它也是性能最好的一个，因为IntStream可以让我们避免自动拆箱操作，也就是从Integer
         * 到int的隐式转换，它在这里毫无用处。
         */

        // 合法地替代joining收集器
        shortMenu = menu.stream().map(Dish::getName).collect(reducing((s1, s2) -> s1 + s2)).get();
        shortMenu = menu.stream().collect(reducing("", Dish::getName, (s1, s2) -> s1 + s2));
    }

    public static class Transaction {
        private final Currency currency;
        private final double value;

        public Transaction(Currency currency, double value) {
            this.currency = currency;
            this.value = value;
        }

        public Currency getCurrency() {
            return currency;
        }

        public double getValue() {
            return value;
        }

        @Override
        public String toString() {
            return currency + " " + value;
        }
    }

    public enum Currency {
        EUR, USD, JPY, GBP, CHF
    }
}
