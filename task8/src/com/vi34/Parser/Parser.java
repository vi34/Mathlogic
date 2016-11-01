package com.vi34.Parser;

import com.vi34.Tree.*;

import static com.vi34.Parser.Tokens.*;

/**
 * Created by vi34 on 29.10.2016.
 */
public class Parser {
    private Lexer lexer;

    private Node parseExponent() {
        Tokens cur = lexer.curType();
        if (cur == LEFT_BRACKET) {
            lexer.nextToken();
            Node res = parseExpression();
            if (lexer.curType() != RIGHT_BRACKET) throw new AssertionError();
            lexer.nextToken();
            return res;
        } if (cur == NUM || cur == W) {
            Node res = new NodeConstant(lexer.curToken());
            lexer.nextToken();
            return res;
        } else {
            System.err.println(cur);
            throw new AssertionError();
        }
    }

    private Node parseMultiplier() {
        Node res = parseExponent();
        Tokens cur = lexer.curType();
        if (cur == EXP) {
            lexer.nextToken();
            return new NodeExp(res, parseMultiplier());
        }
        return res;
    }

    private Node parseSummand() {
        Node res = parseMultiplier();
        while (true) {
            Tokens cur = lexer.curType();
            if (cur == MUL) {
                lexer.nextToken();
                res = new NodeMultiply(res, parseMultiplier());
            } else break;
        }
        return res;
    }

    private Node parseExpression() {
        Node res = parseSummand();
        while (true) {
            Tokens cur = lexer.curType();
            if (cur != ADD && cur != SUB) {
                break;
            }
            lexer.nextToken();
            if (cur == ADD) {
                res = new NodeAdd(res, parseSummand());
            }
            if (cur == SUB) {
                res = new NodeSubtract(res, parseSummand());
            }
        }
        return res;
    }

    public Node parseOrdinal(String s) {
        lexer = new Lexer(s);
        return parseExpression();
    }
}
