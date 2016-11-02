package com.vi34.parsing.tree;

import com.vi34.ordinals.CNFOrdinal;
import com.vi34.ordinals.OrdinalCalc;

import java.util.List;

/**
 * Created by vi34 on 29.10.2016.
 */
public class Const implements Node {
    private final String s;

    public Const(String s) {
        this.s = s;
    }

    @Override
    public Oper oper() {
        return Oper.VALUE;
    }

    @Override
    public String toString() {
        return s;
    }

    @Override
    public CNFOrdinal toCNF() {
        if (s.equals("w")) {
            return new CNFOrdinal(new CNFOrdinal(OrdinalCalc.ONE, OrdinalCalc.ONE), OrdinalCalc.ZERO);
        }
        return new CNFOrdinal(Long.parseLong(s));
    }
}
