package com.chenjj.java8.lambda.methodReference;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/19.
 */
public class Apple implements Serializable {
    private static final long serialVersionUID = 6864309742604556016L;

    private Integer weight;
    private String color = "";

    public Apple(int weight, String color) {
        this.weight = weight;
        this.color = color;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String toString() {
        return "Apple{" +
                "color='" + color + '\'' +
                ", weight=" + weight +
                '}';
    }
}
