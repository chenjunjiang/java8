package com.chenjj.java8.parametric;

/**
 * ApplePredicate只适用于Apple,我们可以将具体类型抽象化
 *
 * @param <T>
 */
public interface Predicate<T> {
    boolean test(T t);
}
