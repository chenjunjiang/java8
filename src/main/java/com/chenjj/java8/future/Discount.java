package com.chenjj.java8.future;

import static com.chenjj.java8.future.Util.format;
import static com.chenjj.java8.future.Util.randomDelay;

/**
 * 假设获取商品价格的折扣是一个远程服务，我们用delay()模拟远程调用耗时
 */
public class Discount {
    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    /**
     * 获取折扣价格字符串
     *
     * @param quote
     * @return
     */
    public static String applyDiscount(Quote quote) {
        System.out.println("applyDiscount:" + Thread.currentThread().getName() + ":" + quote.getPrice());
        return quote.getShopName() + " price is " +
                Discount.apply(quote.getPrice(),
                        quote.getDiscountCode());
    }

    private static double apply(double price, Code code) {
        //delay();
        randomDelay();
        return format(price * (100 - code.percentage) / 100);
    }
}
