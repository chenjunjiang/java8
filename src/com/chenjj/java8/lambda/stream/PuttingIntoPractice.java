package com.chenjj.java8.lambda.stream;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/4/8.
 */
public class PuttingIntoPractice {
    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        // 找出2011年的所有交易并按交易额排序（从低到高）
        /*transactions.stream().filter(transaction -> transaction.getYear() ==
                2011).sorted(Comparator.comparing(t -> t.getValue())).collect
                (Collectors.toList());*/

        List<Transaction> tr2011 = transactions.stream().filter(transaction ->
                transaction.getYear() == 2011).sorted(Comparator.comparing
                (Transaction::getValue)).collect(Collectors.toList());

        System.out.println(tr2011);

        // 交易员都在哪些不同的城市工作过
        List<String> cities = transactions.stream().map(t -> t.getTrader()
                .getCity())
                .distinct()
                .collect(Collectors.toList());

       /* transactions.stream().map(t -> t.getTrader().getCity())
                .collect(Collectors.toSet());*/

        System.out.println(cities);

        // 查找所有来自于剑桥的交易员，并按姓名排序
        List<Trader> traders =
                transactions.stream()
                        .map(Transaction::getTrader)
                        .filter(trader -> trader.getCity().equals("Cambridge"))
                        .distinct()
                        .sorted(Comparator.comparing(Trader::getName))
                        .collect(Collectors.toList());

        // 返回所有交易员的姓名字符串，按字母顺序排序
        // 请注意，此解决方案效率不高（所有字符串都被反复连接，每次迭代的时候都要建立一个新
        // 的String对象）
        String traderStr =
                transactions.stream()
                        .map(transaction -> transaction.getTrader().getName())
                        .distinct()
                        .sorted()
                        .reduce("", (n1, n2) -> n1 + n2);

        // 使用joining（内部会用到StringBuilder）
        /*String traderStr =
                transactions.stream()
                        .map(transaction -> transaction.getTrader().getName())
                        .distinct()
                        .sorted()
                        .collect(Collectors.joining());*/
        System.out.println(traderStr);

        // 有没有交易员是在米兰工作的
        boolean milanBased =
                transactions.stream()
                        .anyMatch(transaction -> transaction.getTrader()
                                .getCity()
                                .equals("Milan"));

        System.out.println(milanBased);

        //打印生活在剑桥的交易员的所有交易额
        transactions.stream()
                .filter(t -> "Cambridge".equals(t.getTrader().getCity()))
                .map(Transaction::getValue)
                .forEach(System.out::println);

        //所有交易中，最高的交易额是多少
        Optional<Integer> highestValue =
                transactions.stream()
                        .map(Transaction::getValue)
                        .reduce(Integer::max);
        System.out.println(highestValue.get());
        System.out.println(highestValue.orElse(0));

        //找到交易额最小的交易
        Optional<Transaction> smallestTransaction = transactions.stream()
                .reduce((t1, t2) ->
                        t1.getValue() < t2.getValue() ? t1 : t2);
        // 你还可以做得更好。流支持min和max方法，它们可以接受一个Comparator作为参数，指定
        // 计算最小或最大值时要比较哪个键值：
        /*Optional<Transaction> smallestTransaction =
                transactions.stream()
                        .min(Comparator.comparing(Transaction::getValue));*/

        System.out.println(smallestTransaction.get().getValue());
    }
}
