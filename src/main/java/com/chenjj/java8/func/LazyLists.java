package com.chenjj.java8.func;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 实现自己的延迟列表
 * <p>
 * Java 8的Stream以其延迟性而著称。它们被刻意设计成这样,即延迟操作,有其独特的原因:
 * Stream就像是一个黑盒,它接收请求生成结果。当你向一个Stream发起一系列的操作请求时,这
 * 些请求只是被一一保存起来。只有当你向Stream发起一个终端操作时,才会实际地进行计算。这
 * 种设计具有显著的优点,特别是你需要对Stream进行多个操作时(你有可能先要进行filter操
 * 作,紧接着做一个map,最后进行一次终端操作reduce);这种方式下Stream只需要遍历一次,
 * 不需要为每个操作遍历一次所有的元素。
 */
public class LazyLists {
    public static void main(String[] args) {
        /**
         * 这类请注意静态内部类和非静态内部类的区别已经初始化方法：
         * 假设类A有静态内部类B和非静态内部类C，创建B和C的区别为：
         * A a=new A();
         * A.B b=new A.B();
         * A.C c=a.new C();
         * https://www.cnblogs.com/insist-bin/p/11137675.html
         */
        MyList<Integer> emptyList = new Empty<>();
        //LazyLists.MyList<Integer> emptyList = new LazyLists.Empty<>();
        // 这样构造list，tail已经立即出现在内存中了
        MyList<Integer> list = new MyLinkedList<>(5, new MyLinkedList<>(10, emptyList));

        // 真正使用的时候才调用
        LazyList<Integer> numbers = from(2);
        int two = numbers.head();
        int three = numbers.tail().head();
        int four = numbers.tail().tail().head();
        System.out.println(two + " " + three + " " + four); // 2 3 4

        two = primes(numbers).head();
        three = primes(numbers).tail().head();
        int five = primes(numbers).tail().tail().head();
        System.out.println(two + " " + three + " " + five); // 2 3 5
    }

    interface MyList<T> {
        T head();

        MyList<T> tail();

        default boolean isEmpty() {
            return true;
        }

        /**
         * 延迟筛选器
         *
         * @param p
         * @return
         */
        MyList<T> filter(Predicate<T> p);
    }

    static class MyLinkedList<T> implements MyList<T> {
        private final T head;
        private final MyList<T> tail;

        public MyLinkedList(T head, MyList<T> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public T head() {
            return head;
        }

        @Override
        public MyList<T> tail() {
            return tail;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        public MyList<T> filter(Predicate<T> p) {
            return isEmpty() ? this : p.test(head()) ? new MyLinkedList<>(
                    head(), tail().filter(p)) : tail().filter(p);
        }
    }

    static class Empty<T> implements MyList<T> {

        @Override
        public T head() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MyList<T> tail() {
            throw new UnsupportedOperationException();
        }

        public MyList<T> filter(Predicate<T> p) {
            return this;
        }
    }

    static class LazyList<T> implements MyList<T> {
        private final T head;
        private final Supplier<MyList<T>> tail;

        public LazyList(T head, Supplier<MyList<T>> tail) {
            System.out.println("------------------");
            this.head = head;
            this.tail = tail;
        }

        @Override
        public T head() {
            return head;
        }

        @Override
        public MyList<T> tail() {
            // 这里的tail使用了一个Supplier方法提供延迟性,调用 Supplier 的 get 方法会触发延迟列表( LazyList )的节点创建
            return tail.get();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        public MyList<T> filter(Predicate<T> p) {
            return isEmpty() ? this : p.test(head()) ? new LazyList<>(head(),
                    () -> tail().filter(p)) : tail().filter(p);
        }
    }

    /**
     * 创建由数字构成的无限延迟列表了,该方法会创建一系列数字中的下一个元素
     *
     * @param n
     * @return
     */
    public static LazyList<Integer> from(int n) {
        return new LazyList<>(n, () -> from(n + 1));
    }

    /**
     * 质数延迟列表
     *
     * @param numbers
     * @return
     */
    public static MyList<Integer> primes(MyList<Integer> numbers) {
        return new LazyList<>(
                numbers.head(),
                () -> primes(
                        numbers.tail()
                                .filter(n -> n % numbers.head() != 0)
                )
        );
    }

    /*static <T> void printAll(MyList<T> list){
        while (!list.isEmpty()){
            System.out.println(list.head());
            list = list.tail();
        }
    }*/

    /**
     * 这个程序不会永久地运行下去;它最终会由于栈溢出而失效,因为Java不支持尾部调用消除(tail call elimination)
     *
     * @param list
     * @param <T>
     */
    static <T> void printAll(MyList<T> list) {
        if (list.isEmpty())
            return;
        System.out.println(list.head());
        printAll(list.tail());
    }
}
