package Task8.Ordinal;

import Task8.Parser.OrdinalParser;
import static Task8.Ordinal.OrdinalType.CONSTANT;

/**
 * Created by izban on 30.05.2016.
 */
public class Ordinal {
    private final OrdinalType type;
    private final long value;
    private final Ordinal l;
    private final Ordinal r; // (l.first*w^l.second + r, where r.l.second<l.second)

    public Ordinal(long value) {
        type = CONSTANT;
        this.value = value;
        l = r = null;
    }

    public Ordinal(Ordinal l, Ordinal r) {
        type = OrdinalType.NOTCONSTANT;
        this.l = l;
        this.r = r;
        this.value = -1;
    }

    public final static Ordinal ZERO = new Ordinal(0);
    public final static Ordinal ONE = new Ordinal(1);

    @Override
    public String toString() {
        return toString(this);
    }

    private static String toString(Ordinal a) {
        if (a.type == CONSTANT) {
            return Long.toString(a.value);
        }
        StringBuilder res = new StringBuilder();
        res.append("w^(").append(toString(a.l.l)).append(")*").append(a.l.r.toString());
        if (a.r != null) {
            res.append("+").append(toString(a.r));
        }
        return res.toString();
    }

    private static Ordinal first(Ordinal a) {
        if (a.type == CONSTANT) throw new AssertionError();
        return a.l;
    }

    private static Ordinal rest(Ordinal a) {
        if (a.type == CONSTANT) throw new AssertionError();
        return a.r;
    }

    private static Ordinal firstn(Ordinal a, long n) {
        if (n == 0) return null;
        return new Ordinal(first(a), firstn(rest(a), n - 1));
    }

    private static Ordinal restn(Ordinal a, long n) {
        if (n == 0) return a;
        return restn(rest(a), n - 1);
    }

    private static boolean atom(Ordinal a) {
        return a == null || a.type == CONSTANT;
    }

    private static Ordinal fe(Ordinal a) {
        if (atom(a)) {
            return new Ordinal(0);
        }
        return first(first(a));
    }

    private static Ordinal fc(Ordinal a) {
        if (atom(a)) {
            return a;
        }
        return rest(first(a));
    }

    private static long length(Ordinal a) {
        if (atom(a)) {
            return 0;
        }
        return 1 + length(rest(a));
    }

    private static long size(Ordinal a) {
        if (atom(a)) {
            return 1;
        }
        return size(fe(a)) + size(rest(a));
    }

    private static Ordinal concat(Ordinal a, Ordinal b) {
        if (atom(a)) {
            return b;
        }
        return new Ordinal(first(a), concat(rest(a), b));
    }

    private static int cmpw(Ordinal a, Ordinal b) {
        if (!(a.type == CONSTANT && b.type == CONSTANT)) {
            throw new AssertionError();
        }
        return Long.compare(a.value, b.value);
    }

    static int cmpo(Ordinal a, Ordinal b) {
        if (atom(a) && atom(b)) return cmpw(a, b);
        if (atom(a)) return -1;
        if (atom(b)) return 1;
        int res = cmpo(fe(a), fe(b));
        if (res != 0) return res;
        res = cmpw(fc(a), fc(b));
        if (res != 0) return res;
        return cmpo(rest(a), rest(b));
    }

    private static boolean less(Ordinal a, Ordinal b) {
        return cmpo(a, b) < 0;
    }

    public static Ordinal add(Ordinal a, Ordinal b) {
        if (atom(a) && atom(b)) {
            return new Ordinal(a.value + b.value);
        }
        int res = cmpo(fe(a), fe(b));
        if (res < 0) return b;
        if (res == 0) return new Ordinal(new Ordinal(fe(a), add(fc(a), fc(b))), rest(b));
        return new Ordinal(new Ordinal(fe(a), fc(a)), add(rest(a), b));
    }

    public static Ordinal subtract(Ordinal a, Ordinal b) {
        if (atom(a) && atom(b) && a.value <= b.value) return new Ordinal(0);
        if (atom(a) && atom(b)) return new Ordinal(a.value - b.value);
        int res = cmpo(fe(a), fe(b));
        if (res < 0) return new Ordinal(0);
        if (res > 0) return a;
        if (less(fc(a), fc(b))) return new Ordinal(0);
        if (less(fc(b), fc(a))) return new Ordinal(new Ordinal(fe(a), subtract(fc(a), fc(b))), rest(a));
        return subtract(rest(a), rest(b));
    }

    private static long c(Ordinal a, Ordinal b) {
        if (less(fe(b), fe(a))) return 1 + c(rest(a), b);
        return 0;
    }

    private static long count(Ordinal a, Ordinal b, long n) {
        return n + c(restn(a, n), b);
    }

    private static Ordinal padd(Ordinal a, Ordinal b, long n) {
        return concat(firstn(a, n), add(restn(a, n), b));
    }

    private static Ordinal pmult(Ordinal a, Ordinal b, long n) {
        if (a.type == CONSTANT && a.value == 0 || b.type == CONSTANT && b.value == 0) return new Ordinal(0);
        if (atom(a) && atom(b)) return new Ordinal(a.value * b.value);
        if (atom(b)) return new Ordinal(new Ordinal(fe(a), new Ordinal(fc(a).value * b.value)), rest(a));
        long m = count(fe(a), fe(b), n);
        return new Ordinal(new Ordinal(padd(fe(a), fe(b), m), fc(b)), pmult(a, rest(b), m));
    }

    public static Ordinal multiply(Ordinal a, Ordinal b) {
        return pmult(a, b, 0);
    }

    private static long bin(long n, long k) {
        if (k == 0) return 1;
        long res = bin(n, k / 2);
        res = res * res;
        if (k % 2 == 1) res = res * n;
        return res;
    }

    private static Ordinal expw(Ordinal a, Ordinal b) {
        if (a.type != CONSTANT || b.type != CONSTANT) {
            throw new AssertionError();
        }
        return new Ordinal(bin(a.value, b.value));
    }

    private static Ordinal exp1(Ordinal p, Ordinal b) {
        if (cmpo(fe(b), ONE) == 0) return new Ordinal(new Ordinal(fc(b), expw(p, rest(b))), ZERO);
        if (atom(rest(b))) return new Ordinal(new Ordinal(new Ordinal(new Ordinal(subtract(fe(b), ONE), fc(b)), ZERO), expw(p, rest(b))), ZERO);
        Ordinal c = exp1(p, rest(b));
        return new Ordinal(new Ordinal(new Ordinal(new Ordinal(subtract(fe(b), ONE), ONE), fe(c)), fc(c)), ZERO);
    }

    private static Ordinal exp2(Ordinal a, Ordinal q) {
        if (cmpo(q, ONE) == 0) return a;
        return multiply(new Ordinal(new Ordinal(multiply(fe(a), subtract(q, ONE)), ONE), ZERO), a);
    }

    private static boolean limitp(Ordinal a) {
        if (atom(a)) return a.value == 0;
        return limitp(rest(a));
    }

    private static Ordinal limitpart(Ordinal a) {
        if (atom(a)) return ZERO;
        return new Ordinal(first(a), limitpart(rest(a)));
    }

    private static Ordinal natpart(Ordinal a) {
        if (atom(a)) return a;
        return natpart(rest(a));
    }

    private static Ordinal helper(Ordinal a, Ordinal p, long n, Ordinal q) {
        if (cmpo(q, ZERO) == 0) return p;
        return padd(multiply(exp2(a, q), p), helper(a, p, n, subtract(q, ONE)), n);
    }

    private static Ordinal exp3(Ordinal a, Ordinal q) {
        if (cmpo(q, ZERO) == 0) return ONE;
        if (cmpo(q, ONE) == 0) return a;
        if (limitp(a)) return exp2(a, q);
        Ordinal c = limitpart(a);
        long n = length(a);
        return padd(firstn(exp2(c, q), n), helper(c, natpart(a), n, subtract(q, ONE)), n);
    }

    private static Ordinal exp4(Ordinal a, Ordinal b) {
        return multiply(new Ordinal(new Ordinal(multiply(fe(a), limitpart(b)), ONE), ZERO), exp3(a, natpart(b)));
    }

    public static Ordinal exp(Ordinal a, Ordinal b) {
        if (cmpo(b, ZERO) == 0 || cmpo(a, ONE) == 0) return ONE;
        if (cmpo(a, ZERO) == 0) return ZERO;
        if (atom(a) && atom(b)) return expw(a, b);
        if (atom(a)) return exp1(a, b);
        if (atom(b)) return exp3(a, b);
        return exp4(a, b);
    }

    public static Ordinal parseOrdinal(String b) {
        return new OrdinalParser().parseOrdinal(b).calcValue();
    }
}
