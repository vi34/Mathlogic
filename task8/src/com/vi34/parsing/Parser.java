package com.vi34.parsing;

import com.vi34.parsing.tree.*;

import static com.vi34.parsing.Token.*;

/**
 * Created by vi34 on 29.10.2016.
 */
public class Parser {
    private Lexer lexer;

    private Node exp() {
        Token cur = lexer.curType();
        if (cur == LEFT_BRACKET) {
            lexer.nextToken();
            Node res = expr();
            if (lexer.curType() != RIGHT_BRACKET) throw new AssertionError();
            lexer.nextToken();
            return res;
        } if (cur == NUM || cur == W) {
            Node res = new Const(lexer.currentToken());
            lexer.nextToken();
            return res;
        } else {
            System.err.println(cur);
            throw new AssertionError();
        }
    }

    private Node mul() {
        Node res = exp();
        Token cur = lexer.curType();
        if (cur == EXP) {
            lexer.nextToken();
            return new Exp(res, mul());
        }
        return res;
    }

    private Node sum() {
        Node res = mul();
        while (true) {
            Token cur = lexer.curType();
            if (cur == MUL) {
                lexer.nextToken();
                res = new Multiply(res, mul());
            } else break;
        }
        return res;
    }

    private Node expr() {
        Node res = sum();
        while (true) {
            Token cur = lexer.curType();
            if (cur != ADD && cur != SUB) {
                break;
            }
            lexer.nextToken();
            if (cur == ADD) {
                res = new Add(res, sum());
            }
            if (cur == SUB) {
                res = new Sub(res, sum());
            }
        }
        return res;
    }

    public Node parseOrdinal(String s) {
        lexer = new Lexer(s);
        return expr();
    }
}
