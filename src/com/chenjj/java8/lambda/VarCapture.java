package com.chenjj.java8.lambda;

/**
 * Created by Administrator on 2015/9/22 0022.
 */
public class VarCapture {
    private int age;
    private static String name = "zhangsan";

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(String[] args) {
        int num = 10;
        VarCapture varCapture = new VarCapture();
        MyFunc myFunc = (n) -> {
            int v = num + n;
            // num++; num在外层作用域是final的局部变量,不能修改
            varCapture.setAge(10);// 可以改变实例的属性值
            // varCapture = new VarCapture();// varCapture为final,不能改变其值
            System.out.println(name);
            name = "lisi";// 外层静态变量是可以访问和改变的
            return v;
        };

        // num++; num已经是final,不能修改
    }
}
