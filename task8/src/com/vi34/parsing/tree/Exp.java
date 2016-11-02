package com.vi34.parsing.tree;

import com.vi34.ordinals.CNFOrdinal;
import com.vi34.ordinals.OrdinalCalc;

/**
 * Created by vi34 on 29.10.2016.
 */
public class Exp extends Binary {
    public Exp(Node res, Node node) {
        super(res, node);
    }

    @Override
    protected CNFOrdinal f(CNFOrdinal a, CNFOrdinal b) {
        return OrdinalCalc.exp(a, b);
    }

    @Override
    public Oper oper() {
        return Oper.EXP;
    }
}
