package Task8.Parser;

import Task8.Ordinal.Ordinal;
import Task8.Tree.*;

import static Task8.Parser.OrdinalToken.*;

/**
 * Created by izban on 30.05.2016.
 */
public class OrdinalParser {
    private OrdinalLexer lexer;

    private Node parseExponent() {
        OrdinalToken cur = lexer.curType();
        if (cur == LEFT_BRACKET) {
            lexer.nextToken();
            Node res = parseExpression();
            if (lexer.curType() != RIGHT_BRACKET) throw new AssertionError();
            lexer.nextToken();
            return res;
        } if (cur == NUMBER || cur == W) {
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
        OrdinalToken cur = lexer.curType();
        if (cur == EXPONENT) {
            lexer.nextToken();
            return new NodeExp(res, parseMultiplier());
        }
        return res;
    }

    private Node parseSummand() {
        Node res = parseMultiplier();
        while (true) {
            OrdinalToken cur = lexer.curType();
            if (cur == MULTIPLY) {
                lexer.nextToken();
                res = new NodeMultiply(res, parseMultiplier());
            } else break;
        }
        return res;
    }

    private Node parseExpression() {
        Node res = parseSummand();
        while (true) {
            OrdinalToken cur = lexer.curType();
            if (cur != ADD && cur != SUBTRACT) {
                break;
            }
            lexer.nextToken();
            if (cur == ADD) {
                res = new NodeAdd(res, parseSummand());
            }
            if (cur == SUBTRACT) {
                res = new NodeSubtract(res, parseSummand());
            }
        }
        return res;
    }

    public Node parseOrdinal(String s) {
        lexer = new OrdinalLexer(s);
        return parseExpression();
    }
}
