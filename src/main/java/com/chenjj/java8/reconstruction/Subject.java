package com.chenjj.java8.reconstruction;

/**
 * Subject使用registerObserver方法可以注册一个新的观察者，使用notifyObservers
 * 方法通知它的观察者一个新闻的到来。
 */
public interface Subject {
    void registerObserver(Observer o);

    void notifyObservers(String tweet);
}
