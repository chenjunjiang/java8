package com.chenjj.java8.parametric;

import com.chenjj.java8.model.Apple;

/**
 * 我们的需求是需要根据Apple的某些属性来返回一个boolean值。我们把它称为谓词（即一个返回boolean值的函数）。
 * 定义一个接口来对选择标准建模。
 * 我们可以用ApplePredicate的多个实现代表不同的选择标准了。
 */
public interface ApplePredicate {
    boolean test(Apple apple);
}
