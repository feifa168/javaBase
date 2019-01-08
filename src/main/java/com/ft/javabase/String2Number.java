package com.ft.javabase;

import java.security.InvalidParameterException;

public class String2Number {
    public static int conver(String s, int radix) throws InvalidParameterException {
        if (s==null || s.isEmpty()) {
            throw new InvalidParameterException("string is not valid param");
        }

        if (radix<2 || radix>36) {
            throw new InvalidParameterException("radix["+radix+"] is not valid param");
        }

        boolean negative = false;
        int len = s.length();

        int i = 0;
        int result = 0;
        char c = s.charAt(0);
        if (c == '-') {
            negative = true;
            i++;
        } else if (c == '+') {
            i++;
        }

        while (i<len) {
            c = s.charAt(i++);

            // 越界判断
            if (result*radix < result) {
                //throw new NumberFormatException("not invalid number");
                throw new InvalidParameterException("radix " + radix + " conv string " + s + " is not valid param");
            }

            int t = 0;
            if ((c>='0' && c<='9')) {
                t = c-'0';
            }
            else if (radix > 10) {
                if ((c >= 'a' && c < ('a'+radix-10))) {
                    t = c - 'a' + 10;
                } else if ((c >= 'A' && c < ('A'+radix-10))) {
                    t = c - 'A' + 10;
                } else {
                    break;
                }
            }

            result *= radix;
            result += t;
        }

        return negative ? (-result) : result;
    }
}
