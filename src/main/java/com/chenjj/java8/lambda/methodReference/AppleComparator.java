package com.chenjj.java8.lambda.methodReference;

import com.chenjj.java8.model.Apple;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/3/19.
 */
public class AppleComparator implements Comparator<Apple> {
    @Override
    public int compare(Apple apple1, Apple apple2) {
        return apple1.getWeight().compareTo(apple2.getWeight());
    }
}
