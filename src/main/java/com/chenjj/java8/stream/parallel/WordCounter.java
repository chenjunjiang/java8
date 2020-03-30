package com.chenjj.java8.stream.parallel;

import java.util.stream.Stream;

/**
 * 以函数式风格重写单词计数器
 */
public class WordCounter {
    private final int counter;
    private final boolean lastSpace;

    public WordCounter(int counter, boolean lastSpace) {
        this.counter = counter;
        this.lastSpace = lastSpace;
    }

    /**
     * 一个个遍历Character
     *
     * @param c
     * @return
     */
    public WordCounter accumulate(Character c) {
        if (Character.isWhitespace(c)) {
            return lastSpace ? this : new WordCounter(counter, true);
        } else {
            //上一个字符是空格，而当前遍历的字符不是空格时，将单词计数器加一
            return lastSpace ? new WordCounter(counter + 1, false) : this;
        }
    }

    /**
     * 合并两个WordCounter，把其计数器加起来
     *
     * @param wordCounter
     * @return
     */
    public WordCounter combine(WordCounter wordCounter) {
        // 仅 需 要 计 数 器的总和，无需关心lastSpace
        return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
    }

    public int getCounter() {
        return counter;
    }

    public static int countWords(Stream<Character> stream) {
        // stream.reduce(new WordCounter(0,true), (w,c)-> w.accumulate(c),WordCounter::combine);
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                WordCounter::accumulate, WordCounter::combine);
        return wordCounter.getCounter();
    }
}
