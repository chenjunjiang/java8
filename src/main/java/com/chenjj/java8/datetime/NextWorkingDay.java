package com.chenjj.java8.datetime;

import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

/**
 * 该类实现了TemporalAdjuster接口，能够计算明天
 * 的日期，同时过滤掉周六和周日这些节假日。
 */
public class NextWorkingDay implements TemporalAdjuster {
    @Override
    public Temporal adjustInto(Temporal temporal) {
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
    }
}
