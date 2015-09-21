package com.chenjj.java8.lambda;

public class MyNumberImpl{

	public static void main(String[] args) {
		MyNumber myNumber = () -> 123.45;
		System.out.println(myNumber.getValue());
	}
}
