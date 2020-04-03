package com.chenjj.java8.reconstruction;

/**
 * 你需要一个观察者接口，它将不同的观察者聚合在一起。它仅有一个名为notify的
 * 方法，一旦接收到一条新的新闻，该方法就会被调用
 */
public interface Observer {
    void notify(String tweet);
}
