package Task8.Parser;

import Task8.Ordinal.Ordinal;

/**
 * Created by izban on 30.05.2016.
 */
class OrdinalLexer {
    private final String s;
    private int it;

    OrdinalLexer(String s) {
        this.s = s.replace(" ", "");
        it = 0;
        nextToken();
    }

    private String cur;
    String curToken() {
        return cur;
    }

    void nextToken() {
        cur = next();
    }

    OrdinalToken curType() {
        return OrdinalToken.type(cur);
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
                long ans = 0;
                while (it < s.length() && Character.isDigit(s.charAt(it))) {
                    ans = 10 * ans + (s.charAt(it++) - '0');
                }
                return Long.toString(ans);
        }
    }
}
