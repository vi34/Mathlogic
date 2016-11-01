package com.vi34.Tree;

import com.vi34.Ordinal.CNFOrdinal;

/**
 * Created by vi34 on 29.10.2016.
 */
public class NodeConstant extends Node {
    private final String s;

    public NodeConstant(String s) {
        this.s = s;
    }

    @Override
    public NodeType type() {
        return NodeType.VALUE;
    }

    @Override
    public CNFOrdinal toCNF() {
        if (s.equals("w")) {
            return new CNFOrdinal(new CNFOrdinal(CNFOrdinal.ONE, CNFOrdinal.ONE), CNFOrdinal.ZERO);
        }
        return new CNFOrdinal(Long.parseLong(s));
    }
}
