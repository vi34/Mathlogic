package com.vi34.ordinals;


import static com.vi34.ordinals.OrdinalCalc.fc;
import static com.vi34.ordinals.OrdinalCalc.fe;

/**
 * Created by vi34 on 29.10.2016.
 * Store Ordinal in Cantor Normal Form
 * For every ordinal α ∈ 0, there are unique n, p ∈ ω, α1 > · · · > αn > 0,
 * and x1, . . . , xn ∈ ω\{0} such that α > α1 and α = ω^α1*x1 + · · · + ω^αn*xn + p
 */
public class CNFOrdinal implements Comparable<CNFOrdinal> {
    private final long value;
    private final boolean isAtom;
    private final CNFOrdinal first;
    private final CNFOrdinal rest;

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
        res.append("w^(").append(fe(this)).append(")*").append(fc(this));
        if (rest() != null) {
            res.append("+").append(rest().toString());
        }
        return res.toString();
    }
}
