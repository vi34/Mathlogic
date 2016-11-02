package com.vi34.parsing;

/**
 * Created by vi34 on 29.10.2016.
 */
public class Lexer {
    private final String s;
    private String current;
    private int it;

    Lexer(String s) {
        this.s = s.replace(" ", "");
        it = 0;
        nextToken();
    }

    void nextToken() {
        current = next();
    }

    String currentToken() {
        return current;
    }

    Token curType() {
        return Token.type(current);
    }

    private String next() {
        if (it == s.length()) {
            it++;
            return "";
        }
        if (it > s.length()) {
            throw new AssertionError();
        }
        switch (s.charAt(it)) {
            case '=':
            case '+':
            case '-':
            case '*':
            case '^':
            case 'w':
            case '(':
            case ')':
                return String.valueOf(s.charAt(it++));
            default:
                if (!Character.isDigit(s.charAt(it))) {
                    throw new AssertionError();
                }
                long val = 0;
                while (it < s.length() && Character.isDigit(s.charAt(it))) {
                    val = 10 * val + (s.charAt(it++) - '0');
                }
                return Long.toString(val);
        }
    }
}
