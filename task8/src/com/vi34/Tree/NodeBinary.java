package com.vi34.Tree;

import com.vi34.Ordinal.CNFOrdinal;

/**
 * Created by vi34 on 29.10.2016.
 */
public abstract class NodeBinary extends Node {
    NodeBinary() {}

    NodeBinary(Node l, Node r) {
        children = new Node[2];
        children[0] = l;
        children[1] = r;
    }

    @Override
    public String toString() {
        return "(" + children[0].toString() + ")" + type().toString() + "(" + children[1].toString() + ")";
    }

    protected abstract CNFOrdinal f(CNFOrdinal a, CNFOrdinal b);

    @Override
    public CNFOrdinal toCNF() {
        return f(children[0].toCNF(), children[1].toCNF());
    }
}
