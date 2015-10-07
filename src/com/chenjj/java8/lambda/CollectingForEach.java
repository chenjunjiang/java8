package com.chenjj.java8.lambda;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2015/10/7 0007.
 */
public class CollectingForEach {
    public static void main(String[] args) {
        Set<String> books = new HashSet<>();
        books.add("java编程思想");
        books.add("java in action");
        books.add("java权威指南");

        books.forEach(book -> {
            System.out.println(book);
        });

        Iterator<String> iterator = books.iterator();
        iterator.forEachRemaining(book -> {
            System.out.println(book);
        });
    }
}
