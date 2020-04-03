package com.chenjj.java8.reconstruction;

public abstract class ProcessingObject<T> {
    protected ProcessingObject<T> successor;

    public void setSuccessor(ProcessingObject<T> successor) {
        this.successor = successor;
    }

    public T handle(T input) {
        T t = handleWork(input);
        if (successor != null) {
            return successor.handle(t);
        }
        System.out.println(t + "......" + System.currentTimeMillis());
        return t;
    }

    abstract protected T handleWork(T input);

    public void test(String str) {
        str = str + "test";
    }
}
