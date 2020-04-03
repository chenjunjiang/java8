package com.chenjj.java8.reconstruction;

import com.chenjj.java8.enum1.CaloricLevel;
import com.chenjj.java8.model.Dish;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.chenjj.java8.model.Dish.menu;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

/**
 * 重构代码
 */
public class Reconstruction {
    private static final Logger logger = Logger.getLogger(Reconstruction.class.getName());

    public static void main(String[] args) throws IOException {
        /**
         * 从匿名类到 Lambda 表达式的转换
         * 匿名类和Lambda表达式中的this和super的含义是不同的。在匿名类中， this代表的是匿名类类自身，但
         * 是在Lambda中，它代表的是包含类。其次，匿名类可以屏蔽包含类的变量，而Lambda表达式不
         * 能（它们会导致编译错误）
         */
        int a = 10;
        Runnable runnable1 = () -> {
            // int a = 2; // Variable 'a' is already defined in the scope
            System.out.println(a);
        };

        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                int a = 2;
                System.out.println(a);
            }
        };

        /**
         * 在涉及重载的上下文里，将匿名类转换为Lambda表达式可能导致最终的代码更加晦
         * 涩。实际上，匿名类的类型是在初始化时确定的，而Lambda的类型取决于它的上下文。通过下
         * 面这个例子，我们可以了解问题是如何发生的。我们假设你用与Runnable同样的签名声明了一
         * 个函数接口，我们称之为Task（你希望采用与你的业务模型更贴切的接口名时，就可能做这样
         * 的变更）
         */
        //现在，你再传递一个匿名类实现的Task，不会碰到任何问题：
        doSomething(new Task() {
            @Override
            public void execute() {
                System.out.println("Danger danger!!");
            }
        });

        // 但是将这种匿名类转换为Lambda表达式时，就导致了一种晦涩的方法调用，因为Runnable和Task都是合法的目标类型
        // doSomething(() -> System.out.println("Danger danger!!"));
        // 你可以对Task尝试使用显式的类型转换来解决这种模棱两可的情况
        doSomething((Task) () -> System.out.println("Danger danger!!"));

        /**
         * 从 Lambda 表达式到方法引用的转换
         * Lambda表达式非常适用于需要传递代码片段的场景。不过，为了改善代码的可读性，也请
         * 尽量使用方法引用。因为方法名往往能更直观地表达代码的意图。
         */
        // 比如之前按照食物的热量级别对菜肴进行分类是这样做的
        Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
                groupingBy(dish -> {
                    if (dish.getCalories() <= 400) {
                        return CaloricLevel.DIET;
                    } else if (dish.getCalories() <= 700) {
                        return CaloricLevel.NORMAL;
                    } else {
                        return CaloricLevel.FAT;
                    }
                }));

        // 可以将Lambda表达式的内容抽取到一个单独的方法中，将其作为参数传递给groupingBy方法。
        // 变换之后，代码变得更加简洁，程序的意图也更加清晰了
        dishesByCaloricLevel = menu.stream().collect(groupingBy(Dish::getCaloricLevel));

        /**
         * 很多通用的归约操作，比如sum、 maximum，都有内建的辅助方法可以和方法引用结
         * 合使用。比如，在我们的示例代码中，使用Collectors接口可以轻松得到和或者最大值，与采
         * 用Lambada表达式和底层的归约操作比起来，这种方式要直观得多。与其编写
         */
        int totalCalories = menu.stream().map(Dish::getCalories).reduce(0, (d1, d2) -> d1 + d2);
        // 不如尝试使用内置的集合类，它能更清晰地表达问题陈述是什么。
        totalCalories = menu.stream().collect(summingInt(Dish::getCalories));

        /**
         * 有条件的延迟执行
         * 我们经常看到这样的代码，控制语句被混杂在业务逻辑代码之中。典型的情况包括进行安全
         * 性检查以及日志输出。比如，下面的这段代码，它使用了Java语言内置的Logger类
         */
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("Problem: " + generateDiagnostic());
        }
        /**
         * 上面的代码主要有两个问题：
         * 1、日志器的状态（它支持哪些日志等级）通过isLoggable方法暴露给了客户端代码
         * 2、为什么要在每次输出一条日志之前都去查询日志器对象的状态？这只能搞砸你的代码
         * 更好的方案是使用log方法，该方法在输出日志消息之前，会在内部检查日志对象是否已经
         * 设置为恰当的日志等级
         */
        logger.log(Level.FINER, "Problem: " + generateDiagnostic());
        /**
         * 这种方式更好的原因是你不再需要在代码中插入那些条件判断，与此同时日志器的状态也不
         * 再被暴露出去。不过，这段代码依旧存在一个问题。日志消息每次都要生成(调用generateDiagnostic得到结果再进行字符串拼接)
         * ，即使你已经传递了参数，不开启日志。
         * 你需要做的仅仅是延迟消息构造，如此一来，
         * 日志就只会在某些特定的情况下才开启（以此为例，当日志器的级别设置为FINER时），
         * 才日志消息才会真正生成（调用generateDiagnostic得到结果再进行字符串拼接）。
         *
         * 如果你发现你需要频繁地从客户端代码去查询一个对象的状态（比如本例中的日志器的状态），
         * 只是为了传递参数、调用该对象的一个方法（比如输出一条日志），那么可以考虑实现一个新的方法，
         * 以Lambda或者方法表达式作为参数，新方法在检查完该对象的状态之后才调用原来的方法。
         * 你的代码会因此而变得更易读（结构更清晰），封装性更好（对象的状态也不会暴露给客户端代码了）
         */
        logger.log(Level.FINER, () -> "Problem: " + generateDiagnostic());

        /**
         * 环绕执行
         * 如果你发现虽然你的业务代码千差万别，但是它们拥有同样的准备和清理阶段，这时，你完全可以将这部分代码用Lambda
         * 实现。这种方式的好处是可以重用准备和清理阶段的逻辑，减少重复冗余的代码
         * 打开和关闭文件时使用了同样的逻辑，但在处理文件时可以使用不同的Lambda进行参数化
         */
        String oneLine = processFile(b -> b.readLine());
        String twoLines = processFile(b -> b.readLine() + b.readLine());

        /**
         * 使用 Lambda 重构面向对象的设计模式
         */
        // 策略模式
        // 传统方式
        /*Validator numericValidator = new Validator(new IsNumeric());
        numericValidator.validate("aaaa"); // false
        Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
        lowerCaseValidator.validate("bbb");// true*/
        // 使用Lambda表达式
        Validator numericValidator = new Validator((str) -> str.matches("\\d+"));
        Validator lowerCaseValidator = new Validator((str) -> str.matches("[a-z]+"));

        /**
         * 模板方法模式
         * 模板方法模式在你“希望使用这个算法，但是需要对其中的某些行进行改进，才能达到希望的效果”
         * 时是非常有用的。
         * 假设你需要编写一个简单的在线银行应用。
         * 通常，用户需要输入一个用户账户，之后应用才能从银行的数据库中得到用户的详细信息，
         * 最终完成一些让用户满意的操作。不同分行的在线银行应用让客户满意的方式可能还略有不同，
         * 比如给客户的账户发放红利，或者仅仅是少发送一些推广文件。
         */
        // 传统方法，不能的支行继承OnlineBanking类，对processCustomer提供差异化的实现
        // 使用Lambda表达式
        new OnlineBankingLambda().processCustomer(9527, (customer -> System.out.println("hello")));

        /**
         * 观察者模式
         * 观察者模式是一种比较常见的方案，某些事件发生时（比如状态转变），如果一个对象（通
         * 常我们称之为主题）需要自动地通知其他多个对象（称为观察者），就会采用该方案。
         * 比如，观察者设计模式也适用于股票交易的
         * 情形，多个券商可能都希望对某一支股票价格（主题）的变动做出响应。
         */
        Feed feed = new Feed();
        /*feed.registerObserver(new NYTimes());
        feed.registerObserver(new Guardian());
        feed.registerObserver(new LeMonde());
        feed.notifyObservers("The queen said her favourite book is Java 8 in Action!");*/
        // 使用Lambda表达式
        feed.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news in NY! " + tweet);
            }
        });
        feed.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet another news in London... " + tweet);
            }
        });
        feed.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("wine")) {
                System.out.println("Today cheese, wine and news! " + tweet);
            }
        });
        feed.notifyObservers("The queen said her favourite book is Java 8 in Action!");
        /**
         * Lambda适配得很好，那是因为需要执行的动作都很简单，因此才能很方便地消除僵化代
         * 码。但是，观察者的逻辑有可能十分复杂，它们可能还持有状态，抑或定义了多个方法，诸如此
         * 类。在这些情形下，你还是应该继续使用传统方式。
         */

        /**
         *责任链模式
         *责任链模式是一种创建处理对象序列（比如操作序列）的通用方案。一个处理对象可能需要
         * 在完成一些工作之后，将结果传递给另一个对象，这个对象接着做一些工作，再转交给下一个处
         * 理对象，以此类推。
         * 通常，这种模式是通过定义一个代表处理对象的抽象类来实现的，在抽象类中会定义一个字
         * 段来记录后续对象。一旦对象完成它的工作，处理对象就会将它的工作转交给它的后继。
         */
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        ProcessingObject<String> p2 = new SpellCheckerProcessing();
        p1.setSuccessor(p2);
        String result = p1.handle("Aren't labdas really sexy?!!");
        // From Raoul, Mario and Alan: Aren't lambdas really sexy?!!
        System.out.println(result);
        String str = "123";
        p1.test(str);
        System.out.println(str);// 123

        /**
         * 使用工厂模式，你无需向客户暴露实例化的逻辑就能完成对象的创建。比如，我们假定你为
         * 一家银行工作，他们需要一种方式创建不同的金融产品：贷款、期权、股票，等等。
         * 通常，你会创建一个工厂类，它包含一个负责实现不同对象的方法
         */
        Product product = ProductFactory.createProduct("loan");
        // 使用Lambda表达式
        Supplier<Product> supplier = Loan::new;
        product = supplier.get();
    }

    public static String processFile(BufferedReaderProcessor processor) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("data.txt"))) {
            return processor.process(bufferedReader);
        }
    }

    private static String generateDiagnostic() {
        return "test";
    }

    public static void doSomething(Runnable runnable) {
        runnable.run();
    }

    public static void doSomething(Task task) {
        task.execute();
    }
}
