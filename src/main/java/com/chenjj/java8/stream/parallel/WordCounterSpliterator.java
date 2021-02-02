package com.chenjj.java8.stream.parallel;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Spliterator接口用于遍历数据源中的元素，但它是为了并行执行
 * 而设计的。虽然在实践中可能用不着自己开发Spliterator，但了解一下它的实现方式会让你
 * 对并行流的工作原理有更深入的了解。 Java 8已经为集合框架中包含的所有数据结构提供了一个
 * 默认的Spliterator实现。集合实现了Spliterator接口，接口提供了一个spliterator方法。
 * public interface Spliterator<T> {
 * boolean tryAdvance(Consumer<? super T> action);
 * Spliterator<T> trySplit();
 * long estimateSize();
 * int characteristics();
 * }
 * T是Spliterator遍历的元素的类型。tryAdvance方法的行为类似于普通的
 * Iterator，因为它会按顺序一个一个使用Spliterator中的元素，并且如果还有其他元素要遍
 * 历就返回true。但trySplit是专为Spliterator接口设计的，因为它可以把一些元素划出去分
 * 给第二个Spliterator（由该方法返回），让它们两个并行处理。 Spliterator还可通过
 * estimateSize方法估计还剩下多少元素要遍历，因为即使不那么确切，能快速算出来是一个值
 * 也有助于让拆分均匀一点。
 * Spliterator接口声明的最后一个抽象方法是characteristics，它将返回一个int，代
 * 表Spliterator本身特性集的编码。使用Spliterator的客户可以用这些特性来更好地控制和
 * 优化它的使用。
 * 特 性             含 义
 * ORDERED          元素有既定的顺序（例如List），因此Spliterator在遍历和划分时也会遵循这一顺序
 * DISTINCT         对于任意一对遍历过的元素x和y， x.equals(y)返回false
 * SORTED           遍历的元素按照一个预定义的顺序排序
 * SIZED            该Spliterator由一个已知大小的源建立（例如Set），因此estimatedSize()返回的是准确值
 * NONNULL          保证遍历的元素不会为null
 * IMMUTABLE        Spliterator的数据源不能修改。这意味着在遍历时不能添加、删除或修改任何元素
 * CONCURRENT       该Spliterator的数据源可以被其他线程同时修改而无需同步
 * SUBSIZED         该Spliterator和所有从它拆分出来的Spliterator都是SIZED
 * <p>
 * 开发一个简单的方法来数数一个String中的单词数。
 */
public class WordCounterSpliterator implements Spliterator<Character> {
    public static final String SENTENCE =
            " Nel mezzo del cammin di nostra vita " +
                    "mi ritrovai in una selva oscura" +
                    " ché la dritta via era smarrita ";
    // 要处理的字符串
    private final String string;
    // 当前字符索引
    private int currentChar = 0;

    public WordCounterSpliterator(String string) {
        this.string = string;
    }

    public static void main(String[] args) {
        System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
        Stream<Character> stream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
        // System.out.println("Found " + WordCounter.countWords(stream) + " words");
        // 让WordCounter并行工作
        System.out.println("Found " + WordCounter.countWords(stream.parallel()) + " words");
        /**
         * 不幸的是，这次的输出是：Found 26 words，因为原始的String在任意
         * 位置拆分，所以有时一个词会被分为两个词，然后数了两次。这就说明，拆分流会影响结果，而
         * 把顺序流换成并行流就可能使结果出错。解决方案就是要确保String不是在随机位置拆开的，而只能在词尾
         * 拆开。要做到这一点，你必须为Character实现一个Spliterator（WordCounterSpliterator），
         * 它只能在两个词之间拆开String，然后由此创建并行流。
         */

        // 使用自定义Spliterator工作
        Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
        // 传给StreamSupport.stream工厂方法的第二个布尔参数意味着你想创建一个并行流。如果是false就不并行处理
        Stream<Character> stream1 = StreamSupport.stream(spliterator, true);
        System.out.println("Found " + WordCounter.countWords(stream1) + " words");
    }

    /**
     * 当trySplit()返回null，即不可再拆分了，就开始执行tryAdvance处理元素
     * <p>
     * tryAdvance方法把String中当前位置的Character传给了Consumer，并让位置加一。
     * 作为参数传递的Consumer是一个Java内部类，在遍历流时将要处理的Character传给了
     * 一系列要对其执行的函数。这里只有一个归约函数，即WordCounter类的accumulate
     * 方 法 。 如 果 新 的 指 针 位 置 小 于 String 的 总 长 ， 且 还 有 要 遍 历 的 Character ， 则
     * tryAdvance返回true。
     *
     * @param action
     * @return
     */
    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        System.out.println("拆分已满足要求,开始处理字符......" + Thread.currentThread().getName());
        action.accept(string.charAt(currentChar++));
        // 如果还有字符要处理返回true
        return currentChar < string.length();
    }

    /**
     * 拆分以便能并行处理
     *
     * @return
     */
    @Override
    public Spliterator<Character> trySplit() {
        int currentSize = string.length() - currentChar;
        System.out.println("开始拆分......" + currentSize + " " + Thread.currentThread().getName());
        /**
         * 返回null表示要解析的String已经足够小，可以顺序处理
         */
        if (currentSize < 10) {
            System.out.println("String已经足够小，可以顺序处理......" + currentChar + " " + Thread.currentThread().getName());
            return null;
        }
        // 将试图拆分位置设定为要解析的String的中间，这个中间位置对应字符不一定就是空格
        for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
            // 一直循环直到找个一个空格，因为要避免把词在中间断开
            if (Character.isWhitespace(string.charAt(splitPos))) {
                //创 建 一 个 新WordCounterSpliterator来 解 析 String从 开 始 到 拆 分位置的部分,重复这个过程进行拆分
                Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
                // 将当前WordCounterSpliterator的起始位置设为拆分位置，重复这个过程继续拆分
                currentChar = splitPos;
                return spliterator;
            }
        }
        return null;
    }

    /**
     * 估计还剩下多少元素要遍历，每次调用trySplit之前都会先调用estimateSize
     *
     * @return
     */
    @Override
    public long estimateSize() {
        int size = string.length() - currentChar;
        System.out.println("String剩下多少元素要遍历......" + size + " " + Thread.currentThread().getName());
        return size;
    }

    /**
     * 告诉框架这个Spliterator是ORDERED（顺序就是String
     * 中 各 个Character 的 次序 ）、 SIZED （estimatedSize 方 法的 返 回 值 是精 确 的 ）、
     * SUBSIZED（trySplit方法创建的其他Spliterator也有确切大小）、 NONNULL（String
     * 中 不 能 有 为 null 的 Character ） 和 IMMUTABLE （ 在 解 析 String 时 不 能 再 添 加
     * Character，因为String本身是一个不可变类）的。
     *
     * @return
     */
    @Override
    public int characteristics() {
        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }

    /**
     * 常规版本
     *
     * @param s
     * @return
     */
    public static int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                // 上一个字符是空格，而当前遍历的字符不是空格时，将单词计数器加一
                if (lastSpace) {
                    counter++;
                }
                lastSpace = false;
            }
        }
        return counter;
    }
}
