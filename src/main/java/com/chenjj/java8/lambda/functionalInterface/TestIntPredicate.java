package com.chenjj.java8.lambda.functionalInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class TestIntPredicate {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 300; i < 400; i++) {
            // 这里在性能方面是要付出代价的(一个int被装箱成为Integer)。装箱后的值本质上就是把原始类型包裹起来，并保存在堆
            //里。因此，装箱后的值需要更多的内存，并需要额外的内存搜索来获取被包裹的原始值。
            list.add(i);
        }
        /**
         * Java 8为我们前面所说的函数式接口带来了一个专门的版本，以便在输入和输出都是原始类
         * 型时避免自动装箱的操作。在下面的代码中，使用IntPredicate就避免了对值1000进行
         * 装箱操作，但要是用Predicate<Integer>就会把参数1000装箱到一个Integer对象中：
         */
        IntPredicate evenNumbers = (int i) -> i % 2 == 0; // 无装箱
        evenNumbers.test(1000);
        Predicate<Integer> oddNumbers = (Integer i) -> i % 2 == 1; // 装箱
        oddNumbers.test(1000);
    }
}
