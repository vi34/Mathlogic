package com.vi34.parsing.tree;

import com.vi34.ordinals.CNFOrdinal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vi34 on 29.10.2016.
 */
public abstract class Binary implements Node {

    private List<Node> children;

    Binary(Node l, Node r) {
        children = new ArrayList<>(2);
        children.add(l);
        children.add(r);
    }

    protected abstract CNFOrdinal f(CNFOrdinal a, CNFOrdinal b);

    @Override
    public CNFOrdinal toCNF() {
        return f(children.get(0).toCNF(), children.get(1).toCNF());
    }

    @Override
    public String toString() {
        return "(" + children.get(0).toString() + ")" + oper() + "(" + children.get(1).toString() + ")";
    }
}
