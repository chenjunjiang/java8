package com.chenjj.java8.reconstruction;

/**
 * 希望验证输入的内容是否根据标准进行了恰当的格式化（比如只包含小写字母或数字）。
 */
public interface ValidationStrategy {
    boolean execute(String str);
}
