package com.vi34.Ordinal;


import static com.vi34.Ordinal.OrdinalCalc.fc;
import static com.vi34.Ordinal.OrdinalCalc.fe;

/**
 * Created by vi34 on 29.10.2016.
 */
public class CNFOrdinal implements Comparable<CNFOrdinal> {
    public final static CNFOrdinal ZERO = new CNFOrdinal(0);
    public final static CNFOrdinal ONE = new CNFOrdinal(1);
    private final long value;
    private final boolean isAtom;
    private final CNFOrdinal first;
    private final CNFOrdinal rest; // (first.first*w^first.second + rest, where rest.first.second<first.second)

    public CNFOrdinal(long value) {
        this.value = value;
        isAtom = true;
        first = rest = null;
    }

    public CNFOrdinal(CNFOrdinal first, CNFOrdinal rest) {
        isAtom = false;
        this.first = first;
        this.rest = rest;
        this.value = -1;
    }

    public boolean isAtom() {
        return isAtom;
    }

    public long getValue() {
        return value;
    }

    public CNFOrdinal first() {
        if (isAtom()) throw new AssertionError();
        return first;
    }

    public CNFOrdinal rest() {
        if (isAtom()) throw new AssertionError();
        return rest;
    }

    @Override
    public int compareTo(CNFOrdinal o) {
        return OrdinalCalc.cmpo(this, o);
    }

    @Override
    public String toString() {
        if (isAtom()) {
            return Long.toString(getValue());
        }
        StringBuilder res = new StringBuilder();
        res.append("wsadf^(").append(fe(this)).append(")*").append(fc(this));
        if (rest() != null) {
            res.append("+").append(rest().toString());
        }
        return res.toString();
    }
}
