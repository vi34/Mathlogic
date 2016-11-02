package com.vi34.ordinals;

/**
 * Created by vi34 on 02/11/2016.
 */
public class OrdinalCalc {
    public final static CNFOrdinal ZERO = new CNFOrdinal(0);
    public final static CNFOrdinal ONE = new CNFOrdinal(1);

    static int cmpo(CNFOrdinal a, CNFOrdinal b) {
        if (atom(a) && atom(b)) return cmpw(a, b);
        if (atom(a)) return -1;
        if (atom(b)) return 1;
        int res = cmpo(fe(a), fe(b));
        if (res != 0) return res;
        res = cmpw(fc(a), fc(b));
        if (res != 0) return res;
        return cmpo(a.rest(), b.rest());
    }


    private static CNFOrdinal firstn(CNFOrdinal a, long n) {
        if (n == 0) return null;
        return new CNFOrdinal(a.first(), firstn(a.rest(), n - 1));
    }

    private static CNFOrdinal restn(CNFOrdinal a, long n) {
        if (n == 0) return a;
        return restn(a.rest(), n - 1);
    }

    private static boolean atom(CNFOrdinal a) {
        return a == null || a.isAtom();
    }

    static CNFOrdinal fe(CNFOrdinal a) {
        if (atom(a)) {
            return new CNFOrdinal(0);
        }
        return a.first().first();
    }

    public static CNFOrdinal fc(CNFOrdinal a) {
        if (atom(a)) {
            return a;
        }
        return a.first().rest();
    }

    private static long length(CNFOrdinal a) {
        if (atom(a)) {
            return 0;
        }
        return 1 + length(a.rest());
    }

    private static long size(CNFOrdinal a) {
        if (atom(a)) {
            return 1;
        }
        return size(fe(a)) + size(a.rest());
    }

    private static CNFOrdinal concat(CNFOrdinal a, CNFOrdinal b) {
        if (atom(a)) {
            return b;
        }
        return new CNFOrdinal(a.first(), concat(a.rest(), b));
    }

    public static int cmpw(CNFOrdinal a, CNFOrdinal b) {
        if (!(a.isAtom() && b.isAtom())) {
            throw new AssertionError();
        }
        return Long.compare(a.getValue(), b.getValue());
    }

    private static CNFOrdinal padd(CNFOrdinal a, CNFOrdinal b, long n) {
        return concat(firstn(a, n), add(restn(a, n), b));
    }

    private static boolean less(CNFOrdinal a, CNFOrdinal b) {
        return cmpo(a, b) < 0;
    }

    private static long c(CNFOrdinal a, CNFOrdinal b) {
        if (less(fe(b), fe(a))) {
            return 1 + c(a.rest(), b);
        }
        return 0;
    }

    private static long count(CNFOrdinal a, CNFOrdinal b, long n) {
        return n + c(restn(a, n), b);
    }

    public static CNFOrdinal add(CNFOrdinal a, CNFOrdinal b) {
        if (atom(a) && atom(b)) {
            return new CNFOrdinal(a.getValue() + b.getValue());
        }
        int res = cmpo(fe(a), fe(b));
        if (res < 0) return b;
        if (res == 0) return new CNFOrdinal(new CNFOrdinal(fe(a), add(fc(a), fc(b))), b.rest());
        return new CNFOrdinal(new CNFOrdinal(fe(a), fc(a)), add(a.rest(), b));
    }

    private static boolean limitp(CNFOrdinal a) {
        if (atom(a)) {
            return a.getValue() == 0;
        }
        return limitp(a.rest());
    }

    private static CNFOrdinal exp2(CNFOrdinal a, CNFOrdinal b) {
        if (cmpo(b, ONE) == 0) return a;
        return multiply(new CNFOrdinal(new CNFOrdinal(multiply(fe(a), subtract(b, ONE)), ONE), ZERO), a);
    }

    private static CNFOrdinal pmult(CNFOrdinal a, CNFOrdinal b, long n) {
        if (a.isAtom() && a.getValue() == 0 || b.isAtom() && b.getValue() == 0) {
            return new CNFOrdinal(0);
        }
        if (atom(a) && atom(b)) {
            return new CNFOrdinal(a.getValue() * b.getValue());
        }
        if (atom(b)) {
            return new CNFOrdinal(new CNFOrdinal(fe(a), new CNFOrdinal(fc(a).getValue() * b.getValue())), a.rest());
        }
        long m = count(fe(a), fe(b), n);
        return new CNFOrdinal(new CNFOrdinal(padd(fe(a), fe(b), m), fc(b)), pmult(a, b.rest(), m));
    }

    private static CNFOrdinal expw(CNFOrdinal a, CNFOrdinal b) {
        if (!a.isAtom() || ! b.isAtom()) {
            throw new AssertionError();
        }
        return new CNFOrdinal((long) Math.pow(a.getValue(), b.getValue()));
    }

    public static CNFOrdinal subtract(CNFOrdinal a, CNFOrdinal b) {
        if (atom(a) && atom(b) && a.getValue() <= b.getValue()) return new CNFOrdinal(0);
        if (atom(a) && atom(b)) return new CNFOrdinal(a.getValue() - b.getValue());
        int res = cmpo(fe(a), fe(b));
        if (res < 0) return new CNFOrdinal(0);
        if (res > 0) return a;
        if (less(fc(a), fc(b))) return new CNFOrdinal(0);
        if (less(fc(b), fc(a))) return new CNFOrdinal(new CNFOrdinal(fe(a), subtract(fc(a), fc(b))), a.rest());
        return subtract(a.rest(), b.rest());
    }

    public static CNFOrdinal multiply(CNFOrdinal a, CNFOrdinal b) {
        return pmult(a, b, 0);
    }

    private static CNFOrdinal limitpart(CNFOrdinal a) {
        if (atom(a)) return ZERO;
        return new CNFOrdinal(a.first(), limitpart(a.rest()));
    }

    private static CNFOrdinal natpart(CNFOrdinal a) {
        if (atom(a)) return a;
        return natpart(a.rest());
    }

    private static CNFOrdinal exp1(CNFOrdinal p, CNFOrdinal b) {
        if (cmpo(fe(b), ONE) == 0) return new CNFOrdinal(new CNFOrdinal(fc(b), expw(p, b.rest())), ZERO);
        if (atom(b.rest())) return new CNFOrdinal(new CNFOrdinal(new CNFOrdinal(new CNFOrdinal(subtract(fe(b), ONE), fc(b)), ZERO), expw(p, b.rest())), ZERO);
        CNFOrdinal c = exp1(p, b.rest());
        return new CNFOrdinal(new CNFOrdinal(new CNFOrdinal(new CNFOrdinal(subtract(fe(b), ONE), ONE), fe(c)), fc(c)), ZERO);
    }

    public static CNFOrdinal exp(CNFOrdinal a, CNFOrdinal b) {
        if (cmpo(b, ZERO) == 0 || cmpo(a, ONE) == 0) return ONE;
        if (cmpo(a, ZERO) == 0) return ZERO;
        if (atom(a) && atom(b)) return expw(a, b);
        if (atom(a)) return exp1(a, b);
        if (atom(b)) return exp3(a, b);
        return exp4(a, b);
    }

    private static CNFOrdinal helper(CNFOrdinal a, CNFOrdinal p, long n, CNFOrdinal q) {
        if (cmpo(q, ZERO) == 0) return p;
        return padd(multiply(exp2(a, q), p), helper(a, p, n, subtract(q, ONE)), n);
    }

    private static CNFOrdinal exp3(CNFOrdinal a, CNFOrdinal q) {
        if (cmpo(q, ZERO) == 0) return ONE;
        if (cmpo(q, ONE) == 0) return a;
        if (limitp(a)) return exp2(a, q);
        CNFOrdinal c = limitpart(a);
        long n = length(a);
        return padd(firstn(exp2(c, q), n), helper(c, natpart(a), n, subtract(q, ONE)), n);
    }

    private static CNFOrdinal exp4(CNFOrdinal a, CNFOrdinal b) {
        return multiply(new CNFOrdinal(new CNFOrdinal(multiply(fe(a), limitpart(b)), ONE), ZERO), exp3(a, natpart(b)));
    }
}
