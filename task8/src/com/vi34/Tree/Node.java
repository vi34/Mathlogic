package com.vi34.Tree;

import com.vi34.Ordinal.CNFOrdinal;

/**
 * Created by vi34 on 29.10.2016.
 */
public abstract class Node {
    Node[] children;
    protected abstract NodeType type();
    public abstract CNFOrdinal toCNF();
}
