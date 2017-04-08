package com.chenjj.java8.lambda.methodReference;

/**
 * Created by Administrator on 2017/3/12.
 */
public class ConcurrentGreeter extends Greeter {
    public void greet() {
        Thread thread = new Thread(super::greet);
        thread.start();
    }

    public static void main(String[] args) {
        ConcurrentGreeter concurrentGreeter = new ConcurrentGreeter();
        concurrentGreeter.greet();
    }
}
