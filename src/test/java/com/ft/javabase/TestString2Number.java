package com.ft.javabase;

import org.junit.Test;

import java.security.InvalidParameterException;

public class TestString2Number {
    private void testString2Number(String s, int radix) {
        try {
            int result = String2Number.conver(s, radix);
            System.out.println("radix " + radix + " from " + s + " to " + result);
        } catch (InvalidParameterException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testIntParser() {
        testString2Number("1F", 16);
        testString2Number("1g", 16);
        testString2Number("1g", 17);
        testString2Number("1h", 17);
        testString2Number("1h", 18);
        testString2Number("1i", 18);
        testString2Number("+7fffffff", 16);
        testString2Number("-7fffffff", 16);
        testString2Number("8fffffff", 16);
        testString2Number("-8fffffff", 16);
        testString2Number("12", 10);
        testString2Number("-12", 10);

        try {
            //System.out.println(Integer.parseInt("1h", 16));
            System.out.println(Integer.parseInt("1g", 17));
            System.out.println(Integer.parseInt("j", 20));
            System.out.println(Integer.parseInt("1a", 32));
            System.out.println(Integer.parseInt("1v", 32));
            System.out.println(Integer.parseInt("7fffffff", 16));
            System.out.println(Integer.parseInt("-7fffffff", 16));
            System.out.println(Integer.parseInt("-8fffffff", 16));
            System.out.println(Integer.parseInt("8fffffff", 16));
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }
}
