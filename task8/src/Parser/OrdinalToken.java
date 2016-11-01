package Task8.Parser;

/**
 * Created by izban on 30.05.2016.
 */
public enum OrdinalToken {
    EQUAL,
    ADD,
    SUBTRACT,
    MULTIPLY,
    EXPONENT,
    W,
    NUMBER,
    LEFT_BRACKET,
    RIGHT_BRACKET;

    public static OrdinalToken type(String s) {
        switch (s) {
            case "=":
                return EQUAL;
            case "+":
                return ADD;
            case "-":
                return SUBTRACT;
            case "*":
                return MULTIPLY;
            case "^":
                return EXPONENT;
            case "w":
                return W;
            case "(":
                return LEFT_BRACKET;
            case ")":
                return RIGHT_BRACKET;
            default:
                boolean ok = true;
                for (int i = 0; i < s.length(); i++) ok &= Character.isDigit(s.charAt(i));
                if (ok) return NUMBER;
                throw new AssertionError();
        }
    }
}
