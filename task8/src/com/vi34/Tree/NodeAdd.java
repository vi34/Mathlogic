package com.vi34.Tree;

import com.vi34.Ordinal.CNFOrdinal;
import com.vi34.Ordinal.OrdinalCalc;

/**
 * Created by vi34 on 29.10.2016.
 */
public class NodeAdd extends NodeBinary {
    public NodeAdd(Node res, Node node) {
        super(res, node);
    }

    @Override
    protected CNFOrdinal f(CNFOrdinal a, CNFOrdinal b) {
        return OrdinalCalc.add(a, b);
    }

    @Override
    public NodeType type() {
        return NodeType.ADD;
    }
}
