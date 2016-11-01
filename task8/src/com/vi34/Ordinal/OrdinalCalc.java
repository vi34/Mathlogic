package com.vi34.Ordinal;

/**
 * Created by vi34 on 02/11/2016.
 */
public class OrdinalCalc {

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

    public static boolean atom(CNFOrdinal a) {
        return a == null || a.isAtom();
    }

    public static CNFOrdinal fe(CNFOrdinal a) {
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

    private static boolean less(CNFOrdinal a, CNFOrdinal b) {
        return cmpo(a, b) < 0;
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

    private static long c(CNFOrdinal a, CNFOrdinal b) {
        if (less(fe(b), fe(a))) return 1 + c(a.rest(), b);
        return 0;
    }

    private static long count(CNFOrdinal a, CNFOrdinal b, long n) {
        return n + c(restn(a, n), b);
    }

    private static CNFOrdinal padd(CNFOrdinal a, CNFOrdinal b, long n) {
        return concat(firstn(a, n), add(restn(a, n), b));
    }

    private static CNFOrdinal pmult(CNFOrdinal a, CNFOrdinal b, long n) {
        if (a.isAtom() && a.getValue() == 0 || b.isAtom() && b.getValue() == 0) return new CNFOrdinal(0);
        if (atom(a) && atom(b)) return new CNFOrdinal(a.getValue() * b.getValue());
        if (atom(b)) return new CNFOrdinal(new CNFOrdinal(fe(a), new CNFOrdinal(fc(a).getValue() * b.getValue())), a.rest());
        long m = count(fe(a), fe(b), n);
        return new CNFOrdinal(new CNFOrdinal(padd(fe(a), fe(b), m), fc(b)), pmult(a, b.rest(), m));
    }

    public static CNFOrdinal multiply(CNFOrdinal a, CNFOrdinal b) {
        return pmult(a, b, 0);
    }

    private static CNFOrdinal expw(CNFOrdinal a, CNFOrdinal b) {
        if (!a.isAtom() || ! b.isAtom()) {
            throw new AssertionError();
        }
        return new CNFOrdinal((long) Math.pow(a.getValue(), b.getValue()));
    }

    private static CNFOrdinal exp1(CNFOrdinal p, CNFOrdinal b) {
        if (cmpo(fe(b), CNFOrdinal.ONE) == 0) return new CNFOrdinal(new CNFOrdinal(fc(b), expw(p, b.rest())), CNFOrdinal.ZERO);
        if (atom(b.rest())) return new CNFOrdinal(new CNFOrdinal(new CNFOrdinal(new CNFOrdinal(subtract(fe(b), CNFOrdinal.ONE), fc(b)), CNFOrdinal.ZERO), expw(p, b.rest())), CNFOrdinal.ZERO);
        CNFOrdinal c = exp1(p, b.rest());
        return new CNFOrdinal(new CNFOrdinal(new CNFOrdinal(new CNFOrdinal(subtract(fe(b), CNFOrdinal.ONE), CNFOrdinal.ONE), fe(c)), fc(c)), CNFOrdinal.ZERO);
    }

    private static CNFOrdinal exp2(CNFOrdinal a, CNFOrdinal q) {
        if (cmpo(q, CNFOrdinal.ONE) == 0) return a;
        return multiply(new CNFOrdinal(new CNFOrdinal(multiply(fe(a), subtract(q, CNFOrdinal.ONE)), CNFOrdinal.ONE), CNFOrdinal.ZERO), a);
    }

    private static boolean limitp(CNFOrdinal a) {
        if (atom(a)) return a.getValue() == 0;
        return limitp(a.rest());
    }

    private static CNFOrdinal limitpart(CNFOrdinal a) {
        if (atom(a)) return CNFOrdinal.ZERO;
        return new CNFOrdinal(a.first(), limitpart(a.rest()));
    }

    private static CNFOrdinal natpart(CNFOrdinal a) {
        if (atom(a)) return a;
        return natpart(a.rest());
    }

    private static CNFOrdinal helper(CNFOrdinal a, CNFOrdinal p, long n, CNFOrdinal q) {
        if (cmpo(q, CNFOrdinal.ZERO) == 0) return p;
        return padd(multiply(exp2(a, q), p), helper(a, p, n, subtract(q, CNFOrdinal.ONE)), n);
    }

    private static CNFOrdinal exp3(CNFOrdinal a, CNFOrdinal q) {
        if (cmpo(q, CNFOrdinal.ZERO) == 0) return CNFOrdinal.ONE;
        if (cmpo(q, CNFOrdinal.ONE) == 0) return a;
        if (limitp(a)) return exp2(a, q);
        CNFOrdinal c = limitpart(a);
        long n = length(a);
        return padd(firstn(exp2(c, q), n), helper(c, natpart(a), n, subtract(q, CNFOrdinal.ONE)), n);
    }

    private static CNFOrdinal exp4(CNFOrdinal a, CNFOrdinal b) {
        return multiply(new CNFOrdinal(new CNFOrdinal(multiply(fe(a), limitpart(b)), CNFOrdinal.ONE), CNFOrdinal.ZERO), exp3(a, natpart(b)));
    }

    public static CNFOrdinal exp(CNFOrdinal a, CNFOrdinal b) {
        if (cmpo(b, CNFOrdinal.ZERO) == 0 || cmpo(a, CNFOrdinal.ONE) == 0) return CNFOrdinal.ONE;
        if (cmpo(a, CNFOrdinal.ZERO) == 0) return CNFOrdinal.ZERO;
        if (atom(a) && atom(b)) return expw(a, b);
        if (atom(a)) return exp1(a, b);
        if (atom(b)) return exp3(a, b);
        return exp4(a, b);
    }
}
