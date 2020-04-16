package com.chenjj.java8.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

/**
 * https://blog.csdn.net/u013604031/article/details/49812941
 * https://www.cnblogs.com/theRhyme/p/9756154.html
 */
public class DateTimeExamples {
    public static void main(String[] args) {
        /**
         * LocalDate
         * 该类的实例是一个不可变对象，它只提供了简单的日期，并不含当天的时间信息。
         * 另外，它也不附带任何与时区相关的信息。
         */
        // 创建一个LocalDate对象并读取其值
        LocalDate date = LocalDate.of(2020, 4, 13);
        int year = date.getYear();
        System.out.println("year:" + year);
        Month month = date.getMonth();
        System.out.println("month:" + month.getValue());
        int day = date.getDayOfMonth();
        System.out.println("day:" + day);
        int len = date.lengthOfMonth();
        System.out.println("len:" + len);
        // 闰年
        boolean leap = date.isLeapYear();
        System.out.println("leap:" + leap);
        // 获取当前日期
        LocalDate now = LocalDate.now();
        System.out.println(now);
        // 使用TemporalField读取LocalDate的值
        year = date.get(ChronoField.YEAR);
        int mon = date.get(ChronoField.MONTH_OF_YEAR);
        day = date.get(ChronoField.DAY_OF_MONTH);

        /**
         * 一天中的时间，比如13:45:20，可以使用LocalTime类表示
         */
        LocalTime time = LocalTime.of(13, 45, 20);
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        System.out.println(hour + ":" + minute + ":" + second);

        /**
         * LocalDate和LocalTime都可以通过解析代表它们的字符串创建
         */
        date = LocalDate.parse("2014-03-18");
        time = LocalTime.parse("13:45:20");

        /**
         * 合并日期和时间
         * 这个复合类名叫LocalDateTime，是LocalDate和LocalTime的合体。它同时表示了日期
         * 和时间，但不带有时区信息，你可以直接创建，也可以通过合并日期和时间对象构造
         */
        // 直接创建LocalDateTime对象，或者通过合并日期和时间的方式创建
        LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 20);
        System.out.println(dt1);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        LocalDateTime dt3 = date.atTime(13, 45, 25);
        LocalDateTime dt4 = date.atTime(time);
        LocalDateTime dt5 = time.atDate(date);
        // 可以使用toLocalDate或者toLocalTime方法，从LocalDateTime中提取LocalDate或者LocalTime组件
        LocalDate date1 = dt1.toLocalDate();
        LocalTime time1 = dt1.toLocalTime();
        LocalTime time2 = dt2.toLocalTime();

        /**
         * 机器的日期和时间格式
         * 作为人，我们习惯于以星期几、几号、几点、几分这样的方式理解日期和时间。毫无疑问，
         * 这种方式对于计算机而言并不容易理解。从计算机的角度来看，建模时间最自然的格式是表示一
         * 个持续时间段上某个点的单一大整型数。这也是新的java.time.Instant类对时间建模的方
         * 式，基本上它是以Unix元年时间（传统的设定为UTC时区1970年1月1日午夜时分）开始所经历的
         * 秒数进行计算。
         */
        // 当前时刻的时间戳(毫秒数)
        System.out.println(Instant.now().toEpochMilli());
        // 当前时刻的时间戳(秒数)
        System.out.println(Instant.now().getEpochSecond());
        // 通过向静态工厂方法ofEpochSecond传递一个代表秒数的值创建一个该类的实例
        Instant instant = Instant.ofEpochSecond(3);
        System.out.println(instant.getEpochSecond());// 3
        System.out.println(instant); // 1970-01-01T00:00:03Z
        // 4秒 之 后 再 加 上100万纳秒（ 1秒）
        Instant instant1 = Instant.ofEpochSecond(4, 1_000_000_000);
        // 4秒之前的100万纳秒（ 1秒）
        // Instant instant1 = Instant.ofEpochSecond(4,-1_000_000_000);
        System.out.println(instant1); // 1970-01-01T00:00:05Z
        // Instant的设计初衷是为了便于机器使用。它包含的是由秒及纳秒所构成的数字。所以，它无法处理那些我们非常容易理解的时间单位
        // java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: DayOfMonth
        // day = Instant.now().get(ChronoField.DAY_OF_MONTH);

        time1 = LocalTime.of(13, 45, 20);
        time2 = LocalTime.of(13, 45, 25);
        // 时间差
        Duration duration = Duration.between(time1, time2);
        System.out.println(duration.getSeconds());// 5
        duration = Duration.between(dt1, dt3);
        System.out.println(duration.getSeconds());// 5
        Duration d2 = Duration.between(instant1, instant);
        System.out.println(d2.getSeconds());// -2
        /**
         * 由于LocalDateTime和Instant是为不同的目的而设计的，一个是为了便于人阅读使用，
         * 另一个是为了便于机器处理，所以你不能将二者混用。如果你试图在这两类对象之间创建
         * duration，会触发一个DateTimeException异常。此外，由于Duration类主要用于以秒和纳
         * 秒衡量时间的长短，你不能仅向between方法传递一个LocalDate对象做参数。
         */
        // 如果你需要以年、月或者日的方式对多个时间单位建模，可以使用Period类。使用该类的工厂方法between，你可以使用得到两个LocalDate之间的时长
        Period thenDays = Period.between(LocalDate.of(2014, 3, 18), LocalDate.of(2014, 3, 8));
        System.out.println(thenDays.getDays());// -10
        Duration threeMinutes = Duration.ofMinutes(3);
        // threeMinutes = Duration.of(3, ChronoUnit.MINUTES);
        System.out.println(threeMinutes.getSeconds());// 180
        Period tenDays = Period.ofDays(10);
        Period threeWeeks = Period.ofWeeks(3);
        Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);

        /**
         * 操纵、解析和格式化日期
         * 如果你已经有一个LocalDate对象，想要创建它的一个修改版，最直接也最简单的方法是使
         * 用withAttribute方法。 withAttribute方法会创建对象的一个副本，并按照需要修改它的属
         * 性。注意，下面的这段代码中所有的方法都返回一个修改了属性的对象。它们都不会修改原来的
         * 对象！所有的日期和时间API类都实现这两个方法，它
         * 们定义了单点的时间，比如LocalDate、 LocalTime、 LocalDateTime以及Instant。
         *如果Temporal对象不支持请求访问的字段，它会抛出一个UnsupportedTemporalTypeException异常，比
         * 如 试 图 访 问Instant 对 象 的ChronoField.MONTH_OF_YEAR 字 段 ， 或 者LocalDate 对 象 的
         * ChronoField.NANO_OF_SECOND字段时都会抛出这样的异常。
         */
        LocalDate date2 = LocalDate.of(2014, 3, 18);
        LocalDate date3 = date2.withYear(2011);
        LocalDate date4 = date3.withDayOfMonth(25);
        LocalDate date5 = date4.with(ChronoField.MONTH_OF_YEAR, 9);
        System.out.println(date5); // 2011-09-25
        date1 = LocalDate.of(2014, 3, 18);
        date2 = date1.plusWeeks(1);
        date3 = date2.minusYears(3);
        date4 = date3.plus(6, ChronoUnit.MONTHS);

        /**
         * 有的时候，你需要进行一些更加复杂的操作，比如，将日期调整到下个周日、下个工作日，
         * 或者是本月的最后一天。这时，你可以使用重载版本的with方法，向其传递一个提供了更多定制化选择的TemporalAdjuster对象，
         * 更 加 灵 活 地 处 理 日 期 。 对 于 最 常 见 的 用 例 ， 日 期 和 时 间 API 已 经 提 供 了 大 量 预 定 义 的
         * TemporalAdjuster。你可以通过TemporalAdjuster类的静态工厂方法访问它们
         */
        LocalDate date6 = LocalDate.of(2020, 4, 16);
        LocalDate date7 = date6.with(nextOrSame(DayOfWeek.SATURDAY));
        LocalDate date8 = date7.with(lastDayOfMonth());
        System.out.println(date7); // 2020-04-18
        System.out.println(date8); // 2020-04-30
        /**
         * TemporalAdjuster中包含的工厂方法列表
         * 方法名                    描 述
         * dayOfWeekInMonth         创建一个新的日期，它的值为同一个月中每一周的第几天
         * firstDayOfMonth          创建一个新的日期，它的值为当月的第一天
         * firstDayOfNextMonth      创建一个新的日期，它的值为下月的第一天
         * firstDayOfNextYear       创建一个新的日期，它的值为明年的第一天
         * firstDayOfYear           创建一个新的日期，它的值为当年的第一天
         * firstInMonth             创建一个新的日期，它的值为同一个月中，第一个符合星期几要求的值
         * lastDayOfMonth           创建一个新的日期，它的值为当月的最后一天
         * lastDayOfNextMonth       创建一个新的日期，它的值为下月的最后一天
         * lastDayOfNextYear        创建一个新的日期，它的值为明年的最后一天
         * lastDayOfYear            创建一个新的日期，它的值为今年的最后一天
         * lastInMonth              创建一个新的日期，它的值为同一个月中，最后一个符合星期几要求的值
         * next/previous            创建一个新的日期，并将其值设定为日期调整后或者调整前，第一个符合指定星期几要求的日期
         * nextOrSame/previousOrSame  创建一个新的日期，并将其值设定为日期调整后或者调整前，第一个符合指定星
         * 期几要求的日期，如果该日期已经符合要求，直接返回该对象
         */

        /**
         * 自定义TemporalAdjuster
         * @FunctionalInterface
         * public interface TemporalAdjuster {
         *     Temporal adjustInto(Temporal temporal);
         * }
         */
        LocalDate date9 = LocalDate.now();
        LocalDate date10 = date9.with(new NextWorkingDay());
        System.out.println(date10); // 2020-04-17
        /**
         * 如果你想要使用Lambda表达式定义TemporalAdjuster对象，推荐使用TemporalAdjusters类的静态工厂方法ofDateAdjuster，
         * 它接受一个UnaryOperator<LocalDate>类型的参数
         */
        TemporalAdjuster temporalAdjuster = TemporalAdjusters.ofDateAdjuster(temporal -> {
            // 获取temporal所代表的日期
            DayOfWeek dayOfWeek = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            // 正在情况加1天即可
            int dayToAdd = 1;
            // 如果是周五则需要加3天才到工作日(周一)
            if (dayOfWeek == DayOfWeek.FRIDAY) {
                dayToAdd = 3;
            } else if (dayOfWeek == DayOfWeek.SATURDAY) {
                // 如果是周六则需要加2天才到工作日(周一)
                dayToAdd = 2;
            }
            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        });
        date9.with(temporalAdjuster);

        /**
         * 格式化以及解析日期时间对象
         */
        LocalDate date11 = LocalDate.of(2014, 3, 18);
        String s1 = date11.format(DateTimeFormatter.BASIC_ISO_DATE);
        String s2 = date11.format(DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.println(s1); // 20140318
        System.out.println(s2); // 2014-03-18
        // 通过解析代表日期或时间的字符串重新创建该日期对象，字符串和formatter不一致会抛异常
        LocalDate date12 = LocalDate.parse("20140318", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate date13 = LocalDate.parse("2014-03-18", DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.println(date12);
        /**
         * 和老的java.util.DateFormat相比较，所有的DateTimeFormatter实例都是线程安全
         * 的。所以，你能够以单例模式创建格式器实例，就像DateTimeFormatter所定义的那些常量，
         * 并能在多个线程间共享这些实例。 DateTimeFormatter类还支持一个静态工厂方法，它可以按
         * 照某个特定的模式创建格式器
         */
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate date14 = LocalDate.of(2014, 3, 18);
        String formattedDate = date14.format(dateTimeFormatter);
        System.out.println(formattedDate); // 18/03/14
        LocalDate date15 = LocalDate.parse(formattedDate, dateTimeFormatter);
        System.out.println(date15);
        // 创建一个本地化的DateTimeFormatter
        DateTimeFormatter italianFormatter =
                DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
        LocalDate date16 = LocalDate.of(2014, 3, 18);
        formattedDate = date16.format(italianFormatter);
        // marzo是意大利语的三月
        System.out.println(formattedDate); // 18. marzo 2014
        /**
         * 如果你还需要更加细粒度的控制， DateTimeFormatterBuilder类还提供了更复杂
         * 的格式器，你可以选择恰当的方法，一步一步地构造自己的格式器。
         */
        italianFormatter = new DateTimeFormatterBuilder().appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive() // 不区分大小写
                .toFormatter(Locale.ITALIAN);
        LocalDate date17 = LocalDate.of(2014, 3, 18);
        formattedDate = date17.format(italianFormatter);
        System.out.println(formattedDate);

        /**
         * 处理不同的时区和历法
         * java.time.ZoneId的设计目标就是要让你无需为时区处理的复杂和
         * 繁琐而操心，跟其他日期和时间类一样， ZoneId类也是无法修改的。
         */
        // 为时间点添加时区信息
        ZoneId shanghaiZone = ZoneId.of("Asia/Shanghai");
        ZoneId romeZone = ZoneId.of("Europe/Rome");
        LocalDate date18 = LocalDate.now();
        // ZonedDateTime zonedDateTime = date18.atStartOfDay(shanghaiZone);
        // Asia/Shanghai时区比UTC/格林尼治时间多8个小时
        // System.out.println(zonedDateTime); // 2020-04-16T00:00+08:00[Asia/Shanghai]
        ZonedDateTime zonedDateTime = date18.atStartOfDay(romeZone);
        // Europe/Rome时区比UTC/格林尼治时间多2个小时
        System.out.println(zonedDateTime); // 2020-04-16T00:00+02:00[Europe/Rome]
        LocalDateTime localDateTime = LocalDateTime.now();
        zonedDateTime = localDateTime.atZone(romeZone);
        System.out.println(zonedDateTime); // 2020-04-16T18:21:58.359+02:00[Europe/Rome]
        zonedDateTime = localDateTime.atZone(shanghaiZone);
        System.out.println(zonedDateTime); // 2020-04-16T18:21:58.359+08:00[Asia/Shanghai]
        Instant instant2 = Instant.now();
        // 默认的Instant是0时区
        System.out.println(instant2); // 2020-04-16T10:34:26.348Z
        zonedDateTime = instant2.atZone(shanghaiZone);
        System.out.println(zonedDateTime); // 2020-04-16T18:21:58.359+08:00[Asia/Shanghai]

        LocalDateTime localDateTime1 = LocalDateTime.now();
        // 包含时差信息的日期和时间
        ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime1, newYorkOffset);
        System.out.println(localDateTime1);
        System.out.println(offsetDateTime); // 2020-04-16T18:48:55.038-05:00
    }
}
