package com.chenjj.java8.stream;

import com.chenjj.java8.enum1.CaloricLevel;
import com.chenjj.java8.enum1.Currency;
import com.chenjj.java8.model.Dish;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.chenjj.java8.model.Dish.menu;
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

        /**
         * 分组
         */
        // 把菜单中的菜按照类型进行分类，有肉的放一组，有鱼的放一组，其他的都放另一组
        Map<Dish.Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));
        System.out.println(dishesByType);

        // 把热量不到400卡路里的菜划分为“低热量”（diet），热量400到700卡路里的菜划为“普通”（normal），高于700卡路里的划为“高热量”（fat）
        menu.stream().collect(groupingBy(d -> {
            if (d.getCalories() <= 400) {
                return CaloricLevel.DIET;
            } else if (d.getCalories() <= 700) {
                return CaloricLevel.NORMAL;
            } else {
                return CaloricLevel.FAT;
            }
        }));

        /**
         * 要实现多级分组，我们可以使用一个由双参数版本的Collectors.groupingBy工厂方法创
         * 建的收集器，它除了普通的分类函数之外，还可以接受collector类型的第二个参数。那么要进
         * 行二级分组的话，我们可以把一个内层groupingBy传递给外层groupingBy，并定义一个为流
         * 中项目分类的二级标准
         */
        // 先按类型分组再进行热量分组
        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream().collect(
                // 一级分类函数
                groupingBy(Dish::getType,
                        // 二级分类函数
                        groupingBy(d -> {
                            if (d.getCalories() <= 400) {
                                return CaloricLevel.DIET;
                            } else if (d.getCalories() <= 700) {
                                return CaloricLevel.NORMAL;
                            } else {
                                return CaloricLevel.FAT;
                            }
                        })));
        System.out.println(dishesByTypeCaloricLevel);

        /**
         * 按子组收集数据
         */
        // 传递给第一个groupingBy的第二个收集器可以是任何类型，而不一定是另一个groupingBy。
        // 例如，要数一数菜单中每类菜有多少个，可以传递counting收集器作为groupingBy收集器的第二个参数
        Map<Dish.Type, Long> typesCount = menu.stream().collect(groupingBy(Dish::getType, counting()));
        System.out.println(typesCount);
        // 按照菜的类型分类,查找菜单中热量最高的菜肴
        Map<Dish.Type, Optional<Dish>> mostCaloricByType = menu.stream().collect(groupingBy(Dish::getType, maxBy(Comparator.comparingInt(Dish::getCalories))));
        System.out.println(mostCaloricByType);
        // 把收集器的结果转换为另一种类型，可以使用Collectors.collectingAndThen工厂方法返回的收集器
        Map<Dish.Type, Dish> mostCaloricByType1 = menu.stream().collect(
                groupingBy(Dish::getType,
                        collectingAndThen(
                                maxBy(Comparator.comparingInt(Dish::getCalories)),
                                Optional::get)));

        /**
         * 一般来说，通过groupingBy工厂方法的第二个参数传递的收集器将会对分到同一组中的所
         * 有流元素执行进一步归约操作.
         */
        // 统计出每一组Dish的所有菜肴热量总和
        Map<Dish.Type, Integer> totalCaloriesByType = menu.stream().collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));
        System.out.println(totalCaloriesByType);

        /**
         * 常常和groupingBy联合使用的另一个收集器是mapping方法生成的。这个方法接受两
         * 个参数：一个函数对流中的元素做变换，另一个则将变换的结果对象收集起来。其目的是在累加
         * 之前对每个输入元素应用一个映射函数，这样就可以让接受特定类型元素的收集器适应不同类型
         * 的对象。
         */
        // 对于每种类型的Dish,菜单中都有哪些CaloricLevel,可以把groupingBy和mapping收集器结合起来
        Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream().collect(
                groupingBy(Dish::getType,
                        mapping(d -> {
                            if (d.getCalories() <= 400) {
                                return CaloricLevel.DIET;
                            } else if (d.getCalories() <= 700) {
                                return CaloricLevel.NORMAL;
                            } else {
                                return CaloricLevel.FAT;
                            }
                        }, toSet())));
        System.out.println(caloricLevelsByType);
        // 上面这个例子对于返回的Set是什么类型并没有任何保证,但通过使用toCollection可以以有更多的控制。
        // 例如，你可以给它传递一个构造函数引用来要求HashSet
        caloricLevelsByType = menu.stream().collect(
                groupingBy(Dish::getType,
                        mapping(d -> {
                            if (d.getCalories() <= 400) {
                                return CaloricLevel.DIET;
                            } else if (d.getCalories() <= 700) {
                                return CaloricLevel.NORMAL;
                            } else {
                                return CaloricLevel.FAT;
                            }
                        }, toCollection(HashSet::new))));

        /**
         * 分区是分组的特殊情况：由一个谓词（返回一个布尔值的函数）作为分类函数，它称分区函
         * 数。分区函数返回一个布尔值，这意味着得到的分组Map的键类型是Boolean，于是它最多可以
         * 分为两组——true是一组， false是一组。
         */
        // 把菜单按照素食和非素食分开
        Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian));
        System.out.println(partitionedMenu);
        // 对于分区产生的素食和非素食子流再分别按类型对菜肴分组
        Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = menu.stream().collect(
                partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));
        System.out.println(vegetarianDishesByType);
        // 找到素食和非素食中热量最高的菜
        Map<Boolean, Dish> mostCaloricPartitionedByVegetarian = menu.stream().collect(
                partitioningBy(Dish::isVegetarian,
                        collectingAndThen(
                                maxBy(Comparator.comparingInt(Dish::getCalories)), Optional::get)));
        System.out.println(mostCaloricPartitionedByVegetarian);

        // 将数字按质数和非质数分区
        System.out.println(partitionPrimes(10));

        // 使用自定义Collector(ToListCollector)
        List<Dish> dishes = menu.stream().collect(new ToListCollector<>());
        System.out.println(dishes);
        /**
         * 对于IDENTITY_FINISH的收集操作，还有一种方法可以得到同样的结果而无需从头实现新
         * 的Collectors接口。Stream有一个重载的collect方法可以接受另外三个函数——supplier、
         * accumulator和combiner，其语义和Collector接口的相应方法返回的函数完全相同。
         * 这种形式虽然比前一个写法更为紧凑和简洁，却不那么易读。此外，以恰当
         * 的类来实现自己的自定义收集器有助于重用并可避免代码重复。另外值得注意的是，这个
         * collect方法不能传递任何Characteristics，所以它永远都是一个IDENTITY_FINISH和
         * CONCURRENT但并非UNORDERED的收集器
         */
        dishes = menu.stream().collect(ArrayList::new, List::add, List::addAll);
        System.out.println(dishes);

        /**
         * 开发你自己的收集器,让按质数和非质数分区的实现比上边性能更好
         */
        Map<Boolean, List<Integer>> partitionPrimes = partitionPrimesWithCustomCollector(10);
        System.out.println(partitionPrimes);

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

    public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream.rangeClosed(2, n).boxed().collect(
                partitioningBy(candidate -> isPrime(candidate)));
    }

    /*public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
        return IntStream.rangeClosed(2, n).boxed().collect(new PrimeNumbersCollector());
    }*/

    public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
        return IntStream.rangeClosed(2, n).boxed().collect(() -> new HashMap<Boolean, List<Integer>>() {
            {
                put(true, new ArrayList<>());
                put(false, new ArrayList<>());
            }
        }, (acc, candidate) -> {
            acc.get(PrimeNumbersCollector.isPrime(acc.get(true), candidate)).add(candidate);
        }, ((map1, map2) -> {
            map1.get(true).addAll(map2.get(true));
            map1.get(false).addAll(map2.get(false));
        }));
    }

    private static boolean isPrime(int candidate) {
        System.out.println("candidate:" + candidate);
        int candidateRoot = (int) Math.sqrt(candidate);
        System.out.println("candidateRoot:" + candidateRoot);
        // noneMatch: true if either no elements of the stream match the provided predicate or the stream is empty, otherwise false
        // 如果candidateRoot小于2,那么生成的IntStream就为空,调用noneMatch直接返回true
        return IntStream.rangeClosed(2, candidateRoot).noneMatch(i -> candidate % i == 0);
    }
}
