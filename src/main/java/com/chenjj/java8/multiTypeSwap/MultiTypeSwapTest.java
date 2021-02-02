package com.chenjj.java8.multiTypeSwap;

import java.io.Serializable;
import java.util.Comparator;

public class MultiTypeSwapTest {
    public static void main(String[] args) {
        /**
         * 这是一个通过强制转化创建的基于Integer类型的Comparator对象,
         * 同时它又实现了Serializable接口;
         * 在强转的时候通过&连结两个想要转化的类型来实现同时转化为两个类型;
         * 注意:在进行该类转换时，您只能指定一个类，因为 Java 不支持类继承多个类。接口可以多个。
         */
        Comparator<Integer> comparator = (Comparator<Integer> & Serializable) (x1, x2) -> Integer.compare(x1, x2);
        System.out.println(comparator instanceof Serializable);
    }
}
