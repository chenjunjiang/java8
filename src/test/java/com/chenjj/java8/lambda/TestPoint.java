package com.chenjj.java8.lambda;

import com.chenjj.java8.lambda.test.Point;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestPoint {
    @Test
    public void testMoveRightBy() throws Exception {
        Point p1 = new Point(5, 5);
        Point p2 = p1.moveRightBy(10);
        assertEquals(15, p2.getX());
        assertEquals(5, p2.getY());
    }

    /**
     * 测试可见 Lambda 函数的行为
     * 由于moveRightBy方法声明为public，测试工作变得相对容易。你可以在用例内部完成测试。
     * 但是Lambda并无函数名（毕竟它们都是匿名函数），因此要对你代码中的Lambda函数进行测试实
     * 际上比较困难，因为你无法通过函数名的方式调用它们。
     * 有些时候，你可以借助某个字段访问Lambda函数，这种情况，你可以利用这些字段，通过
     * 它们对封装在Lambda函数内的逻辑进行测试。比如，我们假设你在Point类中添加了静态字段
     * compareByXAndThenY，通过该字段，使用方法引用你可以访问Comparator对象
     */
    @Test
    public void testComparingTwoPoints() {
        Point p1 = new Point(10, 15);
        Point p2 = new Point(10, 20);
        int result = Point.compareByXAndThenY.compare(p1, p2);
        assertEquals(-1, result);
    }

    /**
     * 测试使用 Lambda 的方法的行为
     * 但是Lambda的初衷是将一部分逻辑封装起来给另一个方法使用。从这个角度出发，你不应
     * 该将Lambda表达式声明为public，它们仅是具体的实现细节。相反，我们需要对使用Lambda表达
     * 式的方法进行测试。比如下面这个方法moveAllPointsRightBy
     */
    @Test
    public void testMoveAllPointsRightBy() {
        List<Point> points =
                Arrays.asList(new Point(5, 5), new Point(10, 5));
        List<Point> expectedPoints =
                Arrays.asList(new Point(15, 5), new Point(20, 5));
        List<Point> newPoints = Point.moveAllPointsRightBy(points, 10);
        assertEquals(expectedPoints, newPoints);
    }
}
