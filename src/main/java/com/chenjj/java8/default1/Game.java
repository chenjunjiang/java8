package com.chenjj.java8.default1;

import java.util.Arrays;
import java.util.List;

public class Game {
    public static void main(String[] args) {
        List<Resizable> list = Arrays.asList(new Square(), new Rectangle(), new Ellipse());
        Utils.paint(list);

        Monster monster = new Monster();
        monster.rotateBy(180);
        monster.moveVertically(10);
    }
}
