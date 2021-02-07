package com.chenjj.java8.optional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * 如果发现自己在编写的方法始终无法返回值，并且相信该方法的用户每次
 * 在调用它时都要考虑到这种可能性，那么或许就应该返回一个 optional 。
 * 但是，应当注意到与返回 optional 相关的真实的性能影响；
 * 对于注重性能的方法，最好是返回一个 null ，或者抛出异常 。
 * 最后，尽量不要将 optional 用作返回值以外的任何其他用途 。
 * <p>
 * 声明方法接受一个Optional参数，或者将结果作为Optional类型返回，让
 * 你的同事或者未来你方法的使用者，很清楚地知道它可以接受空值，或者它可能返回一个空值。
 * <p>
 * 由于Optional类设计时就没特别考虑将其作为类的字段使用，所以它也并未实现
 * Serializable接口。由于这个原因，如果你的应用使用了某些要求序列化的库或者框架，在
 * 域模型中使用Optional，有可能引发应用程序故障。
 * <p>
 * Optional类的方法
 * 方 法                  描 述
 * empty                 返回一个空的 Optional 实例
 * filter                如果值存在并且满足提供的谓词，就返回包含该值的 Optional 对象；否则返回一个空的Optional 对象
 * flatMap               如果值存在，就对该值执行提供的 mapping 函数调用，返回一个 Optional 类型的值，否则就返回一个空的 Optional 对象
 * get                   如果该值存在，将该值用 Optional 封装返回，否则抛出一个 NoSuchElementException 异常
 * ifPresent             如果值存在，就执行使用该值的方法调用，否则什么也不做
 * isPresent             如果值存在就返回 true，否则返回 false
 * map                   如果值存在，就对该值执行提供的 mapping 函数调用
 * of                   将指定值用 Optional 封装之后返回，如果该值为 null，则抛出一个 NullPointerException异常
 * ofNullable           将指定值用 Optional 封装之后返回，如果该值为 null，则返回一个空的 Optional 对象
 * orElse               如果有值则将其返回，否则返回一个默认值
 * orElseGet            如果有值则将其返回，否则返回一个由指定的 Supplier 接口生成的值
 * orElseThrow          如果有值则将其返回，否则抛出一个由指定的 Supplier 接口生成的异常
 */
public class OptionalMain {
    public static void main(String[] args) {
        // 声明一个空的Optional
        Optional<Car> optionalCar = Optional.empty();
        // 根据一个非空值创建Optional,如果car是一个null，这段代码会立即抛出一个NullPointerException，而不是等到你
        //试图访问car的属性值时才返回一个错误。
        Car car = new Car();
        optionalCar = Optional.of(car);
        // 可接受null的Optional,如果car是null，那么得到的Optional对象就是个空对象。
        car = null;
        optionalCar = Optional.ofNullable(car);

        /**
         * 从insurance公司对象中提取公司的名称
         */
        Insurance insurance = new Insurance();
        String name = null;
        if (insurance != null) {
            insurance.setName("人保");
            name = insurance.getName();
        }
        // 使用Optional
        Optional<Insurance> optionalInsurance = Optional.ofNullable(insurance);
        /**
         *如果optionalInsurance包含一个值，那函数就将该值作为参数传递给map，对该值进行转换。如
         * 果optionalInsurance为空，就什么也不做。
         */
        Optional<String> optional = optionalInsurance.map(Insurance::getName);
        System.out.println(optional.isPresent());

        // 用Optional封装可能为null的值
        Map<String, Object> map = new HashMap<>();
        Optional<Object> value = Optional.ofNullable(map.get("key"));

        /**
         * 异常与 Optional 的对比
         * 由于某种原因，函数无法返回某个值，这时除了返回null， Java API比较常见的替代做法是
         * 抛出一个异常。这种情况比较典型的例子是使用静态方法Integer.parseInt(String)，将
         * String转换为int。在这个例子中，如果String无法解析到对应的整型，该方法就抛出一个
         * NumberFormatException。最后的效果是，发生String无法转换为int时，代码发出一个遭遇
         * 非法参数的信号，唯一的不同是，这次你需要使用try/catch 语句，而不是使用if条件判断来
         * 控制一个变量的值是否非空。
         * 你也可以用空的Optional对象，对遭遇无法转换的String时返回的非法值进行建模，这时
         * 你期望parseInt的返回值是一个optional。我们无法修改最初的Java方法，但是这无碍我们进
         * 行需要的改进，你可以实现一个工具方法，将这部分逻辑封装于其中，最终返回一个我们希望的
         * Optional对象
         */
        Optional<Integer> xx = stringToInt("xx");
        System.out.println(xx.isPresent());// false

        /**
         * 基础类型的Optional对象，以及为什么应该避免使用它们
         * 与 Stream 对 象 一样 ， Optional 也 提 供 了类 似的 基 础类
         * 型——OptionalInt、 OptionalLong以及OptionalDouble——所以stringToInt方法可
         * 以不返回Optional<Integer>，而是直接返回一个OptionalInt类型的对象。我们
         * 讨论过使用基础类型Stream的场景，尤其是如果Stream对象包含了大量元素，出于性能的考量，
         * 使用基础类型是不错的选择，但对Optional对象而言，这个理由就不成立了，因为Optional
         * 对象最多只包含一个值。我们不推荐大家使用基础类型的Optional，因为基础类型的Optional不支持map、
         * flatMap以及filter方法，而这些却是Optional类最有用的方法。此外，与Stream一样，
         * Optional对象无法由基础类型的Optional组合构成，所以，举
         * 例而言，如果stringToInt方法返回的是OptionalInt类型的对象，你就不能将其作为方法引用传
         * 递给另一个Optional对象的flatMap方法。
         */

        Properties props = new Properties();
        props.setProperty("a", "5");
        props.setProperty("b", "true");
        props.setProperty("c", "-3");
        assertEquals(5, readDuration(props, "a"));
        assertEquals(0, readDuration(props, "b"));
        assertEquals(0, readDuration(props, "c"));
        assertEquals(0, readDuration(props, "d"));
    }

    /**
     * 将String转换为Integer，并返回一个Optional对象
     */
    public static Optional<Integer> stringToInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * 命令式编程的方式从属性中读取duration值
     *
     * @param props
     * @param name
     * @return
     */
    /*public static int readDuration(Properties props, String name) {
        String value = props.getProperty(name);
        if (value != null) {
            try {
                int i = Integer.parseInt(value);
                if (i > 0) {
                    return i;
                }
            } catch (NumberFormatException nfe) {
            }
        }
        return 0;
    }*/

    /**
     * 使用Optional从属性中读取duration
     */
    public static int readDuration(Properties props, String name) {
        return Optional
                .ofNullable(props.getProperty(name))
                .flatMap(OptionalMain::stringToInt)
                .filter(i -> i > 0)
                .orElse(0);
    }


    /**
     * 如果不用Optional
     * @param person
     * @return
     */
    /*public String getCarInsuranceName(Person person) {
        if (person == null) {
            return "Unknown";
        }
        Car car = person.getCar();
        if (car == null) {
            return "Unknown";
        }
        Insurance insurance = car.getInsurance();
        if (insurance == null) {
            return "Unknown";
        }
        return insurance.getName();
    }*/

    /**
     * 使用Optional
     * 可以看到，处理潜在可能缺失的值时，
     * 使用Optional具有明显的优势。这一次，你可以用非常容易却又普适的方法实现之前你期望的
     * 效果——不再需要使用那么多的条件分支，也不会增加代码的复杂性。
     *
     * @param person
     * @return
     */
    public String getCarInsuranceName(Person person) {
        Optional<Person> optionalPerson = Optional.of(person);
        // 使用map会有编译错误,因为optionalPerson.map后返回的是Optional<Optional<Car>>
        // optionalPerson.map(Person::getCar).map(Car::getInsurance);
        return optionalPerson
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown"); // 如 果Optional的结果值为空，设置默认值
    }

    /**
     * 这样实现感觉和if xxx!=null && yyy!=null 太类似了
     *
     * @param person
     * @param car
     * @return
     */
    /*public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
        if (person.isPresent() && car.isPresent()) {
            return Optional.of(findCheapestInsurance(person.get(), car.get()));
        } else {
            return Optional.empty();
        }
    }*/

    /**
     * 这种处理方式更好
     * <p>
     * 这段代码中，你对第一个Optional对象调用flatMap方法，如果它是个空值，传递给它
     * 的Lambda表达式不会执行，这次调用会直接返回一个空的Optional对象。反之，如果person
     * 对象存在，这次调用就会将其作为函数Function的输入，并按照与flatMap方法的约定返回
     * 一个Optional<Insurance>对象。这个函数的函数体会对第二个Optional对象执行map操
     * 作，如果第二个对象不包含car，函数Function就返回一个空的Optional对象，整个
     * nullSafeFindCheapestInsuranc方法的返回值也是一个空的Optional对象。最后，如果
     * person和car对象都存在，作为参数传递给map方法的Lambda表达式能够使用这两个值安全
     * 地调用原始的findCheapestInsurance方法，完成期望的操作。
     *
     * @param person
     * @param car
     * @return
     */
    public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
        return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
        // 不能用map，否则返回值是Optional<Optional<Insurance>>
        // person.map(p -> car.map(c -> findCheapestInsurance(p, c)));
    }

    /**
     * 找出年龄大于或者等于minAge参数的Person所对应的保险公司列表
     * <p>
     * filter方法接受一个谓词作为参数。 如果Optional对象的值存在，并且它符合谓词的条件，
     * filter方法就返回其值；否则它就返回一个空的Optional对象。
     *
     * @param person
     * @param minAge
     * @return
     */
    public String getCarInsuranceName(Optional<Person> person, int minAge) {
        return person
                .filter(p -> p.getAge() >= minAge)
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }

    private Insurance findCheapestInsurance(Person person, Car car) {
        Insurance insurance = new Insurance();
        return insurance;
    }
}
