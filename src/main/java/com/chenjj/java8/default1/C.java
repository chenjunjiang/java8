package com.chenjj.java8.default1;

/**
 * 如果一个类使用相同的函数签名从多个地方（比如另一个类或接口）继承了方法，通过三条
 * 规则可以进行判断。
 * 1、类中的方法优先级最高。类或父类中声明的方法的优先级高于任何声明为默认方法的优先级。
 * 2、如果无法依据第一条进行判断，那么子接口的优先级更高：函数签名相同时，优先选择
 * 拥有最具体实现的默认方法的接口，即如果B继承了A，那么B就比A更加具体。
 * 3、最后，如果还是无法判断，继承了多个接口的类必须通过显式覆盖和调用期望的方法，
 * 显式地选择使用哪一个默认方法的实现。
 */
/*public class C implements B, A {
    public static void main(String... args) {
        // Hello from B
        new C().hello();
    }
}*/

/**
 * 依据规则(1)，类中声明的方法具有更高的优先级。 D并未覆盖hello方法，可是它实现了接
 * 口A。所以它就拥有了接口A的默认方法。规则(2)说如果类或者父类没有对应的方法，那么就应
 * 该选择提供了最具体实现的接口中的方法。因此，编译器会在接口A和接口B的hello方法之间做
 * 选择。由于B更加具体(B继承了A)，所以程序会再次打印输出“Hello from B”。
 */
/*public class C extends D implements B, A {
    public static void main(String... args) {
        // Hello from B
        new C().hello();
    }
}*/

/**
 * 假设B不再继承A,这时规则(2)就无法进行判断了，因为从编译器的角度看没有哪一个接口的实现更加具体，两
 * 个都差不多。 A接口和B接口的hello方法都是有效的选项。所以， Java编译器这时就会抛出一个
 * 编译错误，因为它无法判断哪一个方法更合适：“Error: class C inherits unrelated defaults for hello()
 * from types B and A.”
 * 解决这种两个可能的有效方法之间的冲突，没有太多方案；你只能显式地决定你希望在C中
 * 使用哪一个方法。为了达到这个目的，你可以覆盖类C中的hello方法，在它的方法体内显式地
 * 调用你希望调用的方法。 Java 8中引入了一种新的语法X.super.m(…)，其中X是你希望调用的m
 * 方法所在的父接口。
 */
public class C implements B, A {

    @Override
    public void hello() {
        // 显式地选择调用接口B中的方法
        B.super.hello();
    }

    public static void main(String... args) {
        // Hello from B
        new C().hello();
    }
}
