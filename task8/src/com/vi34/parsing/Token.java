package com.vi34.parsing;

/**
 * Created by vi34 on 29.10.2016.
 */
public enum Token {
    ADD,
    SUB,
    MUL,
    EXP,
    EQ,
    W,
    NUM,
    LEFT_BRACKET,
    RIGHT_BRACKET;

    public static Token type(String s) {
        switch (s) {
            case "=":
                return EQ;
            case "+":
                return ADD;
            case "-":
                return SUB;
            case "*":
                return MUL;
            case "^":
                return EXP;
            case "w":
                return W;
            case "(":
                return LEFT_BRACKET;
            case ")":
                return RIGHT_BRACKET;
            default:
                if (s.matches("^[0-9]*$")) {
                    return NUM;
                }
                throw new AssertionError();
        }
    }
}
